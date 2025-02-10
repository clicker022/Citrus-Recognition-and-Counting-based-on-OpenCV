package ImageProcessing;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caifukai
 * operate7:凸包运算并提取轮廓
 */
public class ConvexPackageOperation {
    private Mat sourceImg = new Mat();

    public ConvexPackageOperation() {

    }

    public ConvexPackageOperation(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat getImg() {
        return this.sourceImg;
    }

    public void setImg(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat convexPackage() {
        Mat dst = new Mat();
        Mat src = sourceImg.clone();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(src,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        List<MatOfPoint> hullList = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contours.get(i),hull);
            hullList.add(matOfIntToPoints(contours.get(i),hull));
        }
        dst = Mat.zeros(src.size(), CvType.CV_8UC3);
        for(int i=0,size = contours.size();i<size;i++) {
            Scalar color = new Scalar(0,220,0);
            Scalar color2 = new Scalar(255,255,255);
            //Imgproc.drawContours(dst, contours, i, color, 1, Imgproc.LINE_AA,new Mat(),0,new Point());
            Imgproc.drawContours(dst, hullList, i, color2, 1, Imgproc.LINE_AA,new Mat(),0,new Point());
        }
        //Imgproc.threshold(dst,dst,0,255,Imgproc.THRESH_BINARY);
        return dst;
    }

    public static MatOfPoint matOfIntToPoints(MatOfPoint contour, MatOfInt indexes) {
        int[] arrIndex = indexes.toArray();
        Point[] arrContour = contour.toArray();
        Point[] arrPoints = new Point[arrIndex.length];

        for (int i=0;i<arrIndex.length;i++) {
            arrPoints[i] = arrContour[arrIndex[i]];
        }

        MatOfPoint hull = new MatOfPoint();
        hull.fromArray(arrPoints);
        return hull;
    }
}
