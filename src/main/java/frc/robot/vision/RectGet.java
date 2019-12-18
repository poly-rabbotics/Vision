package frc.robot.vision;

import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
import org.opencv.objdetect.*;

public class RectGet {

    private MatOfPoint contours;
    private Rect r;

    public void getRectangle(MatOfPoint contours, Rect output) {
        this.contours = contours;
        r = Imgproc.boundingRect(contours);
        output = r;
    }
    public void drawRectangle(Mat output) {
        Imgproc.rectangle(output, new Point(r.x, r.y), new Point (r.x + r.width, r.y + r.height), new Scalar(0, 255, 255), 5);
    }
}