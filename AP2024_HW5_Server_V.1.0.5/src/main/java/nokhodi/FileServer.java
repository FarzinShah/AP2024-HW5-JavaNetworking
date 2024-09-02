package nokhodi;

import java.io.*;
import java.net.*;

public class FileServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server is listening on port 5000");

            while (true) {
                Socket socket = serverSocket.accept();
                new FileReceiver(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

class FileReceiver extends Thread {
    private Socket socket;

    public FileReceiver(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (InputStream input = socket.getInputStream();
             DataInputStream dataInput = new DataInputStream(input)) {

            String fileName = dataInput.readUTF();
            long fileSize = dataInput.readLong();
            long start = dataInput.readLong();
            long end = dataInput.readLong();

            System.out.println("Receiving part of the file: " + fileName + " from " + start + " to " + end);

            RandomAccessFile file = new RandomAccessFile("received_" + fileName, "rw");
            file.seek(start);

            byte[] buffer = new byte[4096];
            long bytesRead = 0;
            while (bytesRead < (end - start)) {
                int read = dataInput.read(buffer, 0, (int) Math.min(buffer.length, (end - start) - bytesRead));
                if (read == -1) break;
                file.write(buffer, 0, read);
                bytesRead += read;
            }

            file.close();
            System.out.println("Part received: " + start + " to " + end);

        } catch (IOException ex) {
            System.out.println("File receiver exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
