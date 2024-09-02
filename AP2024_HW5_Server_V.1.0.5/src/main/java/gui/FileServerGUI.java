package gui;
import nokhodi.ServerDownloader;
import util.Constant;
import util.Source;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static util.Constant.*;

public class FileServerGUI {
    public static FileServerGUI instance;

    public JFrame frame;
    private static JTextArea logArea;
    private JLabel currentUsername;

    private ServerSocket serverSocket;
    private List<FileHandler> handlers = new ArrayList<>();
    private File saveDirectory;
    private String currentClient="";



    public FileServerGUI() throws IOException {
        initialize();

    }

    private void initialize() {
        frame = new JFrame("Server");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0xCE7700));
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.setIconImage( Toolkit.getDefaultToolkit().getImage(ICON_PATH));



        JButton btnSetDirectory = new JButton("Set Save Directory");
        btnSetDirectory.addActionListener(e -> chooseSaveDirectory());
        panel.add(btnSetDirectory);
        JButton logClearance = new JButton("Clear");
        logClearance.addActionListener(e->clearLog());

        panel.add(logClearance);

        currentUsername = new JLabel("username: "+Constant.CURRENT_USERNAME);
        currentUsername.setLocation(0,0);
        currentUsername.setBackground(Color.WHITE);
        panel.add(currentUsername);


//        log(java.time.LocalDateTime.now()+ ": client "+ Constant.CURRENT_USERNAME + " is logged in.");
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

    public void startServer() {

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(GUI_PORT);
                log("Server started on port 5007");


                while (true) {
//                    log(java.time.LocalDateTime.now()+ ": client "+ Constant.CURRENT_USERNAME + " is logged in.");
                    log("Waiting for a client to connect...");
                    Socket socket = serverSocket.accept();
                    log("New client connected: " + socket.getInetAddress() + ":" + socket.getPort());
                    currentClient = String.valueOf(socket.getInetAddress());
                    log(java.time.LocalDateTime.now()+ ": client "+ Constant.CURRENT_USERNAME + " is logged in.");
                    FileHandler receiver = new FileHandler(socket);
                    handlers.add(receiver);
                    new Thread(receiver).start();
                }
            } catch (IOException e) {
                log("Server error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private static void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }
    private void clearLog(){
        SwingUtilities.invokeLater(() -> logArea.setText(""));

    }

    class FileHandler implements Runnable {
        private Socket socket;

        public FileHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
//            log(java.time.LocalDateTime.now()+ ": client "+ Constant.CURRENT_USERNAME + " is logged in.");
            try (DataInputStream dataInput = new DataInputStream(socket.getInputStream());
                 DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream())) {

                String command = dataInput.readUTF();

                if (command.equals("upload")) {
                    receiveFile(dataInput);
                    writeToIDFile(ID);
                    Source.idToShared.get(ID).add(currentClient);
                    ID++;
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
                file = new File(saveDirectory, "U"+ID + "_received_" + fileName);
            } else {
                file = new File("U"+ID + "_received_" + fileName);
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
    public static FileServerGUI getInstance() throws IOException {
        if(instance==null){
            instance = new FileServerGUI();
        }
        return instance;
    }
        public static void writeToIDFile(int data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(idFilePath))) {
                writer.write(String.valueOf(data));
                writer.newLine();
            System.out.println("Data has been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static class ClientRunnable implements Runnable {
        Socket socket;
        int id;
        Scanner scanner;
        PrintWriter printWriter;
        Scanner terminal;

        public ClientRunnable(int id, Socket socket) {
            this.id = id;
            this.socket = socket;
        }

        private void setupStreams() throws IOException {
            scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream());
            terminal = new Scanner(System.in);
        }

        private void listen() {
            while (true) {
                String s = scanner.nextLine();
                System.out.println("client " + id + " sent: " + s);
                log("Server started on port 5007");

                String response = "server response to:" + s;
                printWriter.println(response);
                printWriter.flush();
            }
        }



        @Override
        public void run() {
            try {
                setupStreams();
                listen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
