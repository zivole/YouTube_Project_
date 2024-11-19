const express = require('express');
const { createComment, getCommentsByVideoId, updateComment, deleteComment } = require('../controllers/commentController');

const router = express.Router();

router.post('/', createComment);
router.get('/', getCommentsByVideoId);
router.put('/:id', updateComment);
router.delete('/:id', deleteComment);

module.exports = router;
