import React, {useContext, useEffect, useState} from "react";
import {GraphqlContext} from "../../../utils/DiContext";
import Theory from "../../../../entities/model/Theory";
import Urls from "../../../utils/Urls";


export default function HomePage() {
    const graphql = useContext(GraphqlContext);
    const [theories, setTheories] = useState([] as Theory[]);
    useEffect(fetchTheories, []);

    return <main className="app__page">
        <h1>Welcome to Math Asm!</h1>
        {theories.map(th => <a href={Urls.getTheoryOverview(th.id)}>{th.name}</a>)}
    </main>;

    function fetchTheories() {
        graphql.query("{ theories {id,name}}")
            .then(q => setTheories(q.theories));
    }
}
