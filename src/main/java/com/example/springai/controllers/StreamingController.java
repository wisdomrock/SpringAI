package com.example.springai.controllers;

import com.example.springai.advisors.TokenUsageAuditAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@Slf4j
@RestController
@RequestMapping
class StreamingController {
    private final ChatClient chatClient;

    StreamingController(ChatClient.Builder builder) {
        ChatOptions chatOptions = ChatOptions.builder()
                .presencePenalty(0.6)
                .maxTokens(200)
                .build();
        chatClient = builder
                .defaultOptions(chatOptions)
                .defaultAdvisors(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor())
                .build();
    }

    @GetMapping("stream")
    Flux<String> stream(@RequestParam String message){
        return chatClient.prompt()
                .user(message)
                .stream().content();
    }

}
