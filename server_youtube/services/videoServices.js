const path = require('path');
const fs = require('fs');
const mongoose = require('mongoose');
const Video = require('../models/videoModel');
const User = require('../models/userModel');
const { getUsernameFromToken } = require('../controllers/tokenController');
const multer = require('multer');

// Define storage settings
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, uniqueSuffix + '-' + file.originalname);
    }
});

// Set multer upload with file size limit
const upload = multer({
    storage: storage,
    limits: { fileSize: 10 * 1024 * 1024 * 1024 } // Set file size limit to 10GB
});


async function requireLogin(req, res, next) {
    const token = req.headers.authorization?.split(' ')[1];
    console.log('Authorization token:', token);
    if (!token) {
        return res.status(403).send('User not logged in');
    }

    try {
        const username = await getUsernameFromToken(token);
        console.log('Username from token:', username);
        if (!username) {
            return res.status(403).send('User not logged in');
        }

        const user = await User.findOne({ username });
        console.log('User found:', user);
        if (!user) {
            return res.status(403).send('User not logged in');
        }

        req.user = user;
        next();
    } catch (error) {
        console.error('Error in requireLogin middleware:', error);
        res.status(403).send('User not logged in');
    }
}


const addVideo = async (req, res) => {
    try {
        const { title, publishedDate, thumbnail } = req.body;

        if (!req.files || !req.files.file) {
            return res.status(400).send({ error: 'Video file is required.' });
        }

        const user = await User.findOne({ username: req.user.username });
        if (!user) {
            return res.status(404).send('User not found');
        }

        const videoFile = req.files.file[0];
        const videoPath = videoFile.path;

        const newVideo = new Video({
            title,
            thumbnail,
            publisher: user.username,
            views: '0',
            publishedDate,
            path: videoPath,
            userId: user._id,
            userImage: user.image || 'default.jpg'
        });

        await newVideo.save();
        res.status(201).send({ message: 'Video uploaded successfully!', videoId: newVideo._id });
    } catch (error) {
        console.error('Error in addVideo service:', error);
        res.status(500).send({ error: 'Error uploading video.' });
    }
};


const updateVideo = async (videoId, updateData) => {
    try {
        console.log('Updating video in database with ID:', videoId);
        const updatedVideo = await Video.findByIdAndUpdate(videoId, updateData, { new: true });
        if (!updatedVideo) {
            console.log('Video not found in database');
            throw new Error('Video not found');
        }
        console.log('Video successfully updated:', updatedVideo);
        return updatedVideo;
    } catch (error) {
        console.error('Error updating video:', error);
        throw error;
    }
};


const deleteVideo = async (videoId) => {
    try {
        console.log('Deleting video from database with ID:', videoId);
        const deletedVideo = await Video.findByIdAndDelete(videoId);
        if (!deletedVideo) {
            console.log('Video not found in database');
            throw new Error('Video not found');
        }
        console.log('Video successfully deleted:', deletedVideo);
        return deletedVideo;
    } catch (error) {
        console.error('Error deleting video:', error);
        throw error;
    }
};



// const net = require('net');

// function notifyCppServer(userId, videoId) {
//     return new Promise((resolve, reject) => {
//         const client = new net.Socket();
//         let videosList = '';  // This will store the list of videos

//         // Connect to the C++ TCP server
//         client.connect(5555, '127.0.0.1', function() {
//             console.log('Connected to C++ server');
//             const message = `${userId} ${videoId}`;
//             console.log('Sending message to C++ server:', message);
//             client.write(message); // Send message to C++ server
//         });

//         // Handle data from the C++ server (e.g., video recommendations)
//         client.on('data', function(data) {
//             videosList += data.toString();  // Append received data to the videosList
//             console.log('Received data chunk from C++ server:', data.toString());  // Log each data chunk

//             // Check if all data is received (optional: depending on protocol)
//             // Here you could implement some sort of delimiter or signal from C++ server
//             if (videosList.includes('\n')) {  // Assuming the server ends data with a newline
//                 console.log('Received full recommendations from C++ server:', videosList);

                

//                 // Split the video list by space (or another delimiter if required)
//                 const videoArray = videosList.trim().split(' ');  // Adjust based on actual data format
//              //   resolve(videoArray);  // Resolve the promise with the array of video IDs


//                    // Check if the data format is correct
//                 if (!Array.isArray(videoArray) || videoArray.length === 0) {
//                      console.error('Invalid data format received from C++ server');
//                      return reject(new Error('Invalid data format received from C++ server'));
//                }

//                 resolve(videoArray);  // Resolve the promise with the array of video IDs

//                 // Gracefully close connection
//                 client.end(); 
//             }
//         });

//         client.on('error', function(err) {
//             console.error('Error connecting to C++ server:', err.message);
//             console.log('Error stack trace:', err.stack);  // Log the stack trace for more details
//             reject(err);  // Reject the promise in case of error
//         });
        
//         client.on('end', function() {
//             console.log('Connection to C++ server closed');
//         });

//         client.on('error', function(err) {
//             console.error('Error connecting to C++ server:', err.message);
//             reject(err);  // Reject the promise in case of error
//         });

//         // Optionally handle timeout if the server is unresponsive
//         client.setTimeout(5000, function() {
//             console.error('Connection to C++ server timed out');
//             client.end();  // Gracefully close the connection on timeout
//             reject(new Error('Connection timed out'));
//         });
//     });
// }

const net = require('net');

