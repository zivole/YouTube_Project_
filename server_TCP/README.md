# serverTCP

# Multithreaded TCP Server with Video Recommendation System

## Project Description

This project implements a multithreaded TCP server that handles multiple clients concurrently using C++'s ⁠ std::thread ⁠. Each client can send multiple requests to the server, which returns video recommendations based on user viewing history. The recommendation system prioritizes popular videos and those watched by users with similar interests.

The server is also connected to a Node.js server, which notifies it of users watching videos and retrieves recommendations to display to users.

## Features

•⁠  ⁠*Multithreaded Server*: Uses ⁠ std::thread ⁠ to handle multiple client connections simultaneously.
•⁠  ⁠*Video Recommendation System*: Provides personalized video recommendations based on user history and popular content.
•⁠  ⁠*Node.js Integration*: Interacts with a Node.js server to manage real-time notifications and recommendation requests.

## File Descriptions

### 1. ⁠ main.cpp ⁠
This is the entry point of the TCP server. It manages the initialization of the server socket, accepts client connections, and spawns threads to handle multiple clients concurrently. It also handles communication with the Node.js server.

### 2. ⁠ User.cpp ⁠ and ⁠ User.h ⁠
These files define the ⁠ User ⁠ class, which represents individual users in the system. The ⁠ User ⁠ class stores user-specific data, such as viewing history and preferences, and provides methods to update user information and fetch recommendations.

### 3. ⁠ VideoRecommendationSystem.cpp ⁠ and ⁠ VideoRecommendationSystem.h ⁠
These files define the ⁠ VideoRecommendationSystem ⁠ class, which contains the logic for generating video recommendations. The system analyzes user history, identifies popular videos, and suggests content watched by users with similar interests.

## Running the Server

The server can be run either on:

### 1. A Virtual Machine
Ensure the virtual machine is set up with a C++ compiler and necessary libraries. You can SSH into the virtual machine and compile and run the server from the terminal.

### 2. WSL (Windows Subsystem for Linux) in Visual Studio
If you're developing on Windows, you can use WSL with Visual Studio to build and run the server. To do this:

•⁠  ⁠Install WSL and a Linux distribution (e.g., Ubuntu).
•⁠  ⁠Open Visual Studio, and configure your project to use the WSL environment.
•⁠  ⁠Use Visual Studio to build and debug the server as you would in a Linux terminal.

This flexibility allows you to develop in a Linux-like environment even when working on Windows.

### 3. For macOS
To ensure proper functionality on macOS, there are a few modifications you need to make when compiling and running the server:

 *Socket Option Changes*:  
   On macOS, replace the ⁠ setsockopt ⁠ line for socket options in ⁠ main.cpp ⁠ as follows:

      ⁠ cpp
      if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEPORT, &opt, sizeof(opt))) {
          perror("setsockopt SO_REUSEPORT");
          exit(EXIT_FAILURE);
      }
   These adjustments ensure compatibility with macOS, particularly in handling socket options using SO_REUSEPORT, which is required on this operating system.

## How to Compile

To compile the project, you need a C++ compiler (e.g., `g++`).

### Step-by-Step Compilation:

1. Open a terminal and navigate to the project directory.
2. Use the following command to compile the project:
   ```⁠bash
   g++ -std=c++11 -pthread main.cpp User.cpp VideoRecommendationSystem.cpp -o server
3.⁠ ⁠This command compiles ⁠ main.cpp ⁠, ⁠ User.cpp ⁠, and ⁠ VideoRecommendationSystem.cpp ⁠, linking the necessary threading libraries.

4.⁠ ⁠After successful compilation, run the server using:
   ```bash
   ./server
