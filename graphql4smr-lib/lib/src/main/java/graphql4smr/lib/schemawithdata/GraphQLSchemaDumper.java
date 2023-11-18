package graphql4smr.lib.schemawithdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import graphql4smr.lib.util.Util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GraphQLSchemaDumper {
    private InternalFormat internalFormat;
    public GraphQLSchemaDumper(InternalFormat internalFormat){
        this.internalFormat = internalFormat;
    }
    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this.internalFormat);
    }

    public String toJsonPP(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this.internalFormat);
    }

    public String createSQLiteDump(){
        //String out = "BEGIN TRANSACTION;\n";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BEGIN TRANSACTION;\n");
        this.internalFormat.getTable_schema(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                for(TableSchema tableSchema: tableSchemas){
                    stringBuilder.append("CREATE TABLE " + tableSchema.table + " (" + tableSchema.fields.stream()
                            .map(e -> {
                                switch (e.field_type){
                                    case "text":
                                        return e.field_name + " Text";
                                    case "foreign_key":
                                        return String.format("%s Text,\nFOREIGN KEY(\"%s\") REFERENCES \"%s\"(\"%s\")",e.field_name,e.field_name,e.references_table,e.references_field);
                                    default:
                                        throw new RuntimeException();
                                }


                            })
                            .collect(Collectors.joining(",\n")) +");");
                    stringBuilder.append("\n");
                };
                return tableSchemas;
            }
        });
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        internalFormat.getTable_content(new WriteAccess.WriteAccessInterface<List<TableContent>>() {
            @Override
            public List<TableContent> readAndWrite(List<TableContent> tableContents) {
                for(TableContent tableContent: tableContents){
                    internalFormat.getTable_schema(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
                        @Override
                        public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {

                            tableContent.getTable(new WriteAccess.WriteAccessInterface<String>() {


                                @Override
                                public String readAndWrite(String tableContent_table) {
                                    tableContent.getTable_values(new WriteAccess.WriteAccessInterface<List<ColumnEntry>>() {
                                        @Override
                                        public List<ColumnEntry> readAndWrite(List<ColumnEntry> columnEntries) {

                                    TableSchema tableSchemaList = tableSchemas.stream()
                                            .filter(e2 -> {
                                                return e2.table.equals(tableContent_table);
                                            })
                                            .findAny().get();
                                    String temp = tableSchemaList.fields.stream().map(e4 -> e4.field_name.toString()).collect(Collectors.joining(","));

                                    stringBuilder.append("INSERT INTO " + tableContent_table + " (" + temp + ") VALUES \n" + columnEntries.stream()
                                            .map(e -> "(" + tableSchemaList.fields.stream()
                                                    .map(e3 -> {
                                                        if (e.get(e3.field_name) != null) {
                                                            return "'" + e.get(e3.field_name).toString() + "'";
                                                        } else {
                                                            return "\"" + null + "\"";
                                                        }
                                                    })
                                                    .collect(Collectors.joining(",")) + ")"
                                            ).collect(Collectors.joining(",\n")) + ";");
                                    stringBuilder.append("\n");
                                    stringBuilder.append("\n");
                                            return columnEntries;
                                        }
                                    });

                                    return tableContent_table;
                                }
                            });
                            return tableSchemas;
                        }
                    });


                }

                return tableContents;
            }
        });

        stringBuilder.append("\nCOMMIT;\n");
        return stringBuilder.toString();
    }
    public String createGraphQLSchema() {
        return new CRUDDofInternalFormat(internalFormat).schemaprinter();
    }

    /*
    public String createGraphQLSchema(){

        String schema = "type Query{counter: Int} type Mutation {increment(zahl:Int):Int}";


        StringBuilder stringBuilder = new StringBuilder();
        this.internalFormat.getTable_schema(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                for(TableSchema tableSchema: tableSchemas){
                    stringBuilder.append("type " + tableSchema.table_type + "  {\n" + tableSchema.fields.stream()
                            .map(e -> {return "\t" +e.field_name + ":"+(e.references_field != null?
                                    tableSchemas.stream().filter(e2 -> e2.table.equals(e.references_table)).findFirst().get().table_type:"String")+"";})
                            .collect(Collectors.joining("\n")) +"\n};");
                    stringBuilder.append("\n");
                };
                return tableSchemas;
            }
        });


        stringBuilder.append("\n");
        stringBuilder.append("type Query{\n");

        this.internalFormat.getTable_schema(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                for(TableSchema tableSchema: tableSchemas){
                    Optional<FieldSchema> temp = tableSchema.fields.stream().filter(e -> e.primary_key!=null && e.primary_key).findFirst();
                    if(temp.isPresent()){
                        stringBuilder.append("\t"+tableSchema.table +"("+temp.get().field_name+": String): "+ tableSchema.table_type +"");
                        stringBuilder.append("\n");
                    }

                    stringBuilder.append("\t"+tableSchema.table +":[" +tableSchema.table_type +"]");
                    stringBuilder.append("\n");
                };
                return tableSchemas;
            }
        });

        stringBuilder.append("};");
        return stringBuilder.toString();
    }
     */
}
