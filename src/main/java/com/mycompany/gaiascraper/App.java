/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//THIS IS THE ONE I SHARED
package com.mycompany.gaiascraper;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author Ohiwere Ahimie
 */

public class App implements ActionListener{
    String userId = "";
    JFrame frame;
    JLabel label;
    JTextField textField;
    JButton saveButton;
    JButton runButton;
    
    public App(){
        frame = new JFrame();
        readUserId();
        label = new JLabel("Account number");
        textField = new JTextField(userId);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        runButton = new JButton("Start");
        runButton.addActionListener(this);
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;  
        gbc.gridx = 0;  
        gbc.gridy = 0;  
        panel.add(label, gbc);
        gbc.gridx = 1;  
        gbc.gridy = 0;
        panel.add(textField, gbc);
        gbc.gridx = 2;  
        gbc.gridy = 0;
        panel.add(saveButton, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;  
        gbc.gridx = 1;  
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(runButton, gbc);
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Gaia Scraper");
        
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void archiveProfile(){
        try {
            Document doc = Jsoup.connect("https://www.gaiaonline.com/journal/?mode=archive&u=" + userId).get();
            Map<String, Integer> titles = new HashMap<>();
            Elements archive = doc.getElementsByClass("archives-list");
            // should only be 1 if it exists
            
            // System.out.println("Archive empty: " + archive.isEmpty());
            if(!archive.isEmpty()){
                Elements headings = archive.get(0).getElementsByTag("li");
                for(Element h : headings){
                    String title = h.text().substring(h.text().indexOf("]")+2);
                    // make the title pdf safe
                    title = title.replaceAll("[^a-zA-Z0-9_\\-\\s\\.\\(\\)\\{\\}\\[\\]~!\\,;\\'\\’@\\#$\\%\\+=]", " ");
                    while(title.contains("  ")){
                        title = title.replaceAll("  ", " ");
                    }
                    title = title.trim();
                    
                    if(titles.containsKey(title)){
                        titles.put(title, titles.get(title)+1);
                        title = title + " (" + (titles.get(title)-1) + ")";
                    }else{
                        titles.put(title, 1);
                    }
                    
                    System.out.println(title);

                    Elements links = h.getElementsByTag("a");
                     String entry = "https://www.gaiaonline.com" + links.get(0).attr("href");

     //                System.out.println("data/phantomjs/bin/phantomjs.exe --debug=yes --ignore-ssl-errors=true --ssl-protocol=any --web-security=true data/phantomjs/examples/rasterize.js \"" + entry +"\" \"" + userId + "/Diary/" + title + ".pdf\"");
                     Process p = Runtime.getRuntime().exec("data/phantomjs/bin/phantomjs.exe --debug=yes --ignore-ssl-errors=true --ssl-protocol=any --web-security=true data/phantomjs/examples/rasterize.js \"" + entry +"\" \"" + userId + "/Diary/" + title + ".pdf\"");
                     BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                     String line;
                     while ((line = reader.readLine()) != null){
                         // System.out.println(line);
                     }
                     p.waitFor();
                }
            }else{
                // if user has entries and they're not private, they might be in this other format

                Element firstGroup = doc.getElementById("archive-group");
                if(firstGroup != null){
                    archive = firstGroup.nextElementSiblings();
//                    System.out.println("num elements: " + archive.size());
                    for(Element e: archive){

                        Elements links = e.getElementsByTag("a");
                        if(!links.isEmpty()){
                            String entry = "https://www.gaiaonline.com" + links.get(0).attr("href");

                            String title = e.text().substring(e.text().indexOf("]")+2);
                            // make the title pdf safe
                            title = title.replaceAll("[^a-zA-Z0-9_\\-\\s\\.\\(\\)\\{\\}\\[\\]~!\\,;\\'\\’@\\#$\\%\\+=]", " ");
                            while(title.contains("  ")){
                                title = title.replaceAll("  ", " ");
                            }
                            title = title.trim();
                            if(titles.containsKey(title)){
                                titles.put(title, titles.get(title)+1);
                                title = title + " (" + (titles.get(title)-1) + ")";
                            }else{
                                titles.put(title, 1);
                            }
                            System.out.println(title);

             //                System.out.println("data/phantomjs/bin/phantomjs.exe --debug=yes --ignore-ssl-errors=true --ssl-protocol=any --web-security=true data/phantomjs/examples/rasterize.js \"" + entry +"\" \"" + userId + "/Diary/" + title + ".pdf\"");
                            Process p = Runtime.getRuntime().exec("data/phantomjs/bin/phantomjs.exe --debug=yes --ignore-ssl-errors=true --ssl-protocol=any --web-security=true data/phantomjs/examples/rasterize.js \"" + entry +"\" \"" + userId + "/Diary/" + title + ".pdf\"");
                            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                            String line;
                            while ((line = reader.readLine()) != null){
                                // System.out.println(line);
                            }
                            p.waitFor();
                        }
                    }
                }
            }
            showComplete();
            
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, e);
            showError();
        } catch (InterruptedException ex) {
          Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
          showError();
        } catch (Exception ex) {
          Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
          showError();
        }
    }

//    @author Brian Cole
//    Got from: https://coderanch.com/t/472574/java/extract-directory-current-jar
    private void readUserId(){
        File data = new File("data");
        if(!data.isDirectory()){
            String sourceDirectory = "/data";
            String writeDirectory = "data";
            final URL dirURL = getClass().getResource( sourceDirectory );
            final String path = sourceDirectory.substring( 1 );

            if( ( dirURL != null ) && dirURL.getProtocol().equals( "jar" ) ) {
                try {
                    final JarURLConnection jarConnection = (JarURLConnection) dirURL.openConnection();
                    System.out.println( "jarConnection is " + jarConnection );
                    
                    final ZipFile jar = jarConnection.getJarFile();
                    
                    final Enumeration< ? extends ZipEntry > entries = jar.entries(); // gives ALL entries in jar
                    
                    while( entries.hasMoreElements() ) {
                        final ZipEntry entry = entries.nextElement();
                        final String name = entry.getName();
                        // System.out.println( name );
                        if( !name.startsWith( path ) ) {
                            // entry in wrong subdir -- don't copy
                            continue;
                        }
                        final String entryTail = name.substring( path.length() );
                        
                        final File f = new File( writeDirectory + File.separator + entryTail );
                        if( entry.isDirectory() ) {
                            // if its a directory, create it
                            final boolean bMade = f.mkdir();
                            System.out.println( (bMade ? "  creating " : "  unable to create ") + name );
                        }
                        else {
                            System.out.println( "  writing  " + name );
                            final InputStream is = jar.getInputStream( entry );
                            final OutputStream os = new BufferedOutputStream( new FileOutputStream( f ) );
                            final byte buffer[] = new byte[4096];
                            int readCount;
                            // write contents of 'is' to 'os'
                            while( (readCount = is.read(buffer)) > 0 ) {
                                os.write(buffer, 0, readCount);
                            }
                            os.close();
                            is.close();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            else if( dirURL == null ) {
                throw new IllegalStateException( "can't find " + sourceDirectory + " on the classpath" );
            }
            else {
                // not a "jar" protocol URL
                throw new IllegalStateException( "don't know how to handle extracting from " + dirURL );
            }
            
        }
        
        try {
            File myObj = new File("data/userId.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
              userId = myReader.nextLine();
            }
            myReader.close();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
//        }
        
//        try {
//            File f = new File("data/userId.txt");
//            if(!f.isFile()){
//                // export default version from jar
//                URL url = getClass().getResource("/data/userId.txt");
//                String[] array = url.toString().split("!");
//                Map<String, String> env = new HashMap<>();
//                FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env);
//                final Path path = fs.getPath(array[1]);
//                userId = Files.readString(path);
//                f.createNewFile();
//                writeUserId(userId);
//                fs.close();
//            }else{
//                Scanner myReader = new Scanner(f);
//                while (myReader.hasNextLine()) {
//                  userId = myReader.nextLine();
//                }
//                myReader.close();
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
//        try {
//            File f = new File("data/phantomjs");
//            if(!f.isDirectory()){
//            
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    private void writeUserId(String id){
        try {
            FileWriter myWriter = new FileWriter("data/userId.txt");
            myWriter.write(id);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    void showComplete(){
        JFrame f = new JFrame();
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        p.setLayout(new GridLayout(0, 1));
        JTextArea a = new JTextArea("Archival process has completed. If nothing was archived, the user's account may be private.");
        a.setEditable(false);
        p.add(a);
        
        f.add(p, BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Gaia Scraper");
        
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
    }
    
    void showError(){
        JFrame f = new JFrame();
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        p.setLayout(new GridLayout(0, 1));
        JTextArea a = new JTextArea("Something went wrong. Please check that the website is up and the id you gave is correct. Try again later.");
        a.setEditable(false);
        p.add(a);
        
        f.add(p, BorderLayout.CENTER);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Gaia Scraper");
        
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
    }
    
    public static void main(String[] args) {
        new App();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == saveButton){
            writeUserId(textField.getText());
            userId = textField.getText();
        }
        
        if(e.getSource() == runButton){
            frame.dispose();
            archiveProfile();
        }
    }
}

