/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.CvSource;
//import edu.wpi.cscore.CvSink;
import java.util.ArrayList;
//import org.opencv.core.*;
//import org.opencv.core.Core.*;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
//import org.opencv.objdetect.*;

import frc.robot.vision.GripPipeline;
import frc.robot.vision.RectGet;
import edu.wpi.first.vision.VisionThread;
import org.opencv.core.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private VisionThread visionThread;
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
    camera.setResolution(RobotMap.IMG_WIDTH, RobotMap.IMG_HEIGHT);
    CvSource outputStream = CameraServer.getInstance().putVideo("Contours", 320, 240);
    RectGet rg = new RectGet();
    visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
      ArrayList<MatOfPoint> contours = pipeline.filterContoursOutput();
      if (!contours.isEmpty()) {
        Rect r = new Rect();
        rg.getRectangle(contours.get(0), r);
        Mat dst = new Mat(320, 240, CvType.makeType(16, 3));
        //rg.drawRectangle(contours);
        for (int i = 0; i < contours.size(); i++ ) {
          Imgproc.drawContours(dst, contours, i, new Scalar(255, 1, 1));
        }
        
        outputStream.putFrame(dst);
        SmartDashboard.putNumber("Rect: ", r.x);
        System.out.println(r.x);
      }
    });
    visionThread.start();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
