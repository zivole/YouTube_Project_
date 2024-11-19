import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import NavigationBar from '../components/NavigationBar';
import LeftSideBar from '../components/LeftSideBar';
import CategoryButtons from '../components/CategoryButtons';
import VideoGrid from '../components/VideoGrid';
import OffCanvasMenu from '../components/OffCanvasMenu';
import axios from 'axios';
import '../../App.css';

const HomePage = ({ isAuthenticated, onLogout, currentUser, users, videos, onToggleDarkMode }) => {
  const location = useLocation();
  const { isDarkModeProp } = location.state || {};
  const [isDarkMode, setIsDarkMode] = useState(isDarkModeProp);
  const [searchTerm, setSearchTerm] = useState('');
  const [category, setCategory] = useState('All');
  const [filteredVideos, setFilteredVideos] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const filterVideos = () => {
      let filtered = videos;
      if (category !== 'All') {
        filtered = filtered.filter(video => video.category === category);
      }
      if (searchTerm) {
        filtered = filtered.filter(video => video.title.toLowerCase().includes(searchTerm.toLowerCase()));
      }
      setFilteredVideos(filtered);
    };

    filterVideos();
  }, [searchTerm, category, videos]);

  const handleToggleDarkMode = (isDarkMode) => {
    setIsDarkMode(isDarkMode);
    onToggleDarkMode(isDarkMode);
  };

  const handleSearch = async (query) => {
    setSearchTerm(query);
    try {
      const response = await axios.get(`http://localhost:8080/api/videos/search?q=${query}`);
      setFilteredVideos(response.data);
    } catch (error) {
      console.error('Error searching videos:', error);
    }
  };

  const handleCategoryChange = (category) => {
    setCategory(category);
  };

  const handleVideoClick = (id) => {
    navigate(`/videos/${id}`);
  };

  return (
    <div className={`App ${isDarkMode ? 'dark-mode' : ''}`}>
      <NavigationBar
        isDarkMode={isDarkMode}
        onToggleDarkMode={handleToggleDarkMode}
        onSearch={handleSearch}
        isAuthenticated={isAuthenticated}
        onLogout={onLogout}
        currentUser={currentUser}
      />
      <OffCanvasMenu isDarkMode={isDarkMode} onToggleDarkMode={handleToggleDarkMode} />
      <div className="container-fluid">
        <div className="row">
          <CategoryButtons onCategoryChange={handleCategoryChange} />
        </div>
        <div className="row">
          <LeftSideBar />
          <div className="col">
            <VideoGrid
              isDarkMode={isDarkMode}
              searchTerm={searchTerm}
              category={category}
              videos={filteredVideos}
              users={users}
              onVideoClick={handleVideoClick}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
