package com.alexshay.youtube.stream.chatbot.connection;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageAuthorDetails;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import com.google.api.services.youtube.model.LiveChatMessageSnippet;
import com.google.api.services.youtube.model.LiveChatSuperChatDetails;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Lists live chat messages and SuperChat details from a live broadcast.
 *
 * The videoId is often included in the video's url, e.g.:
 * https://www.youtube.com/watch?v=L5Xc93_ZL60
 *                                 ^ videoId
 * The video URL may be found in the browser address bar, or by right-clicking a video and selecting
 * Copy video URL from the context menu.
 *
 * @author Jim Rogers
 */
public class ListLiveChatMessages {

    /**
     * Common fields to retrieve for chat messages
     */
    private static final String LIVE_CHAT_FIELDS =
            "items(authorDetails(channelId,displayName,isChatModerator,isChatOwner,isChatSponsor,"
                    + "profileImageUrl),snippet(displayMessage,superChatDetails,publishedAt)),"
                    + "nextPageToken,pollingIntervalMillis";

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Lists live chat messages and SuperChat details from a live broadcast.
     *
     * @param videoId videoId (optional). If the videoId is given, live chat messages will be retrieved
     * from the chat associated with this video. If the videoId is not specified, the signed in
     * user's current live broadcast will be used instead.
     */
    public static void action(String liveChatId) {
        listChatMessages(liveChatId, null, 0);
    }

    /**
     * Lists live chat messages, polling at the server supplied interval. Owners and moderators of a
     * live chat will poll at a faster rate.
     *
     * @param liveChatId The live chat id to list messages from.
     * @param nextPageToken The page token from the previous request, if any.
     * @param delayMs The delay in milliseconds before making the request.
     */
    private static void listChatMessages(
            final String liveChatId,
            final String nextPageToken,
            long delayMs) {
        System.out.println(
                String.format("Getting chat messages in %1$.3f seconds...", delayMs * 0.001));
        Timer pollTimer = new Timer();
        pollTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // Get chat messages from YouTube
                            LiveChatMessageListResponse response = youtube
                                    .liveChatMessages()
                                    .list(liveChatId, "snippet, authorDetails")
                                    .setPageToken(nextPageToken)
                                    .setFields(LIVE_CHAT_FIELDS)
                                    .execute();

                            // Display messages and super chat details
                            List<LiveChatMessage> messages = response.getItems();
                            for (LiveChatMessage message : messages) {
                                LiveChatMessageSnippet snippet = message.getSnippet();
                                System.out.println(buildOutput(
                                        snippet.getDisplayMessage(),
                                        message.getAuthorDetails(),
                                        snippet.getSuperChatDetails()));
                                ///////////////////////
                                ///////////////////////
                            }

                            // Request the next page of messages
                            listChatMessages(
                                    liveChatId,
                                    response.getNextPageToken(),
                                    response.getPollingIntervalMillis());
                        } catch (Throwable t) {
                            System.err.println("Throwable: " + t.getMessage());
                            t.printStackTrace();
                        }
                    }
                }, delayMs);
    }

    /**
     * Formats a chat message for console output.
     *
     * @param message The display message to output.
     * @param author The author of the message.
     * @param superChatDetails SuperChat details associated with the message.
     * @return A formatted string for console output.
     */
    private static String buildOutput(
            String message,
            LiveChatMessageAuthorDetails author,
            LiveChatSuperChatDetails superChatDetails) {
        StringBuilder output = new StringBuilder();
        if (superChatDetails != null) {
            output.append(superChatDetails.getAmountDisplayString());
            output.append("SUPERCHAT RECEIVED FROM ");
        }
        output.append(author.getDisplayName());
        List<String> roles = new ArrayList<>();
        if (author.getIsChatOwner()) {
            roles.add("OWNER");
        }
        if (author.getIsChatModerator()) {
            roles.add("MODERATOR");
        }
        if (author.getIsChatSponsor()) {
            roles.add("SPONSOR");
        }
        if (roles.size() > 0) {
            output.append(" (");
            String delim = "";
            for (String role : roles) {
                output.append(delim).append(role);
                delim = ", ";
            }
            output.append(")");
        }
        if (message != null && !message.isEmpty()) {
            output.append(": ");
            output.append(message);
        }
        return output.toString();
    }
}