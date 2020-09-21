package eu.alkismavridis.mathasmscript.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {
    @Bean
    fun graphql(schema:GraphQLSchema) : GraphQL {
        return GraphQL
                .newGraphQL(schema)
                .build()
    }

    @Bean
    @Qualifier("graphqlObjectMapper")
    fun graphqlObjectMapper() : ObjectMapper {
        return ObjectMapper()
    }
}
