import React from "react";

const Dropdown = ({
  dropdownValues,
  name,
  id,
  labelText,
  onSelect,
  selectedValue,
}) => {
  return (
    <div>
      <label htmlFor={id}>{labelText}</label>
      <select name={name} id={id} onChange={onSelect} value={selectedValue}>
        {dropdownValues.map((value) => (
          <option value={value.selectValue} key={value.selectValue}>
            {value.displayText}
          </option>
        ))}
      </select>
    </div>
  );
};

export default Dropdown;
