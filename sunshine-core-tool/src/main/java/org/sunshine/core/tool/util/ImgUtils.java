package org.sunshine.core.tool.util;

import org.springframework.util.Assert;
import org.sunshine.core.tool.support.Img;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 工具类来源hutool
 *
 * @author Teamo
 * @since 2022/03/23
 */
public class ImgUtils {

    /**
     * RGB颜色范围上限
     */
    private static final int RGB_COLOR_BOUND = 256;

    /**
     * 图形交换格式
     */
    public static final String IMAGE_TYPE_GIF = "gif";

    /**
     * 联合照片专家组
     */
    public static final String IMAGE_TYPE_JPG = "jpg";

    /**
     * 联合照片专家组
     */
    public static final String IMAGE_TYPE_JPEG = "jpeg";

    /**
     * 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
     */
    public static final String IMAGE_TYPE_BMP = "bmp";

    /**
     * 可移植网络图形
     */
    public static final String IMAGE_TYPE_PNG = "png";

    /**
     * Photoshop的专用格式Photoshop
     */
    public static final String IMAGE_TYPE_PSD = "psd";

    /**
     * 联合照片专家组部分16进制HEX
     */
    private static final String JPG_HEX = "FFD8FF";

    /**
     * 可移植网络图形部分16进制HEX
     */
    private static final String PNG_HEX = "89504E47";

    /**
     * 图形交换格式部分16进制HEX
     */
    private static final String GIF_HEX = "47494638";

    /**
     * Bitmap（位图）部分16进制HEX
     */
    private static final String BMP_HEX = "424D";

    /**
     * Photoshop部分16进制HEX
     */
    private static final String PSD_HEX = "38425053";

