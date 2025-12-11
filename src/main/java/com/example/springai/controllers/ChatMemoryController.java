package com.example.springai.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chat")
class ChatMemoryController {
    private final ChatClient chatClient;

    public ChatMemoryController(@Qualifier("memoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("chat-memory")
    ResponseEntity<String> chatMemory(@RequestHeader String username, @RequestParam String message){
        //ChatMemory.CONVERSATION_ID used by InMemoryChatMemoryRepository
        return ResponseEntity.ok(chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, username))
                .call().content());
    }
}
