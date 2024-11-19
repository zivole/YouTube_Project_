import React from 'react';
import { Link } from 'react-router-dom';

const FormButton = ({ text, to, className, onClick }) => {
  if (to) {
    return (
      <Link to={to} className={className}>
        {text}
      </Link>
    );
  }

  return (
    <button type="button" className={className} onClick={onClick}>
      {text}
    </button>
  );
};

export default FormButton;
