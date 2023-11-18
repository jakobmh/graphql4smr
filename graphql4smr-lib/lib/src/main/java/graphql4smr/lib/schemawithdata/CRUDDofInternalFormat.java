package graphql4smr.lib.schemawithdata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.ExecutionStrategy;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.language.Field;
import graphql.language.Node;
import graphql.schema.*;
import graphql.schema.idl.SchemaPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.ml.neuralnet.MapUtils;

import java.util.*;

import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLObjectType.*;
import static graphql.schema.GraphQLFieldDefinition.*;
import static graphql.Scalars.*;
import static graphql4smr.lib.util.Util.sendAsJson;

import java.util.Map;
import java.util.stream.Collectors;


public class CRUDDofInternalFormat {

    private InternalFormat internalFormat;
    private GraphQLObjectType graphQLObjectType;
    private boolean graphQLObjectTypecached = false;
    private boolean cacheGraphQLboolean = false;
    private GraphQL cacheGraphQL;

    public CRUDDofInternalFormat(InternalFormat internalFormat){
        this.internalFormat = internalFormat;
    }

    public String schemaprinter(){
        CRUDDofInternalFormat crudDofSchema = new CRUDDofInternalFormat(internalFormat);
        SchemaPrinter.Options options = SchemaPrinter.Options.defaultOptions();
        options = options.includeIntrospectionTypes(false);
        options = options.includeScalarTypes(false);
        options = options.includeSchemaDefinition(false);
        options = options.includeDirectiveDefinitions(false);
        options = options.useAstDefinitions(false);
        options = options.includeDirectives(false);
        options = options.includeDirectiveDefinitions(false);
        return "" + new SchemaPrinter(options).print(crudDofSchema.create());
    }

    public GraphQLSchema create(){
        if(!graphQLObjectTypecached)cachegetGraphQLObjectType();

        GraphQLSchema schema = GraphQLSchema.newSchema().query(getGraphQLObjectType()).build();
        return schema;
    }

    public String requestjson(String graphQLQueryjson) {
        //System.out.println(graphQLQueryjson);
        JsonObject jsonObject = new JsonParser().parse(graphQLQueryjson).getAsJsonObject();
        jsonObject.isJsonObject();
        String query = jsonObject.get("query").getAsString();
        return request(query);
    }


    public void cachegetGraphQLObjectType(){
        graphQLObjectType = getGraphQLObjectType();
        graphQLObjectTypecached = true;
    }

    public void cacheGraphQL(){
        GraphQLSchema schema = GraphQLSchema.newSchema().query(graphQLObjectType).build();

        cacheGraphQL = GraphQL.newGraphQL(schema).doNotAddDefaultInstrumentations()
                //.instrumentation(new ExecutionStrategy())
                //.instrumentation(new TracingInstrumentation())
                //.instrumentation(new MaxQueryDepthInstrumentation(4))
                .build();
        cacheGraphQLboolean = true;
    }
    public String request(String graphQLQuery) {
        if(!graphQLObjectTypecached)cachegetGraphQLObjectType();
        if(!cacheGraphQLboolean)cacheGraphQL();

        ExecutionResult executionResult = cacheGraphQL.execute(graphQLQuery);

        //return executionResult.getData().toString();

        Map<String, Object> toSpecificationResult = executionResult.toSpecification();

        return  sendAsJson(toSpecificationResult);
    }


