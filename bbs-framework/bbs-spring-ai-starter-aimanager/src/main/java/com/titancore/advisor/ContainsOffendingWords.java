package com.titancore.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Set;

public class ContainsOffendingWords  implements CallAroundAdvisor, StreamAroundAdvisor {

    private Set<String> offendingWords;

    private static final String SYSTEM_PROMPT_TEMPLATE = """
             执行内容安全检查（优先级顺序）：
                 A. 若违禁词列表非空：
                    1. 用户意图匹配「%s」→ {"isViolation":true,"reason":"20字以下违禁原因"}
                    2. 其他情况 → {"isViolation":false,"reason":"20字以下安全说明"}
                 B. 若违禁词列表为空：
                    1. 违反核心价值观 → {"isViolation":true,"reason":"20字以下违规原因"}
                    2. 符合监管要求 → {"isViolation":false,"reason":"20字以下合规说明"}
                 C. 异常默认：
                    → {"isViolation":false,"reason":"系统默认通过"}
                 要求：
                 - 仅返回合法JSON
                 - 原因≤20字
                 - 布尔值小写
                 - 禁用Markdown
                 检查后需记录日志！
        """;

    public ContainsOffendingWords(Set<String> offendingWords){
        this.offendingWords = offendingWords;
    }
    /**
     * 整合违禁词检查的请求处理
     */
    private AdvisedRequest processRequest(AdvisedRequest advisedRequest) {
        String prompt = SYSTEM_PROMPT_TEMPLATE.formatted(offendingWords);
        return AdvisedRequest.from(advisedRequest)
                .withSystemText(prompt)
                .build();
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.processRequest(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.processRequest(advisedRequest));
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int getOrder() {
        return 101;
    }
}
