import PackageContent from "../entities/model/PackageContent";
import MasPackage from "../entities/model/MasPackage";

export default function addNewPackage(newPackage: MasPackage, target: PackageContent) : PackageContent {
    return {
        packageData: target.packageData,
        statements: target.statements,
        packages: [...target.packages, newPackage]
    };
}
