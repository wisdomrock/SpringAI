package com.example.springai.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping
class ChatController {
    private final ChatClient chatClient;

    @Value("classpath:/promptTemplates/userTemplate.st")
    private Resource userPromptTemplate;

    ChatController(ChatClient.Builder builder) {
        chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("chat")
    String chat(@RequestParam String message){
        return chatClient.prompt(message).call().content();
    }

    @GetMapping("chat-prompt")
    String chatPrompt(@RequestParam String customerMessage, @RequestParam String customerName){
        return chatClient.prompt()
                .user(promptUserSpec ->
                        promptUserSpec.text(userPromptTemplate)
                        .param("customerName", customerName)
                        .param("customerMessage", customerMessage))
                .call().content();
    }

}
