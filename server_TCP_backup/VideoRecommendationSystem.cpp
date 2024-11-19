#include "VideoRecommendationSystem.h"

// Function to register that a user watched a video
void VideoRecommendationSystem::userWatchedVideo(const std::string& userID, const std::string& videoID) {
    // If the user doesn't exist, create an empty entry for them
    if (userWatchedVideos.find(userID) == userWatchedVideos.end()) {
        userWatchedVideos[userID] = std::unordered_set<std::string>();
    }

    // Add the video to the user's watched list
    userWatchedVideos[userID].insert(videoID);

    // Add the user to the set of users who watched this video
    videoToUsers[videoID].insert(userID);
}

// Function to get video recommendations based on user history
std::vector<std::string> VideoRecommendationSystem::getRecommendations(const std::string& userID) {
    std::unordered_set<std::string> watchedVideos;

    // Check if the user has watched any videos
    if (userWatchedVideos.find(userID) != userWatchedVideos.end()) {
        watchedVideos = userWatchedVideos[userID];
    }

    std::unordered_set<std::string> recommendedVideosSet;

    // Find videos watched by other users who watched the same content as the current user
    for (const auto& videoID : watchedVideos) {
        for (const auto& otherUserID : videoToUsers[videoID]) {
            const auto& otherUserVideos = userWatchedVideos[otherUserID];
            for (const auto& otherVideoID : otherUserVideos) {
                // Recommend videos the user hasn't watched yet
             //   if (watchedVideos.find(otherVideoID) == watchedVideos.end()) {
                    recommendedVideosSet.insert(otherVideoID);
                //}
            }
        }
    }

    // Convert the set of recommended videos to a vector
    std::vector<std::string> result;
    for (const auto& videoID : recommendedVideosSet) {
        result.push_back(videoID);
    }


    return result;
}
