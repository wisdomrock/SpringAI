package com.example.springai.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RAGController {
    private final @Qualifier("memoryChatClient") ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st")
    private Resource promptTemplate;

    @GetMapping("random/chat")
    ResponseEntity<String> chatMemory(@RequestHeader String username, @RequestParam String message){
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocs.stream().map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
        String answer = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(promptTemplate)
                        .param("documents", similarContext))
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, username))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);
    }

}