    /**
     * 根据文件流判断图片类型
     *
     * @param is 输入流
     * @return jpg/png/gif/bmp
     */
    public static String getImgTypeByStream(InputStream is) {
        Assert.notNull(is, "Image InputStream must be not null!");
        //读取文件前几个字节来判断图片格式
        String type = HexUtils.encodeToString(IoUtils.readBytes(is, 4), false);
        if (type.contains(JPG_HEX)) {
            return IMAGE_TYPE_JPG;
        } else if (type.contains(PNG_HEX)) {
            return IMAGE_TYPE_PNG;
        } else if (type.contains(GIF_HEX)) {
            return IMAGE_TYPE_GIF;
        } else if (type.contains(BMP_HEX)) {
            return IMAGE_TYPE_BMP;
        } else if (type.contains(PSD_HEX)) {
            return IMAGE_TYPE_PSD;
        } else {
            return StringPool.UNKNOWN;
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------- scale

    /**
     * 缩放图像（按比例缩放），目标文件的扩展名决定目标文件类型
     *
     * @param srcImageFile  源图像文件
     * @param destImageFile 缩放后的图像文件，扩展名决定目标类型
     * @param scale         缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(File srcImageFile, File destImageFile, float scale) {
        scale(read(srcImageFile), destImageFile, scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像来源流
     * @param destStream 缩放后的图像写出到的流
     * @param scale      缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(InputStream srcStream, OutputStream destStream, float scale) {
        scale(read(srcStream), destStream, scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像来源流
     * @param destStream 缩放后的图像写出到的流
     * @param scale      缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, float scale) {
        scale(read(srcStream), destStream, scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg   源图像来源流
     * @param destFile 缩放后的图像写出到的流
     * @param scale    缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(Image srcImg, File destFile, float scale) {
        Img.from(srcImg).setTargetImageType(FileNameUtils.extName(destFile)).scale(scale).write(destFile);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg 源图像来源流
     * @param out    缩放后的图像写出到的流
     * @param scale  缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(Image srcImg, OutputStream out, float scale) {
        scale(srcImg, getImageOutputStream(out), scale);
    }

    /**
     * 缩放图像（按比例缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImg          源图像来源流
     * @param destImageStream 缩放后的图像写出到的流
     * @param scale           缩放比例。比例大于1时为放大，小于1大于0为缩小
     */
    public static void scale(Image srcImg, ImageOutputStream destImageStream, float scale) {
        writeJpg(scale(srcImg, scale), destImageStream);
    }

    /**
     * 缩放图像（按比例缩放）
     *
     * @param srcImg 源图像来源流
     * @param scale  缩放比例。比例大于1时为放大，小于1大于0为缩小
     * @return {@link Image}
     */
    public static Image scale(Image srcImg, float scale) {
        return Img.from(srcImg).scale(scale).getImg();
    }

    /**
     * 缩放图像（按长宽缩放）<br>
     * 注意：目标长宽与原图不成比例会变形
     *
     * @param srcImg 源图像来源流
     * @param width  目标宽度
     * @param height 目标高度
     * @return {@link Image}
     */
    public static Image scale(Image srcImg, int width, int height) {
        return Img.from(srcImg).scale(width, height).getImg();
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认格式与源图片相同，无法识别原图片默认JPG
     *
     * @param srcImageFile  源图像文件地址
     * @param destImageFile 缩放后的图像地址
     * @param width         缩放后的宽度
     * @param height        缩放后的高度
     * @param fixedColor    补充的颜色，不补充为{@code null}
     */
    public static void scale(File srcImageFile, File destImageFile, int width, int height, Color fixedColor) {
        Img.from(srcImageFile)
                .setTargetImageType(FileNameUtils.extName(destImageFile))
                .scale(width, height, fixedColor)
                .write(destImageFile);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 缩放后的图像目标流
     * @param width      缩放后的宽度
     * @param height     缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     */
    public static void scale(InputStream srcStream, OutputStream destStream, int width, int height, Color fixedColor) {
        scale(read(srcStream), getImageOutputStream(destStream), width, height, fixedColor);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 缩放后的图像目标流
     * @param width      缩放后的宽度
     * @param height     缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     */
    public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, int width, int height, Color fixedColor) {
        scale(read(srcStream), destStream, width, height, fixedColor);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式，此方法并不关闭流
     *
     * @param srcImage        源图像
     * @param destImageStream 缩放后的图像目标流
     * @param width           缩放后的宽度
     * @param height          缩放后的高度
     * @param fixedColor      比例不对时补充的颜色，不补充为{@code null}
     */
    public static void scale(Image srcImage, ImageOutputStream destImageStream, int width, int height, Color fixedColor) {
        writeJpg(scale(srcImage, width, height, fixedColor), destImageStream);
    }

    /**
     * 缩放图像（按高度和宽度缩放）<br>
     * 缩放后默认为jpeg格式
     *
     * @param srcImage   源图像
     * @param width      缩放后的宽度
     * @param height     缩放后的高度
     * @param fixedColor 比例不对时补充的颜色，不补充为{@code null}
     * @return {@link Image}
     */
    public static Image scale(Image srcImage, int width, int height, Color fixedColor) {
        return Img.from(srcImage).scale(width, height, fixedColor).getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- cut

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImgFile  源图像文件
     * @param destImgFile 切片后的图像文件
     * @param rectangle   矩形对象，表示矩形区域的x，y，width，height
     */
    public static void cut(File srcImgFile, File destImgFile, Rectangle rectangle) {
        cut(read(srcImgFile), destImgFile, rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 切片后的图像输出流
     * @param rectangle  矩形对象，表示矩形区域的x，y，width，height
     */
    public static void cut(InputStream srcStream, OutputStream destStream, Rectangle rectangle) {
        cut(read(srcStream), destStream, rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 切片后的图像输出流
     * @param rectangle  矩形对象，表示矩形区域的x，y，width，height
     */
    public static void cut(ImageInputStream srcStream, ImageOutputStream destStream, Rectangle rectangle) {
        cut(read(srcStream), destStream, rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param destFile  输出的文件
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     */
    public static void cut(Image srcImage, File destFile, Rectangle rectangle) {
        write(cut(srcImage, rectangle), destFile);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param out       切片后的图像输出流
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     */
    public static void cut(Image srcImage, OutputStream out, Rectangle rectangle) {
        cut(srcImage, getImageOutputStream(out), rectangle);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，此方法并不关闭流
     *
     * @param srcImage        源图像
     * @param destImageStream 切片后的图像输出流
     * @param rectangle       矩形对象，表示矩形区域的x，y，width，height
     */
    public static void cut(Image srcImage, ImageOutputStream destImageStream, Rectangle rectangle) {
        writeJpg(cut(srcImage, rectangle), destImageStream);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImage  源图像
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height
     * @return {@link BufferedImage}
     */
    public static Image cut(Image srcImage, Rectangle rectangle) {
        return Img.from(srcImage).setPositionBaseCentre(false).cut(rectangle).getImg();
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)，填充满整个图片（直径取长宽最小值）
     *
     * @param srcImage 源图像
     * @param x        原图的x坐标起始位置
     * @param y        原图的y坐标起始位置
     * @return {@link Image}
     */
    public static Image cut(Image srcImage, int x, int y) {
        return cut(srcImage, x, y, -1);
    }

    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param srcImage 源图像
     * @param x        原图的x坐标起始位置
     * @param y        原图的y坐标起始位置
     * @param radius   半径，小于0表示填充满整个图片（直径取长宽最小值）
     * @return {@link Image}
     */
    public static Image cut(Image srcImage, int x, int y, int radius) {
        return Img.from(srcImage).cut(x, y, radius).getImg();
    }

    /**
     * 图像切片（指定切片的宽度和高度）
     *
     * @param srcImageFile 源图像
     * @param descDir      切片目标文件夹
     * @param destWidth    目标切片宽度。默认200
     * @param destHeight   目标切片高度。默认150
     */
    public static void slice(File srcImageFile, File descDir, int destWidth, int destHeight) {
        slice(read(srcImageFile), descDir, destWidth, destHeight);
    }

    /**
     * 图像切片（指定切片的宽度和高度）
     *
     * @param srcImage   源图像
     * @param descDir    切片目标文件夹
     * @param destWidth  目标切片宽度。默认200
     * @param destHeight 目标切片高度。默认150
     */
    public static void slice(Image srcImage, File descDir, int destWidth, int destHeight) {
        if (destWidth <= 0) {
            destWidth = 200; // 切片宽度
        }
        if (destHeight <= 0) {
            destHeight = 150; // 切片高度
        }
        int srcWidth = srcImage.getWidth(null); // 源图宽度
        int srcHeight = srcImage.getHeight(null); // 源图高度

        if (srcWidth < destWidth) {
            destWidth = srcWidth;
        }
        if (srcHeight < destHeight) {
            destHeight = srcHeight;
        }

        int cols; // 切片横向数量
        int rows; // 切片纵向数量
        // 计算切片的横向和纵向数量
        if (srcWidth % destWidth == 0) {
            cols = srcWidth / destWidth;
        } else {
            cols = (int) Math.floor((double) srcWidth / destWidth) + 1;
        }
        if (srcHeight % destHeight == 0) {
            rows = srcHeight / destHeight;
        } else {
            rows = (int) Math.floor((double) srcHeight / destHeight) + 1;
        }
        // 循环建立切片
        Image tag;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // 四个参数分别为图像起点坐标和宽高
                // 即: CropImageFilter(int x,int y,int width,int height)
                tag = cut(srcImage, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                // 输出为文件
                write(tag, FileUtils.file(descDir, "_r" + i + "_c" + j + ".jpg"));
            }
        }
    }

    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像文件
     * @param destDir      切片目标文件夹
     * @param rows         目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols         目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(File srcImageFile, File destDir, int rows, int cols) {
        sliceByRowsAndCols(srcImageFile, destDir, IMAGE_TYPE_JPEG, rows, cols);
    }

    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像文件
     * @param destDir      切片目标文件夹
     * @param format       目标文件格式
     * @param rows         目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols         目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(File srcImageFile, File destDir, String format, int rows, int cols) {
        try {
            sliceByRowsAndCols(ImageIO.read(srcImageFile), destDir, format, rows, cols);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 图像切割（指定切片的行数和列数），默认RGB模式
     *
     * @param srcImage 源图像，如果非{@link BufferedImage}，则默认使用RGB模式
     * @param destDir  切片目标文件夹
     * @param rows     目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols     目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(Image srcImage, File destDir, int rows, int cols) {
        sliceByRowsAndCols(srcImage, destDir, IMAGE_TYPE_JPEG, rows, cols);
    }

    /**
     * 图像切割（指定切片的行数和列数），默认RGB模式
     *
     * @param srcImage 源图像，如果非{@link BufferedImage}，则默认使用RGB模式
     * @param destDir  切片目标文件夹
     * @param format   目标文件格式
     * @param rows     目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols     目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public static void sliceByRowsAndCols(Image srcImage, File destDir, String format, int rows, int cols) {
        if (!destDir.exists()) {
            FileUtils.mkdir(destDir);
        } else if (!destDir.isDirectory()) {
            throw new IllegalArgumentException("Destination Dir must be a Directory !");
        }

        try {
            if (rows <= 0 || rows > 20) {
                rows = 2; // 切片行数
            }
            if (cols <= 0 || cols > 20) {
                cols = 2; // 切片列数
            }
            // 读取源图像
            final BufferedImage bi = toBufferedImage(srcImage);
            int srcWidth = bi.getWidth(); // 源图宽度
            int srcHeight = bi.getHeight(); // 源图高度

            int destWidth = NumberUtils.partValue(srcWidth, cols); // 每张切片的宽度
            int destHeight = NumberUtils.partValue(srcHeight, rows); // 每张切片的高度

            // 循环建立切片
            Image tag;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    tag = cut(bi, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
                    // 输出为文件
                    ImageIO.write(toRenderedImage(tag), format, new File(destDir, "_r" + i + "_c" + j + "." + format));
                }
            }
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------- convert

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destStream 目标图像输出流
     */
    public static void convert(InputStream srcStream, String formatName, OutputStream destStream) {
        write(read(srcStream), formatName, getImageOutputStream(destStream));
    }

    /**
     * 图像类型转换：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像流
     * @param formatName      包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destImageStream 目标图像输出流
     * @param isSrcPng        源图片是否为PNG格式
     */
    public static void convert(Image srcImage, String formatName, ImageOutputStream destImageStream, boolean isSrcPng) {
        try {
            ImageIO.write(isSrcPng ? copyImage(srcImage, BufferedImage.TYPE_INT_RGB) : toBufferedImage(srcImage), formatName, destImageStream);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------- grey

    /**
     * 彩色转为黑白
     *
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     */
    public static void gray(File srcImageFile, File destImageFile) {
        gray(read(srcImageFile), destImageFile);
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     */
    public static void gray(InputStream srcStream, OutputStream destStream) {
        gray(read(srcStream), getImageOutputStream(destStream));
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     */
    public static void gray(ImageInputStream srcStream, ImageOutputStream destStream) {
        gray(read(srcStream), destStream);
    }

    /**
     * 彩色转为黑白
     *
     * @param srcImage 源图像流
     * @param outFile  目标文件
     */
    public static void gray(Image srcImage, File outFile) {
        write(gray(srcImage), outFile);
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param out      目标图像流
     */
    public static void gray(Image srcImage, OutputStream out) {
        gray(srcImage, getImageOutputStream(out));
    }

    /**
     * 彩色转为黑白<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像流
     * @param destImageStream 目标图像流
     */
    public static void gray(Image srcImage, ImageOutputStream destImageStream) {
        writeJpg(gray(srcImage), destImageStream);
    }

    /**
     * 彩色转为黑白
     *
     * @param srcImage 源图像流
     * @return {@link Image}灰度后的图片
     */
    public static Image gray(Image srcImage) {
        return Img.from(srcImage).gray().getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- binary

    /**
     * 彩色转为黑白二值化图片，根据目标文件扩展名确定转换后的格式
     *
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     */
    public static void binary(File srcImageFile, File destImageFile) {
        binary(read(srcImageFile), destImageFile);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param imageType  图片格式(扩展名)
     */
    public static void binary(InputStream srcStream, OutputStream destStream, String imageType) {
        binary(read(srcStream), getImageOutputStream(destStream), imageType);
    }

    /**
     * 彩色转为黑白黑白二值化图片<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param imageType  图片格式(扩展名)
     */
    public static void binary(ImageInputStream srcStream, ImageOutputStream destStream, String imageType) {
        binary(read(srcStream), destStream, imageType);
    }

    /**
     * 彩色转为黑白二值化图片，根据目标文件扩展名确定转换后的格式
     *
     * @param srcImage 源图像流
     * @param outFile  目标文件
     */
    public static void binary(Image srcImage, File outFile) {
        write(binary(srcImage), outFile);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流，输出JPG格式
     *
     * @param srcImage  源图像流
     * @param out       目标图像流
     * @param imageType 图片格式(扩展名)
     */
    public static void binary(Image srcImage, OutputStream out, String imageType) {
        binary(srcImage, getImageOutputStream(out), imageType);
    }

    /**
     * 彩色转为黑白二值化图片<br>
     * 此方法并不关闭流，输出JPG格式
     *
     * @param srcImage        源图像流
     * @param destImageStream 目标图像流
     * @param imageType       图片格式(扩展名)
     */
    public static void binary(Image srcImage, ImageOutputStream destImageStream, String imageType) {
        write(binary(srcImage), imageType, destImageStream);
    }

    /**
     * 彩色转为黑白二值化图片
     *
     * @param srcImage 源图像流
     * @return {@link Image}二值化后的图片
     */
    public static Image binary(Image srcImage) {
        return Img.from(srcImage).binary().getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- press

    /**
     * 给图片添加文字水印
     *
     * @param imageFile 源图像文件
     * @param destFile  目标图像文件
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(File imageFile, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) {
        pressText(read(imageFile), destFile, pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressText  水印文字
     * @param color      水印的字体颜色
     * @param font       {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(InputStream srcStream, OutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) {
        pressText(read(srcStream), getImageOutputStream(destStream), pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressText  水印文字
     * @param color      水印的字体颜色
     * @param font       {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(ImageInputStream srcStream, ImageOutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) {
        pressText(read(srcStream), destStream, pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param destFile  目标流
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(Image srcImage, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) {
        write(pressText(srcImage, pressText, color, font, x, y, alpha), destFile);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param to        目标流
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(Image srcImage, OutputStream to, String pressText, Color color, Font font, int x, int y, float alpha) {
        pressText(srcImage, getImageOutputStream(to), pressText, color, font, x, y, alpha);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像
     * @param destImageStream 目标图像流
     * @param pressText       水印文字
     * @param color           水印的字体颜色
     * @param font            {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x               修正值。 默认在中间，偏移量相对于中间偏移
     * @param y               修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha           透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressText(Image srcImage, ImageOutputStream destImageStream, String pressText, Color color, Font font, int x, int y, float alpha) {
        writeJpg(pressText(srcImage, pressText, color, font, x, y, alpha), destImageStream);
    }

    /**
     * 给图片添加文字水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像
     * @param pressText 水印文字
     * @param color     水印的字体颜色
     * @param font      {@link Font} 字体相关信息，如果默认则为{@code null}
     * @param x         修正值。 默认在中间，偏移量相对于中间偏移
     * @param y         修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 处理后的图像
     */
    public static Image pressText(Image srcImage, String pressText, Color color, Font font, int x, int y, float alpha) {
        return Img.from(srcImage).pressText(pressText, color, font, x, y, alpha).getImg();
    }

    /**
     * 给图片添加图片水印
     *
     * @param srcImageFile  源图像文件
     * @param destImageFile 目标图像文件
     * @param pressImg      水印图片
     * @param x             修正值。 默认在中间，偏移量相对于中间偏移
     * @param y             修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(File srcImageFile, File destImageFile, Image pressImg, int x, int y, float alpha) {
        pressImage(read(srcImageFile), destImageFile, pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressImg   水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(InputStream srcStream, OutputStream destStream, Image pressImg, int x, int y, float alpha) {
        pressImage(read(srcStream), getImageOutputStream(destStream), pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param destStream 目标图像流
     * @param pressImg   水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x          修正值。 默认在中间，偏移量相对于中间偏移
     * @param y          修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha      透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(ImageInputStream srcStream, ImageOutputStream destStream, Image pressImg, int x, int y, float alpha) {
        pressImage(read(srcStream), destStream, pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param outFile  写出文件
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x        修正值。 默认在中间，偏移量相对于中间偏移
     * @param y        修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(Image srcImage, File outFile, Image pressImg, int x, int y, float alpha) {
        write(pressImage(srcImage, pressImg, x, y, alpha), outFile);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param out      目标图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x        修正值。 默认在中间，偏移量相对于中间偏移
     * @param y        修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(Image srcImage, OutputStream out, Image pressImg, int x, int y, float alpha) {
        pressImage(srcImage, getImageOutputStream(out), pressImg, x, y, alpha);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage        源图像流
     * @param destImageStream 目标图像流
     * @param pressImg        水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x               修正值。 默认在中间，偏移量相对于中间偏移
     * @param y               修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha           透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     */
    public static void pressImage(Image srcImage, ImageOutputStream destImageStream, Image pressImg, int x, int y, float alpha) {
        writeJpg(pressImage(srcImage, pressImg, x, y, alpha), destImageStream);
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage 源图像流
     * @param pressImg 水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param x        修正值。 默认在中间，偏移量相对于中间偏移
     * @param y        修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha    透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 结果图片
     */
    public static Image pressImage(Image srcImage, Image pressImg, int x, int y, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, x, y, alpha).getImg();
    }

    /**
     * 给图片添加图片水印<br>
     * 此方法并不关闭流
     *
     * @param srcImage  源图像流
     * @param pressImg  水印图片，可以使用{@link ImageIO#read(File)}方法读取文件
     * @param rectangle 矩形对象，表示矩形区域的x，y，width，height，x,y从背景图片中心计算
     * @param alpha     透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return 结果图片
     */
    public static Image pressImage(Image srcImage, Image pressImg, Rectangle rectangle, float alpha) {
        return Img.from(srcImage).pressImage(pressImg, rectangle, alpha).getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- rotate

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param imageFile 被旋转图像文件
     * @param degree    旋转角度
     * @param outFile   输出文件
     */
    public static void rotate(File imageFile, int degree, File outFile) {
        rotate(read(imageFile), degree, outFile);
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param image   目标图像
     * @param degree  旋转角度
     * @param outFile 输出文件
     */
    public static void rotate(Image image, int degree, File outFile) {
        write(rotate(image, degree), outFile);
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流
     *
     * @param image  目标图像
     * @param degree 旋转角度
     * @param out    输出流
     */
    public static void rotate(Image image, int degree, OutputStream out) {
        writeJpg(rotate(image, degree), getImageOutputStream(out));
    }

    /**
     * 旋转图片为指定角度<br>
     * 此方法不会关闭输出流，输出格式为JPG
     *
     * @param image  目标图像
     * @param degree 旋转角度
     * @param out    输出图像流
     */
    public static void rotate(Image image, int degree, ImageOutputStream out) {
        writeJpg(rotate(image, degree), out);
    }

    /**
     * 旋转图片为指定角度<br>
     * 来自：<a href="http://blog.51cto.com/cping1982/130066">http://blog.51cto.com/cping1982/130066</a>
     *
     * @param image  目标图像
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Image rotate(Image image, int degree) {
        return Img.from(image).rotate(degree).getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- flip

    /**
     * 水平翻转图像
     *
     * @param imageFile 图像文件
     * @param outFile   输出文件
     */
    public static void flip(File imageFile, File outFile) {
        flip(read(imageFile), outFile);
    }

    /**
     * 水平翻转图像
     *
     * @param image   图像
     * @param outFile 输出文件
     */
    public static void flip(Image image, File outFile) {
        write(flip(image), outFile);
    }

    /**
     * 水平翻转图像
     *
     * @param image 图像
     * @param out   输出
     */
    public static void flip(Image image, OutputStream out) {
        flip(image, getImageOutputStream(out));
    }

    /**
     * 水平翻转图像，写出格式为JPG
     *
     * @param image 图像
     * @param out   输出
     */
    public static void flip(Image image, ImageOutputStream out) {
        writeJpg(flip(image), out);
    }

    /**
     * 水平翻转图像
     *
     * @param image 图像
     * @return 翻转后的图片
     */
    public static Image flip(Image image) {
        return Img.from(image).flip().getImg();
    }

    // ---------------------------------------------------------------------------------------------------------------------- compress

    /**
     * 压缩图像，输出图像只支持jpg文件
     *
     * @param imageFile 图像文件
     * @param outFile   输出文件，只支持jpg文件
     * @param quality   压缩比例，必须为0~1
     */
    public static void compress(File imageFile, File outFile, float quality) {
        Img.from(imageFile).setQuality(quality).write(outFile);
    }

    // ---------------------------------------------------------------------------------------------------------------------- other

    /**
     * {@link Image} 转 {@link RenderedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式。
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     */
    public static RenderedImage toRenderedImage(Image img) {
        if (img instanceof RenderedImage) {
            return (RenderedImage) img;
        }

        return copyImage(img, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 首先尝试强转，否则新建一个{@link BufferedImage}后重新绘制，使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param img {@link Image}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        return copyImage(img, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制<br>
     * 默认的，png图片使用 {@link BufferedImage#TYPE_INT_ARGB}模式，其它使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param image     {@link Image}
     * @param imageType 目标图片类型，例如jpg或png等
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(Image image, String imageType) {
        return toBufferedImage(image, imageType, null);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制<br>
     * 默认的，png图片使用 {@link BufferedImage#TYPE_INT_ARGB}模式，其它使用 {@link BufferedImage#TYPE_INT_RGB} 模式
     *
     * @param image           {@link Image}
     * @param imageType       目标图片类型，例如jpg或png等
     * @param backgroundColor 背景色{@link Color}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(Image image, String imageType, Color backgroundColor) {
        final int type = IMAGE_TYPE_PNG.equalsIgnoreCase(imageType)
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;
        return toBufferedImage(image, type, backgroundColor);
    }

    /**
     * {@link Image} 转 {@link BufferedImage}<br>
     * 如果源图片的RGB模式与目标模式一致，则直接转换，否则重新绘制
     *
     * @param image           {@link Image}
     * @param imageType       目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @param backgroundColor 背景色{@link Color}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(Image image, int imageType, Color backgroundColor) {
        BufferedImage bufferedImage;
        if (image instanceof BufferedImage) {
            bufferedImage = (BufferedImage) image;
            if (imageType != bufferedImage.getType()) {
                bufferedImage = copyImage(image, imageType, backgroundColor);
            }
            return bufferedImage;
        }

        bufferedImage = copyImage(image, imageType, backgroundColor);
        return bufferedImage;
    }

    /**
     * 将已有Image复制新的一份出来
     *
     * @param img       {@link Image}
     * @param imageType 目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @return {@link BufferedImage}
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_ARGB_PRE
     * @see BufferedImage#TYPE_INT_BGR
     * @see BufferedImage#TYPE_3BYTE_BGR
     * @see BufferedImage#TYPE_4BYTE_ABGR
     * @see BufferedImage#TYPE_4BYTE_ABGR_PRE
     * @see BufferedImage#TYPE_BYTE_GRAY
     * @see BufferedImage#TYPE_USHORT_GRAY
     * @see BufferedImage#TYPE_BYTE_BINARY
     * @see BufferedImage#TYPE_BYTE_INDEXED
     * @see BufferedImage#TYPE_USHORT_565_RGB
     * @see BufferedImage#TYPE_USHORT_555_RGB
     */
    public static BufferedImage copyImage(Image img, int imageType) {
        return copyImage(img, imageType, null);
    }

    /**
     * 将已有Image复制新的一份出来
     *
     * @param img             {@link Image}
     * @param imageType       目标图片类型，{@link BufferedImage}中的常量，例如黑白等
     * @param backgroundColor 背景色，{@code null} 表示默认背景色（黑色或者透明）
     * @return {@link BufferedImage}
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_ARGB_PRE
     * @see BufferedImage#TYPE_INT_BGR
     * @see BufferedImage#TYPE_3BYTE_BGR
     * @see BufferedImage#TYPE_4BYTE_ABGR
     * @see BufferedImage#TYPE_4BYTE_ABGR_PRE
     * @see BufferedImage#TYPE_BYTE_GRAY
     * @see BufferedImage#TYPE_USHORT_GRAY
     * @see BufferedImage#TYPE_BYTE_BINARY
     * @see BufferedImage#TYPE_BYTE_INDEXED
     * @see BufferedImage#TYPE_USHORT_565_RGB
     * @see BufferedImage#TYPE_USHORT_555_RGB
     */
    public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
        // ensures that all the pixels loaded
        // issue#1821@Github
        img = new ImageIcon(img).getImage();

        final BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), imageType);
        final Graphics2D bGr = GraphicsUtils.createGraphics(bimage, backgroundColor);
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    /**
     * 将Base64编码的图像信息转为 {@link BufferedImage}
     *
     * @param base64 图像的Base64表示
     * @return {@link BufferedImage}
     */
    public static BufferedImage toImage(String base64) {
        return toImage(Base64.getDecoder().decode(base64));
    }

    /**
     * 将的图像bytes转为 {@link BufferedImage}
     *
     * @param imageBytes 图像bytes
     * @return {@link BufferedImage}
     */
    public static BufferedImage toImage(byte[] imageBytes) {
        return read(new ByteArrayInputStream(imageBytes));
    }

    /**
     * 将图片对象转换为Base64的Data URI形式，格式为：data:image/[imageType];base64,[data]
     *
     * @param image     图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     */
    public static String toBase64DataUri(Image image, String imageType) {
        return UrlUtils.getDataUriBase64("image/" + imageType, toBase64(image, imageType));
    }

    /**
     * 将图片对象转换为Base64形式
     *
     * @param image     图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     */
    public static String toBase64(Image image, String imageType) {
        return Base64Utils.encodeToString(toBytes(image, imageType));
    }

    /**
     * 将图片对象转换为bytes形式
     *
     * @param image     图片对象
     * @param imageType 图片类型
     * @return Base64的字符串表现形式
     */
    public static byte[] toBytes(Image image, String imageType) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(image, imageType, out);
        return out.toByteArray();
    }

    /**
     * 根据文字创建PNG图片
     *
     * @param str             文字
     * @param font            字体{@link Font}
     * @param backgroundColor 背景颜色，默认透明
     * @param fontColor       字体颜色，默认黑色
     * @param out             图片输出地
     */
    public static void createImage(String str, Font font, Color backgroundColor, Color fontColor, ImageOutputStream out) {
        writePng(createImage(str, font, backgroundColor, fontColor, BufferedImage.TYPE_INT_ARGB), out);
    }

    /**
     * 根据文字创建透明背景的PNG图片
     *
     * @param str       文字
     * @param font      字体{@link Font}
     * @param fontColor 字体颜色，默认黑色
     * @param out       图片输出地
     */
    public static void createTransparentImage(String str, Font font, Color fontColor, ImageOutputStream out) {
        writePng(createImage(str, font, null, fontColor, BufferedImage.TYPE_INT_ARGB), out);
    }

    /**
     * 根据文字创建图片
     *
     * @param str             文字
     * @param font            字体{@link Font}
     * @param backgroundColor 背景颜色，默认透明
     * @param fontColor       字体颜色，默认黑色
     * @param imageType       图片类型，见：{@link BufferedImage}
     * @return 图片
     */
    public static BufferedImage createImage(String str, Font font, Color backgroundColor, Color fontColor, int imageType) {
        // 获取font的样式应用在str上的整个矩形
        final Rectangle2D r = getRectangle(str, font);
        // 获取单个字符的高度
        int unitHeight = (int) Math.floor(r.getHeight());
        // 获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
        int width = (int) Math.round(r.getWidth()) + 1;
        // 把单个字符的高度+3保证高度绝对能容纳字符串作为图片的高度
        int height = unitHeight + 3;

        // 创建图片
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics g = image.getGraphics();
        if (null != backgroundColor) {
            // 先用背景色填充整张图片,也就是背景
            g.setColor(backgroundColor);
            g.fillRect(0, 0, width, height);
        }

        g.setColor(fontColor == null ? Color.BLACK : fontColor);
        g.setFont(font);// 设置画笔字体
        g.drawString(str, 0, font.getSize());// 画出字符串
        g.dispose();

        return image;
    }

    /**
     * 获取font的样式应用在str上的整个矩形
     *
     * @param str  字符串，必须非空
     * @param font 字体，必须非空
     * @return {@link Rectangle2D}
     */
    public static Rectangle2D getRectangle(String str, Font font) {
        return font.getStringBounds(str,
                new FontRenderContext(AffineTransform.getScaleInstance(1, 1),
                        false,
                        false));
    }

    /**
     * 写出图像为JPG格式
     *
     * @param image           {@link Image}
     * @param destImageStream 写出到的目标流
     */
    public static void writeJpg(Image image, ImageOutputStream destImageStream) {
        write(image, IMAGE_TYPE_JPG, destImageStream);
    }

    /**
     * 写出图像为PNG格式
     *
     * @param image           {@link Image}
     * @param destImageStream 写出到的目标流
     */
    public static void writePng(Image image, ImageOutputStream destImageStream) {
        write(image, IMAGE_TYPE_PNG, destImageStream);
    }

    /**
     * 写出图像为JPG格式
     *
     * @param image {@link Image}
     * @param out   写出到的目标流
     */
    public static void writeJpg(Image image, OutputStream out) {
        write(image, IMAGE_TYPE_JPG, out);
    }

    /**
     * 写出图像为PNG格式
     *
     * @param image {@link Image}
     * @param out   写出到的目标流
     */
    public static void writePng(Image image, OutputStream out) {
        write(image, IMAGE_TYPE_PNG, out);
    }

    /**
     * 按照目标格式写出图像：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param srcStream  源图像流
     * @param formatName 包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destStream 目标图像输出流
     */
    public static void write(ImageInputStream srcStream, String formatName, ImageOutputStream destStream) {
        write(read(srcStream), formatName, destStream);
    }

    /**
     * 写出图像：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param image     {@link Image}
     * @param imageType 图片类型（图片扩展名）
     * @param out       写出到的目标流
     */
    public static void write(Image image, String imageType, OutputStream out) {
        write(image, imageType, getImageOutputStream(out));
    }

    /**
     * 写出图像为指定格式：GIF=》JPG、GIF=》PNG、PNG=》JPG、PNG=》GIF(X)、BMP=》PNG<br>
     * 此方法并不关闭流
     *
     * @param image           {@link Image}
     * @param imageType       图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream) {
        return write(image, imageType, destImageStream, 1);
    }

    /**
     * 写出图像为指定格式
     *
     * @param image           {@link Image}
     * @param imageType       图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @param quality         质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality) {
        return write(image, imageType, destImageStream, quality, null);
    }

    /**
     * 写出图像为指定格式
     *
     * @param image           {@link Image}
     * @param imageType       图片类型（图片扩展名）
     * @param destImageStream 写出到的目标流
     * @param quality         质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @param backgroundColor 背景色{@link Color}
     * @return 是否成功写出，如果返回false表示未找到合适的Writer
     */
    public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality, Color backgroundColor) {
        if (StringUtils.isBlank(imageType)) {
            imageType = IMAGE_TYPE_JPG;
        }

        final BufferedImage bufferedImage = toBufferedImage(image, imageType, backgroundColor);
        final ImageWriter writer = getWriter(bufferedImage, imageType);
        return write(bufferedImage, writer, destImageStream, quality);
    }

    /**
     * 写出图像为目标文件扩展名对应的格式
     *
     * @param image      {@link Image}
     * @param targetFile 目标文件
     */
    public static void write(Image image, File targetFile) {
        FileUtils.touch(targetFile);
        ImageOutputStream out = null;
        try {
            out = getImageOutputStream(targetFile);
            write(image, FileNameUtils.extName(targetFile), out);
        } finally {
            IoUtils.closeQuietly(out);
        }
    }

    /**
     * 通过{@link ImageWriter}写出图片到输出流
     *
     * @param image   图片
     * @param writer  {@link ImageWriter}
     * @param output  输出的Image流{@link ImageOutputStream}
     * @param quality 质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     * @return 是否成功写出
     */
    public static boolean write(Image image, ImageWriter writer, ImageOutputStream output, float quality) {
        if (writer == null) {
            return false;
        }

        writer.setOutput(output);
        final RenderedImage renderedImage = toRenderedImage(image);
        // 设置质量
        ImageWriteParam imgWriteParams = null;
        if (quality > 0 && quality < 1) {
            imgWriteParams = writer.getDefaultWriteParam();
            if (imgWriteParams.canWriteCompressed()) {
                imgWriteParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                imgWriteParams.setCompressionQuality(quality);
                // ColorModel.getRGBdefault();
                final ColorModel colorModel = renderedImage.getColorModel();
                imgWriteParams.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
            }
        }

        try {
            if (null != imgWriteParams) {
                writer.write(null, new IIOImage(renderedImage, null, null), imgWriteParams);
            } else {
                writer.write(renderedImage);
            }
            output.flush();
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        } finally {
            writer.dispose();
        }
        return true;
    }

    /**
     * 从文件中读取图片
     *
     * @param imageFile 图片文件
     * @return 图片
     */
    public static BufferedImage read(File imageFile) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + imageFile.getName() + "] is not supported!");
        }

        return result;
    }

    /**
     * 从流中读取图片
     *
     * @param imageStream 图片文件
     * @return 图片
     */
    public static BufferedImage read(InputStream imageStream) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageStream);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 从图片流中读取图片
     *
     * @param imageStream 图片文件
     * @return 图片
     */
    public static BufferedImage read(ImageInputStream imageStream) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageStream);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 从URL中读取图片
     *
     * @param imageUrl 图片文件
     * @return 图片
     */
    public static BufferedImage read(URL imageUrl) {
        BufferedImage result;
        try {
            result = ImageIO.read(imageUrl);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of [" + imageUrl + "] is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageOutputStream}
     *
     * @param out {@link OutputStream}
     * @return {@link ImageOutputStream}
     */
    public static ImageOutputStream getImageOutputStream(OutputStream out) {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(out);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type is not supported!");
        }

        return result;
    }

    /**
     * 获取{@link ImageOutputStream}
     *
     * @param outFile {@link File}
     * @return {@link ImageOutputStream}
     */
    public static ImageOutputStream getImageOutputStream(File outFile) {
        ImageOutputStream result;
        try {
            result = ImageIO.createImageOutputStream(outFile);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        if (null == result) {
            throw new IllegalArgumentException("Image type of file [" + outFile.getName() + "] is not supported!");
        }

        return result;
    }

    /**
     * 根据给定的Image对象和格式获取对应的{@link ImageWriter}，如果未找到合适的Writer，返回null
     *
     * @param img        {@link Image}
     * @param formatName 图片格式，例如"jpg"、"png"
     * @return {@link ImageWriter}
     */
    public static ImageWriter getWriter(Image img, String formatName) {
        final ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(toBufferedImage(img, formatName));
        final Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
        return iter.hasNext() ? iter.next() : null;
    }

    // -------------------------------------------------------------------------------------------------------------------- Color

    /**
     * 生成随机颜色
     *
     * @return 随机颜色
     */
    public static Color randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Color(random.nextInt(RGB_COLOR_BOUND), random.nextInt(RGB_COLOR_BOUND), random.nextInt(RGB_COLOR_BOUND));
    }

    /**
     * 获得修正后的矩形坐标位置，变为以背景中心为基准坐标（即x,y == 0,0时，处于背景正中）
     *
     * @param rectangle        矩形
     * @param backgroundWidth  参考宽（背景宽）
     * @param backgroundHeight 参考高（背景高）
     * @return 修正后的{@link Point}
     */
    public static Point getPointBaseCentre(Rectangle rectangle, int backgroundWidth, int backgroundHeight) {
        return new Point(
                rectangle.x + (Math.abs(backgroundWidth - rectangle.width) / 2), //
                rectangle.y + (Math.abs(backgroundHeight - rectangle.height) / 2)//
        );
    }

    /**
     * 图片颜色转换<br>
     * 可以使用灰度 (gray)等
     *
     * @param colorSpace 颜色模式，如灰度等
     * @param image      被转换的图片
     * @return 转换后的图片
     */
    public static BufferedImage colorConvert(ColorSpace colorSpace, BufferedImage image) {
        return filter(new ColorConvertOp(colorSpace, null), image);
    }

    /**
     * 转换图片<br>
     * 可以使用一系列平移 (translation)、缩放 (scale)、翻转 (flip)、旋转 (rotation) 和错切 (shear) 来构造仿射变换。
     *
     * @param xform 2D仿射变换，它执行从 2D 坐标到其他 2D 坐标的线性映射，保留了线的“直线性”和“平行性”。
     * @param image 被转换的图片
     * @return 转换后的图片
     */
    public static BufferedImage transform(AffineTransform xform, BufferedImage image) {
        return filter(new AffineTransformOp(xform, null), image);
    }

    /**
     * 图片过滤转换
     *
     * @param op    过滤操作实现，如二维转换可传入{@link AffineTransformOp}
     * @param image 原始图片
     * @return 过滤后的图片
     */
    public static BufferedImage filter(BufferedImageOp op, BufferedImage image) {
        return op.filter(image, null);
    }

    /**
     * 图片滤镜，借助 {@link ImageFilter}实现，实现不同的图片滤镜
     *
     * @param filter 滤镜实现
     * @param image  图片
     * @return 滤镜后的图片
     */
    public static Image filter(ImageFilter filter, Image image) {
        return Toolkit.getDefaultToolkit().createImage(
                new FilteredImageSource(image.getSource(), filter));
    }
}
