import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import loginFieldsData from '../../data/loginFields.json';
import { handleInputChange } from '../../utils/formHandlers';

const useLoginForm = (users, onLogin) => {
    const [fields, setFields] = useState([]);
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        setFields(loginFieldsData.fields);
    }, []);

    useEffect(() => {
        if (success) {
            const timer = setTimeout(() => {
                setSuccess('');
                navigate('/');
            }, 500);
            return () => clearTimeout(timer);
        }
    }, [success, navigate]);

    // const handleSubmit = (event) => {
    //     event.preventDefault();
    //     const { username, password } = formData;

    //     if (!username || !password) {
    //         setError('Username and password are required.');
    //         setSuccess('');
    //         return;
    //     }

    //     // Find the user matching the provided username and password
    //     const user = users.find(user => user.username === username && user.password === password);

    //     if (user) {
    //         setSuccess('Login successful!');
    //         setError('');
    //         onLogin(user);  // Call the onLogin callback to update authentication state
    //     } else {
    //         setError('Invalid username or password.');
    //         setSuccess('');
    //     }
    // };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const { username, password } = formData;
        const newUser = {username, password};
        try {
            const response = await fetch('http://localhost:8080/api/users/signin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newUser),
            });

            const result = await response.json();
            if (response.ok) {
                setSuccess('Login successful!');
                setError('');
                console.log("fetching the token for username: " + username)
                // Get the token
                const tokenResponse = await fetch('http://localhost:8080/api/tokens', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({"username": username}),
                });
                console.log(tokenResponse.status)
                const tokenResult = await tokenResponse.json();
                console.log("the token is" + JSON.stringify(tokenResult.token))
                localStorage.setItem('token', tokenResult.token);
                console.log("local: ", localStorage.getItem('token'));
                onLogin(result.user);  // Call the onLogin callback to update authentication state with the received token
            } else {
                console.log("response is not ok")
                setError(result.message);
                setSuccess('');
            }
        } catch (err) {
            setError('Something went wrong. Please try again.');
            setSuccess('');
        }
    };

    //console.log("done")
    return {
        fields,
        formData,
        error,
        success,
        handleInputChange: (e) => handleInputChange(e, formData, setFormData),
        handleSubmit
    };
};

export default useLoginForm;
