import React, {useContext, useEffect, useState} from "react";
import PackageContent from "../../../../entities/model/PackageContent";
import {GraphqlContext, RouterContext} from "../../../utils/DiContext";

import "./PackageExplorer.scss";
import addNewPackage from "../../../../actions/AddNewPackage";
import getFirstMessageOf from "../../../../actions/GetFirstMessageOfErrors";
import StatementBox from "../StatementBox/StatementBox";
import Link from "../Link";
import Urls from "../../../utils/Urls";

interface Props {
    theoryId: number,
    packageName: string
}

export default function PackageExplorer(props: Props) {
    const router = useContext(RouterContext);
    const graphql = useContext(GraphqlContext);

    const [inputTextValue, setInputTextValue] = useState("");
    const [currentPackage, setCurrentPackage] = useState<PackageContent>(null);
    
    useEffect(() => fetchPackage(props.packageName), [props.packageName]);


    return <section className="package-explorer">
        <input
            type="text"
            value={inputTextValue}
            onChange={el => setInputTextValue(el.target.value)}/>
        <button onClick={() => router.route(Urls.getTheoryPackage(props.theoryId, inputTextValue))}>Go</button>
        <button onClick={createNewPackage}>New package</button>
        <button onClick={deletePackage}>Delete package</button>
        <Link href={Urls.getScriptCreationPage(props.theoryId, props.packageName)} openInNewTab={true}>New Script</Link>

        {currentPackage && <>
          <div>
              {currentPackage.packageData.path && <Link
                  href={getLinkToParent()}
                  className="app__shy-button package-explorer__package">
                  ..
                </Link>
              }
            {currentPackage.packages.map(p => <Link
              key={p.id}
              href={Urls.getTheoryPackage(props.theoryId, p.path)}
              className="app__shy-button package-explorer__package">
              {p.name}
            </Link>)}
          </div>
          <div>
              {currentPackage.statements.map(s => <StatementBox key={s.id} stmt={s} />)}
          </div>
        </>}
    </section>;


    function fetchPackage(path: string) {
        graphql
            .query(FETCH_PACKAGE, {theoryId: props.theoryId, packageName: path})
            .then(q => setCurrentPackage(q.ls));
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
            .then(() => router.route(getLinkToParent()))
            .catch(errors => window.alert(getFirstMessageOf(errors)))
    }
    
    function getLinkToParent() : string {
        const indexOfDot = currentPackage.packageData.path.lastIndexOf(".");
        const parent = indexOfDot < 0 ? "" : currentPackage.packageData.path.slice(0, indexOfDot);
        
        return Urls.getTheoryPackage(props.theoryId, parent);
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
