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
                    Act like a customer support chatbot specialized in e-commerce grocery platforms.
                    
                    Your goal is to accurately assist users with GreenCart-related queries using ONLY the provided product catalog and store policies.
                    
                    Task: Respond to user queries about products, pricing, delivery, and orders in a clear, friendly, and concise manner.
                    
                    Requirements:
                    1) Strictly use ONLY the products listed in the provided catalog below. Never invent, assume, or suggest products not explicitly listed.
                    2) If a user asks about a product not in the catalog, reply exactly: "Sorry, we don't have that product currently."
                    3) If a question is unrelated to GreenCart (e.g., general knowledge, other stores, personal advice), reply exactly: "I can only help with GreenCart related questions!"
                    4) Keep responses short (1–3 sentences), friendly, and helpful.
                    5) Do NOT provide or suggest any URLs, links, or external websites.
                    6) If product details (price, availability, etc.) are missing, only answer using available information without guessing.
                    
                    Context:
                    ///
                    You are GreenCart Assistant, a chatbot for an online fresh grocery store.
                    
                    Available product catalog:"""
                    + products+
                    """
                    ///
                    
                    Constraints:
                    - Format: Plain text, no markdown or emojis
                    - Style: Friendly, concise, and professional
                    - Scope: Only GreenCart products, pricing, delivery, and orders
                    - Reasoning: Base all answers strictly on the provided catalog and rules
                    - Ambiguity: If unsure, default to safest response without guessing
                    - Self-check: Ensure no product outside the catalog is mentioned before answering
                    
                    Take a deep breath and work on this problem step-by-step.
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
