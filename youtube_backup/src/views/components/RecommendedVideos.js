import React from 'react';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import { timeSince , formatViews } from '../../utils/videoUtils';


const RecommendedVideos = ({ recommendedVideos, currentUser }) => {
  const navigate = useNavigate();

  const handleVideoClick = (videoId,video) => {
    navigate(`/videos/${videoId}`, { state: { mainVideo: video }});
  };
  // const handleClick = () => {
  //   navigate(`/videos/${video._id}`, { state: { mainVideo: video } }); // Ensure the correct field name for the video ID
  // };

  return (
    <div className='recommended'>
      <div className="list-group">
        {recommendedVideos.map(video => (
          <div
            key={video._id}
            className='list-group-item list-group-item-action'
            onClick={() => handleVideoClick(video._id, video)}
            style={{ cursor: 'pointer' }}
          >
            <img src={video.thumbnail} alt="Thumbnail" className="thumbnail-image rounded float-start" />
            <div className="video-info">
              <h4>{video.title}</h4>
              <p>{video.publisher}</p>
              <p>{formatViews(video.views)} views </p> 
              <p> {timeSince(new Date(video.publishedDate))}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
);
}

export default RecommendedVideos;