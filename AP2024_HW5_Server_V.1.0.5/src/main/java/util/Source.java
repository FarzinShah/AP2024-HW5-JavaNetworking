package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static util.Constant.filePath;

public class Source {
    public static Source instance;
    public ArrayList<Account> accounts = new ArrayList<>(100);
    LinkedList<String> data = new LinkedList<>();
    public static HashMap<Integer,LinkedList<String>> idToShared;

    Source(){
        idToShared = new HashMap<>();
        accounts.add(new Account("a","a"));
        data.add("a"+":"+"a");
        writeToFile(filePath, data);

        for (int i = 4020001; i < 4020101; i++) {
            idToShared.put(i,new LinkedList<>());
        }

    }


    public static Source getInstance(){
        if(instance==null){
            instance=new Source();
        }
        return instance;
    }

    public static void writeSingle(String input){
        LinkedList<String> a = new LinkedList<>();
        a.add(input);
        writeToFile(filePath,a);
    }


    public static void writeToFile(String filePath, LinkedList<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine(); // Add a newline after each line of data
            }
            System.out.println("Data has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
