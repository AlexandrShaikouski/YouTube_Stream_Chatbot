package com.alexshay.youtube.stream.chatbot.web.service;

import com.alexshay.youtube.stream.chatbot.connection.DeleteLiveChatMessage;
import com.alexshay.youtube.stream.chatbot.connection.InsertLiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CheckMessage {
    private List<String> markedWords;
    private static String previousMessage;

    public void checkMessage(final LiveChatMessage message) {
        final LiveChatMessageAuthorDetails author = message.getAuthorDetails();
        final LiveChatMessageSnippet snippet = message.getSnippet();
        final String displayedMessage = snippet.getDisplayMessage();

        if(!isAuthorHighAccess(author)) {
            if(isRepeatedMessage(displayedMessage)){
                DeleteLiveChatMessage.action(message.getId());
            } else if (isMessageConsistMarkedWords(displayedMessage)) {
                InsertLiveChatMessage.action(snippet.getLiveChatId(), createWarningMessage(message));
            }


        }
        previousMessage = displayedMessage;
    }

    private String createWarningMessage(final LiveChatMessage message) {
        String displayMessage = null;
        
        return displayMessage;
    }

    private boolean isMessageConsistMarkedWords(final String displayedMessage) {
        return markedWords.contains(displayedMessage);
    }

    private boolean isRepeatedMessage(final String displayedMessage) {
        return !Objects.isNull(displayedMessage) && displayedMessage.equals(previousMessage);
    }

    private boolean isAuthorHighAccess(LiveChatMessageAuthorDetails author) {
        return author.getIsChatSponsor() || author.getIsChatModerator() || author.getIsChatOwner();
    }
}
