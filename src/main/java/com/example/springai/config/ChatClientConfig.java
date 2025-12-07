package com.example.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBooleanProperty(value="spring.ai.chat.client.enabled", havingValue=false)
@Configuration
public class ChatClientConfig {

    @Bean //injected using @Qualifier("openAiChartClient")
    ChatClient openAiChartClient(OpenAiChatModel openAiChatModel){
        return ChatClient.create(openAiChatModel);
    }

    @Bean //injected using @Qualifier("ollamaChartClient")
    ChatClient ollamaChartClient(OllamaChatModel ollamaChatModel){
        //return ChatClient.create(ollamaChatModel);
        ChatClient.Builder chatClientBuilder = ChatClient.builder(ollamaChatModel);
        //there will be more control with the Builder as we can pass additional info
        return chatClientBuilder.build();
    }

}
