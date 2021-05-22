export default class Urls {
    static getTheoryOverview(id: number) : string { return "/?p=theory-overview&id="+id+"&package="; }
    
    static getTheoryPackage(theoryId: number, packageName: string) : string {
        return `/?p=theory-overview&id=${theoryId}&package=${packageName}`;
    }
    
    static getScriptCreationPage(theoryId: number, packageName: string) : string {
        return `/?p=create-script&theoryId=${theoryId}&package=${packageName}`;
    }
}
