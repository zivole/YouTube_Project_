const User = require('../models/userModel');

const isPasswordValid = (password) => {
  const minLength = 8;
  const hasUpperCase = /[A-Z]/.test(password);
  const hasLowerCase = /[a-z]/.test(password);
  const hasNumber = /[0-9]/.test(password);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  return password.length >= minLength && hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar;
};

async function signUp(firstName, lastName, username, password, confirmPassword, imageView) {
  if (!firstName) throw new Error('First name is required.');
  if (!lastName) throw new Error('Last name is required.');
  if (!username) throw new Error('Username is required.');
  if (!password) throw new Error('Password is required.');
  if (password !== confirmPassword) throw new Error('Passwords do not match!');
  if (!isPasswordValid(password)) throw new Error('Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character.');
  if (!imageView) throw new Error('Image is required.');

  const existingUser = await User.findOne({ username });
  if (existingUser) throw new Error('Username already exists.');

  const user = new User({firstName, lastName, username, password, image: imageView });
  await user.save();

  return { message: 'User created successfully', user: { id: user._id, firstName, lastName, username, image: imageView } };
}


async function signIn(username, password) {
  if (!username) throw new Error('Username is required.');
  if (!password) throw new Error('Password is required.');

  const user = await User.findOne({ username });
  if (!user) throw new Error('User not found.');

  // Directly compare plain text passwords
  if (password !== user.password) throw new Error('Invalid password.');

  return { message: 'success', user: { id: user.id,firstName: user.firstName, lastName: user.lastName, username: user.username, image: user.image } };
}

module.exports = {
  signUp,
  isPasswordValid,
  signIn
};
