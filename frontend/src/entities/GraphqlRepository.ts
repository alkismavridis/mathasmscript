import Mutation from "./model/Mutation";
import Query from "./model/Query";

export default interface GraphqlRepository {
    query(query: string, params?: any): Promise<Query>;
    mutation(query: string, params?: any): Promise<Mutation>;
}
