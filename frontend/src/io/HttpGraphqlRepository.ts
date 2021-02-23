import Mutation from "../entities/model/Mutation";
import Query from "../entities/model/Query";
import GraphqlRepository from "../entities/GraphqlRepository";


export default class HttpGraphqlRepository implements GraphqlRepository {
    constructor(private url: string) {}

    private run(query: string, params?: any): Promise<any> {
        const data = {
            query: query,
            variables: params
        };

        return fetch(this.url, {
            method: "post",
            headers: new Headers({
                "accept": "*/*",
                "content-type": "application/json",
                "sec-ch-ua": "\"Chromium\";v=\"88\", \"Google Chrome\";v=\"88\", \";Not\\\\A\\\"Brand\";v=\"99\"",
                "sec-ch-ua-mobile": "?1",
                "sec-fetch-mode": "cors",
            }),
            body: JSON.stringify(data)
        })
            .then(data => data.json())
            .then((response: any) => {
                if (response.errors || !response.data)
                    throw response.errors;
                else
                    return response.data;
            });
    }

    mutation(query: string, params?: any): Promise<Mutation> {
        return this.run(query, params) as Promise<Mutation>;
    }

    query(query: string, params?: any): Promise<Query> {
        return this.run(query, params) as Promise<Query>;
    }
}
