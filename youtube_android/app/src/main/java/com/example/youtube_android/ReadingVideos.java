//package com.example.youtube_android;
//
//import android.content.Context;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import com.example.youtube_android.entities.VideoItem;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.List;
//
//public class ReadingVideos {
//
//    public static void readingVideos(Context context, List<VideoItem> videos) {
//        try {
//            InputStream inputStream = context.getResources().openRawResource(R.raw.videos);
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            JSONArray array = new JSONArray(stringBuilder.toString());
//            String packageName = context.getPackageName();
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject jsonPost = array.getJSONObject(i);
//                int id = jsonPost.getInt("id");
//                String videoTitle = jsonPost.getString("title");
//                String channelName = jsonPost.getString("channel");
//                String viewsCount = jsonPost.getString("views");
//                String publishDate = jsonPost.getString("time");
//                String channelImage = jsonPost.getString("channel_thumbnail");
//                String thumbnailPath = jsonPost.getString("thumbnail");
//                String videoPath = jsonPost.getString("video_url");
//
//                Log.d("ReadingVideos", "VideoItem: " + videoTitle + ", Thumbnail: " + thumbnailPath + ", Channel Image: " + channelImage + ", Video URL: " + videoPath);
//
//                // Assuming all resources are drawable images and videos are in the raw folder
//                int channelImageRes = getResourceId(context, channelImage, "drawable");
//                int thumbnailPathRes = getResourceId(context, thumbnailPath, "drawable");
//
//                if (channelImageRes != 0 && thumbnailPathRes != 0) {
//                    videos.add(new VideoItem(id, "android.resource://" + packageName + "/drawable/" + thumbnailPath, videoTitle, channelName, "android.resource://" + packageName + "/drawable/" + channelImage, "android.resource://" + packageName + "/raw/" + videoPath, viewsCount, publishDate));
//                } else {
//                    Log.e("ReadingVideos", "Resource not found for video: " + videoTitle);
//                }
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static int getResourceId(Context context, String resourceName, String resourceType) {
//        return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
//    }
//}
