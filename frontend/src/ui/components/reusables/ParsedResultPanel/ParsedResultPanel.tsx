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
        <section style={{display: "flex"}}>
            <div className="parsed-result-panel__theory-image"/>
            <div className="parsed-result-panel__var-grid">
                {props.data.variables.map(renderVariable)}
            </div>
        </section>
    </section>;
}

function renderVariable(variable: MasVariable) : ReactElement {
    return <React.Fragment key={variable.name}>
        {getVarKindSymbol(variable)}
        <div className="parsed-result-panel__var-type" data-type={variable.value.type}>{variable.name}</div>
        <div>{variable.value.text}</div>
    </React.Fragment>;
}

function getVarKindSymbol(variable: MasVariable) : ReactElement {
    switch(variable.kind) {
        case "IMPORT": return <div title="Imported statement">🡆</div>;
        case "EXPORT": return <div title="Exported statement">🡄</div>;
        case "LOCAL": return <div/>;
        default: return <div>?</div>;
    }
}
