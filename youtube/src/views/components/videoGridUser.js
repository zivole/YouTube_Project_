import React from 'react';
import VideoCard from '../components/VideoCard';
import '../styles/VideoGrid.css';

const VideoGridUser = ({ isDarkMode, videos }) => {
  return (
    <div className={`videoGrid over-container d-flex p-3 mx-auto flex-column ${isDarkMode ? 'dark-mode' : ''}`}>
      <div className="row g-4">
        {videos.length > 0 ? (
          videos.map((video, index) => (
            <div className="col-12 col-md-6 col-lg-4" key={index}>
              <VideoCard video={video} isDarkMode={isDarkMode} />
            </div>
          ))
        ) : (
          <div className="col-12">
            <p>No videos found.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default VideoGridUser;

