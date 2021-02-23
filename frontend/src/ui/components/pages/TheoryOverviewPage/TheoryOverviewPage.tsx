import React from 'react';
import PackageExplorer from "../../reusables/PackageExplorer/PackageExplorer";

interface Props {
    theoryId: number;
}

export default function TheoryOverviewPage(props: Props) {
    return <main className="app__page">
        <h1>Theory #{props.theoryId}</h1>

        <PackageExplorer theoryId={props.theoryId} onPackageChanged={c => {}} />
    </main>;
}

