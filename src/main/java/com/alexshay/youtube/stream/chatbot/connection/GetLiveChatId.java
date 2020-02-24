/*
 * Copyright (c) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.alexshay.youtube.stream.chatbot.connection;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;

/**
 * Gets a live chat id from a video id or current signed in user.
 *
 * The videoId is often included in the video's url, e.g.:
 * https://www.youtube.com/watch?v=L5Xc93_ZL60
 *                                 ^ videoId
 * The video URL may be found in the browser address bar, or by right-clicking a video and selecting
 * Copy video URL from the context menu.
 *
 * @author Jim Rogers
 */
public class GetLiveChatId {
    /**
     * Retrieves the liveChatId from the broadcast associated with a videoId.
     *
     * @param youtube The object is used to make YouTube Data API requests.
     * @param videoId The videoId associated with the live broadcast.
     * @return A liveChatId, or null if not found.
     */
    public static String getLiveChatId(YouTube youtube, String videoId) throws IOException {
        // Get liveChatId from the video
        String liveChatId = null;
        YouTube.Videos.List videoList = youtube.videos()
                .list("liveStreamingDetails")
                .setFields("items/liveStreamingDetails/activeLiveChatId")
                .setId(videoId);
        VideoListResponse response = videoList.execute();
        for (Video v : response.getItems()) {
            liveChatId = v.getLiveStreamingDetails().getActiveLiveChatId();
        }

        return liveChatId;
    }
}
