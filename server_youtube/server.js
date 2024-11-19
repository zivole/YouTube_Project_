const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path');

//const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const tokenRoutes = require('./routes/tokenRoutes');
const videoRoutes = require('./routes/videoRoutes');
const commentRoutes = require('./routes/commentRoutes');
const {initializeVideos, initializeUsers, initializeComments} = require('./services/mongoScript');
const app = express();
const PORT = 8080;

app.use(cors());
app.use(bodyParser.json({ limit: '100000mb' }));


mongoose.connect('mongodb://localhost:27017/YoutubeDB', {
  useNewUrlParser: true,
  useUnifiedTopology: true,
  serverSelectionTimeoutMS: 5000, // Keep trying to send operations for 5 seconds
  socketTimeoutMS: 45000, // Close sockets after 45 seconds of inactivity
})
  .then(() => console.log('MongoDB connected...'))
  .catch(err => console.error('MongoDB connection error:', err.message));

// Serve static files from the 'uploads' directory
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

app.use('/api/users', userRoutes);
app.use('/api/videos', videoRoutes);
app.use('/api/tokens', tokenRoutes);
app.use('/api/comments', commentRoutes);


let flag = true;
if(flag){
    console.log("flag", flag);
    initializeUsers(); 
    initializeVideos();
    initializeComments();
    !flag;
}

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});