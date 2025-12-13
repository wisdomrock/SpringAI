package com.example.springai.controllers;

import com.example.springai.advisors.RequestHeaders;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
public class RAGController implements RequestHeaders {
    private final VectorStore vectorStore;
    private final @Qualifier("memoryChatClient") ChatClient chatClient;
    private final @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st") Resource promptTemplate;
    private final @Value("classpath:/promptTemplates/systemPromptPolicyTemplate.st") Resource policyTemplate;


    @GetMapping("random/chat")
    ResponseEntity<String> randomChat(@RequestHeader String username, @RequestParam String message){
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)
                .build();
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarContext = similarDocs.stream().map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
        //"code":500,"message":"Conversation roles must alternate user/assistant/user/assistant/
        String answer = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(promptTemplate)
                        .param("documents", similarContext))
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, username))
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);
    }

    @GetMapping("document/chat")
    ResponseEntity<String> documentChat(@RequestHeader(name=USER_ID_HEADER, required = false) String username, @RequestParam String message){
        //RetrievalAugmentationAdvisor is configured to the ChatClient which will do RAG automatically
        //.advisors(advisorSpec -> {if(StringUtils.isNotBlank(username)) advisorSpec.param(ChatMemory.CONVERSATION_ID, username);})
        //"code":500,"message":"Conversation roles must alternate user/assistant/user/assistant/
        String answer = chatClient.prompt()
                .user(message)
                .call().content();
        return ResponseEntity.ok(answer);
    }

}
