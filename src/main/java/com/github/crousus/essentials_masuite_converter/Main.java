package com.github.crousus.essentials_masuite_converter;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        JOptionPane.showMessageDialog(new JFrame(""),"Make sure you have the 'userdata' folder of essentials in the same directory as this .jar File");
        String server = JOptionPane.showInputDialog("Please input the server name for your homes.");
        int playerCount = 0;
        int homeCount = 0;

        File file = new File("masuite_homes.sql");
        try {
            file.createNewFile();
            BufferedWriter writer = Files.newBufferedWriter(file.toPath(),StandardCharsets.UTF_8);

            writer.write("INSERT INTO `masuite_homes` (`id`, `name`, `owner`, `server`, `world`, `x`, `y`, `z`, `yaw`, `pitch`) VALUES ");

            File directory = new File("userdata");
            if(!directory.exists()) {
                JOptionPane.showMessageDialog(new JFrame(""),"No 'userdata' folder of essentials in the same directory as this .jar File found!");
            }
            File[] players = directory.listFiles();
            String sqlString = "";
            boolean isFirst = true;

            for (File current : players) {
                File config = new File("userdata/" + current.getName());
                DataInputStream input = new DataInputStream(new FileInputStream(config));

                Scanner fileScanner = new Scanner(input);
                boolean homesFound = false;
                int homeCompletion = 0;
                while(fileScanner.hasNext()){
                    String line = fileScanner.next();
                    fileScanner.useDelimiter("\\n");
                    if(!line.startsWith(" "))
                        homesFound = false;
                    if(homesFound){
                        if(homeCompletion < 7){
                            if(homeCompletion == 0) {
                                String lineConnector = isFirst ? "" : "),\n";
                                if(!isFirst)
                                    sqlString = sqlString.substring(0,sqlString.length()-2);
                                sqlString = sqlString + lineConnector + "(NULL, '" + line.substring(0, line.length() - 1).replaceAll(" ", "") + "', '"+ current.getName().replaceAll(".yml", "") + "', '" + server + "', " ;
                                isFirst = false;
                            }
                            else
                                sqlString = sqlString + "'"+ line.substring(line.indexOf(':')+1).replaceAll(" ", "")+"', ";
                            homeCompletion++;
                        }
                        if(homeCompletion == 7) {
                            homeCompletion = 0;
                            homeCount++;
                        }
                    }

                    if(line.equals("homes:")) {
                        homesFound = true;
                        playerCount++;
                    }
                }

            }
            if(sqlString.length() > 10) {
                sqlString = sqlString.substring(0, sqlString.length() - 2);
                sqlString = sqlString + ")";
                writer.write(sqlString);
                writer.flush();
                writer.close();
            }

            JOptionPane.showMessageDialog(new JFrame(""), homeCount + " homes of " + playerCount + " players converted into " + file.getName());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(""),e.getMessage());
        }

    }

}
