package ImageProcessing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author caifukai
 * operate5:对填充后的二值图像进行距离变换
 */
public class DistanceTransformation {
    private Mat sourceImg = new Mat();

    public DistanceTransformation() {
    }

    public DistanceTransformation(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat getImg() {
        return this.sourceImg;
    }

    public void setImg(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }
    public Mat distanceTrans() {
        Mat dist = new Mat();
        Mat src = sourceImg.clone();
        Imgproc.distanceTransform(src,dist,Imgproc.DIST_L1,3,5);
        Core.normalize(dist,dist,0.0,1.0,Core.NORM_MINMAX);
        return dist;
    }
    public Mat distDisplay() {
        Mat dist = distanceTrans();
        Mat distDisplayScaled = new Mat();
        Core.multiply(dist, new Scalar(255),distDisplayScaled);
        Mat distDisplay = new Mat();
        distDisplayScaled.convertTo(distDisplay,CvType.CV_8U);
        return distDisplay;
    }

}
