import React from 'react';
import logo from '../assets/YouTube-Logo.darkmode.svg';

const Logo = ({ className }) => (
  <img src={logo} alt="Logo" className={`logo ${className}`} />
);

export default Logo;
