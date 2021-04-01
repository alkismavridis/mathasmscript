import MasPackage from "./MasPackage";
import ParserResult from "./ParserResult";

export default class Mutation {
    commit: ParserResult
    mkdir: MasPackage
    rmdir: Boolean
}
