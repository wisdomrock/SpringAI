package com.example.springai.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping
class ChatController {
    private final ChatClient chatClient;

    ChatController(ChatClient.Builder builder) {
        chatClient = builder.build();
    }

    @GetMapping("chat")
    String chat(@RequestParam String message){
        return chatClient.prompt(message).call().content();
    }

}
