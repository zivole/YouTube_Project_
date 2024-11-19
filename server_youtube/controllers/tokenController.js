const User = require('../models/userModel');
const jwt = require('jsonwebtoken');


const createToken = async (req, res) => {
  const { username } = req.body; 
  try {
    console.log("this is the token service")
    console.log(username)
    const userAccount= await User.findOne({ username });
    console.log("********")
    console.log(userAccount)
    if(userAccount == null){
      console.log("could not find user with username: " + username)
        return { success: false, token:'' };
    }
      const token = jwt.sign({ username: username }, 'SecretKey');
      console.log(JSON.stringify({ success: true, token: token }))
      result = { success: "true", token: token };
      res.status(201).json(result);
  } catch (error) {
      res.status(400).json({ message: error.message });
  }
};





const getUserFromToken = async (req, res) => {
  try {
    // Extract the token from the Authorization header
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ message: 'No token provided' });
    }

    const token = authHeader.split(' ')[1];

    // Verify and decode the token
    const decoded = jwt.verify(token, 'SecretKey');

    // Extract the username from the decoded token
    const username = decoded.username;

    // Find the user in the database
    const user = await User.findOne({ username });

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    return res.status(200).json(user);
  } catch (error) {
    console.error('Error decoding token:', error.message);
    return res.status(500).json({ message: 'Failed to decode token' });
  }
};


async function getUsernameFromToken(token) {
  try {
    // Verify and decode the token
    const decoded = jwt.verify(token, 'SecretKey');

    // Extract the username from the decoded token
    const username = decoded.username;

    return username;
  } catch (error) {
    console.error('Error decoding token:', error.message);
    return '';
  }
}


  module.exports = { createToken, getUserFromToken, getUsernameFromToken};


