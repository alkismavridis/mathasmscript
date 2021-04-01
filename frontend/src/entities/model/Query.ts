import Theory from "./Theory";
import PackageContent from "./PackageContent";
import ParserResult from "./ParserResult";

export default class Query {
    theories: Theory[];
    ls: PackageContent;
    lsParent: PackageContent;
    execute: ParserResult;
}
