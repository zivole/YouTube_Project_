import React from 'react';
import { useNavigate } from 'react-router-dom';
import Alert from '../components/Alert';
import { ReactComponent as LogoDark } from '../assets/YouTube-Logo.darkmode.svg';
import useLoginForm from '../hooks/useLoginForm';
import { Link } from 'react-router-dom';
import '../styles/sign_up&login.css';

const LoginForm = ({ users, onLogin }) => {
    const navigate = useNavigate();
    const { fields, formData, error, success, handleInputChange, handleSubmit } = useLoginForm(users, onLogin);

    return (
        <div className="signup">
            <div className="signup_login_card p-4">
                <div className="signup_login_header text-center mb-4">
                    <LogoDark className='signup_login_logo' />
                </div>
                <div className="signup_login_card-body">
                    <h2 className="signup_login_card-title text-center">Login</h2>
                    <Alert error={error} success={success} className="signup_login_alert" />
                    <form onSubmit={handleSubmit}>
                        {fields.map(field => (
                            <div key={field.id} className="input-group mb-3">
                                <span className="input-group-text"><i className={`bi bi-${field.icon}`}></i></span>
                                <input
                                    type={field.type}
                                    id={field.id}
                                    className="form-control"
                                    placeholder={field.placeholder}
                                    value={formData[field.id] || ''}
                                    onChange={handleInputChange}
                                    name={field.id}
                                />
                            </div>
                        ))}
                        <div className="d-flex justify-content-center">
                            <Link to="/" className="btn btn-secondary me-2">Cancel</Link>
                            <button type="submit" className="btn btn-primary">Login</button>
                        </div>
                    </form>
                    <div className="signup_login_text-center mt-3">
                        <Link to="/signup">Don't have an account? Sign up</Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default LoginForm;
