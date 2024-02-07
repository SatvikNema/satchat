import React from "react";

const TextInput = ({ id, labelText, onChange, value }) => {
  return (
    <div>
      <label htmlFor={id}>{labelText}</label>
      <input type="text" id={id} onChange={onChange} value={value} />
    </div>
  );
};

export default TextInput;
