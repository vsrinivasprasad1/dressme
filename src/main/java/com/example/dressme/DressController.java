package com.example.dressme;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
public class DressController {

    private final ChatClient singularity;

    private final Map<String, PromptChatMemoryAdvisor> memoryAdvisor = new HashMap<>();

    private final QuestionAnswerAdvisor questionAnswerAdvisor;


    public DressController(ChatClient singularity, VectorStore vectorStore) {
        this.singularity = singularity;
        this.questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore);

    }


    @GetMapping("/dress/{user}/inquire")
    String inquire(@PathVariable("user") String user, @RequestParam String question) {
        var advisor = this.memoryAdvisor.computeIfAbsent(user, k -> new PromptChatMemoryAdvisor(new InMemoryChatMemory()));
        return this.singularity.prompt().user(question).advisors(advisor, questionAnswerAdvisor).call().content();
    }


}
