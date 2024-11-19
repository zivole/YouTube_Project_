// const express = require('express');
// const {validatePassword, getUser, updateUser,getUserByUsernameImage ,getUserVideosById, deleteUser,signUp, signIn, checkUsernameExists, getUserByUsername } = require('../controllers/userController');
// const {getUserFromToken} = require('../controllers/tokenController');
// const router = express.Router();



// router.post('/signup', signUp); 
// router.post('/signin', signIn); // Route for sign-in
// router.post('/validate_password', validatePassword);
// router.get('/', getUserByUsername);
// router.get('/user_by_token', getUserFromToken);
// router.get('/:id', getUser);
// router.patch('/:id', updateUser);
// router.delete('/:id', deleteUser);
// router.post('/check-username', checkUsernameExists);

// // Route to get user details by username
// router.get('/username', getUserByUsernameImage);

// // Route to get user videos by user ID
// router.get('/:id/videos', getUserVideosById);


// module.exports = router;


const express = require('express');
const {
  validatePassword,
  getUser,
  updateUser,
  getUserByUsernameImage,
  getUserVideosById,
  deleteUser,
  signUp,
  signIn,
  checkUsernameExists,
  getUserByUsername
} = require('../controllers/userController');
const { getUserFromToken } = require('../controllers/tokenController');
const { getUserVideos } = require('../controllers/videoController');
const router = express.Router();

router.post('/signup', signUp);
router.post('/signin', signIn);
router.post('/validate_password', validatePassword);
router.get('/', getUserByUsername);
router.get('/user_by_token', getUserFromToken);
router.post('/check-username', checkUsernameExists);

// Route to get user details by username
router.get('/username', getUserByUsernameImage);
router.get('/:id/videos', getUserVideos);
// More general routes should be placed after more specific ones
router.get('/:id', getUser);
router.patch('/:id', updateUser);
router.delete('/:id', deleteUser);

module.exports = router;
