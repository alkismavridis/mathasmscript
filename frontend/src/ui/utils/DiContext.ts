import React from "react";
import HttpGraphqlRepository from "../../io/HttpGraphqlRepository";
import GraphqlRepository from "../../entities/GraphqlRepository";
import Router from "./Router";


const SERVER_URL = "http://localhost:8080/graphql"

export const GraphqlContext = React.createContext<GraphqlRepository>(new HttpGraphqlRepository(SERVER_URL));
export const RouterContext = React.createContext<Router>(null);
