// graphql/GraphQLProvider.java
package com.function.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Objects;

public class GraphQLProvider {
    private static GraphQL graphQL;

    public static void init() {
        if (graphQL == null) {
            TypeDefinitionRegistry typeRegistry = new SchemaParser()
                .parse(new InputStreamReader(Objects.requireNonNull(
                    GraphQLProvider.class.getClassLoader().getResourceAsStream("graphql/schema.graphqls")
                )));
            
            RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder
                    .dataFetcher("obtenerRol", new RolQuery().obtenerRol())
                    .dataFetcher("listarRoles", new RolQuery().listarRoles())
                    .dataFetcher("usuariosByRol", new RolQuery().getUsuariosByRol())
                )  
                .type("Mutation", builder -> builder
                    .dataFetcher("crearRol", new RolMutation().crearRol())
                    .dataFetcher("actualizarRol", new RolMutation().actualizarRol())
                    .dataFetcher("eliminarRol", new RolMutation().eliminarRol()))
                .build();

            SchemaGenerator schemaGenerator = new SchemaGenerator();
            GraphQLSchema schema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
            graphQL = GraphQL.newGraphQL(schema).build();
        }
    }

    public static GraphQL getGraphQL() {
        if (graphQL == null) init();
        return graphQL;
    }
}
