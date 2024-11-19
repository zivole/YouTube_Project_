
const express = require('express');

const {
    get20Videos,
    getUserVideos,
    addVideoController,
    getVideo,
    deleteVideo,
    searchVideos,
    getVideosByCategory,
    requireLogin,
    updateVideo,
    getRecommendedVideos
} = require('../controllers/videoController');
const { upload } = require('../services/videoServices');

const router = express.Router();
  
router.get('/', get20Videos);
router.get('/:pid', getVideo);
router.get('/:pid/recommended', getRecommendedVideos);
router.patch('/:pid', requireLogin, updateVideo);
router.delete('/:pid', requireLogin, deleteVideo);
router.post('/add', requireLogin, upload.fields([
    { name: 'file', maxCount: 1 },
    { name: 'thumbnail', maxCount: 1 }
     ]), addVideoController);
router.get('/search', searchVideos);
router.get('/category', getVideosByCategory);
router.get('/users/:id/videos', getUserVideos);

module.exports = router;
