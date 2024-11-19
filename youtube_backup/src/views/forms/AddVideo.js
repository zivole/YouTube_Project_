import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/AddVideo.css';
import logo from '../assets/YouTube_Logo_2017.svg';
import axios from 'axios'; // Import axios


const AddVideo = ({ currentUser }) => {
    const [videoName, setVideoName] = useState('');
    const [file, setFile] = useState(null);
    const [thumbnail, setThumbnail] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    useEffect(() => {
        console.log("Current User in AddVideo.js:", currentUser);
    }, [currentUser]);

    useEffect(() => {
        console.log("Token in AddVideo.js:", token);
    }, [token]);

    useEffect(() => {
        if (success) {
            console.log('Navigating to home page...');
            const timer = setTimeout(() => {
                setSuccess(false);
                navigate('/', { state: { currentUser } });
            }, 500);
            return () => clearTimeout(timer);
        }
    }, [success, navigate, currentUser]);


const handleSubmit = async (e) => {
e.preventDefault();
console.log('Video Name:', videoName);
console.log('File:', file);

if (!videoName || !file) {
    setError('Both fields are required.');
    return;
}
if (!file.type.startsWith('video/')) {
    setError('Please upload a video file.');
    return;
}
if (!currentUser) {
    setError('User is not logged in.');
    return;
}
setError('');

const videoElement = document.createElement('video');
videoElement.src = URL.createObjectURL(file);

videoElement.onloadeddata = () => {
    videoElement.currentTime = 5;

    videoElement.onseeked = async () => {
        const canvas = document.createElement('canvas');
        const aspectRatio = videoElement.videoWidth / videoElement.videoHeight;
        const thumbnailWidth = 320;
        const thumbnailHeight = thumbnailWidth / aspectRatio;

        canvas.width = thumbnailWidth;
        canvas.height = thumbnailHeight;

        const ctx = canvas.getContext('2d');
        ctx.drawImage(videoElement, 0, 0, canvas.width, canvas.height);

        const thumbnailDataUrl = canvas.toDataURL('image/png', 0.8);
        setThumbnail(thumbnailDataUrl);
        console.log("Thumbnail created:", thumbnailDataUrl); // Debugging line

        const formData = new FormData();
        formData.append('title', videoName);
        formData.append('file', file);
        formData.append('thumbnail', thumbnailDataUrl); // Append the base64 string
        formData.append('publishedDate', new Date().toISOString());
        formData.append('username', currentUser);
        formData.append('userToken', token);

        try {
            await axios.post('http://localhost:8080/api/videos/add', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${token}`,
                },
            });

            setSuccess(true);
        } catch (err) {
            setError('Error uploading video.');
            console.error(err);
        } finally {
            setIsSubmitting(false);
        }
    };
};
}
  
    return (
        <div className="add-video-container">
            <div className="add-video-card">
                <div className="add-video-card-body">
                    <div className="add-video-card-title">
                        <img src={logo} alt="logo" className="header_logoImage" />
                    </div>
                    {error && <div className="alert alert-danger">{error}</div>}
                    {success && <div className="alert alert-success">Video uploaded successfully!</div>}
                    <form onSubmit={handleSubmit}>
                        <div className="input-group add-video-input-group">
                            <label htmlFor="video-name">Video Name</label>
                            <input
                                type="text"
                                id="video-name"
                                name="video-name"
                                placeholder="Enter video name"
                                value={videoName}
                                onChange={(e) => setVideoName(e.target.value)}
                            />
                        </div>
                        <div className="input-group add-video-input-group">
                            <div className="file-input-wrapper add-video-file-input-wrapper">
                                <input
                                    type="file"
                                    id="file-input"
                                    name="file"
                                    onChange={(e) => {
                                        setFile(e.target.files[0]);
                                        console.log('Selected file:', e.target.files[0]);
                                      }}
                                />
                            </div>
                        </div>
                        <button type="submit" className="btn btn-primary add-video-btn" disabled={isSubmitting}>
                            {isSubmitting ? 'Uploading...' : 'Add New Video'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AddVideo;
