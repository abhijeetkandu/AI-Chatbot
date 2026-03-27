package com.abhijeet.chatbot.service;



import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;


public class ProductService {
    private final JdbcTemplate jdbcTemplate;

    public ProductService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate =jdbcTemplate;
    }

    public String getProductsAsText(){
        List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT name, price FROM products"
        );
        if(products.isEmpty()){
            return "No products available currently.";
        }

        StringBuilder sb  = new StringBuilder();
        int i = 1;
        for(Map<String, Object> product : products){
            sb.append(i++).append(". ")
                    .append(product.get("name"))
                    .append(" - ₹")
                    .append(product.get("price"))
                    .append(" / unit\n");
        }
        return sb.toString();
    }
}
