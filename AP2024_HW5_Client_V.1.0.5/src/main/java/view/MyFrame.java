package view;
//


import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class MyFrame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type 'start' to launch the graphical application:");

//        while (true) {
            String input = scanner.nextLine();

            if ("start".equalsIgnoreCase(input)) {
                launchGUI();
//                break;
            } else {
                System.out.println("Invalid command. Please type 'start' to launch the graphical application:");
            }
        }

//        scanner.close();
//    }

    private static void launchGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("My Graphic Application");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 300);
                frame.add(new JLabel("Hello, World!"));
                frame.setVisible(true);
            }
        });
    }
}



//import gui.FileClientGUI;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//
//public class MyFrame extends JFrame {
//    private JTextField filePathField;
//
//
//    MyFrame(){
//        setBounds(100, 100, 600, 200);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        getContentPane().setLayout(new BorderLayout(0, 0));
//
//        JPanel panel = new JPanel();
//        getContentPane().add(panel, BorderLayout.NORTH);
//
//        JLabel lblFilePath = new JLabel("File Path:");
//        panel.add(lblFilePath);
//
//        filePathField = new JTextField();
//        panel.add(filePathField);
//        filePathField.setColumns(20);
//
//        JButton btnBrowse = new JButton("Browse");
//        btnBrowse.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                chooseFile();
//            }
//        });
//        panel.add(btnBrowse);
//
//        JButton btnSendFile = new JButton("Send File");
//        btnSendFile.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                FileClientGUI.sendFile();
//            }
//        });
//        getContentPane().add(btnSendFile, BorderLayout.SOUTH);
//    }
//    private void chooseFile(File selectedFile) {
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        int result = fileChooser.showOpenDialog(this);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            selectedFile = fileChooser.getSelectedFile();
//            filePathField.setText(selectedFile.getAbsolutePath());
//        }
//    }
//}
