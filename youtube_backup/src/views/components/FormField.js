import React from 'react';

const FormField = ({ id, type, placeholder, value, onChange, icon, className }) => (
  <div className={`mb-3 input-group ${className}`}>
    <span className="input-group-text"><i className={`bi bi-${icon}`}></i></span>
    <input
      type={type}
      id={id}
      className="form-control"
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      required
    />
  </div>
);

export default FormField;
