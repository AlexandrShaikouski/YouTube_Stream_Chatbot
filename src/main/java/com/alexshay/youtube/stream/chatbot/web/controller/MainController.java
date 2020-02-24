package com.alexshay.youtube.stream.chatbot.web.controller;

import com.alexshay.youtube.stream.chatbot.web.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class MainController {

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping
    public String getMainPage() {
        return "index";
    }

    @GetMapping("action")
    public Model goChatbot(@RequestParam String videoId, Model model) {

        try {
            boolean isChatbotGo = chatbotService.isChatbotGo(videoId);
        } catch (IOException e) {

        }


        return model;
    }
}
