import React, {useContext, useEffect, useState} from "react";
import PackageContent from "../../../../entities/model/PackageContent";
import {GraphqlContext} from "../../../utils/DiContext";

interface Props {
    theoryId: number,
    onPackageChanged: (p: PackageContent) => void
}

export default function PackageExplorer(props: Props) {
    const [inputTextValue, setInputTextValue] = useState("");
    const graphql = useContext(GraphqlContext);

    const [currentPackage, setCurrentPackage] = useState<PackageContent>(null);
    useEffect(() => fetchPackage(""), []);


    return <section className="package-explorer">
        <input
            type="text"
            value={inputTextValue}
            onChange={el => setInputTextValue(el.target.value)}/>
        <button onClick={() => fetchPackage(inputTextValue)}>Go</button>

        {currentPackage && <>
          <div>
            <button
              onClick={() => fetchParent(currentPackage.packageData.path)}
              className="app__shy-button package-explorer__package">
              ..
            </button>
            {currentPackage.packages.map(p => <button
              key={p.id}
              onClick={() => fetchPackage(p.path)}
              className="app__shy-button package-explorer__package">
              {p.path}
            </button>)}
          </div>
          <div>
              {currentPackage.statements.map(s => <button
                  key={s.id}
                  title={s.text}
                  onClick={() => window.alert("You clicked statement " + s.path + " ---\n " + s.text)}
                  className="app__shy-button package-explorer__stmt">
                  {s.path}
              </button>)}
          </div>
        </>}
    </section>;


    function fetchPackage(path: string) {
        graphql
            .query(FETCH_PACKAGE, {theoryId: props.theoryId, packageName: path})
            .then(q => {
                setCurrentPackage(q.ls);
                props.onPackageChanged(q.ls);
            });
    }

    function fetchParent(path: string) {
        graphql
            .query(FETCH_PARENT, {theoryId: props.theoryId, packageName: path})
            .then(q => {
                setCurrentPackage(q.lsParent);
                props.onPackageChanged(q.lsParent);
            });
    }
}

const FETCH_PACKAGE = `
    query($theoryId: Long!, $packageName:String!) {
        ls(theoryId: $theoryId, packageName:$packageName) {
            packageData { id, name, path }
            statements {id, name, path, text}
            packages{id, name, path}
        }
    }
`;

const FETCH_PARENT = `
    query($theoryId: Long!, $packageName:String!) {
        lsParent(theoryId: $theoryId, packageName:$packageName) {
            packageData { id, name, path }
            statements {id, name, path, text}
            packages{id, name, path}
        }
    }
`;
