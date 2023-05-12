package org.sunshine.core.tool.util;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 *
 * @author L.cm
 */
public class FileUtils extends FileCopyUtils {

    /**
     * 默认为true
     *
     * @author L.cm
     */
    public static class TrueFilter implements FileFilter, Serializable {
        private static final long serialVersionUID = -6420452043795072619L;

        public final static TrueFilter TRUE = new TrueFilter();

        @Override
        public boolean accept(File pathname) {
            return true;
        }
    }

    /**
     * 扫描目录下的文件
     *
     * @param path 路径
     * @return 文件集合
     */
    public static List<File> list(String path) {
        File file = new File(path);
        return list(file, TrueFilter.TRUE);
    }

    /**
     * 扫描目录下的文件
     *
     * @param path            路径
     * @param fileNamePattern 文件名 * 号
     * @return 文件集合
     */
    public static List<File> list(String path, final String fileNamePattern) {
        File file = new File(path);
        return list(file, pathname -> {
            String fileName = pathname.getName();
            return PatternMatchUtils.simpleMatch(fileNamePattern, fileName);
        });
    }

    /**
     * 扫描目录下的文件
     *
     * @param path   路径
     * @param filter 文件过滤
     * @return 文件集合
     */
    public static List<File> list(String path, FileFilter filter) {
        File file = new File(path);
        return list(file, filter);
    }

    /**
     * 扫描目录下的文件
     *
     * @param file 文件
     * @return 文件集合
     */
    public static List<File> list(File file) {
        List<File> fileList = new ArrayList<>();
        return list(file, fileList, TrueFilter.TRUE);
    }

    /**
     * 扫描目录下的文件
     *
     * @param file            文件
     * @param fileNamePattern Spring AntPathMatcher 规则
     * @return 文件集合
     */
    public static List<File> list(File file, final String fileNamePattern) {
        List<File> fileList = new ArrayList<>();
        return list(file, fileList, pathname -> {
            String fileName = pathname.getName();
            return PatternMatchUtils.simpleMatch(fileNamePattern, fileName);
        });
    }

    /**
     * 扫描目录下的文件
     *
     * @param file   文件
     * @param filter 文件过滤
     * @return 文件集合
     */
    public static List<File> list(File file, FileFilter filter) {
        List<File> fileList = new ArrayList<>();
        return list(file, fileList, filter);
    }

