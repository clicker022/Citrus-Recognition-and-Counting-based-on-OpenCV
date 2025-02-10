package ImageProcessing;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author caifukai
 * operate6:基于距离变换的分水岭分割
 */
public class WatershedSplit {
    private Mat sourceImg = new Mat();
    private Mat initialImg = new Mat();
    private Mat binImg = new Mat();
    private int areaNum = 0;

    public WatershedSplit() {
    }

    public WatershedSplit(Mat sourceImg, Mat initialImg, Mat binImg) {

        this.sourceImg = sourceImg;
        this.initialImg = initialImg;
        this.binImg = binImg;
    }

    public int getAreaNum() {
        watershedImg();
        return areaNum;
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

    public Mat watershedImg() {
        Mat dst = sourceImg.clone();
        Mat ini = initialImg.clone();
        Mat bin = binImg.clone();

        Mat bg = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(9,9),new Point(-1,-1));
        Imgproc.dilate(bin,bg,kernel,new Point(-1,-1),3);
        bg.convertTo(bg,CvType.CV_8UC1);

        Mat fg = new Mat();
        Imgproc.threshold(dst,fg,0.6,1.0,Imgproc.THRESH_BINARY);
        Core.normalize(fg,fg,0,255,Core.NORM_MINMAX);
        fg.convertTo(fg,CvType.CV_8UC1);

        Mat unknown = new Mat();
        Core.subtract(bg,fg,unknown);

        Mat marker1 = new Mat();
        Imgproc.connectedComponents(fg,marker1);

        Mat markers = Mat.ones(marker1.size(),marker1.type());
        Core.add(marker1,markers,markers);
        markers.convertTo(markers,CvType.CV_8UC1);

        for (int row = 0; row < unknown.rows(); row++) {
            for (int col = 0; col < unknown.cols(); col++) {
                double c = unknown.get(row,col)[0];
                if (c == 255) {
                    markers.put(row,col,0);
                }
            }
        }
        Core.normalize(markers,markers,0,255,Core.NORM_MINMAX);

        markers.convertTo(markers,CvType.CV_32SC1);
        Imgproc.watershed(ini,markers);
        Mat mark = Mat.zeros(markers.size(),CvType.CV_8UC1);
        markers.convertTo(mark,CvType.CV_8UC1);
        double[] area = new double[256];
        double maxArea = -1;
        int index = 0;
        for (int i = 0; i < mark.rows(); i++) {
            for (int j = 0; j < mark.cols(); j++) {
                area[(int)mark.get(i,j)[0]]++;
            }
        }
        for (int i = 0; i < area.length; i++) {
            if (area[i] >= maxArea) {
                maxArea = area[i];
                index = i;
            }
        }
        for (int i = 0; i < mark.rows(); i++) {
            for (int j = 0; j < mark.cols(); j++) {
                if ((int)mark.get(i,j)[0] == index) {
                    mark.put(i,j,0);
                }
            }
        }
        for (int i = 0; i < area.length; i++) {
            if (area[i] > 0) {
                areaNum++;
            }
        }
        Imgproc.erode(mark,mark,new Mat(), new Point(-1, -1), 3);
        return mark;
    }

}
