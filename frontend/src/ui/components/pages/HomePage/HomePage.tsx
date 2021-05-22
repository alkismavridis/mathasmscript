import React, {useContext, useEffect, useState} from "react";
import {GraphqlContext} from "../../../utils/DiContext";
import Theory from "../../../../entities/model/Theory";
import Urls from "../../../utils/Urls";
import Link from "../../reusables/Link";


export default function HomePage() {
    const graphql = useContext(GraphqlContext);
    const [theories, setTheories] = useState([] as Theory[]);
    useEffect(fetchTheories, []);

    return <main className="app__page">
        <h1>Welcome to Math Asm!</h1>
        {theories.map(th => <Link href={Urls.getTheoryOverview(th.id)} key={th.id}>{th.name}</Link>)}
    </main>;

    function fetchTheories() {
        graphql.query("{ theories {id,name}}")
            .then(q => setTheories(q.theories));
    }
}
