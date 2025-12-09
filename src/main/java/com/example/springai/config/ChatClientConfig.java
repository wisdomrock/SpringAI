package com.example.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBooleanProperty(value="spring.ai.chat.client.enabled", havingValue=false)
@Configuration
public class ChatClientConfig {

    @Bean //injected using @Qualifier("openAiChatClient")
    ChatClient openAiChatClient(OpenAiChatModel openAiChatModel){
        return ChatClient.create(openAiChatModel);
    }

    @Bean //injected using @Qualifier("ollamaChatClient")
    ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel){
        //return ChatClient.create(ollamaChatModel);
        ChatClient.Builder chatClientBuilder = ChatClient.builder(ollamaChatModel);
        //there will be more control with the Builder as we can pass additional info
        return chatClientBuilder.build();
    }

    @Bean //injected using @Qualifier("openAiChatOptions")
    ChatOptions openAiChatOptions(){
        //SpringAI also support configure ChatOptions thru application yaml file
        return OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.CHATGPT_4_O_LATEST)
                .maxTokens(500)
                .maxCompletionTokens(200)
                .build();
    }
}
