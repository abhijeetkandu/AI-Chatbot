package com.abhijeet.chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatbotController {

    private ChatClient chatClient;

    public ChatbotController(ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

    }
    @PostMapping("/groq")
    public ResponseEntity<String> getAiResponseFromGroq(@RequestParam("query") String query){
        return ResponseEntity.ok(chatClient.prompt(query).call().content());

    }

}
