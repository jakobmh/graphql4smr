
package graphql4smr.lib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql4smr.lib.util.LockBuilder;

import java.io.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class GraphQL4SMRStringWrapper implements Serializable {

    public Stringwrapper stringwrapper;


    public GraphQL4SMRStringWrapper() {
        stringwrapper = new Stringwrapper("Start");
    }

    public GraphQL4SMRStringWrapper(Class lockclass) {
        stringwrapper = new Stringwrapper("Start",lockclass);
    }
    public GraphQL4SMRStringWrapper(LockBuilder lockbuilder) {
        stringwrapper = new Stringwrapper("Start",lockbuilder);
    }

    private static String sendAsJson(Map<String, Object> toSpecificationResult) {
        Gson gson = new Gson();
        return gson.toJson(toSpecificationResult);
    }

    public  String requestjson(String graphQLQueryjson) {
        //System.out.println(graphQLQueryjson);
        JsonObject jsonObject = new JsonParser().parse(graphQLQueryjson).getAsJsonObject();
        jsonObject.isJsonObject();
        String query = jsonObject.get("query").getAsString();
        return request(query);
    }
    public  String request(String graphQLQuery){

        String schema = "type Query{stringwrapper: String} type Mutation {setstringwrapper(newstring:String):String}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("stringwrapper", new StaticDataFetcher(stringwrapper.getValue())))
                .type("Mutation", builder -> {

                    return builder.dataFetcher("setstringwrapper", new DataFetcher() {
                        @Override
                        public Object get(DataFetchingEnvironment environment) throws Exception {
                            //counter.setValue(counter.getValue()+1);

                            String newstring = environment.getArgument("newstring");
                            //System.out.println(zahl);
                            stringwrapper.setValue(i->{

                                String temp = i.getValue();
                                SleepUtil.delayrandom(); //simulates times in longer query
                                return newstring +temp;
                            });
                            return stringwrapper.getValue();
                        }
                    });
                    //return  builder;
                })
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