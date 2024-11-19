


import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useLocation } from 'react-router-dom';
import '../styles/UserManagement.css';
import NavigationBar from '../components/NavigationBar';
import OffCanvasMenu from '../components/OffCanvasMenu';
import VideoGridUser from '../components/videoGridUser'; // Ensure this matches the file name exactly

const UserManagement = ({ isDarkMode, handleToggleDarkMode, handleSearch, isAuthenticated, currentUser, addVideo, onLogout }) => {
  const { id } = useParams();
  const location = useLocation();
  const { user } = location.state || {};
  const { isDarkModeProp } = location.state || {};
  const [isDarkModeLocal, setIsDarkModeLocal] = useState(isDarkModeProp);
  const [userDetails, setUserDetails] = useState(user);
  const [loading, setLoading] = useState(!user);
  const [filteredVideos, setFilteredVideos] = useState([]);
  
  useEffect(() => {
    if (user) {
      console.log('User details from state:', user);
      const fetchUserVideos = async () => {
        try {
          const videosResponse = await axios.get(`http://localhost:8080/api/users/${user._id}/videos`);
          console.log('Fetched videos:', videosResponse.data);
          setFilteredVideos(videosResponse.data);
        } catch (error) {
          console.error('Error fetching user videos:', error);
        } finally {
          setLoading(false);
        }
      };

      fetchUserVideos();
    }
  }, [id, user]);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className={`App ${isDarkModeLocal ? 'dark-mode' : ''}`}>
      <NavigationBar
        isDarkMode={isDarkModeLocal}
        onToggleDarkMode={handleToggleDarkMode}
        onSearch={handleSearch}
        isAuthenticated={isAuthenticated}
        onLogout={onLogout}
        currentUser={currentUser}
        addVideo={addVideo}
      />
      <OffCanvasMenu isDarkMode={isDarkModeLocal} onToggleDarkMode={handleToggleDarkMode} />
      <div className="user-managment-container container-fluid">
        <div className="row text-center my-4">
          <div className="col-12">
            <img src={userDetails.image} alt="User" className="user-image" />
            <h2>{userDetails.firstName} {userDetails.lastName} videos:</h2>
          </div>
        </div>
        <div className="row">
          <div className="col">
            <VideoGridUser
              isDarkMode={isDarkModeLocal}
              videos={filteredVideos}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;
