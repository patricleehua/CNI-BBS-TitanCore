package com.titancore.config;

import com.titancore.domain.entity.AiCheckOffendingWordsResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
@Description("All function can call back by Ai!")
public class AiFunctionCallBack {

    public record CheckWordsLogRecord(AiCheckOffendingWordsResponse aiCheckOffendingWordsResponse){}

    @Bean("recordOffendingWordsLog")
    @Description("record log after checking for offending words")
    public Function<CheckWordsLogRecord,String> recordOffendingWordsLog(){
        //todo 记录到数据库
        return (response)->{
            System.out.println("记录日志:"+response.aiCheckOffendingWordsResponse.getReason());
            return "ok";
        };
    }

}
