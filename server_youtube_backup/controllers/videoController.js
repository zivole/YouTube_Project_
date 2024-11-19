const Video = require('../models/videoModel');
const User = require('../models/userModel');
const videoService = require('../services/videoServices');

const { notifyCppServer } = require('../services/videoServices');

async function getVideo(req, res) {
    try {
        const videoID = req.params.pid;
        const userID = req.headers['x-user-id'];  // Extract user ID from headers
        const username = req.headers['x-username'];  // Extract username from headers
        
        console.log(`Fetching video with ID: ${videoID}`);
        console.log(`Current user ID: ${userID}, Username: ${username}`);
        
        const video = await videoService.getVideoById(videoID);

        if (!video) {
            return res.status(404).json({ error: 'Video not found' });
        }

        // Update views count
        await videoService.updateViews(videoID);

        // Notify the C++ server with the current user and video ID

        console.log('Returning video:', video);
        res.status(200).json(video);
    } catch (err) {
        console.error('Error fetching video:', err);
        res.status(404).send('Video not found');
    }
}


// async function getRecommendedVideos(req, res) {
//     try {
//         const videoID = req.params.pid;  // Video currently being displayed
//         const userID = req.headers['x-user-id'];  // Extract user ID from headers
//         const username = req.headers['x-username'];  // Extract username from headers
//         let videosRec = new Set();  // Using a Set to avoid duplicates

//         console.log(`Fetching video with ID: ${videoID}`);
//         console.log(`Current user ID: ${userID}, Username: ${username}`);

//         // If there is no logged-in user, return the 10 most popular videos by view count
//         if (!userID) {
//             console.log('No user logged in, returning top 10 most popular videos');

//             const popularVideos = await Video.find({ _id: { $ne: videoID } })  // Exclude the current video
//                 .sort({ views: -1 })
//                 .limit(10);  // Fetch top 10 most popular videos excluding the current video
//             return res.status(200).json(popularVideos);
//         }

//         console.log('Notifying C++ server...');
//         // Notify the C++ server with the current user and video ID
//         const videoList = await notifyCppServer(userID, videoID);  // Forward user ID and video ID to C++ server
//         console.log('Received video list from C++ server:', videoList);

//         // Ensure videoList is iterable and contains individual video IDs
//         if (Array.isArray(videoList)) {
//             console.log('Processing video list...');
//             // Fetch each recommended video by ID from the database
//             for (let videoId of videoList) {
//                 // Ensure that the current video (videoID) is excluded
//                 if (videoId.trim() !== videoID) {
//                     let video = await Video.findById(videoId.trim());  // Fetch video details from DB (ensure to trim the ID)
//                     if (video) {
//                         videosRec.add(video);  // Add video to the set
//                     }
//                 }
//             }
//         } else {
//             throw new Error('Received invalid data from C++ server');
//         }

//         console.log('Accessing video recommendations...');

//         // Convert Set to an Array to sort by view count
//         let videosArray = Array.from(videosRec);

//         // If the list contains more than 10 videos, return only the top 10 most popular videos
//         if (videosArray.length > 10) {
//             // Sort by view count in descending order and limit to 10
//             videosArray = videosArray.sort((a, b) => b.views - a.views).slice(0, 10);
//         }

//         // Sort the videos by view count in descending order if the list has fewer than 10 videos
//         videosArray.sort((a, b) => b.views - a.views);

//         // If there are fewer than 6 recommended videos, add random videos to reach 6
//         if (videosArray.length < 6) {
//             const randomVideos = await Video.aggregate([
//                 { $match: { _id: { $ne: videoID } } },  // Ensure current video is excluded
//                 { $sample: { size: 6 - videosArray.length } }
//             ]);  // Fetch random videos from DB excluding the current video
//             videosArray = videosArray.concat(randomVideos);  // Add random videos to the list
//         }

//         res.status(200).json(videosArray);  // Return the final sorted list of videos
//     } catch (err) {
//         console.error('Error fetching video recommendations:', err);
//         res.status(404).send('Error fetching recommendations');
//     }
// }

