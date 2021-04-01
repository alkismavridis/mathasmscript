import InspectionComment from "./InspectionComment";
import MasVariable from "./MasVariable";

export default class ParserResult {
    status: string;
    scriptName: string;
    packageName: string;
    variables: MasVariable[];
    comments: InspectionComment[];
}
