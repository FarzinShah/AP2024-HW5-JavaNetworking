package gui;

public class GUI_ServerStarter  {
    public static GUI_ServerStarter instance;

    public GUI_ServerStarter(){
        try {
            System.out.println("ssss");
            FileServerGUI window = FileServerGUI.getInstance();
            window.frame.setVisible(true);
            window.startServer();
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }
    }
    public static GUI_ServerStarter getInstance(){
        if(instance == null){
            instance = new GUI_ServerStarter();
        }
        return instance;
    }



}
