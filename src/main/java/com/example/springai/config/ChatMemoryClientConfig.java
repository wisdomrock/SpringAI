package com.example.springai.config;

import com.example.springai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//MessageWindowChatMemory - what to store
//InMemoryChatMemoryRepository, JdbcChatMemoryRepository
//SystemMessages are treated specially and only one such message will be kept
//MessageChatMemoryAdvisor, PromptChatMemoryAdvisor
//VectorStoreChatMemoryAdvisor stores memory in vector DB (Qdrant, Pinecone)
@Configuration
public class ChatMemoryClientConfig {

    //@Bean("memoryChatClient")
    @Bean //injected using @Qualifier("memoryChatClient")
    ChatClient memoryChatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory){
        ChatOptions chatOptions = ChatOptions.builder()
                //.model()
                //.presencePenalty(0.6)
                //.stopSequences(List.of("END"))
                .temperature(1.0)
                .build();
        //For OpenAI specific ChatOptions, use OpenAiChatOptions
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        return chatClientBuilder
                .defaultOptions(chatOptions)
                .defaultAdvisors(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor(), memoryAdvisor)
                .build();
    }
}
