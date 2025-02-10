package ImageProcessing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * @author caifukai
 * operate2:使用Otsu阈值分割将灰度图像二值化
 */
public class OtsuThresholdSegmentation {
    private Mat sourceImg = new Mat();
    public OtsuThresholdSegmentation() {}
    public OtsuThresholdSegmentation(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat getImg() {
        return this.sourceImg;
    }

    public void setImg(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }
    public Mat otsu() {
        Mat binaryImg = new Mat();
        Imgproc.threshold(sourceImg,binaryImg,0,255,Imgproc.THRESH_OTSU);
        return binaryImg;
    }
}
