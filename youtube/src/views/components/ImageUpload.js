import React from 'react';
import '../styles/sign_up&login.css';

const ImageUpload = ({ image, handleImageChange, className }) => (
  <div className={`signup_login_image-upload ${className}`}>
    <label htmlFor="file-input">
      <div className="signup_login_upload-button">
        {image ? <img src={image} alt="Selected" className=" rounded-circle" />   :  <i className="bi bi-plus-lg"></i> }
      </div>
    </label>
    <input  
    type="file" 
    id="file-input" 
    className="signup_login_file-input" 
    onChange={handleImageChange} 
    required />
    <small className="signup_login_form-text">Add Image</small>
  </div>
);

export default ImageUpload;



// export default ImageUpload;
// import React from 'react';

// const ImageUpload = ({ image, handleImageChange }) => {
//     return (
//         <div className="mb-3 signup_login_upload-button">
//             <label htmlFor="formFile" className="form-label file-input">Upload Image</label>
//             <input className="signup_login_upload-button" type="file" id="formFile" onChange={handleImageChange} />
//             {image ? (
//                 <div className="mt-3 rounded-circle">
//                     <img src={image} alt="Profile" width="100" height="100" />
//                 </div>
//             ) : (
//               <i className="bi bi-plus-lg"></i>
//             )
          
          
//           }
//              <small className="signup_login_form-text">Add Image</small>
//         </div>
//     );
// };

// export default ImageUpload;


// import React from 'react';
// import '../styles/sign_up&login.css';

// const ImageUpload = ({ image, handleImageChange }) => {
//     return (
//         <div className=" signup_login_upload-button">
//             <label htmlFor="formFile" className="form-label file-input">
//                 {image ? (
//                     <img src={image} alt="Profile" className="profile-image" />
//                 ) : (
//                     <div className="signup_login_upload-icon">
//                         <i className="bi bi-plus-lg"></i>
//                     </div>
//                 )}
//             </label>
//             <input className="signup_login_file-input" type="file" id="formFile" onChange={handleImageChange} />
//             <small className="signup_login_form-text">Add Image</small>
//         </div>
//     );
// };

// export default ImageUpload;


