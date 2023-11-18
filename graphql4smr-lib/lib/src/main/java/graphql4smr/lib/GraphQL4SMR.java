
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

import java.io.*;
import java.util.LinkedList;
import java.util.Map;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

import static graphql4smr.lib.util.Util.sendAsJson;

public class GraphQL4SMR implements Serializable {

    public Integerwrapper counter;


    public GraphQL4SMR() {
        counter = new Integerwrapper(0);
    }

    public GraphQL4SMR(Class lockclass) {
        counter = new Integerwrapper(0,lockclass);
    }



    public  String requestjson(String graphQLQueryjson) {
        //System.out.println(graphQLQueryjson);
        JsonObject jsonObject = new JsonParser().parse(graphQLQueryjson).getAsJsonObject();
        jsonObject.isJsonObject();
        String query = jsonObject.get("query").getAsString();
        return request(query);
    }
    public  String request(String graphQLQuery){

        String schema = "type Query{counter: Int} type Mutation {increment(zahl:Int):Int}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        System.out.println(typeDefinitionRegistry.types());

        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("counter", new StaticDataFetcher(counter.getValue())))
                .type("Mutation", builder -> {

                        return builder.dataFetcher("increment", new DataFetcher() {
                        @Override
                        public Object get(DataFetchingEnvironment environment) throws Exception {
                            //counter.setValue(counter.getValue()+1);

                            Integer zahl = environment.getArgument("zahl");
                            //System.out.println(zahl);
                            counter.setValue(i->{

                                Integer temp = i.getValue();
                                SleepUtil.delaystatic(); //simulates times in longer query
                                return temp + zahl;
                            });
                            return counter.getValue();
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