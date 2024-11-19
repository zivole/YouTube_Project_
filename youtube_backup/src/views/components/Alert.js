import React from 'react';

// Component for displaying alert messages
const Alert = ({ error, success, className }) => (
  <>
    {error && <div className={`alert alert-danger ${className}`} role="alert">{error}</div>}
    {success && <div className={`alert alert-success ${className}`} role="alert">{success}</div>}
  </>
);

export default Alert;
