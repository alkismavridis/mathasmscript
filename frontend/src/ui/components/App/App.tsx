import React, {useState} from 'react';
import './App.scss';
import Header from "../reusables/Header/Header";
import UrlUtils from "../../utils/UrlUtils";
import HomePage from "../pages/HomePage/HomePage";
import HttpGraphqlRepository from "../../../io/HttpGraphqlRepository";
import GraphqlRepository from "../../../entities/GraphqlRepository";
import TheoryOverviewPage from "../pages/TheoryOverviewPage/TheoryOverviewPage";
import ScriptCreationPage from "../pages/ScriptCreationPage/ScriptCreationPage";


class DiContext {
    constructor(public graphqlRepo: GraphqlRepository) {
    }
}

const di = new DiContext(
    new HttpGraphqlRepository("http://localhost:8080/graphql")
);

const AppContext = React.createContext(di);

function App() {
    const [queryParams] = useState(() => UrlUtils.parseQueryParams(window.location.search));

    return <AppContext.Provider value={di}>
        <Header/>
        {renderPageContent(queryParams)}
    </AppContext.Provider>;
}

function renderPageContent(queryParams: any) {
    const pageName = (queryParams.p || "").toLowerCase();

    switch (pageName) {
        case "": return <HomePage/>;
        case "theory-overview": return <TheoryOverviewPage theoryId={queryParams.id}/>;
        case "create-script": return <ScriptCreationPage theoryId={queryParams.id}/>;
        default: return <div>404 - Page Not found :(</div>;
    }
}

export default App;
