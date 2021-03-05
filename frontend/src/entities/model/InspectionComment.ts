import InspectionType from "./InspectionType";

export default class InspectionComment {
    line: number;
    column: number;
    message: string;
    type: InspectionType;
}
