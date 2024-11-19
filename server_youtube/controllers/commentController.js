const Comment = require('../models/commentModel');
const Video = require('../models/videoModel');
const User = require('../models/userModel');
const { getUsernameFromToken } = require('./tokenController');
const commentService = require('../services/commentServices');

const authenticate = async (req) => {
    const token = req.header('Authorization').replace('Bearer ', '');
    const username = await getUsernameFromToken(token);

    if (!username) {
        throw new Error('Please authenticate');
    }

    const user = await User.findOne({ username });

    if (!user) {
        throw new Error('Please authenticate');
    }

    return user;
};

const createComment = async (req, res) => {
    try {
        const user = await authenticate(req);
        const { content, videoId } = req.body;
        const newComment = await commentService.createComment({ content, userId: user._id, videoId });

        res.status(201).json(newComment);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

const getCommentsByVideoId = async (req, res) => {
    try {
        const { videoId } = req.query;
        const comments = await Comment.find({ videoId }).populate('userId', 'username');
        res.status(200).json(comments);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

const updateComment = async (req, res) => {
    try {
        const user = await authenticate(req);
        const { id } = req.params;
        const updatedComment = await commentService.updateCommentById(id, req.body, user._id);
        res.status(200).json(updatedComment);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

const deleteComment = async (req, res) => {
    try {
        const user = await authenticate(req);
        const { id } = req.params;
        await commentService.deleteCommentById(id, user._id);
        res.status(200).json({ message: 'Comment deleted' });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

module.exports = {
    createComment,
    getCommentsByVideoId,
    updateComment,
    deleteComment,
};
