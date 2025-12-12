package com.example.springai.rag;

import io.qdrant.client.QdrantClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@Component
public class HRPolicyLoader {
    private final VectorStore vectorStore;

    private final @Value("classpath:/vector/HR_Policies.pdf")Resource policyFile;

    private final @Value("spring.ai.vectorstore.qdrant.collection-name") String collectionName;

    @PostConstruct
    public void loadPDFIntoVectorStore() throws Exception {
        log.info("All records deleted using a filter expression.");
        Runnable consumer = ()-> {
            TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(policyFile);
            List<Document> docs = tikaDocumentReader.get();
            TextSplitter textSplitter = TokenTextSplitter.builder().withChunkSize(100).withMaxNumChunks(400).build();
            log.info("Adding {} documents to the vector store", docs.size());
            vectorStore.add(textSplitter.split(docs));
        };
        //vectorStore.delete("1 == 1"); // A filter that is always true
        vectorStore.getNativeClient().filter(s->s instanceof  QdrantClient).ifPresent(client -> {
            try {
                // This is specific to the underlying DB's native client API
                // For Qdrant, you might delete the entire collection using a specific operation
                // or perform a delete-by-filter with an "all" condition.
                // Check the specific client's documentation for the correct method.
                log.info("Accessing native client for deletion...");
                // (Specific implementation detail for the native client goes here)
                ((QdrantClient) client).deleteCollectionAsync(collectionName).addListener(consumer, new Executor(){
                    @Override
                    public void execute(Runnable command) {
                        command.run();
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
