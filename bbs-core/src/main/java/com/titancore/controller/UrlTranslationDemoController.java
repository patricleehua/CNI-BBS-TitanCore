package com.titancore.controller;

import com.titancore.domain.vo.FileVo;
import com.titancore.framework.cloud.manager.url.UrlTranslationService;
import com.titancore.framework.cloud.manager.url.UrlType;
import com.titancore.framework.common.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * URL 翻译演示控制器
 * 展示云存储 URL 的自动翻译功能
 *
 * @author TitanCore
 */
//@RestController
@RequestMapping("/demo/url-translation")
@Tag(name = "URL 翻译演示", description = "展示云存储 URL 的自动翻译功能")
@RequiredArgsConstructor
@Slf4j
public class UrlTranslationDemoController {

    private final UrlTranslationService urlTranslationService;

    /**
     * 演示单文件 URL 自动翻译
     * 返回的 FileVo 中带有 @TranslateUrl 注解的字段会自动翻译
     */
    @GetMapping("/file")
    @Operation(summary = "单文件 URL 自动翻译演示")
    public Response<FileVo> getFile() {
        FileVo fileVo = new FileVo();
        fileVo.setFileId("FILE001");
        fileVo.setFileName("spring-boot-logo.png");

        // 模拟阿里云 OSS 原始 URL
        fileVo.setOriginalUrl("https://titan-bucket-open.oss-cn-beijing.aliyuncs.com/images/spring-boot-logo.png");

        fileVo.setFileType("image/png");
        fileVo.setFileSize(102400L);
        fileVo.setCreateTime("2024-03-17 10:00:00");

        // 注意：cdnUrl、thumbnailUrl、internalPath 会被自动翻译
        return Response.success(fileVo);
    }

    /**
     * 演示 MinIO URL 自动翻译
     */
    @GetMapping("/file-minio")
    @Operation(summary = "MinIO URL 自动翻译演示")
    public Response<FileVo> getMinioFile() {
        FileVo fileVo = new FileVo();
        fileVo.setFileId("FILE002");
        fileVo.setFileName("document.pdf");

        // 模拟 MinIO 原始 URL
        fileVo.setOriginalUrl("http://localhost:9000/titan-bucket/documents/document.pdf");

        fileVo.setFileType("application/pdf");
        fileVo.setFileSize(204800L);
        fileVo.setCreateTime("2024-03-17 11:00:00");

        return Response.success(fileVo);
    }

    /**
     * 演示文件列表 URL 自动翻译
     */
    @GetMapping("/file-list")
    @Operation(summary = "文件列表 URL 自动翻译演示")
    public Response<List<FileVo>> getFileList() {
        List<FileVo> files = new ArrayList<>();

        FileVo file1 = new FileVo();
        file1.setFileId("FILE001");
        file1.setFileName("avatar.jpg");
        file1.setOriginalUrl("https://titan-bucket-open.oss-cn-beijing.aliyuncs.com/users/123/avatar.jpg");
        file1.setFileType("image/jpeg");
        file1.setFileSize(51200L);
        files.add(file1);

        FileVo file2 = new FileVo();
        file2.setFileId("FILE002");
        file2.setFileName("report.pdf");
        file2.setOriginalUrl("https://titan-bucket-open.oss-cn-beijing.aliyuncs.com/docs/report.pdf");
        file2.setFileType("application/pdf");
        file2.setFileSize(1024000L);
        files.add(file2);

        return Response.success(files);
    }

    /**
     * 手动调用 URL 翻译服务
     */
    @GetMapping("/translate")
    @Operation(summary = "手动 URL 翻译")
    @Parameter(name = "url", description = "原始 URL")
    @Parameter(name = "type", description = "目标类型：CDN, THUMBNAIL, INTERNAL_PATH, FULL, WATERMARK")
    public Response<String> translateUrl(
            @RequestParam String url,
            @RequestParam(defaultValue = "CDN") String type,
            @RequestParam(required = false) String[] params) {

        UrlType urlType = UrlType.fromCode(type);
        String translatedUrl;

        if (params != null && params.length > 0) {
            // 解析参数
            Object[] parsedParams = parseParams(params);
            translatedUrl = urlTranslationService.translate(url, urlType, parsedParams);
        } else {
            translatedUrl = urlTranslationService.translate(url, urlType);
        }

        return Response.success(translatedUrl);
    }

    /**
     * 批量翻译 URL
     */
    @PostMapping("/translate-list")
    @Operation(summary = "批量 URL 翻译")
    public Response<List<String>> translateUrls(
            @RequestBody List<String> urls,
            @RequestParam(defaultValue = "CDN") String type) {

        UrlType urlType = UrlType.fromCode(type);
        List<String> translatedUrls = urlTranslationService.translateList(urls, urlType);

        return Response.success(translatedUrls);
    }

    /**
     * 生成缩略图 URL
     */
    @GetMapping("/thumbnail")
    @Operation(summary = "生成缩略图 URL")
    @Parameter(name = "url", description = "原始图片 URL")
    @Parameter(name = "width", description = "宽度")
    @Parameter(name = "height", description = "高度")
    public Response<String> getThumbnailUrl(
            @RequestParam String url,
            @RequestParam(defaultValue = "200") int width,
            @RequestParam(defaultValue = "200") int height) {

        String thumbnailUrl = urlTranslationService.toThumbnailUrl(url, width, height);
        return Response.success(thumbnailUrl);
    }

    /**
     * 生成 CDN URL
     */
    @GetMapping("/cdn")
    @Operation(summary = "生成 CDN URL")
    public Response<String> getCdnUrl(@RequestParam String url) {
        String cdnUrl = urlTranslationService.toCdnUrl(url);
        return Response.success(cdnUrl);
    }

    /**
     * 提取内部路径
     */
    @GetMapping("/internal-path")
    @Operation(summary = "提取内部路径")
    public Response<String> getInternalPath(@RequestParam String url) {
        String internalPath = urlTranslationService.toInternalPath(url);
        return Response.success(internalPath);
    }

    /**
     * 解析参数
     */
    private Object[] parseParams(String[] params) {
        Object[] result = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            try {
                result[i] = Integer.parseInt(param);
            } catch (NumberFormatException e) {
                result[i] = param;
            }
        }
        return result;
    }
}
