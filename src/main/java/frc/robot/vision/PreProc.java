package frc.robot.vision;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.vision.VisionPipeline;
import edu.wpi.first.vision.VisionThread;
import frc.robot.RobotMap;

import org.opencv.core.*;
import edu.wpi.cscore.CvSource;
import java.util.ArrayList;
import org.opencv.imgproc.*;


public class PreProc {
    private UsbCamera camera;
    private CvSource outputStream;
    private ContourFilterer filter;
    private ArrayList<Rect> rects;
    private ArrayList<MatOfPoint> contours;
    Mat mat;    

    public PreProc(int cameraNumber, ContourFilterer pipeline) {
        this.camera = CameraServer.getInstance().startAutomaticCapture(cameraNumber);
        this.camera.setResolution(RobotMap.IMG_WIDTH, RobotMap.IMG_HEIGHT);
        this.outputStream = CameraServer.getInstance().putVideo("Contours", RobotMap.IMG_WIDTH, RobotMap.IMG_HEIGHT);
        this.filter = pipeline;
        this.rects = new ArrayList<Rect>();
        this.contours = new ArrayList<MatOfPoint>();
    }
    public void start() {
        VisionThread visionThread = new VisionThread(camera, filter, pipeline -> {
        contours = filter.filterContoursOutput();
            if (!contours.isEmpty()) {
                rects.clear();
                for (MatOfPoint contour : contours) {
                    System.out.println(contour.size());
                    MatOfPoint2f temp = new MatOfPoint2f();
                    contour.convertTo(temp, CvType.CV_32F);
                    //approxPolyDP(contour, contour, Imgproc.arcLength(temp, true) / 100, true);
                    System.out.println(contour.size());
                    rects.add(Imgproc.boundingRect(contour));
                }
            }
            show();
        });
        visionThread.start();
    }
    public void showRects(Mat mat) {
        for (Rect r : rects) {
            Imgproc.rectangle(  mat,  
                                new Point(r.x, r.y), 
                                new Point (r.x + r.width, r.y + r.height), 
                                new Scalar(0, 255, 255), 
                                5);
        }
    }
    public void showContours(Mat mat) {
        for (int i = 0; i < contours.size(); i++ ) {
            Imgproc.drawContours(mat, contours, i, new Scalar(255, 255, 255));
        }
    }
    public static void approxPolyDP(MatOfPoint src, MatOfPoint dst, double epsilon, boolean closed) {
        MatOfPoint2f temp = new MatOfPoint2f();
        src.convertTo(temp, CvType.CV_32F);
        Imgproc.approxPolyDP(temp, temp, epsilon, closed);
        temp.convertTo(dst, CvType.CV_32S);
    }
    private void show() {
        mat = Mat.zeros(RobotMap.IMG_HEIGHT, RobotMap.IMG_WIDTH, CvType.CV_16UC3);
        showRects(mat);
        showContours(mat);
        outputStream.putFrame(mat);
    }

    public ArrayList<Rect> getRects() {
        return rects;
    }
}