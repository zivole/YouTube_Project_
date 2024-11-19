#ifndef USER_H
#define USER_H

#include <string>
#include <unordered_set>

class User {
private:
    std::string userID; // The ID of the user
    std::unordered_set<std::string> watchedVideos; // A set of videos the user has watched

public:
    // Constructor to initialize the user with an ID
    User(const std::string& id);

    // Function to add a video to the user's watched list
    void watchVideo(const std::string& videoID);

    // Function to get the set of videos the user has watched
    const std::unordered_set<std::string>& getWatchedVideos() const;

    // Function to get the user ID
    const std::string& getUserID() const;
};

#endif // USER_H
