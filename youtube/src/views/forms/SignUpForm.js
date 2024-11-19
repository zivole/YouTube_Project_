import React from 'react';
import InputField from '../components/InputField';
import ImageUpload from '../components/ImageUpload';
import { ReactComponent as LogoDark } from '../assets/YouTube-Logo.darkmode.svg';
import FormButton from '../components/FormButton';
import Alert from '../components/Alert';
import useSignUpForm from '../hooks/useSignUpForm';
import { Link } from 'react-router-dom';
import '../styles/sign_up&login.css';

const SignUpForm = ({ users, setUsers }) => {
    const { fields, formData, error, success, handleInputChange, handleImageChange, handleSubmit } = useSignUpForm(users, setUsers);

    return (
        <div className="signup">
            <div className="signup_login_card p-4 login-box">
                <div className="signup_login_header text-center mb-4">
                    <LogoDark className='signup_login_logo' />
                </div>
                <div className="signup_login_card-body">
                    <h2 className="signup_login_card-title text-center">Sign Up</h2>
                    <Alert error={error} success={success ? 'User registered successfully!' : ''} className="signup_login_alert" />
                    <form onSubmit={handleSubmit} noValidate>
                        {fields.map(field => (
                            <InputField
                                key={field.id}
                                id={field.id}
                                type={field.type}
                                placeholder={field.placeholder}
                                name={field.id}
                                value={formData[field.id]}
                                onChange={handleInputChange}
                                icon={field.icon}
                                className="signup_login_form-control"
                            />
                        ))}
                        <ImageUpload 
                            image={formData.image} 
                            handleImageChange={handleImageChange}
                            className="signup_login_image-upload"
                        />
                        <div className="d-flex justify-content-center">
                            <FormButton text="Cancel" to="/" className="signup_login_btn-secondary" />
                            <button type="submit" className="signup_login_btn-primary">Sign Up</button>
                        </div>
                    </form>
                    <div className="signup_login_text-center mt-3">
                        <Link to="/login">Already have an account? Login</Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SignUpForm;
