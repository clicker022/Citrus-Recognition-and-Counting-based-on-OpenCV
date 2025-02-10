package ImageProcessing;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caifukai
 * operate1:将原始图像从RGB转为2R-G-B色差图像
 */
public class RGB2GB {
    private Mat sourceImg = new Mat();

    public RGB2GB() {
    }

    public RGB2GB(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat getImg() {
        return this.sourceImg;
    }

    public void setImg(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat RGB2GB() {
        Mat src = sourceImg.clone();
        Mat GBImg = new Mat();
        List<Mat> channels = new ArrayList<Mat>();
        Core.split(src, channels);
        Mat blue = channels.get(0);
        Mat green = channels.get(1);
        Mat red = channels.get(2);
        Imgproc.cvtColor(src, GBImg, Imgproc.COLOR_BGR2GRAY);
        for (int i = 0; i < GBImg.rows(); i++) {
            for (int j = 0; j < GBImg.cols(); j++) {
                double[] b = blue.get(i, j);
                double[] g = green.get(i, j);
                double[] r = red.get(i, j);
                GBImg.put(i, j, 2 * r[0] - g[0] - b[0]);
            }
        }
        return GBImg;
    }
}
