package nokhodi;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerDownloader {

    private JFrame frame;
    private JTextArea logArea;
    private ServerSocket serverSocket;
    private List<FileHandler> handlers = new ArrayList<>();
    private File saveDirectory;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ServerDownloader window = new ServerDownloader();
                window.frame.setVisible(true);
                window.startServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ServerDownloader() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JButton btnSetDirectory = new JButton("Set Save Directory");
        btnSetDirectory.addActionListener(e -> chooseSaveDirectory());
        panel.add(btnSetDirectory);
    }

    private void chooseSaveDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            saveDirectory = fileChooser.getSelectedFile();
            log("Save directory set to: " + saveDirectory.getAbsolutePath());
        }
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000);
                log("Server started on port 5000");

                while (true) {
                    Socket socket = serverSocket.accept();
                    FileHandler handler = new FileHandler(socket);
                    handlers.add(handler);
                    new Thread(handler).start();
                }
            } catch (IOException e) {
                log("Server error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    class FileHandler implements Runnable {
        private Socket socket;

        public FileHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (DataInputStream dataInput = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream())) {

                String command = dataInput.readUTF();

                if (command.equals("upload")) {
                    receiveFile(dataInput);
                } else if (command.equals("download")) {
                    sendFile(dataInput, dataOutput);
                }

            } catch (IOException ex) {
                log("File handler error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        private void receiveFile(DataInputStream dataInput) throws IOException {
            String fileName = dataInput.readUTF();
            long fileSize = dataInput.readLong();
            long start = dataInput.readLong();
            long end = dataInput.readLong();

            File file;
            if (saveDirectory != null) {
                file = new File(saveDirectory, "received_" + fileName);
            } else {
                file = new File("received_" + fileName);
            }

            try (RandomAccessFile fileWriter = new RandomAccessFile(file, "rw")) {
                fileWriter.seek(start);

                byte[] buffer = new byte[4096];
                long bytesReceived = 0;
                while (bytesReceived < (end - start)) {
                    int read = dataInput.read(buffer, 0, (int) Math.min(buffer.length, (end - start) - bytesReceived));
                    if (read == -1) break;
                    fileWriter.write(buffer, 0, read);
                    bytesReceived += read;
                }

                log("Received part: " + start + " to " + end + " of file: " + fileName);
            }
        }

        private void sendFile(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
            String fileName = dataInput.readUTF();
            File file;
            if (saveDirectory != null) {
                file = new File(saveDirectory, fileName);
            } else {
                file = new File(fileName);
            }

            if (!file.exists()) {
                dataOutput.writeUTF("error");
                dataOutput.writeUTF("File not found");
                return;
            }

            dataOutput.writeUTF("ok");
            dataOutput.writeLong(file.length());

            try (FileInputStream fileInput = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    dataOutput.write(buffer, 0, bytesRead);
                }
            }

            log("Sent file: " + fileName);
        }
    }
}
