import FixedMasStatement from "./FixedMasStatement";
import InspectionComment from "./InspectionComment";
import MasImport from "./MasImport";

export default class ParseResult {
    success: boolean;
    scriptName: string;
    packageName: string;
    imports: MasImport[];
    exports: FixedMasStatement[];
    comments: InspectionComment[];
}
