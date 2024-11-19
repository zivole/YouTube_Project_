import React, { useState, useEffect } from 'react';
import '../styles/play_video.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { FaThumbsUp, FaThumbsDown, FaShare, FaEdit, FaSave, FaTrash, FaComment, FaBell } from 'react-icons/fa';
import { useNavigate, useParams } from 'react-router-dom';
import Comment from './comment';
import { timeSince , formatViews } from '../../utils/videoUtils';
import axios from 'axios';

const MainVideoSection = ({ removeVideo, comments, newComment, setNewComment, addComment, deleteComment, editComment, likeMessage, likedClick, unLikedClick, subscribe, setShowModal, currentUser, onToggleDarkMode }) => {
    const [video, setVideo] = useState(null);
    const [videoURL, setVideoURL] = useState('');
    const [userImageURL, setUserImageURL] = useState('');
    const [isEditing, setIsEditing] = useState(false);
    const [editedTitle, setEditedTitle] = useState('');
    const [editedPublisher, setEditedPublisher] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { id } = useParams();
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    // useEffect(() => {
    //     const fetchVideo = async () => {
    //         try {
    //             console.log(`Fetching video with ID: ${id}`);
    //             const response = await fetch(`http://localhost:8080/api/videos/${id}`);
    //             if (!response.ok) {
    //                 throw new Error('Video not found');
    //             }

    //             const text = await response.text();
    //             if (!text) {
    //                 throw new Error('Empty response');
    //             }

    //             const videoData = JSON.parse(text);
    //             if (!videoData) {
    //                 throw new Error('Invalid video data');
    //             }

    //             console.log('Fetched video data:', videoData);

    //             setVideo(videoData);
    //             setEditedTitle(videoData.title);
    //             setEditedPublisher(videoData.publisher);

    //             setVideoURL(`http://localhost:8080/${videoData.path}`);
    //             setUserImageURL(videoData.userImage);
    //         } catch (error) {
    //             console.error("Error fetching video:", error);
    //             setError(error.message);
    //         } finally {
    //             setLoading(false);
    //         }
    //     };

    //     fetchVideo();
    // }, [id]);

    useEffect(() => {
        const fetchVideo = async () => {
            try {
                console.log(`Fetching video with ID: ${id}`);
                
                // Include the currentUser details in the headers
                const response = await fetch(`http://localhost:8080/api/videos/${id}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`, // Assuming you're using token for authentication
                        'Content-Type': 'application/json',
                        'X-User-ID': currentUser?._id,       // Custom header with currentUser._id
                        'X-Username': currentUser?.username, // Custom header with currentUser.username
                    },
                });
    
                if (!response.ok) {
                    throw new Error('Video not found');
                }
    
                const text = await response.text();
                if (!text) {
                    throw new Error('Empty response');
                }
    
                const videoData = JSON.parse(text);
                if (!videoData) {
                    throw new Error('Invalid video data');
                }
    
                console.log('Fetched video data:', videoData);
                setVideo(videoData);
                setEditedTitle(videoData.title);
                setEditedPublisher(videoData.publisher);
    
                setVideoURL(`http://localhost:8080/${videoData.path}`);
                setUserImageURL(videoData.userImage);
            } catch (error) {
                console.error("Error fetching video:", error);
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };
    
        fetchVideo();
    }, [id, currentUser, token]);
    
    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    if (!video) {
        return <div>No video found</div>;
    }

    const handleEditClick = () => {
        setIsEditing(true);
    };

    const handleSaveClick = async () => {
        if (video && video._id) {
            try {
                console.log('Updating video with ID:', video._id);
                const response = await fetch(`http://localhost:8080/api/videos/${id}`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                    body: JSON.stringify({ title: editedTitle }),
                });
    
                if (!response.ok) {
                    const text = await response.text();
                    throw new Error(`Failed to update video title: ${text}`);
                }
    
                const updatedVideo = await response.json();
                console.log('Updated video data:', updatedVideo);
                setVideo(updatedVideo);
                setIsEditing(false);
            } catch (error) {
                console.error("Error updating video title:", error);
                setError(error.message);
            }
        } else {
            console.error('Video ID is not defined');
        }
    };
    

    const handleDeleteClick = async () => {
        if (video && video._id) {
            try {
                const response = await fetch(`http://localhost:8080/api/videos/${video._id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    const text = await response.text();
                    throw new Error(`Failed to delete video: ${text}`);
                }

                console.log('Video deleted:', video);
                removeVideo(video);
                navigate('/');
            } catch (error) {
                console.error("Error deleting video:", error);
                setError(error.message);
            }
        } else {
            console.error('Video ID is not defined');
        }
    };

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

    return (
        <div className='video-container'>
            <div className='video-wrapper'>
                <video
                    src={videoURL}
                    controls
                    autoPlay
                    muted={false}
                    className="video-element"
                    onError={(e) => console.error("Error loading video:", e)}
                >
                    Your browser does not support the video tag.
                </video>
            </div>
            <div className="video-title">
                {isEditing ? (
                    <input
                        type="text"
                        value={editedTitle}
                        onChange={(e) => setEditedTitle(e.target.value)}
                        className="form-control"
                    />
                ) : (
                    <h3>{video.title}</h3>
                )}
            </div>
            <div className="play-video-info">
                <p>{formatViews(video.views)} views • {timeSince(new Date(video.publishedDate))}</p>
                <div className="bar_buttons">
                    <button className="btn btn-outline-danger m-1" onClick={likedClick}>
                        <FaThumbsUp /> Like
                    </button>
                    <button className="btn btn-outline-danger m-1" onClick={unLikedClick}>
                        <FaThumbsDown /> Unlike
                    </button>
                    <button className="btn btn-outline-danger m-1" onClick={() => setShowModal(true)}>
                        <FaShare /> Share
                    </button>
                    {currentUser && currentUser.username === video.publisher && (
                        <>
                            {isEditing ? (
                                <button className="btn btn-outline-danger m-1" onClick={handleSaveClick}>
                                    <FaSave /> Save
                                </button>
                            ) : (
                                <button className="btn btn-outline-danger m-1" onClick={handleEditClick}>
                                    <FaEdit /> Edit
                                </button>
                            )}
                            <button className="btn btn-outline-danger m-1" onClick={handleDeleteClick}>
                                <FaTrash /> Delete
                            </button>
                        </>
                    )}
                </div>
                {likeMessage && <div className="alert alert-success mt-3">{likeMessage}</div>}
            </div>
            <div className="publisher d-flex align-items-center">
                <img src={userImageURL} alt="Publisher" onClick={handleUserClick}/>
                <div className="publisher-info ms-3" onClick={handleUserClick}>
                    <p>{video.publisher}</p>
                    <span>1M Subscribers</span>
                </div>
                <button className="btn btn-outline-danger ms-auto" onClick={subscribe}>
                    <FaBell /> Subscribe
                </button>
            </div>
            <hr className="my-3" />
            <div className="comment-section">
                <h2>Comments:</h2>
                <div className="comments-list">
                    {comments.map((comment, index) => (
                        <Comment
                            key={index}
                            comment={comment}
                            onDelete={() => deleteComment(index)}
                            onEdit={(newContent) => editComment(index, newContent)}
                            currentUser={currentUser}
                        />
                    ))}
                </div>
                {currentUser && (
                    <div className="comment-input">
                        <input
                            type="text"
                            placeholder="Write a comment..."
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            className="form-control"
                        />
                        <button className="btn btn-outline-danger" onClick={addComment}>
                            <FaComment /> Add comment
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default MainVideoSection;
