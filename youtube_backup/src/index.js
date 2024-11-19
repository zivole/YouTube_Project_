import React from 'react';
import ReactDOM from 'react-dom/client';
import 'bootstrap/dist/css/bootstrap.min.css'; 
import 'bootstrap-icons/font/bootstrap-icons.css'; 

import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
   <React.StrictMode>
    <App />
  </React.StrictMode> 
);

reportWebVitals();
// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals

// import React from 'react';
// import ReactDOM from 'react-dom/client';
// import { BrowserRouter as Router } from 'react-router-dom'; // Add Router here
// import 'bootstrap/dist/css/bootstrap.min.css'; 
// import 'bootstrap-icons/font/bootstrap-icons.css'; 
// import './index.css';
// import App from './App';
// import reportWebVitals from './reportWebVitals';

// const root = ReactDOM.createRoot(document.getElementById('root'));
// root.render(
//   <React.StrictMode>
//     <Router> {/* Wrap App with Router */}
//       <App />
//     </Router>
//   </React.StrictMode>
// );

// reportWebVitals();

// import React from 'react';
// import ReactDOM from 'react-dom';
// import App from './App';
// import reportWebVitals from './reportWebVitals';

// ReactDOM.render(
//   <App />,
//   document.getElementById('root')
// );

// // If you want to start measuring performance in your app, pass a function
// // to log results (for example: reportWebVitals(console.log))
// // or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();