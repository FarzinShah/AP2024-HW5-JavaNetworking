package auth_server;

import gui.GUI_ServerStarter;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
             new Thread(AuthServerStarter::new).start();

    }
}
