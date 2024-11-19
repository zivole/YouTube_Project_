export const timeSince = (date) => {
  const seconds = Math.floor((new Date() - date) / 1000);
  let interval = Math.floor(seconds / 31536000);

  if (interval >= 1) {
    return interval + (interval === 1 ? " year" : " years");
  }
  interval = Math.floor(seconds / 2592000);
  if (interval >= 1) {
    return interval + (interval === 1 ? " month" : " months");
  }
  interval = Math.floor(seconds / 86400);
  if (interval >= 1) {
    return interval + (interval === 1 ? " day" : " days");
  }
  interval = Math.floor(seconds / 3600);
  if (interval >= 1) {
    return interval + (interval === 1 ? " hour" : " hours");
  }
  interval = Math.floor(seconds / 60);
  if (interval >= 1) {
    return interval + (interval === 1 ? " minute" : " minutes");
  }
  return Math.floor(seconds) + (seconds === 1 ? " second" : " seconds");
};


export const formatViews = (views) => {
  const num = parseInt(views.replace(/,/g, ''), 10);
  
  if (isNaN(num)) {
    return views; // if the views is not a number, return it as is
  }

  if (num >= 1000000000) {
    return (num / 1000000000).toFixed(1).replace(/\.0$/, '') + 'B';
  }
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
  }
  return num.toString();
};
