# Welcome to our Youtube platform for android !

## Let's start
jira: https://hodaya272727.atlassian.net/jira/software/projects/SCRUM/list?atlOrigin=eyJpIjoiNmRmZWFkMDI4N2FjNDg5YzgxZmY2YWI5OWYyNjgyZGEiLCJwIjoiaiJ9

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
git clone https://github.com/Guyrose1998/YouTube_android.git
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

Now we can begin using the app and get on our YouTube app! To start running, please press play (the green triangle) - make sure the configuration is set to `MainActivity`. 

## ROOM
- In addition to the external server we built, we created an internal database that will store the data of users, videos and comments locally in case we are offline. Also, as soon as the network comes back, the data is updated directly according to the external server we built.

## About Our Project

### Home Page
Our video page allows you to:
- Displays a list of videos including popular and randomly selected videos.
- See the top toolbar with a search icon that opens a search box when clicked.
- View a navigation drawer bar that contains categories and login/sign up buttons.
- Log in and upload videos using the "+" floating button.
- Once logged in, your profile image will display in the navigation drawer bar. 
- Watch videos, like or dislike them, share them, and write comments.
- Adjust your theme by clicking on the dark mode button.

### Login Page
Our login page, to reach our login page, you can click on the login button that appears on the navigation bar on the homepage. Only users who have already registered on our YouTube site will be able to log in. If the user is not registered or enters incorrect details, an appropriate message will be displayed on the screen. If the user successfully logs in with the correct details, a login message will appear on the screen and the user will be redirected back to the homepage. Additionally, a user who is not registered and is on the login page can directly navigate to the registration page by clicking on the link at the bottom of the login page.
for this part of the project we have 3 users already saved in the server for siging in, The registered users details will be at the MongoDB when you run the server. 

### Signup Page
Our registration page can be accessed by clicking on the login button on the homepage. Here, you can create a new user account by filling out the following fields:
- First name, last name, username, password, and profile picture.
- Password requirements: more than 8 characters, including at least one uppercase and one lowercase letter, numbers, at least one special character, and no spaces.
- Confirm your password to ensure values match.
- To add a picture, click on the circular button marked with a '+' and ensure you are uploading a compatible image file.
- Once all details are correctly filled in, you will receive a notification that your registration has been successfully completed, and you will be redirected back to the home page.
- Users on the signup page can navigate directly to the login page by clicking on the link at the bottom of the page.

### Upload Videos Page
After you've logged in, click on the "+" floating button to navigate to the Upload Video Page. Here, you can:
- Click on an Image View (that displays an upload icon) to pick a video from your gallery.
- Give the video a title and then upload it.
- After uploading, see your video added on the Home Page along with your user’s details.

### Video Page
Each video has its own data including video, title, publisher, publisher photo, views, and date of publish. Features include:
- Like a video by pressing the like button.
- Add comments by pressing the add comment button.
- Edit and delete comments.
- Edit video details and delete the video, which will then return you to the home page without the deleted video.
- Share videos using the share button.
- Navigate through additional videos at the bottom of the page.

### Profile page
- By clicking on the profile picture of a logged in user on the home page in the sidebar you can go to that user's profile page.
- On this page you can edit the details of that user or delete the user from the site.

### Users Videos page
- By clicking on a user's profile picture next to their video on the home page or on the watch video page, you will be taken to that user's video page where all the videos uploaded by that user will be displayed.

- Notice that the dark mode should be set  on the navigation bar, on the left popup menu, there is a button for the dark mode.

### Our Workflow
At the beginning of this project, we divided the missions between the team and updated them in Jira. During our work:
- We checked every detail we changed to ensure our app behavior met our expectations.
- When everything worked as expected, we uploaded our program.
