package graphql4smr.lib.schemawithdata;

import de.uniulm.vs.art.uds.UDScheduler;
import graphql4smr.lib.schemawithdata.*;
import graphql4smr.lib.util.LockBuilder;

import java.util.*;

public class ErdosRenyiSchema {



    public static InternalFormat getSchema(boolean cyclic) {
        double prob = 0.5;
        int N = 10;
        return getSchema(cyclic,prob,N);
    }
    public static InternalFormat getSchema(boolean b, double v, int n) {
        return getSchema(b,v,n,null);
    }
    public static InternalFormat getSchema(boolean cyclic, double prob, int N, LockBuilder lockBuilder){
        int[][] graph = new int[N][N];
        InternalFormat internalFormat;
        if(lockBuilder!=null) {
             internalFormat = new InternalFormat(lockBuilder);
        }else{
             internalFormat = new InternalFormat();
        }
        List<TableSchema> table_schema_list = new LinkedList<>();
        internalFormat.setTable_schema_unsave(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
            @Override
            public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                return table_schema_list;
            }
        });
        internalFormat.setTable_content_unsave(new WriteAccess.WriteAccessInterface<List<TableContent>>() {
            @Override
            public List<TableContent> readAndWrite(List<TableContent> tableContents) {
                return new LinkedList<>();
            }
        });


        int ids = 100;

        internalFormat.setConfig_unsave(new WriteAccess.WriteAccessInterface<Config>() {
            @Override
            public Config readAndWrite(Config o) {
                return new Config();
            }
        });

        for(int i=0; i<N;i++) {
            TableSchema tableSchema = new TableSchema();
            tableSchema.table_type = "Node"+i;
            tableSchema.table = "node"+i;
            tableSchema.fields = new LinkedList<>();
            FieldSchema fieldSchema = new FieldSchema();
            fieldSchema.field_name = "id";
            fieldSchema.field_type = "text";
            fieldSchema.primary_key = true;
            tableSchema.fields.add(fieldSchema);
            table_schema_list.add(tableSchema);
        }


        // Generate an directed random graph with N nodes
        // and probability p of any two nodes to be connected.
        for(int i=0; i < N; i++){
            for(int j= cyclic?0:(i+1); j< N; j++){
                //if(j==i)continue; selfreferencing
                double rand = Math.random();
                if(rand <= prob){
                    graph[i][j]=1;
                    FieldSchema fieldSchema = new FieldSchema();
                    fieldSchema.field_type = "foreign_key";
                    fieldSchema.field_name = "node"+j;
                    fieldSchema.references_table = "node"+j;
                    fieldSchema.references_field = "id";
                    int i_copy = i;
                    internalFormat.getTable_schema_unsave(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
                                @Override
                                public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                                    tableSchemas.get(i_copy).fields.add(fieldSchema);
                                    return tableSchemas;
                                }
                            });

                    //graph[j][i]= 1;

                }

            }

        }
        Map<String,List<String>> idmaplist= new HashMap<>();
        for(int i=0; i<N;i++) {
            List<String> templist = new LinkedList<>();
            templist.add(""+ids++);
            idmaplist.put("node"+i,templist);
        }

            // Filling with Content
        for(int i=0; i<N;i++) {
            TableContent tableContent;
            if(lockBuilder!=null){
                tableContent = new TableContent(lockBuilder);
            }else{
                tableContent = new TableContent();
            }

            int i_copy = i;
            tableContent.getTable_unsave(new WriteAccess.WriteAccessInterface<String>() {
                @Override
                public String readAndWrite(String s) {
                    return "node"+i_copy;
                }
            });
            tableContent.getTable_values_unsave(new WriteAccess.WriteAccessInterface<List<ColumnEntry>>() {
                @Override
                public List<ColumnEntry> readAndWrite(List<ColumnEntry> columnEntries) {
                    return new LinkedList<>();
                }
            });

            ColumnEntry map = new ColumnEntry();
            for (FieldSchema field : internalFormat.gettable_schemaByTableName("node" + i).fields) {
                for (String item : idmaplist.get("node" + i)) {
                    if(field.field_name.equals("id")){
                        map.put(field.field_name,item); //TODO id ref
                    }else{
                        map.put(field.field_name, idmaplist.get(field.field_name).get((int) (Math.random()*idmaplist.get(field.field_name).size()))); //TODO id ref
                    }
                }
            }
            tableContent.getTable_values_unsave(new WriteAccess.WriteAccessInterface<List<ColumnEntry>>() {
                @Override
                public List<ColumnEntry> readAndWrite(List<ColumnEntry> columnEntries) {
                    columnEntries.add(map);
                    return columnEntries;
                }
            });
            internalFormat.getTable_content_unsave(new WriteAccess.WriteAccessInterface<List<TableContent>>() {
                @Override
                public List<TableContent> readAndWrite(List<TableContent> tableContents) {
                    tableContents.add(tableContent);
                    return tableContents;
                }
            });
        }


        /*
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                System.out.print(graph[i][j]);
            }
            System.out.println();
        }
         */
        return internalFormat;
    }
}
