package frc.robot.vision;

import org.opencv.core.*;
import edu.wpi.cscore.CvSource;
import java.util.ArrayList;
import org.opencv.imgproc.*;

public class VerticalTarget {
    private double focalDistance;
    private double targetHeight;
    private double targetWidth;
    private PreProc source;
    public VerticalTarget(PreProc source, double focalDistance, double targetHeight, double targetWidth) {
        this.source = source;
        this.focalDistance = focalDistance;
        this.targetHeight = targetHeight;
        this.targetWidth = targetWidth;
    }
    private double getBottomImageHeight() {
        ArrayList<Rect> rects = source.getRects();
        if (rects.size() != 0) {
            return rects.get(0).y + rects.get(0).height;
        }
        return -1;
    }
    private double getTanAlpha() {
        return getBottomImageHeight() / focalDistance;
    }
    private double getTopImageHeight() {
        ArrayList<Rect> rects = source.getRects();
        if (rects.size() != 0) {
            return rects.get(0).y;
        }
        return -1;
    }
    private double getTanBeta() {
        return getTopImageHeight() / focalDistance;
    }

    public double getDistance() {
        return targetHeight / (getTanBeta() - getTanAlpha());
    }
    public double getTheta() {
        double aspectRatio = targetWidth / targetHeight;
        double apparentWidth = (getTopImageHeight() - getBottomImageHeight()) * aspectRatio;
        return Math.asin(Math.min(apparentWidth / targetWidth, 1));
    }
}