package io.github.cemartin01.graphmapper.graphql.schema;

import graphql.Scalars;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.github.cemartin01.graphmapper.graphql.coercing.CodeScalar;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

public class SchemaLoader {

    private static GraphQLSchema schema;

    public static GraphQLSchema loadSchema() {
        if (schema == null) {
            schema = new SchemaLoader().buildSchema();
        }
        return schema;
    }

    public GraphQLSchema buildSchema() {
        TypeDefinitionRegistry typeRegistry = null;

        try(InputStream stream = getClass().getClassLoader().getResourceAsStream("catering-schema.graphqls")) {
            typeRegistry = new SchemaParser().parse(
                    new InputStreamReader(stream, Charset.forName("UTF-8"))
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("catering-schema.graphqls does not exist");
        }

        TypeResolver characterTypeResolver = env -> {
            return (GraphQLObjectType) env.getSchema().getType("Lunch");
        };

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .scalar(Scalars.GraphQLByte)
                .scalar(CodeScalar.get())
                .type(newTypeWiring("Meal")
                        .typeResolver(characterTypeResolver)
                )
                .build();

        return new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
    }

}
