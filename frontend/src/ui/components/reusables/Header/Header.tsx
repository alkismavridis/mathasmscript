import React from "react";
import "./Header.scss";
import Link from "../Link";

export default function Header() {
    return <header className="header app__row-away-center">
        <Link href="/" className="app__shy-link">MathAsmScript</Link>
        <button className="app__shy-button">☰</button>
    </header>;
};
