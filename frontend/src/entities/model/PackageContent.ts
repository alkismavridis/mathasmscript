import FixedMasStatement from "./FixedMasStatement";
import MasPackage from "./MasPackage";

export default class PackageContent {
    packageData: MasPackage;
    statements: FixedMasStatement[];
    packages: MasPackage[];
}
