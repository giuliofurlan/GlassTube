package com.giufu.youtube.utils;

public class YoutubeConfig {
    public YoutubeConfig(){
    }
    private static final String API_KEY = "AIzaSyBNn-VMyK3OTmV_EGCVYMOPSVC2qQwQqxA";
    private static final String API_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&key="+API_KEY+"&type=video&q=";

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getApiUrl() {
        return API_URL;
    }

}
