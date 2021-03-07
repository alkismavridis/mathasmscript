import FixedMasStatement from "./FixedMasStatement";
import MasPackage from "./MasPackage";

export default class Mutation {
    // commit: ParseResult!
    mvStatement: FixedMasStatement
    mkdir: MasPackage
    rmdir: Boolean
}
