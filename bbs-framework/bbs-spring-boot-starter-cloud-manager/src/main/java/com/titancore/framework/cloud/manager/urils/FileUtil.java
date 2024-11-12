package com.titancore.framework.cloud.manager.urils;

import cn.hutool.core.util.IdUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件工具类
 */
public class FileUtil {
    /**
     * 检查file类型是否为图片，true为是，false为否
     * @param file
     * @return
     */
    public static boolean isImage(MultipartFile file) {
        if (file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        List<String> imageTypes = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
        if (contentType != null && imageTypes.contains(contentType)) {
            return true;
        }
        if (filename != null) {
            String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
            List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
            return validExtensions.contains(extension);
        }
        return false;
    }

    /**
     * 根据原始文件类型生成文件的UUID名
     * @param file
     * @return
     */
    public static String renameFileToUUID(MultipartFile file){
        String uuid = IdUtil.randomUUID();
        String process = uuid.replace("-", "").substring(0, 16);
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 返回重命名后的文件名
        return process + extension;
    }
    /**
     * 检查file类型是否为视频格式，true为是，false为否
     * @param file
     * @return
     */
    public static boolean isVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        if (contentType != null) {
            return contentType.startsWith("video/");
        }
        String filename = file.getOriginalFilename();
        return filename != null && (filename.endsWith(".mp4") ||
                filename.endsWith(".avi") ||
                filename.endsWith(".mov") ||
                filename.endsWith(".wmv") ||
                filename.endsWith(".flv"));
    }

    /**
     * 处理 url 路径转为 文件路径
     * @param path
     * @param key
     * @return
     */
    public static String extractPathAfterKeyword(String path, String key) {
        int index = path.indexOf(key);
        if (index != -1) {
            return path.substring(index + key.length());
        }
        return path;
    }
}
