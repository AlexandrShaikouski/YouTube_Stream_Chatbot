package com.alexshay.youtube.stream.chatbot.web.service.impl;

import com.alexshay.youtube.stream.chatbot.connection.Auth;
import com.alexshay.youtube.stream.chatbot.connection.GetLiveChatId;
import com.alexshay.youtube.stream.chatbot.connection.ListLiveChatMessages;
import com.alexshay.youtube.stream.chatbot.web.service.ChatbotService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    @Autowired
    private ListLiveChatMessages listLiveChatMessages;

    @Override
    public boolean isChatbotGo(String videoId) throws IOException {
        boolean isChatbotGo = false;

        List<String> scopes = Lists.newArrayList(YouTubeScopes.YOUTUBE_READONLY);

        Credential credential = Auth.authorize(scopes, "listlivechatmessages");

        YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                .setApplicationName("youtube-cmdline-listchatmessages-sample").build();

        String liveChatId = GetLiveChatId.getLiveChatId(youtube, videoId);

        if (liveChatId != null && !liveChatId.isEmpty()) {
            listLiveChatMessages.action(liveChatId);
            isChatbotGo = true;
        }

        return isChatbotGo;
    }
}
