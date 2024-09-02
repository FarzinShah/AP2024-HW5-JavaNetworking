package auth_server;

import gui.GUI_ServerStarter;
import org.mindrot.jbcrypt.BCrypt;
import util.Account;
import util.Constant;
import util.Source;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

import static util.Constant.filePath;


public class ClientHandler extends Thread {
    private Socket socket;
    static Thread guiThread;
    static boolean isLaunchable = false;
    static boolean firstLaunch = true;


    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            Constant.counter();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {
        String username = "";
        String password = "";
        String firstOutput = "";
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {
            while (true) {
            firstOutput = reader.readLine();
                System.out.println(firstOutput);
                if (firstOutput.equals("1")) {
                    login(username, password, writer, reader);
                } else if (firstOutput.equals("2")) {
                    signing(username, password, writer, reader);
                }

            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean authenticate(String username, String password) {

        for (int i = 0; i < Source.getInstance().accounts.size(); i++) {
            if (Source.getInstance().accounts.get(i).getUsername().equals(username) &&
                    checkPassword(password,Source.getInstance().accounts.get(i).getHashedPassword())){
                return true;
            }
        }
        return false;
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }




    public void login(String username,String password,PrintWriter writer,BufferedReader reader) throws IOException {
        while (true) {
           Constant.CURRENT_USERNAME = reader.readLine(); //??
            System.out.println(username);

//            if (username == null) break;
            writer.println("username received");

            Constant.CURRENT_PASSWORD = reader.readLine(); // ??
            if (password == null) break;
            writer.println("pass received");
            writer.flush();


//            Constant.CURRENT_USERNAME = username;

            if (authenticate(Constant.CURRENT_USERNAME, Constant.CURRENT_PASSWORD)) {
                writer.println("login Success!");
//                isLaunchable = true;
                //            new AuthServerStarter();
                new Thread(GUI_ServerStarter::getInstance).start();
                break;


            } else {
                writer.println("Login failed");
            }
            writer.flush();

            break;
        }
        if(isLaunchable){
            try {
                launcher();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void signing(String username, String password, PrintWriter writer, BufferedReader reader) throws IOException {
        while (true) {

            username = reader.readLine();

            if (username == null) break;
            writer.println("username received");

            password = reader.readLine();
            if (password == null) break;
            writer.println("pass received");
            writer.flush();
            Source.getInstance().accounts.add(new Account(username,password));
            Source.writeSingle(username+":"+Source.getInstance().accounts.get(Source.getInstance().accounts.size()-1).getHashedPassword());
            writer.println("creation successful!\n");
            writer.flush();
            break;




        }
    }
    private static void launcher() throws InterruptedException {
        new Thread(GUI_ServerStarter::getInstance).start();

        System.out.println("?????");

    }


}
