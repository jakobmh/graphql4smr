package graphql4smr.demo.gnuplotwrapper;

import spark.Request;
import spark.Response;
import spark.Route;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.*;

public class GnuplotExample {


    public static void main(String[] args) {
        System.out.println(randompoints());

    }

    public static String randompoints(){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<10;i++){
            builder.append(i + " " + ((int)(Math.random()*100)) + "\n");
        }
        return builder.toString();
    }



    public static InputStream streampicture(File tempFileoutput) throws Exception {
        return streampicture(tempFileoutput,new LinkedList<Pair<String,String>>(){{add(new Pair<String,String>(randompoints(),"random 1"));add(new Pair<String,String>(randompoints(),"random 2"));}},"randomstuff");
    }
    public static InputStream streampicture(File tempFileoutput, List<Pair<String,String>> data, String title) throws Exception{
        File tempFile = File.createTempFile("prefix-", "-suffix");
        //File tempFile = File.createTempFile("MyAppName-", ".tmp");
        tempFile.deleteOnExit();

        //File tempFileoutput = File.createTempFile("prefix-", "-suffix");
        //tempFileoutput.deleteOnExit();

        String content_without = loadFromFile(data.size());
        System.out.println(content_without);
        List<Pair<File,String>> filelist = new LinkedList<>();
        for (Pair<String,String> datum : data) {
            File tempFileOne = File.createTempFile("prefix-", "-suffix");
            //File tempFile = File.createTempFile("MyAppName-", ".tmp");
            tempFileOne.deleteOnExit();
            writeStringToFile(tempFileOne.getPath(), datum.getValue0());
            filelist.add(new Pair<>(tempFileOne,datum.getValue1()));

        }


        /*
        File tempFileTwo = File.createTempFile("prefix-", "-suffix");
        //File tempFile = File.createTempFile("MyAppName-", ".tmp");
        tempFileTwo.deleteOnExit();
        writeStringToFile(tempFileTwo.getPath(), randompoints());
         */



        List<Object> paramslist = new LinkedList<>();
        paramslist.add(tempFileoutput.getPath());
        paramslist.add(title);

        for (Pair<File,String> file : filelist) {
            paramslist.add(file.getValue0());
            paramslist.add(file.getValue1());
        }

        Object[] params = paramslist.toArray();//new Object[]{tempFileoutput.getPath(),filelist.get(0),filelist.get(1)};
        String content = MessageFormat.format(content_without, params);

        //System.out.print(content);
        writeStringToFile(tempFile.getPath(), content);


        String path = tempFile.getPath();
        Runtime commandPrompt = Runtime.getRuntime();
        try {
            Process pr = commandPrompt.exec(new String[]{"gnuplot", path});
            pr.waitFor();
        } catch (IOException e){
            System.out.println("gnuplot missing");
            System.exit(1);
        }


        System.out.println(tempFileoutput);

        return new FileInputStream(tempFileoutput);
    }

    public static String loadFromFile(int amount) throws IOException {
        //return new String(Files.readAllBytes(Paths.get("helloworld.gp")));

        return "set terminal png size 1024,768\n" +
                "set output \"{0}\"\n" +
                //"set title \"{1}\"\n" +
                "set xlabel \"GraphQL Anfragetiefe\"\n" +
                "set ylabel \"Durchschnittliche Antwortzeit in ms\"\n" +
                "set key right bottom\n" +
                "plot "+
                IntStream.range(1, amount+1).asLongStream().mapToObj(e-> String.format("\"{%s}\" with linespoints title \"{%s}\"", e *2 ,e *2+1)).collect(Collectors.joining(","));
        //"plot \"{1}\" with linespoints title\"{2}\",\"{3}\" with linespoints title \"{4}\"";
        //String.format("plot \"{{0}}\" with linespoints title\"{{1}}\"", e *2 -1,e *2)
    }

    public static void writeStringToFile(String filename, String content)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(content);
        writer.close();
    }

}
