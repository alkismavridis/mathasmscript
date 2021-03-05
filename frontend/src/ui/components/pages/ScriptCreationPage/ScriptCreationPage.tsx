import React, {useContext, useState} from 'react';
import "./ScriptCreationPage.scss";
import {GraphqlContext} from "../../../utils/DiContext";

interface Props {
    theoryId: number
}

export default function ScriptCreationPage(props: Props) {
    const graphl = useContext(GraphqlContext);
    const [scriptText, setScriptText] = useState("");

    return <main className="app__page script-creation-page">
        <h1>Hello from ScriptCreationPage {props.theoryId}</h1>

        <textarea
            className="script-creation-page__textarea"
            value={scriptText}
            onChange={e => setScriptText(e.target.value)}/>

        <section>
            <button onClick={executeScript}>Execute</button>
            <button>Submit</button>
        </section>
    </main>;

    function executeScript() {
        graphl.query(`
            query($theoryId: Long!, $script: String!) {
                execute(theoryId: $theoryId, script: $script) {
                    success, packageName, scriptName
                    exports { id, name, path, text, type }
                    imports { internalName, importUrl, statement {
                        id, name, path, text, type
                    }}
                    comments { line, column, message, type }
                }
            }`, {theoryId: props.theoryId, script: scriptText}
        ).then(q => console.log(q.execute));
    }
}
