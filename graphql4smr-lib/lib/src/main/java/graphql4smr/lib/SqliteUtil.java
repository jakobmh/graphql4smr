package graphql4smr.lib;

import graphql4smr.lib.schemawithdata.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqliteUtil {


    public static void main(String[] args) {
        String sql = "PRAGMA foreign_keys=OFF;\n" +
                "BEGIN TRANSACTION;\n" +
                "CREATE TABLE article (id Text,\n" +
                "author_id Text,\n" +
                "FOREIGN KEY(\"author_id\") REFERENCES \"author\"(\"id\"));\n" +
                "INSERT INTO article VALUES('1','1');\n" +
                "INSERT INTO article VALUES('2','3');\n" +
                "CREATE TABLE author (id Text);\n" +
                "INSERT INTO author VALUES('123');\n" +
                "COMMIT;";

        InternalFormat temp = from(sql);
    }

    public static InternalFormat from(String dump){
        // regex
        // \s*((?<begintrans>BEGIN\s+TRANSACTION[^;]*)|(?<commit>COMMIT[^;]*)|(?<createtable>CREATE\s+TABLE\s*(?<createtabletablename>\w+)\s+\((?<createtabletablecontent>[^;]*)\)[\s|\n]*)|(?<insertinto>INSERT\s+INTO\s+(?<insertintotablename>\w+)\s+(\((?<insertintofields>[^\);]*)\))*\s+VALUES\s*(?<insertintovalues>[^;]*))|(?<everthingelse>[^;]*));
        // https://regex101.com/


        String pattern_str = "\\s*((?<begintrans>BEGIN\\s+TRANSACTION[^;]*)|(?<commit>COMMIT[^;]*)|(?<createtable>CREATE\\s+TABLE\\s*(?<createtabletablename>\\w+)\\s+\\((?<createtabletablecontent>[^;]*)\\)[\\s|\\n]*)|(?<insertinto>INSERT\\s+INTO\\s+(?<insertintotablename>\\w+)\\s+(\\((?<insertintofields>[^\\);]*)\\))*\\s+VALUES\\s*(?<insertintovalues>[^;]*))|(?<everthingelse>[^;]*));";
        Pattern pattern = Pattern.compile(pattern_str, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(dump);

        Config config = new Config();
        List<TableSchema > table_schema = new LinkedList<>();
        List< TableContent > table_content = new LinkedList<>();


        while (matcher.find()) {
            //String group = matcher.group();
            if(matcher.group("begintrans") != null){
                continue;
            }
            if(matcher.group("commit") != null){
                continue;
            }

            if(matcher.group("createtable") != null){
                String tablename = matcher.group("createtabletablename");
                String content = matcher.group("createtabletablecontent");
                // \s*((?<foreignkey>FOREIGN\s+KEY\s*\(\"(?<key>\w+)\"\)\s*REFERENCES\s*\"(?<referencetable>\w+)\"\s*\(\s*\"(?<referencefield>\w+)\"\)\s*)|(?<normalfield>(?<fieldname>\w+)\s+(?<fieldtype>\w+)\s*(?<primarykey>PRIMARY\s+KEY)?[^,]*)),?
                String tablepattern_str = "\\s*((?<foreignkey>FOREIGN\\s+KEY\\s*\\(\\\"(?<key>\\w+)\\\"\\)\\s*REFERENCES\\s*\\\"(?<referencetable>\\w+)\\\"\\s*\\(\\s*\\\"(?<referencefield>\\w+)\\\"\\)\\s*)|(?<normalfield>(?<fieldname>\\w+)\\s+(?<fieldtype>\\w+)\\s*(?<primarykey>PRIMARY\\s+KEY)?[^,]*)),?";
                Pattern tablepattern = Pattern.compile(tablepattern_str, Pattern.CASE_INSENSITIVE);
                Matcher tablematcher = tablepattern.matcher(content);
                TableSchema tableSchema = new TableSchema();
                tableSchema.table = tablename;
                tableSchema.table_type = tablename.substring(0, 1).toUpperCase() + tablename.substring(1);;
                tableSchema.fields = new LinkedList<>();

                while(tablematcher.find()){
                    if(tablematcher.group("foreignkey") != null){
                        String key = tablematcher.group("key");
                        String referencetable = tablematcher.group("referencetable");
                        String referencefield = tablematcher.group("referencefield");

                        tableSchema.fields.removeIf(e -> e.field_name.equals(key));

                        FieldSchema fieldSchema = new FieldSchema();
                        fieldSchema.field_name = key;
                        fieldSchema.field_type = "foreign_key";
                        fieldSchema.references_field = referencefield;
                        fieldSchema.references_table = referencetable;

                        tableSchema.fields.add(fieldSchema);
                    }
                    if(tablematcher.group("normalfield") != null){
                        String fieldname = tablematcher.group("fieldname");
                        String fieldtype = tablematcher.group("fieldtype");

                        if(!tableSchema.fields.stream().anyMatch(e -> e.field_name.equals(fieldname))){
                            FieldSchema fieldSchema = new FieldSchema();
                            fieldSchema.field_name = fieldname;
                            fieldSchema.field_type = "text";
                            if(tablematcher.group("primarykey") != null){
                                fieldSchema.primary_key = true;
                            }
                            tableSchema.fields.add(fieldSchema);
                        }
                    }
                }
                table_schema.add(tableSchema);
                continue;
            }

            if(matcher.group("insertinto") != null){
                String tablename = matcher.group("insertintotablename");
                String fields = matcher.group("insertintofields");
                // \s*('(?<itemwithquotes>[^']+)'|(?<itemwithoutquotes>\w+))\s*,?
                String fieldpattern_str = "\\s*('(?<itemwithquotes>[^']+)'|(?<itemwithoutquotes>\\w+))\\s*,?";
                Pattern fieldpattern = Pattern.compile(fieldpattern_str, Pattern.CASE_INSENSITIVE);
                Matcher fieldsname_matcher = fieldpattern.matcher(fields);
                List<String> fieldnames = new LinkedList<>();
                while(fieldsname_matcher.find()){
                    String item = null;
                    if(fieldsname_matcher.group("itemwithquotes")!=null){
                        item = fieldsname_matcher.group("itemwithquotes");
                    }
                    if(fieldsname_matcher.group("itemwithoutquotes")!=null){
                        item = fieldsname_matcher.group("itemwithoutquotes");
                    }
                    fieldnames.add(item);
                }
                //System.out.println(fields);
                String content = matcher.group("insertintovalues");
                Pattern createtablepattern1 = Pattern.compile("\\((?<item>[^\\)]*)\\)[^,]*");//\((?<item>[^\)]*)\)[^,]*
                Matcher createtablematcher1 = createtablepattern1.matcher(content);
                List<ColumnEntry> columnEntryList = new LinkedList<>();

                while(createtablematcher1.find()){
                    String column = createtablematcher1.group("item");
                    //System.out.println("column:" + column);
                    Matcher fielditem_matcher = fieldpattern.matcher(column);
                    int i = 0;
                    ColumnEntry columnEntry = new ColumnEntry();

                    while(fielditem_matcher.find()){
                        String item = null;
                        if(fielditem_matcher.group("itemwithquotes")!=null){
                            item = fielditem_matcher.group("itemwithquotes");
                        }
                        if(fielditem_matcher.group("itemwithoutquotes")!=null){
                            item = fielditem_matcher.group("itemwithoutquotes");
                        }
                        String key = fieldnames.get(i);
                        String value = item;
                        //System.out.println(key + ":" + value);
                        columnEntry.put(key,value);
                        i++;
                    }
                    columnEntryList.add(columnEntry);
                }
                table_content.add(new TableContent(tablename,columnEntryList));
                continue;
            }

            if(matcher.group("everthingelse") != null){
                continue;
            }

        }



        InternalFormat internalFormat = new InternalFormat(config,table_schema,table_content);

        return internalFormat;
    }



    public static String restoreanddump(String dump){
        try (Connection c = DriverManager.getConnection("jdbc:sqlite::memory:");){
            c.setAutoCommit(false);
            restore(c,dump);
            return dump(c);
        } catch ( Exception e ) {
            throw new RuntimeException();
        }
    }

    public static String dump(Connection connection) throws SQLException, IOException {

        File destination = File.createTempFile("prefix-", "-suffix");
        //File tempFile = File.createTempFile("MyAppName-", ".tmp");
        destination.deleteOnExit();

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("BACKUP TO " + destination.getPath());
        }

        connection.commit();
        connection.close();

        return loadFromFile(sqlite3databasetodump(destination));
    }


    public static void restore(Connection connection, String dump) throws SQLException, IOException {

        //File destination = File.createTempFile("prefix-", "-suffix");
        //File tempFile = File.createTempFile("MyAppName-", ".tmp");
        //destination.deleteOnExit();

        File destination = sqlite3databasetorestore(dump);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("RESTORE FROM " + destination.getPath());
        }

        connection.commit();
    }


    public static File sqlite3databasetodump(File input) throws IOException {
        File output = File.createTempFile("prefix-", "-suffix");
        //File tempFile = File.createTempFile("MyAppName-", ".tmp");
        output.deleteOnExit();

        try
        {
            String line;
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(new String[]{"/bin/sh", "-c", String.format("sqlite3 %s .dump > %s",input.getPath(),output.getPath())});

            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            p.waitFor();
            System.out.println("Done.");
        } catch (Exception err) {
            err.printStackTrace();
        }


        return output;
    }


    public static File sqlite3databasetorestore(String dump) throws IOException {
        File output = File.createTempFile("prefix-", "-suffix");
        //File tempFile = File.createTempFile("MyAppName-", ".tmp");
        output.deleteOnExit();


        File input = File.createTempFile("prefix-", "-suffix");
        //File tempFile = File.createTempFile("MyAppName-", ".tmp");
        input.deleteOnExit();


        writeStringToFile(input.getPath(),dump);
        try
        {
            String line;
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec(new String[]{"/bin/sh", "-c", String.format("sqlite3 %s < %s",output.getPath(),input.getPath())});

            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            p.waitFor();
            System.out.println("Done.");
        } catch (Exception err) {
            err.printStackTrace();
        }


        return output;
    }

    public static String loadFromFile(File destination) throws IOException {
        return new String(Files.readAllBytes(Paths.get(destination.getPath())));
    }

    public static void writeStringToFile(String filename, String content)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(content);
        writer.close();
    }
}
