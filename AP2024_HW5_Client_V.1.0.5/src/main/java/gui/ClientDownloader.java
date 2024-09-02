package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientDownloader {

    private JFrame frame;
    private JTextField filePathField;
    private File selectedFile;
    private File saveDirectory;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ClientDownloader window = new ClientDownloader();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ClientDownloader() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JLabel lblFilePath = new JLabel("File Path:");
        panel.add(lblFilePath);

        filePathField = new JTextField();
        panel.add(filePathField);
        filePathField.setColumns(20);

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
        frame.getContentPane().add(btnDownloadFile, BorderLayout.SOUTH);
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
        int port = 5000;
        int parts = 5;

        long fileSize = selectedFile.length();
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
        int port = 5000;
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
                file = new File("downloaded_"+ fileName);
            }


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
                 DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
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
                    int read = fileReader.read(buffer, 0, (int) Math.min(buffer.length, (end - start) - bytesSent));
                    if (read == -1) break;
                    dataOutput.write(buffer, 0, read);
                    bytesSent += read;
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Error sending file part: " + ex.getMessage()));
            }
        }
    }
}