async function getRecommendedVideos(req, res) {
    try {
        const videoID = req.params.pid;  // Video currently being displayed
        const userID = req.headers['x-user-id'];  // Extract user ID from headers
        const username = req.headers['x-username'];  // Extract username from headers
        let videosRec = new Set();  // Using a Set to avoid duplicates

        console.log(`Fetching video with ID: ${videoID}`);
        console.log(`Current user ID: ${userID}, Username: ${username}`);

        // If there is no logged-in user, return the 10 most popular videos by view count
        if (!userID) {
            console.log('No user logged in, returning top 10 most popular videos');
            const popularVideos = await Video.find({ _id: { $ne: videoID } })  // Exclude the current video
                .sort({ views: -1 })
                .limit(10);  // Fetch top 10 most popular videos excluding the current video
            return res.status(200).json(popularVideos);
        }

        console.log('Notifying C++ server...');
        // Notify the C++ server with the current user and video ID
        const videoList = await notifyCppServer(userID, videoID);  // Forward user ID and video ID to C++ server
        console.log('Received video list from C++ server:', videoList);

        // Ensure videoList is iterable and contains individual video IDs
        if (Array.isArray(videoList)) {
            console.log('Processing video list...');
            // Fetch each recommended video by ID from the database
            for (let videoId of videoList) {
                // Ensure that the current video (videoID) is excluded
                if (videoId.trim() !== videoID) {
                    let video = await Video.findById(videoId.trim());  // Fetch video details from DB (ensure to trim the ID)
                    if (video) {
                        videosRec.add(video._id.toString());  // Add only the video ID to the set to ensure uniqueness
                    }
                }
            }
        } else {
            throw new Error('Received invalid data from C++ server');
        }

        console.log('Accessing video recommendations...');

        // Fetch the video details based on unique video IDs in the set, excluding the current video
        let videosArray = await Video.find({ _id: { $in: Array.from(videosRec), $ne: videoID } });

        // If the list contains more than 10 videos, return only the top 10 most popular videos
        if (videosArray.length > 10) {
            // Sort by view count in descending order and limit to 10
            videosArray = videosArray.sort((a, b) => b.views - a.views).slice(0, 10);
        }

        // Sort the videos by view count in descending order if the list has fewer than 10 videos
        videosArray.sort((a, b) => b.views - a.views);

        // If there are fewer than 6 recommended videos, add random videos to reach 6
        if (videosArray.length < 6) {
            const randomVideos = await Video.aggregate([
                { $match: { _id: { $ne: videoID, $nin: Array.from(videosRec) } } },  // Exclude current video and already recommended videos
                { $sample: { size: 7 - videosArray.length } }
            ]);  // Fetch random videos from DB excluding the current video

            // Filter out any duplicate video IDs before adding random videos to the final array
            const uniqueRandomVideos = randomVideos.filter(video => !videosRec.has(video._id.toString()));

            // Add random videos to the array, ensuring no duplicates
            videosArray = videosArray.concat(uniqueRandomVideos);
        }

        res.status(200).json(videosArray);  // Return the final sorted list of videos
    } catch (err) {
        console.error('Error fetching video recommendations:', err);
        res.status(404).send('Error fetching recommendations');
    }
}



const get20Videos = async (req, res) => {
    console.log("get20Videos: Start fetching videos");

    try {
        const combinedVideos = await videoService.getAllVideos();
      //  console.log("Fetched videos successfully:", JSON.stringify(combinedVideos, null, 2));
        res.json(combinedVideos);
    } catch (error) {
        console.error("Error fetching videos:", error);
        res.status(500).json({ message: "Error fetching videos", error: error.message });
    }

    console.log("get20Videos: End fetching videos");
};



// Get videos by user ID
async function getUserVideos(req, res) {
    try {
        const videos = await videoService.getUserVideos(req.params.id);
        res.status(200).json(videos);
    } catch (err) {
        res.status(500).send('Failed to fetch videos');
    }
}

async function addVideoController(req, res) {
    try {
      await videoService.addVideo(req, res);
    } catch (err) {
      console.error('Error in addVideo controller:', err.message);
      res.status(500).json({ error: 'Failed to add video' });
    }
  }

  

// Search videos by title
async function searchVideos(req, res) {
    try {
        const videos = await videoService.searchVideos(req.query.q);
        res.status(200).json(videos);
    } catch (err) {
        res.status(500).send('Failed to search videos');
    }
}


const updateVideo = async (req, res) => {
    try {
        const videoId = req.params.pid;
        console.log('Received video ID:', videoId);
        const { title } = req.body;

        if (!videoId) {
            console.log('Video ID is missing');
            return res.status(400).json({ message: 'Video ID is required' });
        }

        console.log('Updating video with ID:', videoId);
        console.log('New title:', title);

        const updatedVideo = await videoService.updateVideo(videoId, { title });
        if (!updatedVideo) {
            return res.status(404).json({ message: 'Video not found' });
        }

        res.json(updatedVideo);
    } catch (error) {
        console.error('Error updating video:', error);
        res.status(500).json({ message: 'Failed to update video' });
    }
};

const deleteVideo = async (req, res) => {
    try {
        const videoId = req.params.pid;
        console.log('Received video ID for deletion:', videoId);

        if (!videoId) {
            console.log('Video ID is missing');
            return res.status(400).json({ message: 'Video ID is required' });
        }

        const deletedVideo = await videoService.deleteVideo(videoId);
        if (!deletedVideo) {
            return res.status(404).json({ message: 'Video not found' });
        }

        res.json({ message: 'Video deleted successfully' });
    } catch (error) {
        console.error('Error deleting video:', error);
        res.status(500).json({ message: 'Failed to delete video' });
    }
};



// Get videos by category
async function getVideosByCategory(req, res) {
    try {
        const videos = await videoService.getVideosByCategory(req.query.category);
        res.status(200).json(videos);
    } catch (err) {
        res.status(500).send('Failed to fetch videos by category');
    }
}

// Middleware to check if user is logged in
async function requireLogin(req, res, next) {
    try {
        await videoService.requireLogin(req, res, next);
    } catch (err) {
        res.status(403).send('User not logged in');
    }
}

module.exports = {
    get20Videos,
    getUserVideos,
    getVideo,
    updateVideo,
    deleteVideo,
    searchVideos,
    getVideosByCategory,
    requireLogin,
    getRecommendedVideos,
    addVideoController
};

