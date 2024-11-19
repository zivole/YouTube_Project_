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



## Updated Functionality: Fetching Recommended Videos
In the latest updates, we introduced the ability to fetch recommended videos for the user based on their viewing history and popular videos. The following components have been modified:

- **MainVideoSection.js**: Now fetches video details and sends the current user's ID to fetch relevant recommended videos.
- **RecommendedVideos.js**: Displays a list of recommended videos fetched from the server.
- **play_video.js**: Manages the main video playback and the fetching of recommended videos, including API calls to retrieve video recommendations based on the user's interactions.

Make sure the server and frontend are running and connected for this feature to work seamlessly.

