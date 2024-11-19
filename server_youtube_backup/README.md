# Welcome to Our YouTube Server Platform for Web!
[Jira Link](https://hodaya272727.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog?atlOrigin=eyJpIjoiODgwNjA5NDkxMWE0NDUwYzgzN2VkOGRiMjQ5MzlkYWMiLCJwIjoiaiJ9)

## How to Run the Server?

### Downloading and Installing MongoDB
#### Download MongoDB:

1. Visit the MongoDB official download page: [Download MongoDB](https://www.mongodb.com/try/download/community)
2. Choose the appropriate version for your operating system (Windows, macOS, or Linux).
3. Follow the installation instructions provided on the MongoDB website.

### Setting Up the Project

#### Clone the Repositories

# Clone the backend repository
```
git clone https://github.com/zivole/Server_youtube.git
cd Server_youtube
```
# Clone the frontend repository
```
git clone https://github.com/HodayaBarak/youtube.git ../youtube
```

## Install Multer

Multer is a middleware for handling `multipart/form-data`, which is primarily used for uploading files.

To install Multer, run the following command in the `server_youtube` directory:
```
npm install multer
```
## Install Other Dependencies

Make sure you are in the `server_youtube` directory and install the required dependencies:
```
npm install
```

## Running the Server and Frontend

To simplify running both the server and the frontend, and to push JSON files to MongoDB, we have provided a script.


### Note:
- Ensure MongoDB is running on your local machine and accessible before running this script.
- The `mongoimport` commands assume that your MongoDB instance is running on the default port and no authentication is required. Modify the commands if your setup is different.

## About the Server

Our server is built using Node.js and Express. It follows the MVC architecture and uses Mongoose to interact with MongoDB. The server provides functionalities for user registration, authentication, video upload, and video management.

# Key Components:

## User Model and Services:
- **userModel.js**: Defines the schema for user data.
- **userServices.js**: Contains functions for user registration and authentication.

## Video Model and Services:
- **videoModel.js**: Defines the schema for video data.
- **videoServices.js**: Contains functions for handling video uploads, fetching videos, and updating video details.

## Token Management:
- **tokenController.js**: Manages JWT tokens for user authentication.

## Comment Model and Services:
- **commentModel.js**: Defines the schema for comment data.
- **commentServices.js**: Contains functions for creating, fetching, updating, and deleting comments.

## Controllers:
- **userController.js**: Handles incoming HTTP requests and interacts with the corresponding services.
- **videoController.js**: Handles incoming HTTP requests and interacts with the corresponding services.
- **commentController.js**: Handles HTTP requests related to comments, interacting with the **commentServices.js**.

## Endpoints:
- `/api/users`: Manages user-related operations such as registration and login.
- `/api/videos`: Manages video-related operations such as uploading and fetching videos.
- `/api/tokens`: Manages JWT token operations.
- `/api/comments`: Manages comment-related operations such as creating, fetching, updating, and deleting comments.

# Pages Overview:

## Home Page:
- Displays a list of videos including popular and randomly selected videos.
- Allows users to search for specific videos.

## Sign-In Page:
- Users can log in using their credentials.
- Includes options to navigate back to the home page or to the registration page.

## Sign-Up Page:
- New users can register by providing their details including email, password, and profile picture.

## Video Upload:
- Registered users can upload new videos by providing the video title, description, and file.

## Video Viewing Page:
- Users can watch videos and see related information.
- Registered users can comment on videos.

## Dark Mode:
- The application supports dark mode, which can be toggled using a switch on the upper-right side of the screen.

## Summary of User Permissions:

### Upload, Edit, and Delete Videos:
- Only signed-in users can upload, edit, or delete videos.

### Comments on Videos:
- Only signed-in users can comment on videos, edit their comments, or delete their comments.

### Update and Delete Users:
- Users can update or delete their accounts through the settings page.



## Script Details

## Backend
To set up and run the backend server, follow these steps:

Start the server: This command pushes the JSON files in the database folder to MongoDB and runs the server.

```
   node server.js
```
## Frontend
[Frontend Repository](https://github.com/HodayaBarak/youtube.git)

To set up and run the frontend application, use the following command:
```
   npm start
```


## New Feature: Video Recommendation System

### Logged-in Users:
- When a user is logged in, the server provides personalized video recommendations based on their viewing history.
- The recommendations are generated based on videos that other users with similar viewing patterns have watched.
- If there are fewer than 6 recommended videos, the system will fill the remaining slots with random videos to ensure the user receives a total of 6 videos.

### Not Logged-in Users:
- For users who are not logged in, the server returns a list of the 10 most popular videos.

### General Logic:
- The video recommendation system prioritizes content watched by users with similar interests to offer relevant suggestions.
- If the recommended list is incomplete, random popular videos are added to provide a better user experience.

### TCP Server Requirement:
- The Node.js server connects to a multithreaded TCP server that returns a list of recommended videos based on user interactions.
- **Before running the Node.js server**, you must compile and run the TCP server to ensure the recommendation functionality works correctly.

#### Steps to Compile and Run the TCP Server:
1. Navigate to the directory where the TCP server source files are located.
2. Compile the TCP server:
   ```bash
   g++ -std=c++11 -o tcp_server main.cpp

Here's the updated section in GitHub syntax, including the requirement to compile and run the TCP server:

md
Copy code
## New Feature: Video Recommendation System

### Logged-in Users:
- When a user is logged in, the server provides personalized video recommendations based on their viewing history.
- The recommendations are generated based on videos that other users with similar viewing patterns have watched.
- If there are fewer than 6 recommended videos, the system will fill the remaining slots with random videos to ensure the user receives a total of 6 videos.

### Not Logged-in Users:
- For users who are not logged in, the server returns a list of the 10 most popular videos.

### General Logic:
- The video recommendation system prioritizes content watched by users with similar interests to offer relevant suggestions.
- If the recommended list is incomplete, random popular videos are added to provide a better user experience.

### TCP Server Requirement:
- The Node.js server connects to a multithreaded TCP server that returns a list of recommended videos based on user interactions.
- **Before running the Node.js server**, you must compile and run the TCP server to ensure the recommendation functionality works correctly.

#### Steps to Compile and Run the TCP Server:
1. Navigate to the directory where the TCP server source files are located.
2. Compile the TCP server:
   ```bash
   g++ -std=c++11 -o tcp_server main.cpp
3. After successful compilation, run the server using:
     ```bash
     ./tcp_server   

Please Ensure that the TCP server is running before starting the Node.js server, as it is necessary for fetching video recommendations.
[For more instructions on running a TCP server](https://github.com/HodayaBarak/serverTCP.git)
