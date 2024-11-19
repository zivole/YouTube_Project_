const Comment = require('../models/commentModel');
const Video = require('../models/videoModel');
const User = require('../models/userModel');

async function createComment(commentData) {
    const { content, userId, videoId } = commentData;

    const video = await Video.findById(videoId);
    if (!video) {
        throw new Error('Video not found');
    }

    const comment = new Comment({ content, userId, videoId: video._id });
    await comment.save();

    // Add the comment to the related video
    await Video.findByIdAndUpdate(comment.videoId, { $push: { comments: comment._id } });

    // Populate the userId to include the username
    const populatedComment = await Comment.findById(comment._id).populate('userId', 'username');

    return populatedComment;
}

async function getAllComments() {
    return await Comment.find().populate('userId', 'username').populate('videoId', 'title');
}

async function getCommentsByVideoId(videoId) {
    return await Comment.find({ videoId }).populate('userId', 'username');
}

async function updateCommentById(commentId, updateData, userId) {
    const comment = await Comment.findById(commentId);

    if (!comment) {
        throw new Error('Comment not found');
    }

    if (comment.userId.toString() !== userId.toString()) {
        throw new Error('Unauthorized action');
    }

    Object.assign(comment, updateData);
    await comment.save();

    // Populate the userId to include the username
    const populatedComment = await Comment.findById(comment._id).populate('userId', 'username');

    return populatedComment;
}

async function deleteCommentById(commentId, userId) {
    const comment = await Comment.findById(commentId);

    if (!comment) {
        throw new Error('Comment not found');
    }

    if (comment.userId.toString() !== userId.toString()) {
        throw new Error('Unauthorized action');
    }

    await Comment.deleteOne({ _id: commentId });

    // Remove the comment from the related video
    await Video.findByIdAndUpdate(comment.videoId, { $pull: { comments: comment._id } });
}

module.exports = {
    createComment,
    getAllComments,
    getCommentsByVideoId,
    updateCommentById,
    deleteCommentById,
};
