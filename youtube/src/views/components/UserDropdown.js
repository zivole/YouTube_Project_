import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import { Link ,useNavigate } from 'react-router-dom';

const UserDropdown = ({ onLogout, currentUser }) => {
    const navigate = useNavigate();

    
    if (!currentUser) {
        return null;
    }

    const handleLogout = () => {
        navigate('/');
        onLogout();
    };

    return (
        <div className="nav-item dropdown">
            <a
                className="nav-link dropdown-toggle"
                href="#"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
            >
                <img
                    src={currentUser.image}
                    alt={currentUser.username}
                    width="32"
                    height="32"
                    className="rounded-circle"
                />
            </a>
            <ul className="dropdown-menu">
                <li>
                    <Link className="dropdown-item" to={`/user/${currentUser.username}`}>
                        Profile
                    </Link>
                </li>
                <li>
                    <a className="dropdown-item" href="#">
                        Settings
                    </a>
                </li>
                <li>
                    <hr className="dropdown-divider" />
                </li>
                <li>
                    <a className="dropdown-item" href="#" onClick={handleLogout}>
                        Logout
                    </a>
                </li>
            </ul>
        </div>
    );
};

export default UserDropdown;
