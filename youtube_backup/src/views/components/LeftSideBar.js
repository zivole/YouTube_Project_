import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/LeftSideBar.css';
import { useNavigate } from 'react-router-dom';


const LeftSideBar = () => {
  return (
    <div className="col-auto d-flex flex-column p-3 sidebar">
      <ul className="nav nav-pills flex-column mb-auto">
        <li className="nav-item">
          <a href="#" className="btn btn-light btn-sidebar mb-2">
            <svg className="bi pe-none me-2" width="24" height="24" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 3.293l-6 6V14.5a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5V10.5a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 .5.5V14.5a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-5.207l-6-6zM8 1l6.793 6.793a.5.5 0 0 1-.707.707L8 2.707l-6.086 6.086a.5.5 0 1 1-.707-.707L8 1z"/>
            </svg>
            Home
          </a>
        </li>
        <li className="nav-item">
          <a href="#" className="btn btn-light btn-sidebar mb-2">
            <svg xmlns="http://www.w3.org/2000/svg"width="24" height="24" fill="currentColor" className="bi bi-search" viewBox="0 0 16 16">
              <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
            </svg>
            Explore
          </a>
        </li>
        <li className="nav-item">
          <a href="#" className="btn btn-light btn-sidebar mb-2">
            <svg xmlns="http://www.w3.org/2000/svg"width="24" height="24" fill="currentColor" className="bi bi-search" viewBox="0 0 16 16">
              <path d="M10 14.65v-5.3L15 12l-5 2.65zm7.77-4.33-1.2-.5L18 9.06c1.84-.96 2.53-3.23 1.56-5.06s-3.24-2.53-5.07-1.56L6 6.94c-1.29.68-2.07 2.04-2 3.49.07 1.42.93 2.67 2.22 3.25.03.01 1.2.5 1.2.5L6 14.93c-1.83.97-2.53 3.24-1.56 5.07.97 1.83 3.24 2.53 5.07 1.56l8.5-4.5c1.29-.68 2.06-2.04 1.99-3.49-.07-1.42-.94-2.68-2.23-3.25zm-.23 5.86-8.5 4.5c-1.34.71-3.01.2-3.72-1.14-.71-1.34-.2-3.01 1.14-3.72l2.04-1.08v-1.21l-.69-.28-1.11-.46c-.99-.41-1.65-1.35-1.7-2.41-.05-1.06.52-2.06 1.46-2.56l8.5-4.5c1.34-.71 3.01-.2 3.72 1.14.71 1.34.2 3.01-1.14 3.72L15.5 9.26v1.21l1.8.74c.99.41 1.65 1.35 1.7 2.41.05 1.06-.52 2.06-1.46 2.56z"/>
            </svg>
            Shorts
          </a>
        </li>
        <li className="nav-item">
          <a href="#" className="btn btn-light btn-sidebar mb-2">
            <svg className="bi pe-none me-2" width="24" height="24" fill="currentColor" viewBox="0 0 16 16">
              <path d="M8 1a7 7 0 1 0 4.95 11.95l.707.707A8 8 0 1 1 8 0z"/>
              <path d="M7.5 3a.5.5 0 0 1 .5.5v5.21l3.248 1.856a.5.5 0 0 1-.496.868l-3.5-2A.5.5 0 0 1 7 9V3.5a.5.5 0 0 1 .5-.5"/>
            </svg>
            History
          </a>
        </li>
      </ul>
    </div>
  );
};

export default LeftSideBar;
