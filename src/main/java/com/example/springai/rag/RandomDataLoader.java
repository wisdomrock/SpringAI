package com.example.springai.rag;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.ai.document.Document;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RandomDataLoader {
    private final VectorStore vectorStore;

    @Value("classpath:/vector/RandomDataLoader.txt")
    private Resource sourceFile;

    @PostConstruct
    public void loadSentencesIntoVectorStore() throws Exception {
        List<String> sentences = Files.readAllLines(Paths.get(sourceFile.getURI()));
        List<Document> documents = sentences.stream().map(Document::new).collect(Collectors.toList());
        //vectorStore.add(documents);
    }
}
