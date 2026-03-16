package com.titancore.controller;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import com.github.houbb.sensitive.word.support.resultcondition.WordResultConditions;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import com.titancore.framework.common.response.Response;
import com.titancore.service.AiChatService;
import com.titancore.service.AiDetectTextService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Tag(name = "测试")
@Slf4j
@RequestMapping("/test")
public class TestController {
    @GetMapping("/hello")
    public Response<String> test(){
        return Response.success("hello,test!");
    }

    @Autowired
    private AiDetectTextService aiDetectTextService;
    @Autowired
    private SensitiveWordBs sensitiveWordBs;
    @GetMapping("/word/{word}")
    public Response<String> word(@PathVariable String word){

        System.out.println("SensitiveWordHelper.contains(text) = " + SensitiveWordHelper.contains(word));
        System.out.println("SensitiveWordHelper.findAll(text) = " + SensitiveWordHelper.findAll(word));
        System.out.println("SensitiveWordHelper.replace(text,myWordReplace) = " + SensitiveWordHelper.replace(word));


        System.out.println("=========================");
        System.out.println("sensitiveWordBs.contains(text) = " + sensitiveWordBs.contains(word));
        System.out.println("sensitiveWordBs.findAll(text) = " + sensitiveWordBs.findAll(word));
        System.out.println("sensitiveWordBs.replace(text) = " + sensitiveWordBs.replace(word));

//        return Response.success(aiDetectTextService.isContainsOffendingWords(word,new String[]{"习近平"}));
        return Response.success(sensitiveWordBs.replace(word));
    }

}
