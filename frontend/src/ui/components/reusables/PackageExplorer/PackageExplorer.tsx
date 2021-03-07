import React, {useContext, useEffect, useState} from "react";
import PackageContent from "../../../../entities/model/PackageContent";
import {GraphqlContext} from "../../../utils/DiContext";

import "./PackageExplorer.scss";
import addNewPackage from "../../../../actions/AddNewPackage";
import getFirstMessageOf from "../../../../actions/GetFirstMessageOfErrors";
import StatementBox from "../StatementBox/StatementBox";

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
        <button onClick={deletePackage}>Delete package</button>
        <button onClick={createNewScript}>New Script</button>

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
              {currentPackage.statements.map(s => <StatementBox key={s.id} stmt={s} />)}
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
            .then(m => setCurrentPackage(addNewPackage(m.mkdir, currentPackage)))
            .catch(errors => window.alert(getFirstMessageOf(errors)));
    }

    function deletePackage() {
        graphql.mutation(DELETE_PACKAGE_MUTATION, {
            theoryId: props.theoryId,
            path: currentPackage.packageData.path,
        })
            .then(() => fetchParent(currentPackage.packageData.path))
            .catch(errors => window.alert(getFirstMessageOf(errors)))
    }

    function createNewScript() {
        window.location.href = "/?p=create-script&id=" + props.theoryId
    }
}

const FETCH_PACKAGE = `
    query($theoryId: Long!, $packageName:String!) {
        ls(theoryId: $theoryId, packageName:$packageName) {
            packageData { id, name, path }
            statements {id, name, path, text, type}
            packages{id, name, path}
        }
    }
`;

const FETCH_PARENT = `
    query($theoryId: Long!, $packageName:String!) {
        lsParent(theoryId: $theoryId, packageName:$packageName) {
            packageData { id, name, path }
            statements {id, name, path, text, type}
            packages{id, name, path}
        }
    }
`;

const CREATE_PACKAGE_MUTATION = `
    mutation($theoryId: Long!, $name: String!, $parentPath: String!) {
        mkdir(name: $name, parentPath: $parentPath, theoryId: $theoryId) {
            id, name, path
        }
    }
`;

const DELETE_PACKAGE_MUTATION = `
    mutation($theoryId: Long!, $path: String!) {
        rmdir(path: $path, theoryId: $theoryId)
    }
`;
