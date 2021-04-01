package eu.alkismavridis.mathasmscript.main

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ErrorType
import graphql.GraphQL
import graphql.GraphQLError
import graphql.execution.*
import graphql.kickstart.spring.web.boot.GraphQLWebAutoConfiguration
import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Config {
    @Bean
    fun graphql(schema: GraphQLSchema) : GraphQL {
        return GraphQL.newGraphQL(schema).build()
    }

    @Bean
    @Qualifier("graphqlObjectMapper")
    fun graphqlObjectMapper() : ObjectMapper {
        return ObjectMapper()
    }

    @Bean
    @Qualifier(GraphQLWebAutoConfiguration.QUERY_EXECUTION_STRATEGY)
    fun queryExecutionStrategy() : ExecutionStrategy {
        return AsyncExecutionStrategy(MasDataFetcherExceptionHandler())
    }

    @Bean
    @Qualifier(GraphQLWebAutoConfiguration.MUTATION_EXECUTION_STRATEGY)
    fun mutationExecutionStrategy() : ExecutionStrategy {
        return AsyncSerialExecutionStrategy(MasDataFetcherExceptionHandler())
    }

    private class MasDataFetcherExceptionHandler : DataFetcherExceptionHandler {
        override fun onException(params: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
            return DataFetcherExceptionHandlerResult
                    .newResult()
                    .error(MasGraphqlError(params))
                    .build()
        }

    }

    private class MasGraphqlError(private val params: DataFetcherExceptionHandlerParameters) : GraphQLError {
        override fun getMessage() = this.params.exception.message
        override fun getLocations() = listOf(this.params.sourceLocation)
        override fun getErrorType() = ErrorType.DataFetchingException
    }
}
