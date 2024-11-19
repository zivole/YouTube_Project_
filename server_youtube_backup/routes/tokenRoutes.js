const express = require('express');
const router = express.Router();
const { createToken } = require('../controllers/tokenController');

router.post('/', createToken);
//router.post('/verify', verifyToken);

module.exports = router;
