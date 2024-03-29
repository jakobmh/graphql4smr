/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package graphql4smr.demo;


import graphql4smr.demo.gnuplotwrapper.GnuplotExample;
import graphql4smr.lib.GraphQL4SMR;
import graphql4smr.lib.SqliteUtil;
import graphql4smr.lib.schemawithdata.CRUDDofInternalFormat;
import graphql4smr.lib.schemawithdata.ErdosRenyiSchema;
import graphql4smr.lib.schemawithdata.InternalFormat;
import graphql4smr.lib.schemawithdata.ResourceHolder;
import org.apache.commons.cli.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static spark.Spark.*;




public class App {

    private static String helpstr =
            "plot1\n" +
            "plot2\n" +
            "example4\n" +
            "performancetest (TODO)\n" +
            "gnuplotexample\n" +
            "counterexample\n" +
            "erdosrenyischema\n" +
            "plottergui\n" +
            "--help";

    public static void main(String[] args) throws IOException {


        if (args.length == 0) {
            //new App();
            System.out.println(helpstr);
            return;
        }
        String firstArg = args[0];
        String[] appendingargs = Arrays.copyOfRange(args, 1, args.length);

        switch (firstArg) {
            case "plot1":
                createPlotsdirifnotexist();
                File outputfile = new File("plots/plot1.jpg");
                ImageIO.write(PlotterGui.createplot1(), "jpg", outputfile);
                break;
            case "plot2":
                createPlotsdirifnotexist();
                File outputfile2 = new File("plots/plot2.jpg");
                ImageIO.write(PlotterGui.createplot2(), "jpg", outputfile2);
                break;
            case "example4":
                InternalFormat internalFormat = SqliteUtil.from(ResourceHolder.singleton.example4);
                webAPIfromInternalFormat(internalFormat);
                break;
            case "performancetest"://TODO
                performancetest(appendingargs);
                break;
            case "gnuplotexample":
                try {
                    GnuplotExample.streampicture(new File("exampleoutput.png"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case "counterexample":
                counterexample();
                break;
            case "erdosrenyischema":
                erdosrenyischema();
                break;
            case "plottergui":
                new PlotterGui();
                break;
            case "--help":
                System.out.println(helpstr);
                break;
            default:
                System.out.println(helpstr);
                break;
        }




    }
    private static void createPlotsdirifnotexist(){
        File directory = new File("plots");
        if (! directory.exists()){
            directory.mkdir();
        }
    }

    public static void performancetest(String[] args){
        Options options = new Options();
        options.addRequiredOption("o", "output", true, "Specify output file");
        Option help = new Option("help", "print this message");
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            boolean verbose = cmd.hasOption("verbose");
            String outputFile = cmd.getOptionValue("output");
            try {
                GnuplotExample.streampicture(new File(outputFile));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("print image to " + outputFile);
            System.exit(0);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "Log messages to sequence diagrams converter", options );
            System.exit(1);
        }


    }

    public static void counterexample(){
        port(5555);

        staticFileLocation("/static");

        GraphQL4SMR graphql4smr = new GraphQL4SMR();


        // https://github.com/igorlima/spark-graphql-server/blob/master/src/main/java/com/mycompany/app/Main.java
        // are these lines needed ?
        options("/*", (request,response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null){
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request,response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        get("/hello", (req, res) -> "Hello World");

        post("/graphql", (request, response) -> {
            //String query2 = "{counter}";
            String query2 = request.body();
            String response2 = graphql4smr.requestjson(query2);
            //System.out.println(response);
            response.type("application/json");
            return response2;
        });
    }
    public static void erdosrenyischema() {
        InternalFormat internalFormat = ErdosRenyiSchema.getSchema(false);
        webAPIfromInternalFormat(internalFormat);
    }

    public static void webAPIfromInternalFormat(InternalFormat internalFormat){
        port(5555);

        staticFileLocation("/static");

        //System.out.println(new GraphQLSchemaDumper(internalFormat).createGraphQLSchema());

        CRUDDofInternalFormat crudDofInternalFormat = new CRUDDofInternalFormat(internalFormat);

        // https://github.com/igorlima/spark-graphql-server/blob/master/src/main/java/com/mycompany/app/Main.java
        // are these lines needed ?
        options("/*", (request,response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null){
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request,response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        get("/hello", (req, res) -> "Hello World");

        post("/graphql", (request, response) -> {
            //String query2 = "{counter}";
            String query2 = request.body();
            String response2 = crudDofInternalFormat.requestjson(query2);
            //System.out.println(response);
            response.type("application/json");
            return response2;
        });
    }

    public App(ProxyGraphQLSMR proxyGraphQLSMR){
        port(5555);

        staticFileLocation("/static");

        //GraphQL4SMR graphql4smr = new GraphQL4SMR();

        // https://github.com/igorlima/spark-graphql-server/blob/master/src/main/java/com/mycompany/app/Main.java
        // are these lines needed ?
        options("/*", (request,response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null){
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request,response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        get("/hello", (req, res) -> "Hello World");

        post("/graphql", (request, response) -> {
            //String query2 = "{counter}";
            String query2 = request.body();
            String response2 = proxyGraphQLSMR.proxy(query2);//graphql4smr.requestjson(query2);
            //System.out.println(response);
            response.type("application/json");
            return response2;
        });
        System.out.println("Server started on http://localhost:5555/");
    }

}
