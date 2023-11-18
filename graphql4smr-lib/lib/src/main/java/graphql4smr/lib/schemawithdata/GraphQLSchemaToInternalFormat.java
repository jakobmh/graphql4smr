package graphql4smr.lib.schemawithdata;

import graphql.language.FieldDefinition;
import graphql.language.TypeDefinition;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphQLSchemaToInternalFormat {


    public static InternalFormat create(String graphQLschema){

        InternalFormat internalFormat = new InternalFormat();


        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(graphQLschema);

        System.out.println(typeDefinitionRegistry.types());

        for (Map.Entry<String, TypeDefinition> stringTypeDefinitionEntry : typeDefinitionRegistry.types().entrySet()) {
            String key = stringTypeDefinitionEntry.getKey();
            System.out.println(stringTypeDefinitionEntry.getValue());
            System.out.println();
            String tablename =  key.toLowerCase();

            TableSchema tableSchema = new TableSchema();
            tableSchema.table = tablename;
            tableSchema.table_type = key;
            tableSchema.fields = new LinkedList<>();

            for (Object o : stringTypeDefinitionEntry.getValue().getChildren().toArray()) {
                if(o instanceof FieldDefinition){
                    FieldDefinition fieldDefinition = (FieldDefinition) o;
                    String name = fieldDefinition.getName();
                    String fieldType = "text"; //fieldDefinition.getType();

                    FieldSchema fieldSchema = new FieldSchema();
                    fieldSchema.field_type = fieldType;
                    fieldSchema.field_name = name;
                    tableSchema.fields.add(fieldSchema);
                }
            }

            TableSchema tableSchemassuper = tableSchema;
            internalFormat.getTable_schema(new WriteAccess.WriteAccessInterface<List<TableSchema>>() {
                        @Override
                        public List<TableSchema> readAndWrite(List<TableSchema> tableSchemas) {
                            tableSchemas.add(tableSchemassuper);
                            return tableSchemas;
                        }
                    });
        }

        return internalFormat;
    }
}
