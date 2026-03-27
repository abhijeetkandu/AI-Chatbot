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
        String systemPrompt = """
        You are GreenCart Assistant, a helpful chatbot for GreenCart — 
        an online fresh grocery store that sells organic vegetables and fruits.
        Only answer questions related to GreenCart such as products, pricing, 
        delivery, orders, offers, and the website.
        If someone asks something unrelated to GreenCart, politely say:
        "I can only help with GreenCart related questions!"
        Keep answers short, friendly and helpful.
        """;
        return ResponseEntity.ok(chatClient.prompt().system(systemPrompt).user(query).call().content());

    }

}
