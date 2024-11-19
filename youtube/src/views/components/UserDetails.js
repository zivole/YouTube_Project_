import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/UserDetails.css';
import NavigationBar from '../components/NavigationBar';
import OffCanvasMenu from '../components/OffCanvasMenu';

const UserDetails = ({ onLogout, isDarkMode, onToggleDarkMode, handleSearch, isAuthenticated, currentUser, addVideo }) => {
  const { username } = useParams();
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState({
    firstName: '',
    lastName: '',
    username: '',
    password: '',
    image: ''
  });
  const [originalUserDetails, setOriginalUserDetails] = useState({});
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [passwordError, setPasswordError] = useState(null);
  const [usernameError, setUsernameError] = useState(null);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/users?username=${username}`)
      .then(response => {
        if (response.data) {
          setUserDetails(response.data);
          setOriginalUserDetails(response.data); // Store the original details
          setLoading(false);
        } else {
          console.error('User not found');
        }
      })
      .catch(error => console.error('Error fetching user details:', error));
  }, [username]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserDetails({ ...userDetails, [name]: value });
  };

  const validateAndUpdateUser = () => {
    axios.post('http://localhost:8080/api/users/validate_password', { password: userDetails.password })
      .then(response => {
        if (response.data.valid) {
          setPasswordError(null);
          axios.patch(`http://localhost:8080/api/users/${userDetails._id}`, userDetails)
            .then(response => {
              setUserDetails(response.data);
              setOriginalUserDetails(response.data); // Update the original details
              setEditMode(false);
            })
            .catch(error => console.error('Error updating user details:', error));
        } else {
          setPasswordError('Password does not meet the requirements.');
        }
      })
      .catch(error => console.error('Error validating password:', error));
  };

  const handleUpdate = () => {
    if (userDetails.username !== originalUserDetails.username) {
      axios.post('http://localhost:8080/api/users/check-username', { username: userDetails.username, userId: userDetails._id })
        .then(response => {
          if (response.data.exists) {
            setUsernameError('Username already exists.');
          } else {
            setUsernameError(null);
            validateAndUpdateUser();
          }
        })
        .catch(error => console.error('Error checking username:', error));
    } else {
      setUsernameError(null);
      validateAndUpdateUser();
    }
  };

  const handleDelete = () => {
    axios.delete(`http://localhost:8080/api/users/${userDetails._id}`)
      .then(() => {
        onLogout(); 
        navigate('/'); 
      })
      .catch(error => console.error('Error deleting user:', error));
  };

  const handleShowModal = () => {
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleExitEditMode = () => {
    setEditMode(false);
    setPasswordError(null); 
    setUsernameError(null); 
    setUserDetails(originalUserDetails); 
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
        <div className={`App ${isDarkMode ? 'dark-mode' : ''}`}>
      <NavigationBar
        isDarkMode={isDarkMode}
        onToggleDarkMode={onToggleDarkMode}
        onSearch={handleSearch}
        isAuthenticated={isAuthenticated}
        onLogout={onLogout}
        currentUser={currentUser}
        addVideo={addVideo}
      />
      <OffCanvasMenu isDarkMode={isDarkMode} onToggleDarkMode={onToggleDarkMode} />
      <div className={`user-details-container ${isDarkMode ? 'dark-mode' : ''}`}>
        <div className={`user-details-card ${isDarkMode ? 'dark-mode' : ''}`}>
          <h2>{userDetails.firstName} {userDetails.lastName} details:</h2>
          <img src={userDetails.image} alt="User" className={`user-image ${isDarkMode ? 'dark-mode' : ''}`} />
          {editMode ? (
            <>
              <input 
                type="text" 
                name="firstName" 
                value={userDetails.firstName} 
                onChange={handleInputChange} 
                placeholder="First Name" 
                className={`form-control mb-2 ${isDarkMode ? 'dark-mode' : ''}`}
              />
              <input 
                type="text" 
                name="lastName" 
                value={userDetails.lastName} 
                onChange={handleInputChange} 
                placeholder="Last Name" 
                className={`form-control mb-2 ${isDarkMode ? 'dark-mode' : ''}`}
              />
              <input 
                type="text" 
                name="username" 
                value={userDetails.username} 
                onChange={handleInputChange} 
                placeholder="Username" 
                className={`form-control mb-2 ${isDarkMode ? 'dark-mode' : ''}`}
              />
              {usernameError && <div className="alert alert-danger">{usernameError}</div>}
              <input 
                type="password" 
                name="password" 
                value={userDetails.password} 
                onChange={handleInputChange} 
                placeholder="Password" 
                className={`form-control mb-2 ${isDarkMode ? 'dark-mode' : ''}`}
              />
              {passwordError && <div className="alert alert-danger">{passwordError}</div>}
              <div className="buttons-container">
                <button className={`btn btn-outline-danger ${isDarkMode ? 'dark-mode' : ''}`} onClick={handleUpdate}>Save</button>
                <button className={`btn btn-outline-danger ${isDarkMode ? 'dark-mode' : ''}`} onClick={handleShowModal}>Delete</button>
                <button className={`btn btn-outline-danger ${isDarkMode ? 'dark-mode' : ''}`} onClick={handleExitEditMode}>Cancel</button>
              </div>
            </>
          ) : (
            <>
              <p className={isDarkMode ? 'dark-mode' : ''}><strong>First Name:</strong> {userDetails.firstName}</p>
              <p className={isDarkMode ? 'dark-mode' : ''}><strong>Last Name:</strong> {userDetails.lastName}</p>
              <p className={isDarkMode ? 'dark-mode' : ''}><strong>Username:</strong> {userDetails.username}</p>
              <div className="buttons-container">
                <button className={`btn btn-outline-danger ${isDarkMode ? 'dark-mode' : ''}`} onClick={() => setEditMode(true)}>Edit</button>
                <button className={`btn btn-outline-danger ${isDarkMode ? 'dark-mode' : ''}`} onClick={handleShowModal}>Delete</button>
              </div>
            </>
          )}
        </div>

        <div className={`modal fade ${showModal ? 'show' : ''}`} style={{ display: showModal ? 'block' : 'none' }} tabIndex="-1" role="dialog">
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Confirm Deletion</h5>
                <button type="button" className="btn-close" onClick={handleCloseModal} aria-label="Close"></button>
              </div>
              <div className="modal-body">
                <p>Are you sure you want to delete this user?</p>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-danger" onClick={handleDelete}>Delete</button>
                <button type="button" className="btn btn-secondary" onClick={handleCloseModal}>Close</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserDetails;
