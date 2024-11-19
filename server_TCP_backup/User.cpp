#include "User.h"

// Constructor to initialize the user with an ID
User::User(const std::string& id) : userID(id) {}

// Function to add a video to the user's watched list
void User::watchVideo(const std::string& videoID) {
    watchedVideos.insert(videoID);
}

// Function to get the set of videos the user has watched
const std::unordered_set<std::string>& User::getWatchedVideos() const {
    return watchedVideos;
}

// Function to get the user ID
const std::string& User::getUserID() const {
    return userID;
}
