package gui;

import util.Helper;
import util.Constant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class FileClientGUI {

    public JFrame frame;
    private JTextField filePathField;
    static JTextField noticer;
    JLabel currentUsername;
    int ID = 4021001;
    int port = 5007;

    LinkedList<String> listOfDownloadedFiles;
    LinkedList<String> listOfUploadedFiles;
    JTable tableD;
    JFrame frameD;
    JTable tableU;
    JFrame frameU;



    private File selectedFile;
    private File saveDirectory;

    public static FileClientGUI instance;

    public FileClientGUI() {
        initialize();

    }

    private void initialize() {
        frame = new JFrame("Client");
        frame.setBounds(100, 10, 1300, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JLabel lblFilePath = new JLabel("File Path:");
        panel.add(lblFilePath);

        filePathField = new JTextField();
        panel.add(filePathField);
        filePathField.setColumns(20);
        noticer = new JTextField();
        noticer.setEditable(false);
        panel.add(noticer);
        panel.setBackground(new Color(0x8B9F8B));
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Notebook\\IdeaProjects\\AP2024_HW5_Client_V.1.0.2\\src\\main\\java\\util\\BeautifulHeart.png"));
        noticer.setColumns(20);

        currentUsername = new JLabel("username: " + Constant.CURRENT_USERNAME);
        currentUsername.setLocation(0, 0);
        currentUsername.setBackground(Color.WHITE);
        panel.add(currentUsername);

        JButton btnBrowse = new JButton("Browse");
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });
        panel.add(btnBrowse);



        JButton btnSendFile = new JButton("Send File");
        btnSendFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        panel.add(btnSendFile);
        JButton btnDownloadFile = new JButton("Download File");
        btnDownloadFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadFile();
            }
        });

        panel.add(btnDownloadFile);

        listOfDownloadedFiles = new LinkedList<>();

        JButton btnShowDownloadedList = new JButton("List Of Downloaded File");
        btnShowDownloadedList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showListD();
            }
        });

        panel.add(btnShowDownloadedList);

        listOfUploadedFiles = new LinkedList<>();

        JButton btnShowUploadedList = new JButton("List Of Uploaded File");
        btnShowUploadedList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showListU();
            }
        });

        panel.add(btnShowUploadedList);

    }

    private void showListU(){
        frameU = new JFrame();

        // Frame Title
        frameU.setTitle("Uploaded List");

        String[][] data = new String[listOfUploadedFiles.size()][2];
        for (int i = 0; i < listOfUploadedFiles.size(); i++) {
            data[i][0] = String.valueOf(i+1);
            data[i][1] = listOfUploadedFiles.get(i);
        }


        String[] columnNames = { "Number", "Name" };

        tableU = new JTable(data, columnNames);
        tableU.setBounds(30, 40, 200, 300);

        JScrollPane sp = new JScrollPane(tableU);
        frameU.add(sp);
        frameU.setSize(500, 200);
        frameU.setVisible(true);
    }

    private void showListD(){
        frameD = new JFrame();
        frameD.setTitle("Downloaded List");

        String[][] data = new String[listOfDownloadedFiles.size()][2];
        for (int i = 0; i < listOfDownloadedFiles.size(); i++) {
            data[i][0] = String.valueOf(i+1);
            data[i][1] = listOfDownloadedFiles.get(i);
        }


        String[] columnNames = { "Number", "Name" };

        tableD = new JTable(data, columnNames);
        tableD.setBounds(30, 40, 200, 300);

        JScrollPane sp = new JScrollPane(tableD);
        frameD.add(sp);
        frameD.setSize(500, 200);
        frameD.setVisible(true);
    }


    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void sendFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(frame, "Please select a file to send.");
            return;
        }

        String hostname = "localhost";
        int port = Constant.GUI_PORT;
        int parts = Helper.PARTS;


        long fileSize = selectedFile.length();
        listOfUploadedFiles.add(selectedFile.getName());
        long partSize = fileSize / parts;
        long remainder = fileSize % parts;

        for (int i = 0; i < parts; i++) {
            long start = i * partSize;
            long end = (i == parts - 1) ? (start + partSize + remainder) : (start + partSize);

            new Thread(new FileSender(hostname, port, selectedFile, start, end)).start();
        }

    }

    private void downloadFile() {
        String hostname = "localhost";

        String fileName = JOptionPane.showInputDialog(frame, "Enter the file name to download:");

        if (fileName == null || fileName.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid file name.");
            return;
        }

        try (Socket socket = new Socket(hostname, port);
             DataInputStream dataInput = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream())) {

            dataOutput.writeUTF("download");
            dataOutput.writeUTF(fileName);

            String response = dataInput.readUTF();
            if (response.equals("error")) {
                String errorMessage = dataInput.readUTF();
                JOptionPane.showMessageDialog(frame, "Error: " + errorMessage);
                return;
            }

            long fileSize = dataInput.readLong();
            File file;
            if (saveDirectory != null) {
                file = new File(saveDirectory, "downloaded_" + fileName);
            } else {
                file = new File("D"+ID+"downloaded_" + fileName);
                listOfDownloadedFiles.add("D"+ID+"downloaded_" + fileName);

            }
            ID++;


            try (FileOutputStream fileOutput = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long bytesReceived = 0;
                while (bytesReceived < fileSize && (bytesRead = dataInput.read(buffer)) != -1) {
                    fileOutput.write(buffer, 0, bytesRead);
                    bytesReceived += bytesRead;
                }
            }

            JOptionPane.showMessageDialog(frame, "File downloaded: " + fileName);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error downloading file: " + ex.getMessage());
        }
    }


    public static FileClientGUI getInstance() {
        if (instance == null) {
            instance = new FileClientGUI();
        }
        return instance;
    }

    public static void notice(String text) {
        noticer.setText(text);

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

