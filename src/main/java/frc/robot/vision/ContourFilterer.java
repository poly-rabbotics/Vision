package frc.robot.vision;

import edu.wpi.first.vision.VisionPipeline;
import java.util.ArrayList;
import org.opencv.core.*;


public interface ContourFilterer extends VisionPipeline {
    public ArrayList<MatOfPoint> filterContoursOutput();
}