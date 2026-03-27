package com.abhijeet.chatbot.controller;

import com.abhijeet.chatbot.service.ProductService;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatbotController {

    private ChatClient chatClient;
    private ProductService productService;

    public ChatbotController(ChatClient.Builder chatClientBuilder, ProductService productService){
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.productService = productService;
    }
    @PostMapping("/groq")
    public ResponseEntity<String> getAiResponseFromGroq(@RequestParam("query") String query) {
        try {
            String products = productService.getProductsAsText();
            String systemPrompt = """
                    You are GreenCart Assistant, a helpful chatbot for GreenCart —
                    an online fresh grocery store.
                    
                    Here are the EXACT products currently available:
                    """ + products + """
                    
                    IMPORTANT RULES:
                    - Only mention products listed above. Do NOT make up or guess products.
                    - If asked about a product not in the list, say "Sorry, we don't have that product currently."
                    - Only answer questions related to GreenCart such as products, pricing, delivery and orders.
                    - If someone asks something unrelated, say: "I can only help with GreenCart related questions!"
                    - Keep answers short, friendly and helpful.
                    - Never suggest fake URLs or websites.
                    """;

            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(query)
                    .call()
                    .content();
            String cleaned = response.replaceAll("(?s)<think>.*?</think>", "").trim();
            return ResponseEntity.ok(cleaned);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR: " + e.getMessage());
        }
    }
}
