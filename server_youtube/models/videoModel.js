const mongoose = require('mongoose');

const videoSchema = new mongoose.Schema({
    // id: { type: Number, required: true },
    title: { type: String, required: true },
    thumbnail: { type: String, required: true },
    publisher: { type: String, required: true },
    views: { type: String, required: true, default: '0' },
    publishedDate: { type: String, required: true },
    path: { type: String, required: true },
    userImage: { type: String, required: true },
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    comments: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Comment' }],
});

module.exports = mongoose.model('Video', videoSchema);