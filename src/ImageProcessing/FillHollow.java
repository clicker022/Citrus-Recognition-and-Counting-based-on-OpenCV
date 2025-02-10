package ImageProcessing;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;
import java.util.Vector;

/**
 * @author caifukai
 * operate4:对去噪后的二值图像进行空洞填充
 */
public class FillHollow {
    private Mat sourceImg = new Mat();

    public FillHollow() {
    }

    public FillHollow(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat getImg() {
        return this.sourceImg;
    }

    public void setImg(Mat sourceImg) {
        this.sourceImg = sourceImg;
    }

    public Mat fillHollow() {
        Mat noHoleImg = sourceImg.clone();
        fillHole(sourceImg,noHoleImg,5000);
        return noHoleImg;
    }


    private void fillHole(Mat src, Mat dst, int areaLimit) {
        int checkMode = 0;
        Mat pointLabel = Mat.zeros(src.size(), CvType.CV_8UC1);
        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                if (src.get(i, j)[0] > 10) {
                    pointLabel.put(i, j, 3);
                }
            }
        }
        List<Point> neihborPos = new Vector<Point>();
        neihborPos.add(new Point(-1, 0));
        neihborPos.add(new Point(1, 0));
        neihborPos.add(new Point(0, -1));
        neihborPos.add(new Point(0, 1));
        int neihborCount = 4;
        int currX = 0, currY = 0;
        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                if (pointLabel.get(i, j)[0] == 0) {
                    List<Point> growBuffer = new Vector<Point>();
                    growBuffer.add(new Point(j, i));
                    pointLabel.put(i, j, 1);
                    int checkResult = 0;
                    for (int z = 0; z < growBuffer.size(); z++) {
                        for (int q = 0; q < neihborCount; q++) {
                            currX = (int) growBuffer.get(z).x + (int) neihborPos.get(q).x;
                            currY = (int) growBuffer.get(z).y + (int) neihborPos.get(q).y;
                            if (currX >= 0 && currX < src.cols() && currY >= 0 && currY < src.rows()) {
                                if (pointLabel.get(currY, currX)[0] == 0) {
                                    growBuffer.add(new Point(currX, currY));
                                    pointLabel.put(currY, currX, 1);
                                }
                            }
                        }
                    }
                    if (growBuffer.size() > areaLimit) {
                        checkResult = 2;
                    } else {
                        checkResult = 1;
                    }
                    for (int z = 0; z < growBuffer.size(); z++) {
                        currX = (int) growBuffer.get(z).x;
                        currY = (int) growBuffer.get(z).y;
                        if (pointLabel.get(currY, currX) == null) {
                            continue;
                        }
                        pointLabel.put(currY, currX, checkResult + (int) pointLabel.get(currY, currX)[0]);
                    }
                }
            }
        }
        checkMode = 255 * (1 - checkMode);
        for (int i = 0; i < src.rows(); ++i) {
            for (int j = 0; j < src.cols(); ++j) {
                if (pointLabel.get(i, j)[0] == 2) {
                    dst.put(i, j, checkMode);
                } else if (pointLabel.get(i, j)[0] == 3) {
                    dst.put(i, j, src.get(i, j)[0]);
                }
            }
        }
    }

}
