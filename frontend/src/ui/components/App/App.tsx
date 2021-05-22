import React, {useState} from 'react';
import './App.scss';
import Header from "../reusables/Header/Header";
import UrlUtils from "../../utils/UrlUtils";
import HomePage from "../pages/HomePage/HomePage";
import TheoryOverviewPage from "../pages/TheoryOverviewPage/TheoryOverviewPage";
import ScriptCreationPage from "../pages/ScriptCreationPage/ScriptCreationPage";
import Router from "../../utils/Router";
import {RouterContext} from "../../utils/DiContext";

function App() {
    const [, setUrl] = useState(window.location.href)
    const [router] = useState(() =>  new Router(newUrl => {
        window.history.replaceState(null, "", newUrl);
        setUrl(newUrl);
    }))
    
    const queryParams = UrlUtils.parseQueryParams(window.location.search);
    
    return <RouterContext.Provider value={router}>
        <Header/>
        {renderPageContent(queryParams)}
    </RouterContext.Provider>;
}

function renderPageContent(queryParams: any) {
    const pageName = (queryParams.p || "").toLowerCase();

    switch (pageName) {
        case "": return <HomePage/>;
        case "theory-overview": return <TheoryOverviewPage theoryId={queryParams.id} packageName={queryParams.package || ""}/>;
        case "create-script": return <ScriptCreationPage theoryId={queryParams.theoryId} packageName={queryParams.package}/>;
        default: return <div>404 - Page Not found :(</div>;
    }
}

export default App;
