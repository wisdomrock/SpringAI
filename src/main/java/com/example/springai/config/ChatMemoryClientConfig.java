package com.example.springai.config;

import com.example.springai.advisors.RequestHeaderAdvisor;
import com.example.springai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//MessageWindowChatMemory - what to store
//InMemoryChatMemoryRepository, JdbcChatMemoryRepository (used first by spring boot if found in classpath)
//SystemMessages are treated specially and only one such message will be kept
//MessageChatMemoryAdvisor, PromptChatMemoryAdvisor
//VectorStoreChatMemoryAdvisor stores memory in vector DB (Qdrant, Pinecone)
@Configuration
public class ChatMemoryClientConfig {

    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        //customize the chat memory
        //MessageWindowChatMemory is the default choice of ChatMemory
        //InMemoryChatMemoryRepository is the default ChatMemory Repository
        //SpringAI also supports Neo4jChatMemoryRepository by spring-ai-starter-model-chat-memory-repository-neo4j
        //SpringAI also supports CassandraChatMemoryRepository by spring-ai-starter-model-chat-memory-repository-cassandra
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();
    }

    //@Bean("memoryChatClient")
    @Bean //injected using @Qualifier("memoryChatClient")
    ChatClient memoryChatClient(ChatClient.Builder chatClientBuilder,
                                ChatMemory chatMemory,
                                RequestHeaderAdvisor requestHeaderAdvisor,
                                RetrievalAugmentationAdvisor retrievalAugmentationAdvisor){
        ChatOptions chatOptions = ChatOptions.builder()
                //.model()
                //.presencePenalty(0.6)
                //.stopSequences(List.of("END"))
                .temperature(1.0)
                .maxTokens(300)
                .build();
        //For OpenAI specific ChatOptions, use OpenAiChatOptions
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        return chatClientBuilder
                .defaultOptions(chatOptions)
                .defaultAdvisors(new SimpleLoggerAdvisor(), memoryAdvisor,
                        new TokenUsageAuditAdvisor(), requestHeaderAdvisor, retrievalAugmentationAdvisor)
                .build();
    }

    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore){
        //The RetrievalAugmentationAdvisor will take care of the cross-cutting concerns
        //such as vectorStore similaritySearch, passing system prompt etc.
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(
                      VectorStoreDocumentRetriever.builder()
                          .vectorStore(vectorStore)
                              .topK(3)
                              .similarityThreshold(0.5)
                          .build())
                .build();

    }
}
