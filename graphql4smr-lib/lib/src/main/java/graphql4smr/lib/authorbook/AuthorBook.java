package graphql4smr.lib.authorbook;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.*;
import graphql4smr.lib.Integerwrapper;
import graphql4smr.lib.SleepUtil;
import graphql4smr.lib.util.Util;

import java.io.Serializable;
import java.util.Map;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

import static graphql4smr.lib.util.Util.sendAsJson;


public class AuthorBook implements Serializable {

    public String include_str(String path){return Util.include_str(getClass(), path);}

    public AuthorBook() {
    }
    public AuthorBook(Class lockclass) {

    }

    public static void main(String[] args) {
        AuthorBook authorBook = new AuthorBook();
        authorBook.request("");
    }


    public  String requestjson(String graphQLQueryjson) {
        //System.out.println(graphQLQueryjson);
        JsonObject jsonObject = new JsonParser().parse(graphQLQueryjson).getAsJsonObject();
        jsonObject.isJsonObject();
        String query = jsonObject.get("query").getAsString();
        return request(query);
    }

    public  String request(String graphQLQuery) {

        String schema = include_str("authorbook.schema");
        //System.out.println(schema);

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);


        //System.out.println(typeDefinitionRegistry.types());

        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring.dataFetcher("bookById", new DataFetcher() {
                    @Override
                    public Object get(DataFetchingEnvironment environment) throws Exception {
                        String id = environment.getArgument("id");
                        return Book.getById(id);
                    }
                }))
                .type("Author", typeWiring -> typeWiring
                        .dataFetcher("authorId", new DataFetcher() {
                            @Override
                            public Object get(DataFetchingEnvironment environment) throws Exception {
                                String authorId = environment.getArgument("authorId");
                                return Author.getById(authorId);
                            }
                        })
                )
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        ExecutionResult executionResult = build.execute(graphQLQuery);

        //return executionResult.getData().toString();

        Map<String, Object> toSpecificationResult = executionResult.toSpecification();

        return  sendAsJson(toSpecificationResult);
    }

}
