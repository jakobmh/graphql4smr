package graphql4smr.demo;

import graphql4smr.demo.gnuplotwrapper.GnuplotExample;
import graphql4smr.lib.performancetest.Performancetest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.javatuples.*;

public class PlotterGui {

    public static void main(String[] args){
        new PlotterGui();
    }

    public PlotterGui(){
        BufferedImage img = createplot1();
        PlotterGui.showimage(img);
    }

    public static BufferedImage createplot1(){
        return createplot(false);
    }

    public static BufferedImage createplot2(){
        return createplot(true);
    }

    public static BufferedImage createplot(Boolean disableLocks){
        File tempFileoutput = null;
        try {
            tempFileoutput = File.createTempFile("prefix-", "-suffix");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempFileoutput.deleteOnExit();

        String warmup = Performancetest.test(disableLocks);

        String data3 = Performancetest.testmultithreadeduds(disableLocks);
        String data = Performancetest.test(disableLocks);
        String data2 = Performancetest.testmultithreaded(disableLocks);


        String title = "Anfrage Tiefe";
        try {
            GnuplotExample.streampicture(tempFileoutput,new LinkedList<Pair<String,String>>(){{
                add(new Pair(data,"Single-Threaded"));
                add(new Pair(data2,"Multi-Threaded"));
                add(new Pair(data3,"UDS Multi-Threaded"));


            }},title);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(tempFileoutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("pathtopic: " + tempFileoutput.getPath());

        return img;
    }


    public static void showimage(BufferedImage bufferedImage){

        JFrame jFrame = new JFrame();

        //panel.setSize(500,640);

        ImageIcon icon = new ImageIcon(bufferedImage);
        JScrollPane panel = new JScrollPane(new JLabel(icon));
        panel.setBackground(Color.CYAN);
        //panel.add(label);
        jFrame.getContentPane().add(panel);
        panel.setSize(1024+10,768+10);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
