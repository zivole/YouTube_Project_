// VideoGrid.js
import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/VideoGrid.css';
import VideoCard from './VideoCard';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const VideoGrid = ({ searchTerm, category, isDarkMode }) => {
  const [videos, setVideos] = useState({ popularVideos: [], randomVideos: [] });
  const [filteredVideos, setFilteredVideos] = useState([]);

  useEffect(() => {
    const fetchVideos = async () => {
      try {
        console.log('Fetching videos from API...');
        const response = await axios.get(`${API_URL}/videos`);
        console.log('Fetched videos:', response.data);
        setVideos(response.data);
      } catch (error) {
        console.error('Error fetching videos:', error);
      }
    };

    fetchVideos();
  }, []);

  useEffect(() => {
    const filterVideos = () => {
      let allVideos = [...videos.popularVideos, ...videos.randomVideos];
      if (category !== 'All') {
        allVideos = allVideos.filter(video => video.category === category);
      }
      if (searchTerm) {
        allVideos = allVideos.filter(video => video.title.toLowerCase().includes(searchTerm.toLowerCase()));
      }
      setFilteredVideos(allVideos);
    };

    filterVideos();
  }, [searchTerm, category, videos]);


  return (
    <div className={`videoGrid over-container d-flex p-3 mx-auto flex-column ${isDarkMode ? 'dark-mode' : ''}`}>
      <div className="row g-4">
        {filteredVideos.length > 0 ? (
          filteredVideos.map((video, index) => (
            <div className="col-12 col-md-6 col-lg-4" key={index}>
              <VideoCard video={video} isDarkMode={isDarkMode} />
            </div>
          ))
        ) : (
          <div className="col-12">
            <p>Loading...</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default VideoGrid;