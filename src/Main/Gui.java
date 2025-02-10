package Main;

import ImageProcessing.*;
import ImageProcessing.RGB2GB;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.io.File;
import java.io.IOException;
import javafx.scene.control.Menu;
import javafx.scene.image.ImageView;
import javafx.stage.Window;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;


public class Gui extends Application{

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = (Parent) FXMLLoader.load(this.getClass().getResource("/Main/Gui.fxml"));
            primaryStage.setTitle("柑橘水果识别系统");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    private Mat mat;
    private Mat gb;
    private Mat bin;
    private Mat denoise;
    private Mat noHole;
    private Mat dist;
    private Mat distDisplay;
    private Mat watershed;
    private Mat convexPack;
    private Mat roundFit;


    private boolean saved;
    private File File;
    private int flag;

    //Mat类型转化成Image
    public BufferedImage getImage(Mat matrix) {
        return this.toBufferedImage(matrix);
    }

    private BufferedImage toBufferedImage(Mat matrix) {
        int type = 10;
        if (matrix.channels() > 1) {
            type = 5;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        byte[] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }


    @FXML
    private AnchorPane BasePane;

    @FXML
    private Label Label;

    @FXML
    private ImageView ImageView;

    @FXML
        //打开图片
    void Open(ActionEvent event) {
        Window stage = BasePane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));
        File = fileChooser.showOpenDialog(stage);//显示打开
        if(File!=null){
            mat = Imgcodecs.imread(File.getAbsolutePath());//读取原图像
            Image image = new Image("file:"+File.getAbsolutePath());
            ImageView.setImage(image);
            Label.setText("图片导入成功！");
            /**--预先生成其他事件的数据--**/
            //将原图像转换为2R-G-B色差图并显示
            gb = new RGB2GB(mat).RGB2GB();

            //将2R-G-B色差图用Otsu法二值化为二值图并显示
            bin = new OtsuThresholdSegmentation(gb).otsu();

            //将二值图像进行形态学运算去噪并显示
            denoise = new Denoise(bin).denoise();

            //将去噪图像经空洞填充为无空洞图像并显示
            noHole = new FillHollow(denoise).fillHollow();

            //将无空洞图进行距离变换，变为距离灰度图并显示,
            // dist为将用于分水岭算法的不可显示的图像，distDisplay为可显示的图像
            dist = new DistanceTransformation(noHole).distanceTrans();
            distDisplay = new DistanceTransformation(noHole).distDisplay();

            //基于距离变换的分水岭分割，将粘结区域分割开，并显示
            watershed = new WatershedSplit(dist,mat,noHole).watershedImg();
            //对分水岭分割的各区域进行凸包运算，去除凹陷，并显示
            convexPack = new ConvexPackageOperation(watershed).convexPackage();

            //对各区域进行圆拟合，将拟合圆形与最小外接矩形画到原图中，并显示
            roundFit = new RoundFit(convexPack,mat).roundFit();
            /**--预先生成其他事件的数据--**/
        }
    }

    @FXML
        //保存图片
    void Save(ActionEvent event) throws IOException, InterruptedException {
        Window stage = BasePane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存图片");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));
        File newfile = fileChooser.showSaveDialog(null);
        boolean tag = ImageIO.write((toBufferedImage(this.mat)), "jpg", new File(newfile.getAbsolutePath()));
        switch (flag){//判断保存的图片种类
            case 1:tag = ImageIO.write((toBufferedImage(this.gb)) , "jpg", new File(newfile.getAbsolutePath()));break;
            case 2:tag = ImageIO.write((toBufferedImage(this.bin)) , "jpg", new File(newfile.getAbsolutePath()));break;
            case 3:tag = ImageIO.write((toBufferedImage(this.denoise)) , "jpg", new File(newfile.getAbsolutePath()));break;
            case 4:tag = ImageIO.write((toBufferedImage(this.noHole)) , "jpg", new File(newfile.getAbsolutePath()));break;
            case 5:tag = ImageIO.write((toBufferedImage(this.distDisplay)) , "jpg", new File(newfile.getAbsolutePath()));break;
            case 6:tag = ImageIO.write((toBufferedImage(this.watershed)) , "jpg", new File(newfile.getAbsolutePath()));break;
            case 7:tag = ImageIO.write((toBufferedImage(this.convexPack)) , "jpg", new File(newfile.getAbsolutePath()));break;
            case 8:tag = ImageIO.write((toBufferedImage(this.roundFit)) , "jpg", new File(newfile.getAbsolutePath()));break;
        }

        if (tag) {
            this.Label.setText("保存成功\n");
            this.saved = true;
        } else {
            this.Label.setText("保存失败\n");
        }

    }

    @FXML
        //关闭图片
    void Close(ActionEvent event) {
        int close = 0;
        if (!this.saved) {
            close = JOptionPane.showConfirmDialog((Component)null, "请确定关闭文件", "🍊提示", 0);
        }

        if (close == 0) {
            this.ImageView.setImage((Image)null);
        }
        Label.setText("关闭成功，可以打开其他图片");
    }

    @FXML
        //原始图片
    void Ori(ActionEvent event) {
        Image image = new Image("file:"+File.getAbsolutePath());
        ImageView.setImage(image);
    }

    @FXML
        //2R-G-B 直接显示
    void Rgb(ActionEvent event) {
        flag=1;
        BufferedImage bufferedImage = toBufferedImage(gb);//将Mat类型转化为BufferedImage类型
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);//将BufferedImage类型转化为Image类型
        ImageView.setImage(image);
        Label.setText("正在查看色彩变化过程");
    }

    @FXML
        //Otsu
    void Otsu(ActionEvent event) {
        flag=2;
        BufferedImage bufferedImage = toBufferedImage(bin);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        Label.setText("正在查看二值化过程");
    }

    @FXML
        //二值图像进行形态学运算去噪并显示
    void Wipe(ActionEvent event) {
        flag=3;
        BufferedImage bufferedImage = toBufferedImage(denoise);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        Label.setText("正在查看去噪过程");
    }

    @FXML
        //空洞填充
    void Fill(ActionEvent event) {
        flag=4;
        BufferedImage bufferedImage = toBufferedImage(noHole);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        Label.setText("正在查看空洞填充过程");
    }

    @FXML
        //距离变换
    void Range(ActionEvent event) {
        flag=5;
        BufferedImage bufferedImage = toBufferedImage(distDisplay);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        Label.setText("正在查看距离变换过程");
    }

    @FXML
        //分水岭分割
    void Seg(ActionEvent event) {
        flag=6;
        BufferedImage bufferedImage = toBufferedImage(watershed);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        Label.setText("正在查看分水岭分割过程");
    }

    @FXML
        //凸包运算
    void Bump(ActionEvent event) {
        flag=7;
        BufferedImage bufferedImage = toBufferedImage(convexPack);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        Label.setText("正在查看凸包运算过程");
    }

    @FXML
        //圆拟合
    void Match(ActionEvent event) {
        flag=8;
        BufferedImage bufferedImage = toBufferedImage(roundFit);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        Label.setText("正在查看圆拟合标记过程\n");
    }

    @FXML
        //计数
    void Tag(ActionEvent event) {
        System.out.println(new WatershedSplit(dist,mat,noHole).getAreaNum() - 2);
        Label.setText("🍊柑橘数量为"+(new WatershedSplit(dist,mat,noHole).getAreaNum() - 2));
    }

    @FXML
        //按钮执行的标记与计数
    void Check(ActionEvent event) {
        BufferedImage bufferedImage = toBufferedImage(roundFit);
        Image image =SwingFXUtils.toFXImage(bufferedImage,null);
        ImageView.setImage(image);
        System.out.println(new WatershedSplit(dist,mat,noHole).getAreaNum() - 2);
        Label.setText("🍊柑橘数量为"+(new WatershedSplit(dist,mat,noHole).getAreaNum() - 2));

    }

    @FXML
    void About(ActionEvent event) {
        JOptionPane.showMessageDialog((Component)null,
                "步骤：\n1.点击菜单栏的“文件”，子菜单目录中选择“打开图片”，即可从本地选择待识别的图片。" +
                        "\n\n2.点击菜单栏的“操作”，选择你要进行的某一步识别过程" +
                        "\n\n3.对于想要保存的图片，点击“文件”，选择“保存”即可\n\n祝您使用愉快！",
                "🍊柑橘水果识别系统使用说明", -1);
    }


}

