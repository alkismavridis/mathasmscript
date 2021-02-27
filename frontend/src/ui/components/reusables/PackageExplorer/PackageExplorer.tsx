import React, {useContext, useEffect, useState} from "react";
import PackageContent from "../../../../entities/model/PackageContent";
import {GraphqlContext} from "../../../utils/DiContext";

import "./PackageExplorer.scss";
import addNewPackage from "../../../../actions/AddNewPackage";
import getFirstMessageOf from "../../../../actions/GetFirstMessageOfErrors";

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
        <button onClick={createNewPackage}>New package</button>

        {currentPackage && <>
          <div>
              {currentPackage.packageData.path && <button
                  onClick={() => fetchParent(currentPackage.packageData.path)}
                  className="app__shy-button package-explorer__package">
                  ..
                </button>
              }
            {currentPackage.packages.map(p => <button
              key={p.id}
              onClick={() => fetchPackage(p.path)}
              className="app__shy-button package-explorer__package">
              {p.name}
            </button>)}
          </div>
          <div>
              {currentPackage.statements.map(s => <button
                  key={s.id}
                  title={s.text}
                  onClick={() => window.alert("You clicked statement " + s.path + " ---\n " + s.text)}
                  className="app__shy-button package-explorer__stmt">
                  {s.name}
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

    function createNewPackage() {
        const packageName = window.prompt('New package name...');
        if (!packageName) return;

        graphql.mutation(CREATE_PACKAGE_MUTATION, {
            theoryId: props.theoryId,
            name: packageName,
            parentPath: currentPackage.packageData.path
        })
            .then(m => setCurrentPackage(addNewPackage(m.createPackage, currentPackage)))
            .catch(errors => window.alert(getFirstMessageOf(errors)));
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

const CREATE_PACKAGE_MUTATION = `
    mutation($theoryId: Long!, $name: String!, $parentPath: String!) {
        createPackage(name: $name, parentPath: $parentPath, theoryId: $theoryId) {
            id, name, path
        }
    }
`;