    /**
     * 扫描目录下的文件
     *
     * @param file   文件
     * @param filter 文件过滤
     * @return 文件集合
     */
    private static List<File> list(File file, List<File> fileList, FileFilter filter) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    list(f, fileList, filter);
                }
            }
        } else {
            // 过滤文件
            boolean accept = filter.accept(file);
            if (file.exists() && accept) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * Returns the path to the system temporary directory.
     *
     * @return the path to the system temporary directory.
     */
    public static String getTempDirPath() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Returns a {@link File} representing the system temporary directory.
     *
     * @return the system temporary directory.
     */
    public static File getTempDir() {
        return new File(getTempDirPath());
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file the file to read, must not be {@code null}
     * @return the file contents, never {@code null}
     */
    public static String readToString(final File file) {
        return readToString(file, StandardCharsets.UTF_8);
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file     the file to read, must not be {@code null}
     * @param encoding the encoding to use, {@code null} means platform default
     * @return the file contents, never {@code null}
     */
    public static String readToString(final File file, final Charset encoding) {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            return IoUtils.toString(in, encoding);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file the file to read, must not be {@code null}
     * @return the file contents, never {@code null}
     */
    public static byte[] readToByteArray(final File file) {
        try (InputStream in = Files.newInputStream(file.toPath())) {
            return IoUtils.toByteArray(in);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file the file to write
     * @param data the content to write to the file
     */
    public static void writeToFile(final File file, final String data) {
        writeToFile(file, data, StandardCharsets.UTF_8, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file   the file to write
     * @param data   the content to write to the file
     * @param append if {@code true}, then the String will be added to the
     *               end of the file rather than overwriting
     */
    public static void writeToFile(final File file, final String data, final boolean append) {
        writeToFile(file, data, StandardCharsets.UTF_8, append);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file     the file to write
     * @param data     the content to write to the file
     * @param encoding the encoding to use, {@code null} means platform default
     */
    public static void writeToFile(final File file, final String data, final Charset encoding) {
        writeToFile(file, data, encoding, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file     the file to write
     * @param data     the content to write to the file
     * @param encoding the encoding to use, {@code null} means platform default
     * @param append   if {@code true}, then the String will be added to the
     *                 end of the file rather than overwriting
     */
    public static void writeToFile(final File file, final String data, final Charset encoding, final boolean append) {
        try (OutputStream out = openOutputStream(file, append)) {
            IoUtils.write(data, out, encoding);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 转成file
     *
     * @param multipartFile MultipartFile
     * @param file          File
     */
    public static void toFile(MultipartFile multipartFile, final File file) {
        try {
            FileUtils.toFile(multipartFile.getInputStream(), file);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 转成file
     *
     * @param in   InputStream
     * @param file File
     */
    public static void toFile(InputStream in, final File file) throws IOException {
        try (OutputStream out = openOutputStream(file)) {
            FileUtils.copy(in, out);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * 打开指定文件的FileOutputStream ，检查并创建不存在的父目录。
     *
     * @param file 要打开输出的文件，不得为null
     * @return 指定文件的OutputStream
     * @throws IOException IOException
     */
    public static OutputStream openOutputStream(final File file) throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * 打开指定文件的OutputStream ，检查并创建不存在的父目录。
     *
     * @param file   要打开输出的文件，不得为null
     * @param append 如果为true ，则字节将添加到文件末尾而不是覆盖
     * @return 指定文件的OutputStream
     * @throws IOException IOException
     */
    public static OutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return append ? new FileOutputStream(file, true) : Files.newOutputStream(file.toPath());
    }

    /**
     * Moves a file.
     * <p>
     * When the destination file is on another file system, do a "copy and delete".
     *
     * @param srcFile  the file to be moved
     * @param destFile the destination file
     * @throws NullPointerException if source or destination is {@code null}
     * @throws IOException          if source or destination is invalid
     * @throws IOException          if an IO error occurs moving the file
     */
    public static void moveFile(final File srcFile, final File destFile) throws IOException {
        Assert.notNull(srcFile, "Source must not be null");
        Assert.notNull(destFile, "Destination must not be null");
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' is a directory");
        }
        if (destFile.exists()) {
            throw new IOException("Destination '" + destFile + "' already exists");
        }
        if (destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' is a directory");
        }
        final boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            FileUtils.copy(srcFile, destFile);
            if (!srcFile.delete()) {
                FileUtils.deleteQuietly(destFile);
                throw new IOException("Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
            }
        }
    }

    /**
     * Deletes a file, never throwing an exception. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
     * </ul>
     *
     * @param file file or directory to delete, can be {@code null}
     * @return {@code true} if the file or directory was deleted, otherwise
     * {@code false}
     */
    public static boolean deleteQuietly(@Nullable final File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                FileSystemUtils.deleteRecursively(file);
            }
        } catch (final Exception ignored) {
        }

        try {
            return file.delete();
        } catch (final Exception ignored) {
            return false;
        }
    }

    /**
     * 获取用户路径（绝对路径）
     *
     * @return 用户路径
     * @since 4.0.6
     */
    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 计算目录或文件的总大小<br>
     * 当给定对象为文件时，直接调用 {@link File#length()}<br>
     * 当给定对象为目录时，遍历目录下的所有文件和目录，递归计算其大小，求和返回<br>
     * 此方法不包括目录本身的占用空间大小。
     *
     * @param file 目录或文件,null或者文件不存在返回0
     * @return 总大小，bytes长度
     */
    public static long size(File file) {
        return size(file, false);
    }

    /**
     * 计算目录或文件的总大小<br>
     * 当给定对象为文件时，直接调用 {@link File#length()}<br>
     * 当给定对象为目录时，遍历目录下的所有文件和目录，递归计算其大小，求和返回
     *
     * @param file           目录或文件,null或者文件不存在返回0
     * @param includeDirSize 是否包括每层目录本身的大小
     * @return 总大小，bytes长度
     */
    public static long size(File file, boolean includeDirSize) {
        if (null == file || !file.exists() || isSymlink(file)) {
            return 0;
        }

        if (file.isDirectory()) {
            long size = includeDirSize ? file.length() : 0;
            File[] subFiles = file.listFiles();
            if (ObjectUtils.isEmpty(subFiles)) {
                // empty directory
                return 0L;
            }
            for (File subFile : subFiles) {
                size += size(subFile, includeDirSize);
            }
            return size;
        } else {
            return file.length();
        }
    }

    /**
     * 可读的文件大小
     *
     * @param file 文件
     * @return 大小
     */
    public static String readableFileSize(File file) {
        return readableFileSize(file.length());
    }

    /**
     * 可读的文件大小<br>
     * 参考 <a href="http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc">http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc</a>
     *
     * @param size Long类型大小
     * @return 大小
     * @see SizeUnit#format(long)
     */
    public static String readableFileSize(long size) {
        return SizeUnit.format(size);
    }

    /**
     * 判断是否为符号链接文件
     *
     * @param file 被检查的文件
     * @return 是否为符号链接文件
     */
    public static boolean isSymlink(File file) {
        return isSymlink(file.toPath());
    }

    /**
     * 判断是否为符号链接文件
     *
     * @param path 被检查的文件
     * @return 是否为符号链接文件
     */
    public static boolean isSymlink(Path path) {
        return Files.isSymbolicLink(path);
    }
}
