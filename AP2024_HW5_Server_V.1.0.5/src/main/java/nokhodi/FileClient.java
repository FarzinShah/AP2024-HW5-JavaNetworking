package nokhodi;

import java.io.*;
import java.net.*;

public class FileClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 5000;
        String filePath = "";
        int parts = 10;

        File file = new File(filePath);
        long fileSize = file.length();
        long partSize = fileSize / parts;
        long remainder = fileSize % parts;

        for (int i = 0; i < parts; i++) {
            long start = i * partSize;
            long end = (i == parts - 1) ? (start + partSize + remainder) : (start + partSize);

            new Thread(new FileSender(hostname, port, file, start, end)).start();
        }

    }
}

class FileSender implements Runnable {
    private String hostname;
    private int port;
    private File file;
    private long start;
    private long end;

    public FileSender(String hostname, int port, File file, long start, long end) {
        this.hostname = hostname;
        this.port = port;
        this.file = file;
        this.start = start;
        this.end = end;
    }

    public void run() {
        try (Socket socket = new Socket(hostname, port);
             OutputStream output = socket.getOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(output);
             RandomAccessFile fileReader = new RandomAccessFile(file, "r")) {

            dataOutput.writeUTF(file.getName());
            dataOutput.writeLong(file.length());
            dataOutput.writeLong(start);
            dataOutput.writeLong(end);

            fileReader.seek(start);

            byte[] buffer = new byte[4096];
            long bytesSent = 0;
            while (bytesSent < (end - start)) {
                int read = fileReader.read(buffer, 0, (int) Math.min(buffer.length, (end - start) - bytesSent));
                if (read == -1) break;
                dataOutput.write(buffer, 0, read);
                bytesSent += read;
            }

            System.out.println("Part sent: " + start + " to " + end);

        } catch (IOException ex) {
            System.out.println("File sender exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
