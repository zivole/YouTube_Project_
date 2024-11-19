#ifndef VIDEO_RECOMMENDATION_SYSTEM_H
#define VIDEO_RECOMMENDATION_SYSTEM_H

#include <string>
#include <unordered_map>
#include <unordered_set>
#include <vector>

class VideoRecommendationSystem {
private:
    std::unordered_map<std::string, std::unordered_set<std::string>> userWatchedVideos; // Maps userID to a set of watched videoIDs
    std::unordered_map<std::string, std::unordered_set<std::string>> videoToUsers; // Maps videoID to a set of userIDs who watched the video

public:
    // Function to register that a user watched a video
    void userWatchedVideo(const std::string& userID, const std::string& videoID);

    // Function to get video recommendations based on user history
    std::vector<std::string> getRecommendations(const std::string& userID);
};

#endif // VIDEO_RECOMMENDATION_SYSTEM_H
