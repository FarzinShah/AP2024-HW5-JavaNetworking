package authClient;

import gui.FileClientGUI;
import util.Constant;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
     String hostname;
     int port;
    String firstInput;
     String username;
    String password;


    public Client() {
        firstInput = "";
        username = "";
        password = "";
        hostname = "localhost";
        port = Constant.CLI_PORT;

        try (Socket socket = new Socket(hostname, port)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Hello! \nif you have account, Enter 1. \nif not, for creating one Enter 2.");
                firstInput = consoleReader.readLine();
                if(firstInput.equals("1")) {
                    writer.println(firstInput);
                    System.out.print("Enter username: ");
                    username = consoleReader.readLine();
                    if (username == null || username.isEmpty()) break;
                    writer.println(username);
                    String response = reader.readLine();
                    System.out.println("Server response: " + response);


                    System.out.print("Enter password: ");
                    password = consoleReader.readLine();
                    if (password == null || password.isEmpty()) break;
                    writer.println(password);
                    String response1 = reader.readLine();
                    System.out.println("Server response: " + response1);
                    String response2 = reader.readLine();
                    System.out.println(response2);
                    if(response2.equals("login Success!")){
                        Constant.CURRENT_USERNAME = username;
                        new Thread(() -> {
//            new AuthServerStarter();
                            FileClientGUI window = new FileClientGUI();
                            try {
                                window.frame.setVisible(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }).start();
                    }
                }else if(firstInput.equals("2")){
                    writer.println(firstInput);
                    System.out.print("Enter username: ");
                    username = consoleReader.readLine();
                    if (username == null || username.isEmpty()) break;
                    writer.println(username);
                    String response = reader.readLine();
                    System.out.println("Server response: " + response);


                    System.out.print("Enter password: ");
                    password = consoleReader.readLine();
                    if (password == null || password.isEmpty()) break;
                    writer.println(password);
                    String response1 = reader.readLine();
                    System.out.println("Server response: " + response1);
                    String response2 = reader.readLine();
                    System.out.println(response2);
                    reader.readLine();
                }

//                String response2 = reader.readLine();
//                System.out.println("Server response: " + response);
            }
            System.out.println("???");

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
