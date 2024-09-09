package com.titancore.controller;

import com.titancore.framework.cloud.manager.config.CloudServiceFactory;
import com.titancore.framework.cloud.manager.domain.dto.FileDelDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class TestController {
    @Resource
    private CloudServiceFactory factory;

//    @PostMapping("/test")
//    public String test(@RequestParam("file") MultipartFile file, @RequestParam("userId") long userId) throws IOException {
//        var cloudStorageService = factory.createService();
//        return cloudStorageService.uploadFile(    file.getBytes(),userId,"text.txt");
//    }

//    @GetMapping("/test")
//    public  ResponseEntity<byte[]> test() throws IOException {
//        var cloudStorageService = factory.createService();
//        FileDowloadDTO fileDowloadDTO = new FileDowloadDTO();
//        fileDowloadDTO.setFileName("text.txt");
//        fileDowloadDTO.setUserId("12");
//        byte[] fileBytes  = cloudStorageService.exportOssFile(fileDowloadDTO);
//        HttpHeaders headers = new HttpHeaders();
//        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
//    }

        @GetMapping("/test")
    public boolean test() throws IOException {
        var cloudStorageService = factory.createService();
        FileDelDTO fileDelDTO = new FileDelDTO();
            fileDelDTO.setFileName("text.txt");
            fileDelDTO.setUserId("12");
            fileDelDTO.setPath("file/");
        return cloudStorageService.deleteByPath(fileDelDTO);
    }
}