function notifyCppServer(userId, videoId) {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();
        let videosList = '';  // This will store the list of videos

        // Connect to the C++ TCP server
        client.connect(5555, '127.0.0.1', function() {
            console.log('Connected to C++ server');
            const message = `${userId} ${videoId}`;
            console.log('Sending message to C++ server:', message);
            client.write(message); // Send message to C++ server
        });

        // Handle data from the C++ server (e.g., video recommendations)
        client.on('data', function(data) {
            videosList += data.toString();  // Append received data to the videosList
            console.log('Received data chunk from C++ server:', data.toString());  // Log each data chunk
        });

        client.on('end', function() {
            console.log('Connection to C++ server closed');
            if (videosList) {
                const videoArray = videosList.trim().split(' ');  // Assuming the server separates video IDs by spaces

                // Check if the data format is valid
                if (Array.isArray(videoArray) && videoArray.length > 0) {
                    resolve(videoArray);  // Resolve with the array of video IDs
                } else {
                    reject(new Error('Invalid data format received from C++ server'));
                }
            } else {
                reject(new Error('No data received from C++ server'));
            }
        });

        client.on('error', function(err) {
            console.error('Error connecting to C++ server:', err.message);
            reject(err);  // Reject the promise in case of error
        });

        // Optionally handle timeout if the server is unresponsive
        client.setTimeout(5000, function() {
            console.error('Connection to C++ server timed out');
            client.end();  // Gracefully close the connection on timeout
            reject(new Error('Connection timed out'));
        });
    });
}



const updateViews = async (videoId) => {
    try {
        console.log(`Updating views for video with ID: ${videoId}`);
        
        // Fetch the video first to check the current views value
        const video = await Video.findById(videoId);
        if (!video) {
            console.error(`Video not found in database with ID: ${videoId}`);
            throw new Error('Video not found');
        }

        // Convert views to number if it is a string and remove any commas
        let currentViews = parseInt(video.views.replace(/,/g, ''), 10);
        if (isNaN(currentViews)) {
            currentViews = 0; // Default to 0 if the current value is not a valid number
        }

        // Increment the views
        currentViews += 1;

        // Update the video with the new views count
        const updatedVideo = await Video.findByIdAndUpdate(
            videoId,
            { $set: { views: currentViews.toString() } }, // Convert back to string if needed
            { new: true }
        );

  //      console.log('Updated video:', updatedVideo);
        return updatedVideo;
    } catch (err) {
        console.error('Error updating views:', err.message);
        throw new Error('Error updating views: ' + err.message);
    }
};

const getVideoById = async (videoId) => {
    try {
        console.log(`Fetching video with ID: ${videoId}`);
        const video = await Video.findById(videoId);
        if (!video) {
            console.error(`Video not found in database with ID: ${videoId}`);
            throw new Error('Video not found');
        }
      //  console.log('Fetched video:', video);
        return video;
    } catch (err) {
        console.error('Error fetching video:', err.message);
        throw new Error('Error fetching video: ' + err.message);
    }
};

// Function to get top 10 popular videos
const getTopPopularVideos = (videos) => {
    console.log("Sorting videos by view count");

    // Create a shallow copy of the videos array to avoid modifying the original objects
    const videosCopy = videos.map(video => ({ ...video }));

    // Sort videos by view count in descending order
    const sortedVideos = videosCopy.sort((a, b) => {
        const viewsA = a.views && typeof a.views === 'string' ? parseInt(a.views.replace(/,/g, ''), 10) : 0;
        const viewsB = b.views && typeof b.views === 'string' ? parseInt(b.views.replace(/,/g, ''), 10) : 0;
        return viewsB - viewsA;
    });

    const topVideos = sortedVideos.slice(0, 10);
    return topVideos;
};

// Function to get 10 random videos from the remaining videos
const getRandomVideos = (videos, popularVideos) => {
    console.log("Selecting 10 random videos from the remaining videos");

    // Convert ObjectId to string for comparison
    const popularVideoIds = popularVideos.map(video => video._id.toString());
    const remainingVideos = videos.filter(video => !popularVideoIds.includes(video._id.toString()));

    // Select 10 random videos from the remaining ones
    const randomVideos = [];
    while (randomVideos.length < 10 && remainingVideos.length > 0) {
        const randomIndex = Math.floor(Math.random() * remainingVideos.length);
        randomVideos.push(remainingVideos.splice(randomIndex, 1)[0]);
    }
    return randomVideos;
};

// Main function to get the top 10 popular videos and 10 random videos
const getAllVideos = async () => {
    try {
        console.log("Fetching all videos from the database");
        const videos = await Video.find().lean();

        console.log(`Fetched ${videos.length} videos from the database`);

        // Get the top 10 popular videos
        const popularVideos = getTopPopularVideos(videos);
        // Get 10 random videos from the remaining videos excluding popular videos
        const randomVideos = getRandomVideos(videos, popularVideos);

        return { popularVideos, randomVideos };
    } catch (error) {
        console.error('Error fetching videos:', error);
        throw error;
    }
};

async function getUserVideos(userId) {
    try {
        const videos = await Video.find({ userId });
        return videos;
    } catch (err) {
        throw new Error('Failed to fetch videos: ' + err.message);
    }
}

async function getVideosByCategory(category) {
    try {
        const videos = await Video.find({ category: { $regex: category, $options: 'i' } });
        return videos;
    } catch (err) {
        throw new Error('Failed to fetch videos by category: ' + err.message);
    }
}

async function searchVideos(query) {
    try {
        const videos = await Video.find({ title: { $regex: query, $options: 'i' } });
        return videos;
    } catch (err) {
        throw new Error('Failed to search videos: ' + err.message);
    }
}

module.exports = {
    addVideo,
    deleteVideo,
    updateVideo,
    getAllVideos,
    getUserVideos,
    getVideoById,
    searchVideos,
    getVideosByCategory,
    requireLogin,
    upload,
    notifyCppServer,
    updateViews
};
