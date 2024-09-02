package view;

import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class MyTest {
    public static void main(String[] args) {
        // Start the CLI in a separate thread
        Thread cliThread = new Thread(new CLI());
        cliThread.start();

        // Start the GUI in a separate thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                launchGUI();
            }
        });
    }

    private static void launchGUI() {
        JFrame frame = new JFrame("My Graphic Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(new JLabel("Hello, World!"));
        frame.setVisible(true);
    }
}

class CLI implements Runnable {
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("CLI is running. Type 'exit' to quit:");

        while (true) {
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Exiting CLI...");
                break;
            } else {
                System.out.println("You typed: " + input);
            }
        }

        scanner.close();
    }
}
