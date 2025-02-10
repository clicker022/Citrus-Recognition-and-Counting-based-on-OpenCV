package ImageProcessing;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caifukai
 * operate8:圆拟合
 */
public class RoundFit {
    private Mat sourceImg = new Mat();
    private Mat initialImg = new Mat();
    private int count = 0;

    public RoundFit() {

    }

    public RoundFit(Mat sourceImg,Mat initialImg) {
        this.sourceImg = sourceImg;
        this.initialImg = initialImg;
    }

    public int getCount() {
        roundFit();
        return this.count;
    }
    public Mat getImg() {
        return this.sourceImg;
    }

    public Mat getInitialImg() {
        return this.initialImg;
    }

    public void setImg(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public void setInitialImg(Mat initialImg) {
        this.initialImg = initialImg;
    }

    public Mat roundFit() {
        Mat dst = sourceImg.clone();
        Imgproc.threshold(dst,dst,0,255,Imgproc.THRESH_BINARY);
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(dst,dst,100,200);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        Point[] centers = new Point[contours.size()];
        float[][] radius = new float[contours.size()][1];

        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            centers[i] = new Point();
            Imgproc.minEnclosingCircle(contoursPoly[i], centers[i], radius[i]);
        }

        //Mat drawing = Mat.zeros(dst.size(),CvType.CV_8UC3);
        Mat drawing = initialImg.clone();
        List<MatOfPoint> contoursPolyList = new ArrayList<>(contoursPoly.length);
        for (MatOfPoint2f poly : contoursPoly) {
            contoursPolyList.add(new MatOfPoint(poly.toArray()));
        }
        for (int i = 0; i < contours.size(); i++) {
            Scalar color1 = new Scalar(0,255,0);
            Scalar color2 = new Scalar(255,0,0);
            //Imgproc.drawContours(drawing, contoursPolyList, i, color);
            Imgproc.rectangle(drawing, boundRect[i].tl(), boundRect[i].br(), color1, 2);
            Imgproc.circle(drawing, centers[i], (int) radius[i][0], color2, 3);
            count++;
        }
        return drawing;
    }
}
