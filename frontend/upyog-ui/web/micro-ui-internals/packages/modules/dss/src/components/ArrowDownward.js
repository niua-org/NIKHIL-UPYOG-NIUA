import React from "react";

const Arrow_Downward = (props) => (
  <svg width="16px" height="16px" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" {...props}>
    <polygon points="0 0 24 0 24 24 0 24" fill="none"/>
    <polygon fill="#E54D42" fillRule="nonzero" points="4 12 5.41 13.41 11 7.83 11 20 13 20 13 7.83 18.58 13.42 20 12 12 4" transform="translate(12,12) rotate(180) translate(-12,-12)"/>
  </svg>
);

export function ArrowDownwardElement(marginRight, marginLeft) {
  return <Arrow_Downward style={{ display: "inline-block", verticalAlign: "baseline", marginRight: !marginRight ? "0px" : marginRight, marginLeft: !marginLeft ? "0px" : marginLeft }} />;
}
