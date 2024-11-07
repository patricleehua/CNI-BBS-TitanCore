package com.titancore.controller;

import com.titancore.domain.dto.CaptchaCodeDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import com.titancore.framework.cloud.manager.domain.dto.FileDownloadDTO;
import com.titancore.framework.cloud.manager.urils.MinioUtil;
import com.titancore.framework.common.response.Response;
import com.titancore.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static cn.hutool.poi.excel.sax.AttributeName.s;

@RestController
@Slf4j
@RequestMapping("/common")
@Tag(name = "公共模块")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * 发送验证码
     * 更具类型进行判断发送何种类型的验证码
     *
     * @param captchaCodeDTO
     * @return
     */
    @PostMapping("/open/sendCode")
    @Operation(summary = "发送验证码",description = "可发送邮箱/手机验证码")
    public Response<?> sendCode(@RequestBody CaptchaCodeDTO captchaCodeDTO){
        log.info("发送验证码:{}",captchaCodeDTO);
        return commonService.sendCaptchaCode(captchaCodeDTO);
    }

/*    @PostMapping("/open/file/download")
    @Operation(summary = "文件下载",description = "根据文件路径下载文件")
    public ResponseEntity<InputStreamResource> download(@RequestBody FileDownloadDTO fileDownloadDTO){
       // todo
        HttpHeaders headers = new HttpHeaders();
        // 以流的形式下载文件，这样可以实现任意格式的文件下载
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // 文件名包含中文时乱码问题，使用UTF-8编码转换 处理文件名
        String encodedFileName = UriUtils.encode(fileDownloadDTO.getFileName(), StandardCharsets.UTF_8);
        // 设置响应头，告诉浏览器下载的文件名
        headers.setContentDispositionFormData("attachment", encodedFileName);
        // 获取文件流
        Map<String, Object> downloadFile = commonService.downloadFile(fileDownloadDTO);
        // 设置响应头，告诉浏览器下载的文件大小
        headers.setContentLength((Long) downloadFile.get("fileSize"));
        InputStreamResource resource = new InputStreamResource((InputStream) downloadFile.get("inputStream"));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    private static ThreadLocal<Boolean> downloading = ThreadLocal.withInitial(() -> true);

    @GetMapping("/open/file/download/test1")
    @Operation(summary = "文件下载", description = "根据文件路径下载文件")
    public void download1(HttpServletResponse response) throws IOException {
        // todo
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        FileDownloadDTO fileDownloadDTO = new FileDownloadDTO();
        fileDownloadDTO.setFileName("txt.zip");
        String encodedFileName = UriUtils.encode(fileDownloadDTO.getFileName(), StandardCharsets.UTF_8);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        Map<String, Object> downloadFile = commonService.downloadFile(fileDownloadDTO);
        long fileSize = (Long) downloadFile.get("fileSize");
        InputStream inputStream = (InputStream) downloadFile.get("inputStream");

        response.setContentLengthLong(fileSize);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while (downloading.get() && (bytesRead = inputStream.read(buffer)) != -1) {
                if (!outputStream.isReady()) {
                    System.out.println("下载被客户端中断");
                    downloading.set(false);
                    break;
                }
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
            }
        } catch (IOException e) {
            System.out.println("下载发生错误: " + e.getMessage());
        } finally {
            downloading.remove(); // 清除当前线程的状态
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("关闭输入流时发生错误: " + e.getMessage());
                }
            }
        }
    }*/
    @PostMapping("/upload/media")
    @Operation(summary = "媒体上传(图片/视频)")
    public Response<?> uploadMedia(MultipartFile file,String userId,String type){
        return Response.success(commonService.uploadMedia(file, userId, type));
    }
    @PostMapping("/upload/file")
    @Operation(summary = "文件上传")
    public Response<?> uploadFile(MultipartFile file,String userId){
        return Response.success(commonService.uploadFile(file, userId));
    }

    @PostMapping("/file/createTemporaryUrl")
    @Operation(summary = "生成文件临时Url(供下载使用)")
    public Response<?> generateTemporaryUrl(@RequestBody FileDownloadDTO fileDownloadDTO){
        String temporaryUrl = commonService.createTemporaryUrl(fileDownloadDTO);
        return Response.success(temporaryUrl);
    }
    @PostMapping("/deleteFile")
    @Operation(summary = "删除文件")
    public Response<?> deleteFile(@RequestBody FileDelDTO fileDelDTO){
        return Response.success(commonService.deleteFile(fileDelDTO));
    }
    @PostMapping("/queryFileList/{userId}")
    @Operation(summary = "根据用户ID查询文件列表")
    public Response<?> deleteFile(@PathVariable String userId){
        return Response.success(commonService.queryFileListByUserId(userId));
    }

}
