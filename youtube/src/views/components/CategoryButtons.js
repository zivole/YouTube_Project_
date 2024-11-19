import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/CategoryButtons.css';
import { Link } from 'react-router-dom';



const CategoryButtons = ({ onCategoryChange }) => {
  return (
    <div>
      <Link to="/HomePage">
        <button className="btn btn-light category-btn" onClick={() => onCategoryChange('HomePage')}>All</button>
      </Link>
      <Link to="/Comedy">
        <button className="btn btn-light category-btn" onClick={() => onCategoryChange('Comedy')}>Comedy</button>
      </Link>
      <Link to="Music">
        <button className="btn btn-light category-btn" onClick={() => onCategoryChange('Music')}>Music</button>
      </Link>
      <Link to="Gaming">
        <button className="btn btn-light category-btn" onClick={() => onCategoryChange('Gaming')}>Gaming</button>
      </Link>
      <Link to="News">
        <button className="btn btn-light category-btn" onClick={() => onCategoryChange('News')}>News</button>
      </Link>
      <Link to="Sports">
        <button className="btn btn-light category-btn" onClick={() => onCategoryChange('Sports')}>Sports</button>
      </Link>
    </div>
  );
};

export default CategoryButtons;




//     <div className="col">
//       {/* Category Buttons */}
//       <div className="container d-flex justify-content-center my-3">
//         <div className="d-flex flex-wrap gap-2">
//           <button className="btn btn-light category-btn">All</button>
//           <button className="btn btn-light category-btn">Music</button>
//           <button className="btn btn-light category-btn">Gaming</button>
//           <button className="btn btn-light category-btn">News</button>
//           <button className="btn btn-light category-btn">Live</button>
//           <button className="btn btn-light category-btn">Sports</button>
//           {/* Add more categories as needed */}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default CategoryButtons;
