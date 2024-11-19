// VideoCard.js
import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/VideoCard.css';
import { timeSince, formatViews } from '../../utils/videoUtils';

const VideoCard = ({ video }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { isDarkModeProp } = location.state || {}; 
  const [isDarkMode, setIsDarkMode] = useState(isDarkModeProp);

  const handleUserClick = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/users/username', {
        params: { publisher: video.publisher }
      });
      const user = response.data;
      navigate(`/users/${user._id}/videos`, { state: { user } });
    } catch (error) {
      console.error('Error fetching user details:', error);
    }
  };

  const handleClick = () => {
    navigate(`/videos/${video._id}`, { state: { mainVideo: video, isDarkMode: isDarkModeProp } });
  };

  return (
    <div className={`card h-100 ${isDarkMode ? 'dark-mode' : ''}`} style={{ cursor: 'pointer' }}>
      <img src={video.thumbnail} onClick={handleClick} className="card-img-top" alt={video.title} />
      <div className="card-body">
        <div className="d-flex align-items-center mb-2">
          <img 
            src={video.userImage} 
            onClick={handleUserClick} 
            width="60"
            height="60"
            className="rounded-circle"
            style={{ marginRight: '15px' }} 
            alt="User"
          /> 
          <div>
            <h5 className="card-title" onClick={handleClick}>{video.title}</h5>
            <p className="card-text" onClick={handleClick}>{video.publisher}</p>
            <p className="card-text" onClick={handleClick}>
              {formatViews(video.views)} views • {timeSince(new Date(video.publishedDate))}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default VideoCard;
