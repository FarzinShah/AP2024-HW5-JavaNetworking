package gui;

import util.Helper;

import java.io.*;
import java.net.Socket;

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

            dataOutput.writeUTF("upload");
            dataOutput.writeUTF(file.getName());
            dataOutput.writeLong(file.length());
            dataOutput.writeLong(start);
            dataOutput.writeLong(end);

            fileReader.seek(start);

            byte[] buffer = new byte[4096];
            long bytesSent = 0;
            while (bytesSent < (end - start)) {
//                try {
//                    Thread.sleep(10);
                    int read = fileReader.read(buffer, 0, (int) Math.min(buffer.length, (end - start) - bytesSent));
                    if (read == -1) break;
                    dataOutput.write(buffer, 0, read);
                    bytesSent += read;


//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
            FileClientGUI.notice(Helper.HASHTAG_HANDLER.append(Helper.HASHTAG_PROGRESS).toString());


            System.out.println("Part sent: " + start + " to " + end);
            Helper.PART_COUNTER++;
            if (Helper.PART_COUNTER == Helper.PARTS) {
                System.out.println("Done!");
                Helper.TURN += 1;
                FileClientGUI.notice(Helper.TURN + ". Done!");
                Helper.PART_COUNTER = 0;
            }


        } catch (IOException ex) {
            System.out.println("File sender exception: " + ex.getMessage());
            ex.printStackTrace();
        }

    }
}
