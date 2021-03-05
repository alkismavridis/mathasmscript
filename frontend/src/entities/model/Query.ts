import Theory from "./Theory";
import PackageContent from "./PackageContent";
import ParseResult from "./ParseResult";

export default class Query {
    theories: Theory[];
    ls: PackageContent;
    lsParent: PackageContent;
    execute: ParseResult;
}
