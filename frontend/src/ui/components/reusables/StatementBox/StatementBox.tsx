import React from 'react';
import FixedMasStatement from "../../../../entities/model/FixedMasStatement";
import "./StatementBox.scss";

interface Props {
    stmt: FixedMasStatement
}

export default function StatementBox(props: Props) {
    return <button
            className="app__shy-button statement-box"
            data-type={props.stmt.type}
            onClick={() => window.alert("You clicked statement " + props.stmt.path + " ---\n " + props.stmt.text)}
    >
        <div className="app__strong-small">{props.stmt.name}</div>
        <div>{props.stmt.text}</div>
    </button>;
}
