package com.alexshay.youtube.stream.chatbot.web.service;

import java.io.IOException;

public interface ChatbotService {
    boolean isChatbotGo(String videoId) throws IOException;
}
