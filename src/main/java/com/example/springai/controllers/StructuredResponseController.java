package com.example.springai.controllers;

import com.example.springai.advisors.TokenUsageAuditAdvisor;
import com.example.springai.model.CountryCities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
class StructuredResponseController {
    private final ChatClient chatClient;

    StructuredResponseController(ChatClient.Builder builder) {
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

    @GetMapping("country-cities")
    ResponseEntity<CountryCities> countryCities(@RequestParam String message){
        // "message": "you must provide a model parameter"
        CountryCities countryCities =  chatClient
                .prompt()
                .user(message)
                .call()
                //.entity(CountryCities.class);
                .entity(new BeanOutputConverter<>(CountryCities.class));
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("city-list")
    ResponseEntity<List<String>> cityList(@RequestParam String message){
        // "message": "you must provide a model parameter"
        List<String> countryCities =  chatClient
                .prompt()
                .user(message)
                .call()
                .entity(new ListOutputConverter());
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("city-map")
    ResponseEntity<Map<String, Object>> cityMap(@RequestParam String message){
        // "message": "you must provide a model parameter"
        Map<String, Object> countryCities =  chatClient
                .prompt()
                .user(message)
                .call()
                .entity(new MapOutputConverter());
        return ResponseEntity.ok(countryCities);
    }

}
