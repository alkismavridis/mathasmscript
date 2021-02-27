export default function getFirstMessageOf(errors: any[]) : string {
    const errorWithMessage = errors.find(e => e.message);
    const errorMessage = errorWithMessage? errorWithMessage.message : null;
    return errorMessage || "Graphql operation failed";
}
