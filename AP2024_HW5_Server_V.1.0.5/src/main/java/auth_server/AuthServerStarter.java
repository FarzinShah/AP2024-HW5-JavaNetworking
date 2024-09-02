package auth_server;

import util.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthServerStarter {
    AuthServerStarter(){
        try (ServerSocket serverSocket = new ServerSocket(Constant.CLI_PORT)) {
            System.out.println("Server is listening on port 5011");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                new ClientHandler(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
