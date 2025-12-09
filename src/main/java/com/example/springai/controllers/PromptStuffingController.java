package com.example.springai.controllers;

import com.example.springai.advisors.TokenUsageAuditAdvisor;
import com.example.springai.model.CountryCities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@ConditionalOnBooleanProperty(value = "PromptStuffingController", havingValue = true)
class PromptStuffingController {
    private final ChatClient chatClient;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource systemPromptTemplate;

    PromptStuffingController(ChatClient.Builder builder) {
        //'max_tokens' is not supported with this model. Use 'max_completion_tokens' instead
        //'temperature' does not support 0.5 with this model. Only the default (1) value is supported
        ChatOptions chatOptions = ChatOptions.builder()
                //.model()
                //.presencePenalty(0.6)
                //.stopSequences(List.of("END"))
                .temperature(1.0)
                .build();
        //For OpenAI specific ChatOptions, use OpenAiChatOptions
        chatClient = builder
                .defaultOptions(chatOptions)
                .defaultAdvisors(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor())
                .build();
    }

    @GetMapping("prompt-stuffing")
    String promptStuffing(@RequestParam String message){
        return chatClient.prompt()
                .system(systemPromptTemplate)
                .user(message)
                .call().content();
    }
}
