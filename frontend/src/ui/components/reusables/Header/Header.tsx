import React from "react";
import "./Header.scss";

export default function Header() {
    return <header className="header app__row-away-center">
        <a href="/" className="app__shy-link">MathAsmScript</a>
        <button className="app__shy-button">☰</button>
    </header>;
};
