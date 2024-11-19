// videoServices.js
const API_URL = 'http://localhost:8080/api/videos';

export const fetchAllVideos = async () => {
    const response = await fetch(`${API_URL}`);
    if (response.ok) {
        return await response.json();
    } else {
        throw new Error('Failed to fetch videos');
    }
};

export const fetchVideoById = async (id) => {
    const response = await fetch(`http://localhost:8080/api/videos/${id}`);
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return await response.json();
  };

export const searchVideos = async (query) => {
    const response = await fetch(`${API_URL}/search?q=${query}`);
    if (response.ok) {
        return await response.json();
    } else {
        throw new Error('Failed to search videos');
    }
};

export const addVideo = async (videoData, token) => {
    const response = await fetch(`${API_URL}/add`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
        body: videoData,
    });
    if (response.ok) {
        return await response.json();
    } else {
        throw new Error('Failed to add video');
    }
};

// Add other functions as needed...