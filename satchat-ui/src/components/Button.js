import React from "react";

const Button = ({ onClick, displayText }) => {
  return (
    <div>
      <button onClick={onClick}>{displayText}</button>
    </div>
  );
};

export default Button;
