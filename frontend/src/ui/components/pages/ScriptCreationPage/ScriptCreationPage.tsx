import React, {useContext, useState} from 'react';
import "./ScriptCreationPage.scss";
import {GraphqlContext} from "../../../utils/DiContext";
import ParserResult from "../../../../entities/model/ParserResult";
import ParsedResultPanel from "../../reusables/ParsedResultPanel/ParsedResultPanel";

interface Props {
    theoryId: number
}

export default function ScriptCreationPage(props: Props) {
    const graphql = useContext(GraphqlContext);

    const [scriptText, setScriptText] = useState("");
    const [parsedResult, setParsedResult] = useState(null as ParserResult);
    const [isSending, setSending] = useState(false);

    return <main className="app__page script-creation-page">
        <h1>Create theorem for theory {props.theoryId}</h1>

        <textarea
            className="script-creation-page__textarea"
            value={scriptText}
            onChange={e => setScriptText(e.target.value)}/>

        <section style={{margin: "16px 0"}}>
            <button onClick={executeScript}>Execute</button>
            <button onClick={commitScript}>Commit</button>
        </section>

        {isSending && <div>Sending...</div>}
        {parsedResult && <ParsedResultPanel data={parsedResult}/>}
    </main>;

    function executeScript() {
        if (isSending) return;
        setSending(true);

        graphql.query(`
            query($theoryId: Long!, $script: String!) {
                execute(theoryId: $theoryId, script: $script) {
                    status, packageName, scriptName
                    variables { name, kind, importUrl, value {
                        id, name, path, text, type
                    }}
                    comments { line, column, message, type }
                }
            }`, {theoryId: props.theoryId, script: scriptText}
        ).then(q => setParsedResult(q.execute))
            .finally(() => setSending(false));
    }

    function commitScript() {
        if (isSending) return;

        setSending(true);
        graphql.mutation(`
            mutation($theoryId: Long!, $script: String!) {
                commit(theoryId: $theoryId, script: $script) {
                    status, packageName, scriptName
                    variables { name, kind, importUrl, value {
                        id, name, path, text, type
                    }}
                    comments { line, column, message, type }
                }
            }`, {theoryId: props.theoryId, script: scriptText}
        ).then(m => setParsedResult(m.commit))
            .finally(() => setSending(false));
    }
}
