import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/OffCanvasMenu.css';
import { ReactComponent as Logo } from '../assets/YouTube_Logo_2017.svg';
import { ReactComponent as LogoDark } from '../assets/YouTube-Logo.darkmode.svg';
import { useNavigate } from 'react-router-dom';

const OffCanvasMenu = ({ isDarkMode, onToggleDarkMode }) => {

  const handleToggle = () => {
    onToggleDarkMode(!isDarkMode);
  };

  const navigate = useNavigate();

  const handleHomeClick = () => {
      navigate('/');
  };


  return (
    <div className="offcanvas offcanvas-start offcanvas-width" tabIndex="-1" id="offcanvasNavbar" aria-labelledby="offcanvasNavbarLabel">
      <div className="offcanvas-header">
        {isDarkMode ? <LogoDark className="header__logoImage" /> : <Logo className="header__logoImage" />}
       
      </div>
      <div className="offcanvas-body">
        <div className="col-auto d-flex flex-column p-3 sidebar-offcanvas">
          <ul className="nav nav-pills flex-column mb-auto">
          <li className="nav-item">
                <button onClick={handleHomeClick} className="btn btn-light btn-sidebar-offcanvas mb-2">
                    <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M8 3.293l-6 6V14.5a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5V10.5a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 .5.5V14.5a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-5.207l-6-6zM8 1l6.793 6.793a.5.5 0 0 1-.707.707L8 2.707l-6.086 6.086a.5.5 0 1 1-.707-.707L8 1z"/>
                    </svg>
                    Home
                </button>
            </li>
            <li className="nav-item">
              <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2">
                <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M4.5 8a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7zm0-3a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7zm0 6a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z" />
                </svg>
                Subscriptions
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2">
                <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M4 4v9h8V4H4zM3 3h10a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm3 1h4v2H6V4z" />
                </svg>
                Library
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2">
                <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 1a7 7 0 1 0 4.95 11.95l.707.707A8 8 0 1 1 8 0z" />
                  <path d="M7.5 3a.5.5 0 0 1 .5.5v5.21l3.248 1.856a.5.5 0 0 1-.496.868l-3.5-2A.5.5 0 0 1 7 9V3.5a.5.5 0 0 1 .5-.5" />
                </svg>
                History
              </a>
            </li>
          </ul>
          <hr/>
          <ul className="nav nav-pills flex-column mb-auto">
            <li className="nav-item">
              <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2">
                <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 3.293l-6 6V14.5a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5V10.5a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 .5.5V14.5a.5.5 0 0 0 .5.5h3a.5.5 0 0 0 .5-.5v-5.207l-6-6zM8 1l6.793 6.793a.5.5 0 0 1-.707.707L8 2.707l-6.086 6.086a.5.5 0 1 1-.707-.707L8 1z" />
                </svg>
                Home
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2">
                <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M4.5 8a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7zm0-3a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7zm0 6a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z" />
                </svg>
                Subscriptions
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2">
                <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M4 4v9h8V4H4zM3 3h10a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm3 1h4v2H6V4z" />
                </svg>
                Library
              </a>
            </li>
            <li className="nav-item">
              <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2">
                <svg className="bi pe-none me-2" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 1a7 7 0 1 0 4.95 11.95l.707.707A8 8 0 1 1 8 0z" />
                  <path d="M7.5 3a.5.5 0 0 1 .5.5v5.21l3.248 1.856a.5.5 0 0 1-.496.868l-3.5-2A.5.5 0 0 1 7 9V3.5a.5.5 0 0 1 .5-.5" />
                </svg>
                History
              </a>
            </li>
          </ul>
          <hr/>
          <ul className="nav nav-pills flex-column mb-auto">
          <li className="nav-item">
       <h4 className="text-start">Subscriptions</h4>
          </li>
       <li className="nav-item">
      <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2 d-flex align-items-center">
        <img src="../data/hodaya.png" alt="Subscription 1" className="rounded-circle me-2" width="32" height="32" />
        <span>Subscription 1</span>
      </a>
    </li>
    <li className="nav-item">
      <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2 d-flex align-items-center">
        <img src="../data/guy.png" alt="Subscription 2" className="rounded-circle me-2" width="32" height="32" />
        <span>Subscription 2</span>
      </a>
    </li>
    <li className="nav-item">
      <a href="#" className="btn btn-light btn-sidebar-offcanvas mb-2 d-flex align-items-center">
        <img src="../data/ziv.png" alt="Subscription 3" className="rounded-circle me-2" width="32" height="32" />
        <span>Subscription 3</span>
      </a>
    </li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default OffCanvasMenu;
