import React, { useState, useEffect } from 'react';
import MainVideoSection from '../components/MainVideoSection';
import RecommendedVideos from '../components/RecommendedVideos';
import NavigationBar from '../components/NavigationBar';
import OffCanvasMenu from '../components/OffCanvasMenu';
import '../styles/play_video.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useParams, useLocation } from 'react-router-dom';
import { fetchVideoById, fetchAllVideos } from '../../api';

const PlayVideo = ({
  isAuthenticated,
  onLogout,
  currentUser,
  users,
  addVideo,
  removeVideo,
  videos,
  onToggleDarkMode,
  isDarkMode: isDarkModeProp,
}) => {
  const { id } = useParams();
  const location = useLocation();
  const [video, setVideo] = useState(location.state?.mainVideo || null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [likeMessage, setLikeMessage] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [isDarkMode, setIsDarkMode] = useState(isDarkModeProp);
  const [searchTerm, setSearchTerm] = useState('');
  const [category, setCategory] = useState('All');
  const [recommendedVideos, setRecommendedVideos] = useState([]);
  const token = localStorage.getItem('token');

  // useEffect(() => {
  //   console.log(`Captured video ID: ${id}`);
  //   if (!video) {
  //     const loadVideo = async () => {
  //       try {
  //         console.log(`Fetching video with ID: ${id}`);
  //         const videoData = await fetchVideoById(id);
  //         console.log('Fetched video data:', videoData);
  //         setVideo(videoData);
  //       } catch (error) {
  //         console.error('Failed to fetch video', error);
  //       }
  //     };
  //     loadVideo();
  //   } else {
  //     console.log('Video data from state:', video);
  //   }
  // }, [id, video]);

  // useEffect(() => {
  //   const fetchRecommendedVideos = async () => {
  //     try {
  //       const { popularVideos, randomVideos } = await fetchAllVideos();
  //       console.log('Fetched all videos data:', { popularVideos, randomVideos });
  //       const allVideos = [...popularVideos, ...randomVideos];
  //       const filteredVideos = allVideos.filter(v => v._id !== id);
  //       console.log('Filtered recommended videos:', filteredVideos);
  //       setRecommendedVideos(filteredVideos); // Ensure IDs are compared as strings
  //     } catch (error) {
  //       console.error('Error fetching recommended videos:', error);
  //     }
  //   };

  //   fetchRecommendedVideos();
  // }, [id]);

  useEffect(() => {
    const fetchRecommendedVideos = async () => {
        try {
            console.log(`Fetching video with ID: ${id}`);
            
            // Include the currentUser details in the headers
            const response = await fetch(`http://localhost:8080/api/videos/${id}/recommended`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`, // Assuming you're using token for authentication
                    'Content-Type': 'application/json',
                    'X-User-ID': currentUser?._id,       // Custom header with currentUser._id
                    'X-Username': currentUser?.username, // Custom header with currentUser.username
                },
            });

           
            // Handle HTTP errors
            if (!response.ok) {
              throw new Error('Video not found');
          }

          // Parse the response body as JSON
          const videoData = await response.json();  // Corrected: Use .json() with parentheses

          // Ensure the data is an array, even if it's empty
          if (!Array.isArray(videoData)) {
              throw new Error('Invalid video data format');
          }

          console.log('Fetched video data:', videoData);

          // Set the recommended videos state to the fetched array
          setRecommendedVideos(videoData);

      } catch (error) {
          console.error("Error fetching video:", error);
      } 
  };

  fetchRecommendedVideos();
}, [id, currentUser, token]);

  useEffect(() => {
    const fetchComments = async () => {
      try {
        const commentsResponse = await fetch(`http://localhost:8080/api/comments?videoId=${id}`);
        const commentsData = await commentsResponse.json();
        setComments(Array.isArray(commentsData) ? commentsData : []);
      } catch (error) {
        console.error("Error fetching comments:", error);
      }
    };

    fetchComments();
  }, [id]);

  if (!video) {
    return <div>Loading...</div>;
  }

  const handleToggleDarkMode = () => {
    const newDarkMode = !isDarkMode;
    setIsDarkMode(newDarkMode);
    onToggleDarkMode(newDarkMode);
  };

  const handleSearch = (query) => {
    setSearchTerm(query);
  };

  const handleCategoryChange = (category) => {
    setCategory(category);
  };

  const addComment = async () => {
    if (newComment.trim() === "") return;

    try {
      console.log(newComment)
      const token = localStorage.getItem("token")
      const response = await fetch(`http://localhost:8080/api/comments`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            content: newComment,
            videoId: video._id
          })
        });

      if (!response.ok) {
        throw new Error('Failed to add comment');
      }

      const newCommentData = await response.json();
      setNewComment('');
      setComments([...comments, newCommentData]);
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  const deleteComment = async (index) => {
    const commentToDelete = comments[index];
    const token = localStorage.getItem("token")
    try {
      const response = await fetch(`http://localhost:8080/api/comments/${commentToDelete._id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to delete comment');
      }

      setComments(comments.filter((_, i) => i !== index));
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const editComment = async (index, newContent) => {
    const commentToEdit = comments[index];
    const token = localStorage.getItem("token")
    try {
      const response = await fetch(`http://localhost:8080/api/comments/${commentToEdit._id}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            content: newContent
          })
        });

      if (!response.ok) {
        throw new Error('Failed to edit comment');
      }

      const updatedComment = await response.json();
      setComments(comments.map((comment, i) => (i === index ? updatedComment : comment)));
    } catch (error) {
      console.error('Error editing comment:', error);
    }
  };

  const likedClick = () => {
    setLikeMessage("You have liked this video");
    setTimeout(() => {
      setLikeMessage("");
    }, 3000);
  };

  const unLikedClick = () => {
    setLikeMessage("You have unliked this video");
    setTimeout(() => {
      setLikeMessage("");
    }, 3000);
  };

  const subscribe = () => {
    setLikeMessage("You have subscribed: " + video.publisher);
    setTimeout(() => {
      setLikeMessage("");
    }, 3000);
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
          <div className="col-10">
            <MainVideoSection
              video={video}
              removeVideo={removeVideo}
              comments={comments}
              newComment={newComment}
              setNewComment={setNewComment}
              addComment={addComment}
              deleteComment={deleteComment}
              editComment={editComment}
              likeMessage={likeMessage}
              likedClick={likedClick}
              unLikedClick={unLikedClick}
              subscribe={subscribe}
              setShowModal={setShowModal}
              currentUser={currentUser}
            />
          </div>
          <div className="col-2">
            <RecommendedVideos recommendedVideos={recommendedVideos} currentUser={currentUser} />
          </div>
        </div>
      </div>

      {/* Modal */}
      {showModal && (
        <div className="modal show fade" style={{ display: 'block' }} id="exampleModal" tabIndex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header">
                <h1 className="modal-title fs-5" id="exampleModalLabel">Share this video :</h1>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)} aria-label="Close"></button>
              </div>
              <div className="modal-body">
                {/* Share links */}
                <a href="https://instagram.com" target="_blank">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="bi bi-instagram" viewBox="0 0 16 16">
                    <path d="M8 0C5.829 0 5.556.01 4.703.048 3.85.088 3.269.222 2.76.42a3.9 3.9 0 0 0-1.417.923A3.9 3.9 0 0 0 .42 2.76C.222 3.268.087 3.85.048 4.7.01 5.555 0 5.827 0 8.001c0 2.172.01 2.444.048 3.297.04.852.174 1.433.372 1.942.205.526.478.972.923 1.417.444.445.89.719 1.416.923.51.198 1.09.333 1.942.372C5.555 15.99 5.827 16 8 16s2.444-.01 3.232-.048c.851-.04 1.434-.174 1.943-.372a3.9 3.9 0 0 0 1.416-.923c.445-.445.718-.891.923-1.417.197-.509.332-1.09.372-1.942C15.99 10.445 16 10.173 16 8s-.01-2.445-.048-3.299c-.04-.851-.175-1.433-.372-1.941a3.9 3.9 0 0 0-.923-1.417A3.9 3.9 0 0 0 13.24.42c-.51-.198-1.092-.333-1.943-.372C10.443.01 10.172 0 7.998 0zm-.717 1.442h.718c2.136 0 2.389.007 3.232.046.78.035 1.204.166 1.486.275.373.145.64.319.92.599s.453.546.598.92c.11.281.24.705.275 1.485.039.843.047 1.096.047 3.231s-.008 2.389-.047 3.232c-.035.78-.166 1.203-.275 1.485a2.5 2.5 0 0 1-.599.919c-.28.28-.546.453-.92.598-.28.11-.704.24-1.485.276-.843.038-1.096.047-3.232.047s-2.39-.009-3.233-.047c-.78-.036-1.203-.166-1.485-.276a2.5 2.5 0 0 1-.92-.598 2.5 2.5 0 0 1-.6-.92c-.109-.281-.24-.705-.275-1.485-.038-.843-.046-1.096-.046-3.233s.008-2.388.046-3.231c.036-.78.166-1.204.276-1.486.145-.373.319-.64.599-.92s.546-.453.92-.598c.282-.11.705-.24 1.485-.276.738-.034 1.024-.044 2.515-.045zm4.988 1.328a.96.96 0 1 0 0 1.92.96.96 0 0 0 0-1.92m-4.27 1.122a4.109 4.109 0 1 0 0 8.217 4.109 4.109 0 0 0 0-8.217m0 1.441a2.667 2.667 0 1 1 0 5.334 2.667 2.667 0 0 1 0-5.334" />
                  </svg>
                </a>
                <a href="https://facebook.com" target="_blank">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" className="bi bi-facebook" viewBox="0 0 16 16">
                    <path d="M16 8.049c0-4.446-3.582-8.05-8-8.05C3.58 0-.002 3.603-.002 8.05c0 4.017 2.926 7.347 6.75 7.951v-5.625h-2.03V8.05H6.75V6.275c0-2.017 1.195-3.131 3.022-3.131.876 0 1.791.157 1.791.157v1.98h-1.009c-.993 0-1.303.621-1.303 1.258v1.51h2.218l-.354 2.326H9.25V16c3.824-.604 6.75-3.934 6.75-7.951" />
                  </svg>
                </a>
                <a href="https://whatsapp.com" target="_blank">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-whatsapp" viewBox="0 0 16 16">
                    <path d="M13.601 2.326A7.85 7.85 0 0 0 7.994 0C3.627 0 .068 3.558.064 7.926c0 1.399.366 2.76 1.057 3.965L0 16l4.204-1.102a7.9 7.9 0 0 0 3.79.965h.004c4.368 0 7.926-3.558 7.93-7.93A7.9 7.9 0 0 0 13.6 2.326zM7.994 14.521a6.6 6.6 0 0 1-3.356-.92l-.24-.144-2.494.654.666-2.433-.156-.251a6.56 6.56 0 0 1-1.007-3.505c0-3.626 2.957-6.584 6.591-6.584a6.56 6.56 0 0 1 4.66 1.931 6.56 6.56 0 0 1 1.928 4.66c-.004 3.639-2.961 6.592-6.592 6.592m3.615-4.934c-.197-.099-1.17-.578-1.353-.646-.182-.065-.315-.099-.445.099-.133.197-.513.646-.627.775-.114.133-.232.148-.43.05-.197-.1-.836-.308-1.592-.985-.59-.525-.985-1.175-1.103-1.372-.114-.198-.011-.304.088-.403.087-.088.197-.232.296-.346.1-.114.133-.198.198-.33.065-.134.034-.248-.015-.347-.05-.099-.445-1.076-.612-1.47-.16-.389-.323-.335-.445-.34-.114-.007-.247-.007-.38-.007a.73.73 0 0 0-.529.247c-.182.198-.691.677-.691 1.654s.71 1.916.81 2.049c.098.133 1.394 2.132 3.383 2.992.47.205.84.326 1.129.418.475.152.904.129 1.246.08.38-.058 1.171-.48 1.338-.943.164-.464.164-.86.114-.943-.049-.084-.182-.133-.38-.232" />
                  </svg>
                </a>
                <a href="https://messenger.com" target="_blank">
                  <svg xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-messenger" viewBox="0 0 16 16">
                    <path d="M0 7.76C0 3.301 3.493 0 8 0s8 3.301 8 7.76-3.493 7.76-8 7.76c-.81 0-1.586-.107-2.316-.307a.64.64 0 0 0-.427.03l-1.588.702a.64.64 0 0 1-.898-.566l-.044-1.423a.64.64 0 0 0-.215-.456C.956 12.108 0 10.092 0 7.76m5.546-1.459-2.35 3.728c-.225.358.214.761.551.506l2.525-1.916a.48.48 0 0 1 .578-.002l1.869 1.402a1.2 1.2 0 0 0 1.735-.32l2.35-3.728c.226-.358-.214-.761-.551-.506L9.728 7.381a.48.48 0 0 1-.578.002L7.281 5.98a1.2 1.2 0 0 0-1.735.32z" />
                  </svg>
                </a>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PlayVideo;
