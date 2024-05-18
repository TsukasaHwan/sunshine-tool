package org.sunshine.core.tool.util;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.multipart.MultipartFile;
import org.sunshine.core.tool.support.MoveVisitor;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 *
 * @author Teamo
 */
public class FileUtils extends FileCopyUtils {

    /**
     * 默认为true
     *
     * @author L.cm
     */
    public static class TrueFilter implements FileFilter, Serializable {
        @Serial
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
            return IoUtils.readBytes(in);
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
    public static void writeToFile(MultipartFile multipartFile, final File file) {
        try {
            FileUtils.writeToFile(multipartFile.getInputStream(), file);
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
    public static void writeToFile(InputStream in, final File file) {
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
        return append ? new FileOutputStream(touch(file), true) : getOutputStream(touch(file));
    }

    /**
     * 移动文件或者目录
     *
     * @param srcFile    源文件或者目录
     * @param destFile   目标文件或者目录
     * @param isOverride 是否覆盖目标，只有目标为文件才覆盖
     */
    public static Path move(final File srcFile, final File destFile, boolean isOverride) {
        final Path src = srcFile.toPath();
        Path target = destFile.toPath();
        final CopyOption[] options = isOverride ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[]{};

        if (isDirectory(target)) {
            // 创建子路径的情况，1是目标是目录，需要移动到目录下，2是目标不能存在，自动创建目录
            target = target.resolve(src.getFileName());
        }

        // target 不存在导致NoSuchFileException
        try {
            if (Files.exists(target) && Files.isSameFile(src, target)) {
                // 当用户传入目标路径与源路径一致时，直接返回，否则会导致删除风险。
                return target;
            }
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        // 自动创建目标的父目录
        mkParentDirs(target);
        try {
            return Files.move(src, target, options);
        } catch (final IOException e) {
            if (e instanceof FileAlreadyExistsException) {
                // 目标文件已存在，直接抛出异常
                // issue#I4QV0L@Gitee
                throw Exceptions.unchecked(e);
            }
            // 移动失败，可能是跨分区移动导致的，采用递归移动方式
            try {
                Files.walkFileTree(src, new MoveVisitor(src, target, options));
            } catch (IOException ex) {
                throw Exceptions.unchecked(ex);
            }
            // 移动后删除空目录
            deleteQuietly(srcFile);
            return target;
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

    /**
     * 是否为Windows环境
     *
     * @return 是否为Windows环境
     */
    public static boolean isWindows() {
        return FileNameUtils.WINDOWS_SEPARATOR == File.separatorChar;
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型<br>
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            mkdirsSafely(dir, 5, 1);
        }
        return dir;
    }

    /**
     * 创建所给目录及其父目录
     *
     * @param dir 目录
     * @return 目录
     */
    public static Path mkdir(Path dir) {
        if (null != dir && !exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw Exceptions.unchecked(e);
            }
        }
        return dir;
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param path 文件或目录
     * @return 父目录
     */
    public static Path mkParentDirs(Path path) {
        return mkdir(path.getParent());
    }

    /**
     * 安全地级联创建目录 (确保并发环境下能创建成功)
     *
     * <pre>
     *     并发环境下，假设 test 目录不存在，如果线程A mkdirs "test/A" 目录，线程B mkdirs "test/B"目录，
     *     其中一个线程可能会失败，进而导致以下代码抛出 FileNotFoundException 异常
     *
     *     file.getParentFile().mkdirs(); // 父目录正在被另一个线程创建中，返回 false
     *     file.createNewFile(); // 抛出 IO 异常，因为该线程无法感知到父目录已被创建
     * </pre>
     *
     * @param dir         待创建的目录
     * @param tryCount    最大尝试次数
     * @param sleepMillis 线程等待的毫秒数
     * @return true表示创建成功，false表示创建失败
     */
    public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
        if (dir == null) {
            return false;
        }
        if (dir.isDirectory()) {
            return true;
        }
        // 高并发场景下，可以看到 i 处于 1 ~ 3 之间
        for (int i = 1; i <= tryCount; i++) {
            // 如果文件已存在，也会返回 false，所以该值不能作为是否能创建的依据，因此不对其进行处理
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            if (dir.exists()) {
                return true;
            }
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        return dir.exists();
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     */
    public static File touch(File file) {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkParentDirs(file);
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        }
        return file;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     */
    public static File touch(String parent, String path) {
        return touch(file(parent, path));
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        if (null == file) {
            return null;
        }
        return mkdir(getParent(file, 1));
    }

    /**
     * 获取指定层级的父路径
     *
     * <pre>
     * getParent(file("d:/aaa/bbb/cc/ddd", 0)) -》 "d:/aaa/bbb/cc/ddd"
     * getParent(file("d:/aaa/bbb/cc/ddd", 2)) -》 "d:/aaa/bbb"
     * getParent(file("d:/aaa/bbb/cc/ddd", 4)) -》 "d:/"
     * getParent(file("d:/aaa/bbb/cc/ddd", 5)) -》 null
     * </pre>
     *
     * @param file  目录或文件
     * @param level 层级
     * @return 路径File，如果不存在返回null
     */
    public static File getParent(File file, int level) {
        if (level < 1 || null == file) {
            return file;
        }

        File parentFile;
        try {
            parentFile = file.getCanonicalFile().getParentFile();
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
        if (1 == level) {
            return parentFile;
        }
        return getParent(parentFile, level - 1);
    }

    /**
     * 创建File对象<br>
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父目录
     * @param path   文件路径
     * @return File
     */
    public static File file(String parent, String path) {
        return file(new File(parent), path);
    }

    /**
     * 创建File对象<br>
     * 根据的路径构建文件，在Win下直接构建，在Linux下拆分路径单独构建
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     */
    public static File file(File parent, String path) {
        Assert.isTrue(StringUtils.isNotBlank(path), "File path is blank!");
        return checkSlip(parent, buildFile(parent, path));
    }

    /**
     * 检查父完整路径是否为自路径的前半部分，如果不是说明不是子路径，可能存在slip注入。
     * <p>
     * 见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parentFile 父文件或目录
     * @param file       子文件或目录
     * @return 子文件或目录
     * @throws IllegalArgumentException 检查创建的子文件不在父目录中抛出此异常
     */
    public static File checkSlip(File parentFile, File file) {
        if (null != parentFile && null != file) {
            String parentCanonicalPath;
            String canonicalPath;
            try {
                parentCanonicalPath = parentFile.getCanonicalPath();
                canonicalPath = file.getCanonicalPath();
            } catch (IOException e) {
                // getCanonicalPath有时会抛出奇怪的IO异常，此时忽略异常，使用AbsolutePath判断。
                parentCanonicalPath = parentFile.getAbsolutePath();
                canonicalPath = file.getAbsolutePath();
            }
            Assert.isTrue(canonicalPath.startsWith(parentCanonicalPath), "New file is outside of the parent dir: " + file.getName());
        }
        return file;
    }

    /**
     * 判断文件是否存在，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果存在返回true
     */
    public static boolean exist(File file) {
        return (null != file) && file.exists();
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为目录true
     */
    public static boolean isDirectory(File file) {
        return (null != file) && file.isDirectory();
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param path 文件
     * @return 是否存在
     */
    public static boolean exists(Path path) {
        return path != null && exists(path, false);
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param path          文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 是否存在
     */
    public static boolean exists(Path path, boolean isFollowLinks) {
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.exists(path, options);
    }

    /**
     * 判断是否为目录，如果file为null，则返回false<br>
     * 此方法不会追踪到软链对应的真实地址，即软链被当作文件
     *
     * @param path {@link Path}
     * @return 如果为目录true
     */
    public static boolean isDirectory(Path path) {
        return isDirectory(path, false);
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param path          {@link Path}
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @return 如果为目录true
     */
    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.isDirectory(path, options);
    }

    /**
     * 获得输入流
     *
     * @param file 文件
     * @return 输入流
     */
    public static BufferedInputStream getInputStream(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw Exceptions.unchecked(e);
        }
        return new BufferedInputStream(fis);
    }

    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     */
    public static BufferedOutputStream getOutputStream(File file) {
        final OutputStream out;
        try {
            out = Files.newOutputStream(touch(file).toPath());
        } catch (final IOException e) {
            throw Exceptions.unchecked(e);
        }
        return new BufferedOutputStream(out);
    }

    /**
     * 根据压缩包中的路径构建目录结构，在Win下直接构建，在Linux下拆分路径单独构建
     *
     * @param outFile  最外部路径
     * @param fileName 文件名，可以包含路径
     * @return 文件或目录
     */
    private static File buildFile(File outFile, String fileName) {
        // 替换Windows路径分隔符为Linux路径分隔符，便于统一处理
        fileName = fileName.replace('\\', '/');
        if (!isWindows()
            // 检查文件名中是否包含"/"，不考虑以"/"结尾的情况
            && fileName.lastIndexOf(StringPool.SLASH, fileName.length() - 2) > 0) {
            // 在Linux下多层目录创建存在问题，/会被当成文件名的一部分，此处做处理
            // 使用/拆分路径（zip中无\），级联创建父目录
            final List<String> pathParts = StringUtils.delimitedListToArrayList(fileName, "/");
            //目录个数
            final int lastPartIndex = pathParts.size() - 1;
            for (int i = 0; i < lastPartIndex; i++) {
                //由于路径拆分，slip不检查，在最后一步检查
                outFile = new File(outFile, pathParts.get(i));
            }
            //noinspection ResultOfMethodCallIgnored
            outFile.mkdirs();
            // 最后一个部分如果非空，作为文件名
            fileName = pathParts.get(lastPartIndex);
        }
        return new File(outFile, fileName);
    }
}
