package org.sunshine.core.tool.util;

import cn.hutool.core.io.IoUtil;

import java.io.InputStream;
import java.util.Objects;

/**
 * @author Teamo
 * @since 2022/03/23
 */
public class ImgUtils {
    /**
     * 图形交换格式
     */
    public static final String IMAGE_TYPE_GIF = "gif";

    /**
     * 联合照片专家组
     */
    public static final String IMAGE_TYPE_JPG = "jpg";

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
    public static String findImgType(InputStream is) {
        Objects.requireNonNull(is, "Image stream is empty");
        //读取文件前几个字节来判断图片格式
        String type = IoUtil.readHex(is, 4, false);
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
}
