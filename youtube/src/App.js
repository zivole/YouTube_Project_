

import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import HomePage from './views/forms/HomePage';
import LoginForm from './views/forms/LoginForm';
import SignUpForm from './views/forms/SignUpForm';
import AddVideoForm from './views/forms/AddVideo';
import PlayVideo from './views/forms/play_video';
import UserManagement from './views/components/UserManagement'; 
import UserDetails from './views/components/UserDetails';
import usersData from './data/users.json';
import './App.css'; // Import the main CSS file
import axios from 'axios';

const App = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [users, setUsers] = useState(usersData.users);
  const [currentUser, setCurrentUser] = useState(null);
  const [videos, setVideos] = useState([]);
  const [isDarkMode, setIsDarkMode] = useState(false);

  const handleToggleDarkMode = () => {
    setIsDarkMode(prevMode => !prevMode);
  };
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // Assume the user is authenticated if a token is found
      setIsAuthenticated(true);
      // Fetch user details using the token if necessary
      fetchCurrentUser(token);
    }
  }, []);

  const fetchCurrentUser = async (token) => {
    try {
      const response = await fetch('http://localhost:8080/api/users/user_by_token', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch user details');
      }

      const data = await response.json();
      setCurrentUser(data);
      console.log("date: ", data);
    } catch (error) {
      console.error('Error fetching current user:', error);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      // Assume the user is authenticated if a token is found
      setIsAuthenticated(true);
      // Fetch user details using the token if necessary
      fetchCurrentUser(token);
    }
  }, []);

  useEffect(() => {
    console.log("Current User in App.js:", currentUser); // Debugging line
}, [currentUser]);

  const handleLogin = async (user) => {
    setCurrentUser(user);
    setIsAuthenticated(true);
    //localStorage.setItem('token', user.token); // Save the token for authenticated requests
  };

  const handleLogout = async () => {
    setCurrentUser(null);
    setIsAuthenticated(false);
    localStorage.removeItem('token'); // Remove the token on logout
  };

  // In UserManagement.js
async function fetchUserVideos(userId) {
  try {
      const response = await axios.get(`http://localhost:8080/api/users/${userId}/videos`);
      return response.data;
  } catch (error) {
      console.error('Error fetching user videos:', error);
      throw error;
  }
};

  const addVideo = async (newVideo) => {
    try {
      const token = localStorage.getItem('token'); // Get the token from local storage
      const formData = new FormData();
      formData.append('title', newVideo.title);
      formData.append('video', newVideo.file);
      formData.append('thumbnail', newVideo.thumbnail);

      const response = await fetch('http://localhost:8080/api/videos/add', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      if (!response.ok) {
        throw new Error('Failed to add video');
      }

      const data = await response.json();
      setVideos((prevVideos) => [...prevVideos, data]);
    } catch (error) {
      console.error('Error adding video:', error);
    }
  };
  
  const removeVideo = async (videoId) => {
    try {
      const token = localStorage.getItem('token'); // Get the token from local storage
      const response = await fetch(`http://localhost:8080/api/videos/${videoId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to delete video');
      }

      setVideos((prevVideos) => prevVideos.filter(video => video._id !== videoId));
    } catch (error) {
      console.error('Error deleting video:', error);
    }
  };

  const editVideoTitle = async (videoId, newTitle) => {
    try {
      const token = localStorage.getItem('token'); // Get the token from local storage
      const response = await fetch(`http://localhost:8080/api/videos/${videoId}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ title: newTitle })
      });

      if (!response.ok) {
        throw new Error('Failed to update video title');
      }

      setVideos((prevVideos) =>
        prevVideos.map((video) =>
          video._id === videoId ? { ...video, title: newTitle } : video
        )
      );
    } catch (error) {
      console.error('Error updating video title:', error);
    }
  };

  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={<HomePage
            isAuthenticated={isAuthenticated}
            onLogout={handleLogout}
            currentUser={currentUser}
            users={users}
            addVideo={addVideo}
            videos={videos}
            isDarkMode={isDarkMode}
            onToggleDarkMode={() => setIsDarkMode(!isDarkMode)}
            onSearch={() => { /* Implement search functionality here */ }}
          />}
        />
        <Route
          path="/add"
          element={<AddVideoForm currentUser={currentUser} addVideo={addVideo} />}
        />
        <Route
          path="/login"
          element={<LoginForm onLogin={handleLogin} users={users} />}
        />
        <Route
          path="/signup"
          element={<SignUpForm users={users} setUsers={setUsers} />}
        />
        <Route
          path="/videos/:id"
          element={<PlayVideo
            isAuthenticated={isAuthenticated}
            onLogout={handleLogout}
            currentUser={currentUser}
            users={users}
            addVideo={addVideo}
            videos={videos}
            isDarkMode={isDarkMode}
            onToggleDarkMode={() => setIsDarkMode(!isDarkMode)}
            onSearch={() => { /* Implement search functionality here */ }}
            removeVideo={removeVideo}
          />}
        />
         <Route 
            path="/user/:username" 
             element={
              <UserDetails
              isAuthenticated={isAuthenticated}
              onLogout={handleLogout}
              currentUser={currentUser}
              users={users}
              addVideo={addVideo}
              videos={videos}
              isDarkMode={isDarkMode}
              onToggleDarkMode={() => setIsDarkMode(!isDarkMode)}
              onSearch={() => { /* Implement search functionality here */ }}
            />
          }
        />
        <Route
          path="/users/:id/videos"
          element={<UserManagement
            isAuthenticated={isAuthenticated}
            onLogout={handleLogout}
            currentUser={currentUser}
            users={users}
            addVideo={addVideo}
            videos={videos}
            isDarkMode={isDarkMode}
            onSearch={() => { /* Implement search functionality here */ }}
            removeVideo={removeVideo}
            editVideoTitle={editVideoTitle}
            handleToggleDarkMode={handleToggleDarkMode}
            handleSearch={() => {}}
          />}
        />
      </Routes>
    </Router>
  );
};

export default App;


