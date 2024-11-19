const mongoose = require('mongoose');
const User = require('../models/userModel');
const userServices = require('../services/userServices');
const { isPasswordValid } = require('../services/userServices');
const Video = require('../models/videoModel');



const getUser = async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    res.json(user);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

const updateUser = async (req, res) => {
  try {
    const user = await User.findByIdAndUpdate(req.params.id, req.body, { new: true });
    res.json(user);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

const deleteUser = async (req, res) => {
  try {
    await User.findByIdAndDelete(req.params.id);
    res.json({ message: 'User deleted' });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

const signUp = async (req, res) => {
    const { firstName, lastName, username, password, confirmPassword, imageView } = req.body;
    try {
      const result = await userServices.signUp(firstName, lastName, username, password, confirmPassword, imageView);
      //console.log('Sign in result:', result);
      res.status(201).json(result);
    } catch (error) {
      console.error('Sign in error:', error.message); 
      res.status(400).json({ message: error.message });
    }
  };

  const signIn = async (req, res) => {
    const { username, password } = req.body; 
    try {
        const result = await userServices.signIn(username, password);
        res.status(201).json(result);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};



const getUserByUsername = async (req, res) => {
  const username = req.query.username;
  try {
      const user = await User.findOne({ username });
      if (!user) {
          return res.status(404).json({ message: 'User not found' });
      }
      res.status(200).json(user);
  } catch (error) {
      res.status(500).json({ message: 'Server error', error });
  }
};

const getUserByUsernameImage = async (req, res) => {
  try {
    console.log('getUserByUsernameImage function called');
    const { publisher } = req.query;

    if (!publisher) {
      console.error('Publisher is required but not provided');
      return res.status(400).json({ error: 'Publisher is required' });
    }

    console.log('Searching for user with username:', publisher);
    const user = await User.findOne({ username: publisher });
    if (!user) {
      console.error(`User not found for publisher: ${publisher}`);
      return res.status(404).json({ error: 'User not found' });
    }

    console.log('User found:', user);
    res.json(user);
  } catch (error) {
    console.error('Error fetching user details:', error.message);
    res.status(500).json({ error: 'Internal Server Error', message: error.message });
  }
};


  const validatePassword = (req, res) => {
    const { password } = req.body;
    if (isPasswordValid(password)) {
      res.json({ valid: true });
    } else {
      res.json({ valid: false });
    }
  };

  const checkUsernameExists = async (req, res) => {
    try {
      const { username, userId } = req.body;
      const user = await User.findOne({ username });
  
      if (user && user._id.toString() !== userId) {
        return res.status(200).json({ exists: true });
      }
      return res.status(200).json({ exists: false });
    } catch (err) {
      res.status(500).json({ message: err.message });
    }
  };


  
  module.exports = {
    signUp,
    signIn,
    getUser,
    updateUser,
    deleteUser,
    validatePassword,
    checkUsernameExists,
    getUserByUsername,
    getUserByUsernameImage
  };
  
