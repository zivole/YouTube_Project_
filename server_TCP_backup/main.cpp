#include <iostream>
#include <thread>
#include <vector>
#include <cstring>
#include <netinet/in.h>
#include <unistd.h>
#include <sstream>
#include "VideoRecommendationSystem.h"  // Include your recommendation system header

const int PORT = 5555;

// Global recommendation system instance
VideoRecommendationSystem videoRecSystem;

void handle_client(int client_socket) {
    char buffer[1024] = {0};
    int bytesRead = read(client_socket, buffer, 1024);
    if (bytesRead <= 0) {
        std::cerr << "Error reading from client socket." << std::endl;
        close(client_socket);
        return;
    }

    std::cout << "Received from Node.js: " << buffer << std::endl;

    // Process the received message (assuming format: "userId videoId")
    std::istringstream iss(buffer);
    std::string userID, videoID;
    if (iss >> userID >> videoID) {
        std::cout << "Parsed userID: " << userID << ", videoID: " << videoID << std::endl;

        // Register the video view in the recommendation system
        videoRecSystem.userWatchedVideo(userID, videoID);

        // Generate video recommendations for the user
        std::vector<std::string> recommendations = videoRecSystem.getRecommendations(userID);


// Prepare the response string (comma-separated video IDs)
std::string response ="";
for (size_t i = 0; i < recommendations.size(); ++i) {
    response += recommendations[i];
    if (i != recommendations.size() - 1) {
        response += " ";  // Add a space between video IDs
    }
}

// Print recommendations to the console
std::cout << "Sending recommendations to the client: ";
for (const auto& videoID : recommendations) {
    std::cout << videoID << " ";  // Print each video ID followed by a space
}
std::cout << std::endl;

// Send the recommendations back to the client
send(client_socket, response.c_str(), response.length(), 0);


        // // Prepare the response string (comma-separated video IDs)
        // std::string response = "Recommended videos: ";
        // for (size_t i = 1; i < recommendations.size(); ++i) {
        //     response += recommendations[i];
        //     if (i != recommendations.size() - 1) {
        //         response += " ";
        //     }
        // }

        // std::cout << "Sending recommendations to the client: " << recommendations << std::endl;

        // // Send the recommendations back to the client
        // send(client_socket, response.c_str(), response.length(), 0);
    } else {
        std::cerr << "Error: Invalid message format received from client." << std::endl;
    }

    // Close the client connection
    close(client_socket);
}

int main() {
    int server_fd, client_socket;
    struct sockaddr_in address;
    int opt = 1;
    int addrlen = sizeof(address);

    // Create the socket
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    // Set socket options
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt))) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    // Bind the socket to the port
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    // Start listening for connections
    if (listen(server_fd, 3) < 0) {
        perror("Listen");
        exit(EXIT_FAILURE);
    }

    std::cout << "C++ Server is running, waiting for connections..." << std::endl;

    // Main server loop to accept and handle client connections
    while (true) {
        if ((client_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen)) < 0) {
            perror("Accept");
            exit(EXIT_FAILURE);
        }

        std::cout << "Client connected!" << std::endl;

        // Handle each client in a separate thread
        std::thread(handle_client, client_socket).detach();
    }

    return 0;
}
