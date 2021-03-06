package com.king.hadoop_helper.util;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.zip.ZipOutputStream;

/**
 * @program: kingcloud
 * @description:
 * @author: King
 * @create: 2021-06-04 19:13
 */
public class FileUtil {
    /**
     * 在basePath下保存上传的文件夹
     *
     * @param basePath
     * @param files
     */
    public static boolean saveMultiFile(String basePath, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return false;
        }
        for (MultipartFile file : files) {
            String filePath = basePath + "/" + file.getOriginalFilename();
            makeDir(filePath);
            File dest = new File(filePath);
            try {
                file.transferTo(dest);
                System.out.println(dest.getPath());
            } catch (IllegalStateException | IOException e) {

                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * 确保目录存在，不存在则创建
     *
     * @param filePath
     */
    private static void makeDir(String filePath) {
        if (filePath.lastIndexOf('/') > 0) {
            String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    /**
     * 下载文件夹
     *
     * @param path
     * @param filename
     * @param request
     * @return
     * @throws IOException
     */
    public static ResponseEntity downDir(String path, String filename, HttpServletRequest request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache,no-store,must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"",
                URLEncoder.encode(filename + ".zip", "utf-8")));

        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Language", "UTF-8");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        ByteArrayOutputStream stream = (ByteArrayOutputStream) downloadDir(path);
        byte[] bytes = stream.toByteArray();
        stream.close();

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    public static OutputStream downloadDir(String path) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ZipOutputStream stream1 = new ZipOutputStream(stream);
        new HdfsUtil().compress(path, stream1);
        stream1.close();
        return stream;
    }

    public static final String winPath = "D:\\a\\";
    public static final String linuxPath = "/var/tmp/hadoop_helper/";


    private static final String OS = System.getProperty("os.name").toLowerCase();


    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOS() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x");
    }

    public static boolean isMacOSX() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    public enum OsType {

        Linux("Linux"), Mac_OS("Mac OS"), Mac_OS_X("Mac OS X"), Windows("Windows");

        private OsType(String desc) {
            this.description = desc;
        }

        public String toString() {
            return description;
        }

        private String description;
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public static OsType getOSName() {
        OsType platform = null;
        if (isLinux()) {
            platform = OsType.Linux;
        } else if (isMacOS()) {
            platform = OsType.Mac_OS;
        } else if (isMacOSX()) {
            platform = OsType.Mac_OS_X;
        } else if (isWindows()) {
            platform = OsType.Windows;
        }
        return platform;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(getOSName());// 获取系统类型
        System.out.println(isWindows());// 判断是否为windows系统
    }


    public static String saveFile(MultipartFile uploadFile) {
        StringBuilder sb = new StringBuilder();

        if (uploadFile.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        try {
            String fileName = uploadFile.getOriginalFilename();
            if (isLinux()) {
                File file = new File(linuxPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                uploadFile.transferTo(new File(linuxPath, fileName));
                sb.append(linuxPath).append(uploadFile.getOriginalFilename());
            } else if (isWindows()) {
                File file = new File(winPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                uploadFile.transferTo(new File(winPath, fileName));
                sb.append(winPath).append(uploadFile.getOriginalFilename());
            } else {
                throw new RuntimeException("操作系统不支持");
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        return sb.toString();

    }

    /**
     * @param multipartFiles
     * @return 返回保存的路径
     */
//    public String saveFile(MultipartFile... multipartFiles) {
//        StringBuilder sb = new StringBuilder();
//        try {
//            for (MultipartFile multipartFile : multipartFiles) {
//                multipartFile.transferTo(new File(imgPath, multipartFile.getOriginalFilename()));
//                String osname = System.getProperty("os.name").toLowerCase();
//                if (osname.contains("linux")) {
//                    sb.append(imgPath).append(multipartFile.getOriginalFilename());
//                } else if (osname.contains("windows")) {
//                    sb.append("H:\\king\\springboot\\mooc\\userImg\\").append(multipartFile.getOriginalFilename());
//                }
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "上传失败!";
//        }
//        return sb.toString();
//    }

}
