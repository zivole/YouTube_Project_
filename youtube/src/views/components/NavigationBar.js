import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import '../styles/NavigationBar.css';
import DarkModeToggle from './DarkModeToggle';
import { ReactComponent as Logo } from '../assets/YouTube_Logo_2017.svg';
import { ReactComponent as LogoDark } from '../assets/YouTube-Logo.darkmode.svg';
import UserDropdown from './UserDropdown';
import { Link } from 'react-router-dom';

const NavigationBar = ({ onToggleDarkMode, onSearch, isAuthenticated, onLogout, currentUser, addVideo  }) => {
    const [isDarkMode, setIsDarkMode] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');

    const handleToggle = () => {
        setIsDarkMode(!isDarkMode);
        onToggleDarkMode(!isDarkMode);
    };

    const handleSearch = (event) => {
        event.preventDefault();
        onSearch(searchQuery);
    };


    return (
        <nav className={`navbar navbar-expand-lg ${isDarkMode ? 'dark-mode' : 'light-mode'} header`}>
            <div className="container-fluid">
                <button
                    className="btn me-2 offcanvas-btn menu-button"
                    type="button"
                    data-bs-toggle="offcanvas"
                    data-bs-target="#offcanvasNavbar"
                    aria-controls="offcanvasNavbar"
                >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="24"
                        height="24"
                        fill="currentColor"
                        className="bi bi-list"
                        viewBox="0 0 16 16"
                    >
                        <path
                            fillRule="evenodd"
                            d="M2.5 12a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5z"
                        />
                    </svg>
                </button>
                <Link to="/" className="navbar-brand d-flex align-items-center">
                    {isDarkMode ? <LogoDark className="header__logoImage" /> : <Logo className="header__logoImage" />}
                </Link>
                <DarkModeToggle handleToggle={handleToggle} isDarkMode={isDarkMode} />
                <div className="d-flex flex-grow-1 justify-content-center search">
                    <form className="d-flex search-bar w-50 position-relative" onSubmit={handleSearch}>
                        <input
                            className={`form-control search-input ${isDarkMode ? 'navbar-dark' : ''}`}
                            type="search"
                            placeholder="Search"
                            aria-label="Search"
                            name="search"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                        />
                        <button className={`btn search-btn ${isDarkMode ? 'navbar-dark' : ''}`} type="submit">
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="16"
                                height="16"
                                fill="currentColor"
                                className="bi bi-search"
                                viewBox="0 0 16 16"
                            >
                                <path
                                    d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"
                                />
                            </svg>
                        </button>
                    </form>
                </div>
                <div className="d-flex align-items-center">
                    {isAuthenticated ? (
                        <>
                            <UserDropdown onLogout={onLogout} currentUser={currentUser} />
                            <Link to="/add" className="btn btn-primary me-2" role="button">
                                Add New Video
                            </Link>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="btn btn-outline-primary me-2" role="button">
                                Login
                            </Link>
                            <Link to="/signup" className="btn btn-primary me-2" role="button">
                                Sign-up
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default NavigationBar;
