package com.example.springai.controllers;

import com.example.springai.advisors.TokenUsageAuditAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping
class PromptStuffingController {
    private final ChatClient chatClient;

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource systemPromptTemplate;

    PromptStuffingController(ChatClient.Builder builder) {
        ChatOptions chatOptions = ChatOptions.builder()
                //.model()
                .temperature(0.5)
                .presencePenalty(0.6)
                .maxTokens(200)
                .stopSequences(List.of("END"))
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
