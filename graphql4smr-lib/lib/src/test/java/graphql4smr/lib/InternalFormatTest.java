package graphql4smr.lib;

import com.google.gson.Gson;
import graphql4smr.lib.schemawithdata.*;
import graphql4smr.lib.util.Util;
import org.checkerframework.checker.fenum.qual.SwingTextOrientation;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class InternalFormatTest {

    public String include_str(String path){return Util.include_str(getClass(), path);}


    //@Test TODO
    public void testGraphQLSchemaDumper() {
        String example1 = include_str("schemawithdata/example2database.json");
        Gson gson = new Gson();
        InternalFormat internalFormat = gson.fromJson(example1, InternalFormat.class);
        GraphQLSchemaDumper graphQLSchemaDumper = new GraphQLSchemaDumper(internalFormat);

        System.out.println(graphQLSchemaDumper.createSQLiteDump());

        System.out.println(graphQLSchemaDumper.createGraphQLSchema());

    }


    //@Test //TODO dependendencies sqlite3 muss auf system installiert sein
    public void testCreateSQLiteDump() {
        String example1 = include_str("schemawithdata/example3database.json");
        Gson gson = new Gson();
        InternalFormat internalFormat = gson.fromJson(example1, InternalFormat.class);
        GraphQLSchemaDumper graphQLSchemaDumper = new GraphQLSchemaDumper(internalFormat);



        String sqldump = graphQLSchemaDumper.createSQLiteDump();

        System.out.println(sqldump);

        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite::memory:");
            c.setAutoCommit(false);
            SqliteUtil.restore(c,sqldump);

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }


        String dump = null;
        try {
            dump = SqliteUtil.dump(c);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(dump);

        System.out.println("Opened database successfully");
    }

    @Test
    public void loadSQLdump(){
        String example4 = include_str("schemawithdata/example4sqldump.sql");
        //System.out.println(example4);

        InternalFormat temp = SqliteUtil.from(example4);


        GraphQLSchemaDumper graphQLSchemaDumper = new GraphQLSchemaDumper(temp);

        //System.out.println(graphQLSchemaDumper.createSQLiteDump());
        System.out.println(graphQLSchemaDumper.toJsonPP());


        System.out.println(graphQLSchemaDumper.createGraphQLSchema());

    }
        @Test
    public void testCRUDDofInternalFormat() {
        String example1 = include_str("schemawithdata/example1database.json");

        Gson gson = new Gson();
        InternalFormat internalFormat = gson.fromJson(example1, InternalFormat.class);


        CRUDDofInternalFormat crudDofInternalFormat = new CRUDDofInternalFormat(internalFormat);

        System.out.println(crudDofInternalFormat.schemaprinter());
        System.out.println(crudDofInternalFormat.request("{select_author(id:\"100\") {id,name}}"));
        System.out.println(crudDofInternalFormat.request("{read_author{id,name}}"));
    }

    @Test
    public void testGraphQLSchemaToInternalFormat(){

        String graphQLschema = "type Author {\n" +
                "  id: String\n" +
                "  name: String\n" +
                "}";

        InternalFormat internalFormat = GraphQLSchemaToInternalFormat.create(graphQLschema);
        System.out.println(new CRUDDofInternalFormat(internalFormat).schemaprinter());
    }

    @Test
    public void testErdosRenyiSchema() {
        InternalFormat internalFormat = ErdosRenyiSchema.getSchema(false);
        System.out.println(new GraphQLSchemaDumper(internalFormat).createGraphQLSchema());
    }


    @Test
    public void testCycleDetection() {
        {
            InternalFormat internalFormat = ErdosRenyiSchema.getSchema(false);
            System.out.println(new GraphQLSchemaDumper(internalFormat).createGraphQLSchema());
            assertFalse(new CycleDetection(internalFormat).isCyclic());
        }

        {
            InternalFormat internalFormat = ErdosRenyiSchema.getSchema(true);
            System.out.println(new GraphQLSchemaDumper(internalFormat).createGraphQLSchema());
            assertTrue(new CycleDetection(internalFormat).isCyclic());
        }

    }

    @Test
    public void testToJson(){
        InternalFormat internalFormat1 = ErdosRenyiSchema.getSchema(false);
        String json_str1 = new GraphQLSchemaDumper(internalFormat1).toJsonPP();


        Gson gson = new Gson();
        InternalFormat internalFormat2 = gson.fromJson(json_str1, InternalFormat.class);

        String json_str2 = new GraphQLSchemaDumper(internalFormat2).toJsonPP();

        System.out.println(json_str2);
        assertEquals(json_str1,json_str2);
    }



}