    public GraphQLObjectType getGraphQLObjectType(){
        /*
        GraphQLObjectType fooType = newObject()
                .name("Foo")
                .field(newFieldDefinition()
                        .name("bar")
                        .type(GraphQLString))
                .build();
        */
        HashMap<String,GraphQLObjectType.Builder> temp_list_builder = new HashMap<>();
        HashMap<String,String> tablenametotabletype = new HashMap<>();

        this.internalFormat.getTable_schema_unsave(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                for (TableSchema tableSchema : tableSchemas) {
                    GraphQLObjectType.Builder builder = newObject().name(tableSchema.table_type);
                    temp_list_builder.put(tableSchema.table,builder);
                    tablenametotabletype.put(tableSchema.table,tableSchema.table_type);

                }
                for (TableSchema tableSchema : tableSchemas) {
                    GraphQLObjectType.Builder builder = temp_list_builder.get(tableSchema.table);
                    for (FieldSchema field : tableSchema.fields) {
                        switch (field.field_type) {
                            case "text":
                                builder.field(newFieldDefinition().name(field.field_name).type(GraphQLString).build());
                                break;
                            case "foreign_key":
                                builder.field(newFieldDefinition().name(field.field_name).type(GraphQLTypeReference.typeRef(tablenametotabletype.get(field.references_table))).build());
                                break;
                            default:
                                new RuntimeException();
                        }
                    }

                }
                return tableSchemas;
            }
        });

        List<GraphQLObjectType> temp_list = new LinkedList<>();


        for (GraphQLObjectType.Builder graphQLObjectType : temp_list_builder.values()) {
            GraphQLObjectType testPojo = graphQLObjectType.build();
            temp_list.add(testPojo);
        }


        //.description("This is a test POJO")

        GraphQLObjectType.Builder builder = newObject().name("Query");

        for (GraphQLObjectType testPojo : temp_list) {
            List<String> primarykeylist = new LinkedList<>();
            TableSchema tableSchema = internalFormat.gettable_schemaByTableName(testPojo.getName().toLowerCase());
            for (FieldSchema field : tableSchema.fields) {
                if (field.primary_key != null && field.primary_key) {
                    primarykeylist.add(field.field_name);
                }
            }
            // parent schema
            GraphQLFieldDefinition.Builder selectbuilder = newFieldDefinition().name("select_" + testPojo.getName().toLowerCase())
                    .type(testPojo);
            {
                for (String s : primarykeylist) {
                    selectbuilder = selectbuilder.argument(newArgument()
                            .name(s)
                            .type(GraphQLString)
                    );
                }
                selectbuilder = selectbuilder.dataFetcher(new DataFetcher() {
                    @Override
                    public Object get(DataFetchingEnvironment environment) {
                                /*
                                Map<String,String> temp =  new HashMap<>();
                                temp.put("id","123");
                                temp.put("name","ichbims");
                                return temp;
                                */
                        Map<String, String> primarykeylistvalue = new HashMap<>();
                        for (String s : primarykeylist) {
                            primarykeylistvalue.put(s, environment.getArgument(s));
                        }

                        //System.out.println("id:" + id);


                        List<Optional<ColumnEntry>> liste = new LinkedList<>();
                        internalFormat.getTable_contentByTableName(testPojo.getName().toLowerCase(), new WriteAccess.WriteAccessInterface<TableContent>() {
                            @Override
                            public TableContent readAndWrite(TableContent tableContent) {
                                tableContent.getTable_values(new WriteAccess.WriteAccessInterface<List<ColumnEntry>>() {
                                    @Override
                                    public List<ColumnEntry> readAndWrite(List<ColumnEntry> columnEntries) {
                                        liste.add(
                                                columnEntries.stream()
                                                        .filter(filter -> {
                                                            //System.out.println("filter" + filter.get("id"));
                                                            //return filter.get("id").equals(id);
                                                            Map<String, String> primarykeylistvalue = new HashMap<>();
                                                            boolean returnvalue = true;
                                                            for (String s : primarykeylist) {
                                                                primarykeylistvalue.put(s, environment.getArgument(s));
                                                                returnvalue = returnvalue && filter.get(s).equals(primarykeylistvalue.get(s));
                                                                if (!returnvalue) return false;
                                                            }
                                                            return returnvalue;
                                                        }).findFirst());
                                        return columnEntries;
                                    }
                                });
                                return tableContent;
                            }
                        });
                        if (liste.get(0).isPresent()) {
                            return liste.get(0).get();
                        } else {
                            return null; // new LinkedList<>(); // TODO ERROR handling
                        }
                    }
                });

                selectbuilder.build();
            }

            GraphQLFieldDefinition.Builder readbuilder = newFieldDefinition().name("read_" + testPojo.getName().toLowerCase())
                    .type(GraphQLList.list(testPojo));
            {
                readbuilder.dataFetcher(new DataFetcher() {
                    @Override
                    public Object get(DataFetchingEnvironment environment) {
                                    /*
                                    List<Map<String,String>> templist = new LinkedList();
                                    Map<String,String> temp =  new HashMap<>();
                                    for (Map tableValue : ) {
                                        tableValue.keySet()
                                    }
                                    temp.put("id","123");
                                    temp.put("name","ichbims");
                                     templist.add(temp);
                                     */
                        //System.out.println("environment:" + environment.getSelectionSet().getFields().get(0).getName());
                        //System.out.println("environment2:" + environment.getSelectionSet().getImmediateFields());


                        //System.out.println();
                        LinkedList output = new LinkedList();

                        internalFormat.getTable_contentByTableName(testPojo.getName().toLowerCase(), new WriteAccess.WriteAccessInterface<TableContent>() {
                            @Override
                            public TableContent readAndWrite(TableContent tableContent) {
                                tableContent.getTable_values(new WriteAccess.WriteAccessInterface<List<ColumnEntry>>() {
                                    @Override
                                    public List<ColumnEntry> readAndWrite(List<ColumnEntry> columnEntries) {
                                        for (ColumnEntry columnEntry : columnEntries) {
                                            //String id = (String)map.get("id");
                                            //System.out.println(id);
                                            output.add(columnEntryExpand(environment.getSelectionSet().getImmediateFields(), testPojo.getName().toLowerCase(), columnEntry));
                                        }
                                        return columnEntries;
                                    }
                                });

                                return tableContent;
                            }
                        });

                        return output;
                                    /*
                                    return new LinkedList<Map>(){{
                                        add(new HashMap<String, Object>() {{
                                            put("id", "100");
                                            put("node3", new HashMap<String, String>() {{
                                                put("id", "101");
                                            }});
                                        }});
                                    }};

                                     */


                        //return internalFormat.getTable_contentByTableName(testPojo.getName().toLowerCase()).table_values;
                    }
                });
                readbuilder.build();
            }
            GraphQLFieldDefinition.Builder deletebuilder = newFieldDefinition().name("delete_" + testPojo.getName().toLowerCase())
                    .type(testPojo);
            {
                for (String s : primarykeylist) {
                    deletebuilder = deletebuilder.argument(newArgument()
                            .name(s)
                            .type(GraphQLString)
                    );
                }
                deletebuilder = deletebuilder.dataFetcher(new DataFetcher() {
                    @Override
                    public Object get(DataFetchingEnvironment environment) {
                                /*
                                Map<String,String> temp =  new HashMap<>();
                                temp.put("id","123");
                                temp.put("name","ichbims");
                                return temp;
                                */
                        Map<String, String> primarykeylistvalue = new HashMap<>();
                        for (String s : primarykeylist) {
                            primarykeylistvalue.put(s, environment.getArgument(s));
                        }

                        //System.out.println("id:" + id);


                        List<Optional<ColumnEntry>> liste = new LinkedList<>();
                        internalFormat.getTable_contentByTableName(testPojo.getName().toLowerCase(), new WriteAccess.WriteAccessInterface<TableContent>() {
                            @Override
                            public TableContent readAndWrite(TableContent tableContent) {
                                tableContent.getTable_values(new WriteAccess.WriteAccessInterface<List<ColumnEntry>>() {
                                    @Override
                                    public List<ColumnEntry> readAndWrite(List<ColumnEntry> columnEntries) {
                                        Optional<ColumnEntry> columnEntry_opt = columnEntries.stream()
                                                .filter(filter -> {
                                                    //System.out.println("filter" + filter.get("id"));
                                                    //return filter.get("id").equals(id);
                                                    Map<String, String> primarykeylistvalue = new HashMap<>();
                                                    boolean returnvalue = true;
                                                    for (String s : primarykeylist) {
                                                        primarykeylistvalue.put(s, environment.getArgument(s));
                                                        returnvalue = returnvalue && filter.get(s).equals(primarykeylistvalue.get(s));
                                                        if (!returnvalue) return false;
                                                    }
                                                    return returnvalue;
                                                }).findFirst();
                                        liste.add(columnEntry_opt);
                                        if (columnEntry_opt.isPresent()) {
                                            columnEntries.remove(columnEntry_opt.get());
                                        } else {
                                           // TODO ERROR handling
                                        }
                                        return columnEntries;
                                    }
                                });


                                return tableContent;
                            }
                        });
                        if (liste.get(0).isPresent()) {
                            return liste.get(0).get();
                        } else {
                            return null; // new LinkedList<>(); // TODO ERROR handling
                        }
                    }
                });

                deletebuilder.build();
            }

            builder = builder
                    .field(readbuilder)
                    .field(selectbuilder)
                    .field(deletebuilder)
            ;

        }
        GraphQLObjectType queryType = builder.build();
        return queryType;
    }

    private Map<String,Object> expand(List<SelectedField> fields,String tablename, String id){
        Map<String,Object> output = new HashMap();
        internalFormat.getTable_contentByTableName(tablename, new WriteAccess.WriteAccessInterface<TableContent>() {
            @Override
            public TableContent readAndWrite(TableContent tableContent) {
                tableContent.getTable_values(new WriteAccess.WriteAccessInterface<List<ColumnEntry>>() {
                    @Override
                    public List<ColumnEntry> readAndWrite(List<ColumnEntry> columnEntries) {
                        ColumnEntry temp = columnEntries.stream().filter(e -> id.equals(id)).findFirst().get();

                        output.putAll(columnEntryExpand(fields,tablename,temp));

                        return columnEntries;
                    }
                });
                return tableContent;
            }
        });
        return output;
    }

    private Map<String,Object> columnEntryExpand(List<SelectedField> fields,String tablename,ColumnEntry temp ){
        Map<String,Object> output = new HashMap();

        TableSchema tableSchema = internalFormat.gettable_schemaByTableName(tablename);

        for (SelectedField field : fields) {
            String name = field.getName();
            //System.out.println(name);
            //System.out.println(fields);

            //System.out.println(fields.stream().map(e -> e.get).collect(Collectors.joining(",")));
            //System.out.println(tableSchema.fields.stream().map(e -> e.field_name).collect(Collectors.joining(",")));
            FieldSchema temp2 = tableSchema.fields.stream().filter(e -> e.field_name.equals(name)).findFirst().get();
            switch (temp2.field_type){
                case "text":
                    output.put(name,temp.get(name));
                    break;
                case "foreign_key":
                    output.put(name,expand(field.getSelectionSet().getImmediateFields(), temp2.references_table,temp.get(name)));
                    break;
                default:
                    new RuntimeException();
            }
        }
        return output;
    }

    public String exampleReadRequest() {
        StringBuilder stringBuilder = new StringBuilder();
        internalFormat.getTable_schema(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                TableSchema temp = tableSchemas.get(0);
                stringBuilder.append("{read_"+temp.table+" {"+temp.fields.stream().map(e -> {
                    if(e.field_type.equals("text")){
                        return e.field_name;
                    }else{
                        return e.field_name + "{id}";
                    }
                }).collect(Collectors.joining(","))+"}}");

                return tableSchemas;
            }
        });
        return stringBuilder.toString();
    }


    public String exampleReadRequestRecursion(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        internalFormat.getTable_schema_unsave(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                TableSchema temp = tableSchemas.get(0);
                stringBuilder.append("{read_"+temp.table+" {"+temp.fields.stream().map(e -> {
                    if(e.field_type.equals("text")){
                        return e.field_name;
                    }else{
                        if(n==0) {
                            return e.field_name + "{id}";
                        }else{
                            return e.field_name + exampleReadRequestRecursionHelper(n-1,internalFormat.gettable_schemaByTableName(e.references_table));
                        }
                    }
                }).collect(Collectors.joining(","))+"}}");

                return tableSchemas;
            }
        });
        return stringBuilder.toString();
    }

    private String exampleReadRequestRecursionHelper(int n,TableSchema temp) {
        //TableSchema temp = internalFormat.table_schema.get(0);


        return "{"+temp.fields.stream().map(e -> {
            if(e.field_type.equals("text")){
                return e.field_name;
            }else{
                if(n==0) {
                    return e.field_name + "{id}";
                }else{
                    return e.field_name + exampleReadRequestRecursionHelper(n-1,internalFormat.gettable_schemaByTableName(e.references_table));
                }
            }
        }).collect(Collectors.joining(","))+"}";

    }
}
