import React, {ReactElement} from 'react';
import ParserResult from "../../../../entities/model/ParserResult";
import "./ParsedResultPanel.scss";
import MasVariable from "../../../../entities/model/MasVariable";

interface Props {
    data: ParserResult;
}

export default function ParsedResultPanel(props: Props) {
    return <section className="parsed-result-panel">
        <b style={{marginRight: "24px"}}>Status:</b>
        <span
            className="parsed-result-panel__success"
            data-status={props.data.status}>
            {props.data.status}
        </span>

        <h5>Comments</h5>
        {props.data.comments.map(comment =>
            <div className="parsed-result-panel__comment" data-type={comment.type}>
                {comment.line}:{comment.column} --- {comment.message}
            </div>
        )}

        <h5>Variables</h5>
        <div className="parsed-result-panel__var-grid">
            {props.data.variables.map(renderVariable)}
        </div>
    </section>;
}

function renderVariable(variable: MasVariable) : ReactElement {
    return <>
        {getVarKindSymbol(variable)}
        <div className="parsed-result-panel__var-type" data-type={variable.value.type}>{variable.name}</div>
        <div>{variable.value.text}</div>
    </>;
}

function getVarKindSymbol(variable: MasVariable) : ReactElement {
    switch(variable.kind) {
        case "IMPORT": return <div>🡆</div>;
        case "EXPORT": return <div>🡄</div>;
        case "LOCAL": return <div>✱</div>;
        default: return <div>?</div>;
    }
}
