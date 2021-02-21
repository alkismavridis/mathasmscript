package eu.alkismavridis.mathasmscript.infrastructure.api

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.GraphQL
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class GraphqlHttpListener(
        private val graphQl: GraphQL,
        private val graphqlObjectMapper: ObjectMapper) {

    @CrossOrigin(origins = ["*"])
    @PostMapping(value = ["graphql"])
    fun executeGraphql(
            @RequestParam(required = true) query: String,
            @RequestParam(required = false, name = "variables") variablesString: String?,
            @RequestParam(required = false) operationName:String?
    ) : ResponseEntity<String> {
        try {
            val ctx = GraphqlRequestContext()
            val variables:Map<String, Any?> = this.getVariables(variablesString)

            val queryObject = ExecutionInput.Builder()
                    .query(query)
                    .variables(variables)
                    .operationName(operationName)
                    .context(ctx)
                    .build()

            val result = this.graphQl.execute(queryObject)

            return ResponseEntity
                    .status(if (result.errors.isEmpty()) HttpStatus.OK else HttpStatus.BAD_REQUEST)
                    .body(this.graphqlObjectMapper.writeValueAsString(result.toSpecification()))
        } catch (e:Throwable) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("{\"success\":false, \"errorMessage\":\"${e.message}\"}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getVariables(variablesString: String?) : Map<String, Any?> {
        return if (variablesString == null)
            emptyMap() else
            this.graphqlObjectMapper.readValue(variablesString, Map::class.java) as Map<String, Any?>

    }
}

