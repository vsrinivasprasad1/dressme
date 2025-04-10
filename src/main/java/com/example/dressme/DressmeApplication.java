package com.example.dressme;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

@SpringBootApplication
public class DressmeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DressmeApplication.class, args);
    }

    @Bean
    McpSyncClient mcpSyncClient() {
        var mcpClient = McpClient.sync(new HttpClientSseClientTransport("http://localhost:8081")).build();
        mcpClient.initialize();
        return mcpClient;
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, McpSyncClient mcpSyncClient, DressRepository repository, VectorStore vectorStore) {
        if (false) {
            repository.findAll().forEach(dress ->
            {
                var document = new Document(" id: %s, name: %s, Temparature: %s".formatted(dress.id(), dress.cloths(), dress.temparature()
                ));
                vectorStore.add(List.of(document));

            });
        }

        var system = "you are an AI powered advisor who can understand weather, temparature and advice user what to wear";
        return builder.defaultSystem(system).defaultTools(new SyncMcpToolCallbackProvider(mcpSyncClient)).build();
    }
}

interface DressRepository extends ListCrudRepository<Dress, Integer> {

}

record Dress(@Id int id, int temparature, String cloths) {

}
