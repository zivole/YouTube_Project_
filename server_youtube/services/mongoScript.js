const fs = require('fs');
const mongoose = require('mongoose');
const User = require('../models/userModel');
const Video = require('../models/videoModel');
const Comment = require('../models/commentModel');

function readJSONFile(filePath) {
    try {
        const jsonData = fs.readFileSync(filePath, 'utf8');
        return JSON.parse(jsonData);
    } catch (error) {
        console.error('Error reading JSON file ${filePath}:', error);
        return null;
    }
}

function convertObjectIdFields(data) {
    return data.map(item => {
        if (item._id && item._id.$oid) {
            item._id = new mongoose.Types.ObjectId(item._id.$oid);
        }
        if (item.userId && item.userId.$oid) {
            item.userId = new mongoose.Types.ObjectId(item.userId.$oid);
        }
        if (item.videoId && item.videoId.$oid) {
            item.videoId = new mongoose.Types.ObjectId(item.videoId.$oid);
        }
        if (item.comments) {
            item.comments = item.comments.map(comment => {
                if (comment.$oid) {
                    return new mongoose.Types.ObjectId(comment.$oid);
                }
                return comment;
            });
        }
        return item;
    });
}

// Creates the users in the db
async function initializeUsers() {
    await User.deleteMany();
    try {
        // Read JSON file
        const userData = readJSONFile('./dataBase/users.json');
        if (!userData) {
            console.error('No user data found.');
            return;
        }

        // Convert _id fields
        const convertedUserData = convertObjectIdFields(userData);

        // Insert users into database
        await User.insertMany(convertedUserData);
        console.log('Users inserted successfully.');
    } catch (error) {
        console.error('Error initializing users:', error);
    }
}

// Creates the videos in the db
async function initializeVideos() {
    await Video.deleteMany();
    try {
        // Read JSON file
        let videoData = readJSONFile('./dataBase/videos.json');
        if (!videoData) {
            console.error('No video data found.');
            return;
        }

        // Convert _id, userId, and comments fields
        videoData = convertObjectIdFields(videoData);

        // Insert videos into database
        await Video.insertMany(videoData);
        console.log('Videos inserted successfully.');
    } catch (error) {
        console.error('Error initializing videos:', error);
    }
}

// Creates the comments in the db
async function initializeComments() {
    await Comment.deleteMany();
    try {
        // Read JSON file
        let commentData = readJSONFile('./dataBase/comments.json');
        if (!commentData) {
            console.error('No comment data found.');
            return;
        }

        // Convert _id, userId, and videoId fields
        commentData = convertObjectIdFields(commentData);

        // Insert comments into database
        await Comment.insertMany(commentData);
        console.log('Comments inserted successfully.');
    } catch (error) {
        console.error('Error initializing comments:', error);
    }
}

module.exports = { initializeUsers, initializeVideos, initializeComments };