package util;

import java.io.DataInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Constant {
    public static String CURRENT_USERNAME = "";
    public static String CURRENT_PASSWORD = "";

    public static final int GUI_PORT = 5007;
    public static final int CLI_PORT = 5011;
    public static int ID = 0;

    public static final String filePath = "src\\main\\java\\util\\dataOfAccounts.txt";
    public static final String idFilePath = "src\\main\\java\\util\\idCounter.txt";
    public static final String ICON_PATH = "src\\main\\java\\util\\BeautifulHeart.png";



    public static String readFileAsString(String fileName)
            throws Exception
    {
        String data = "";
        data = new String(
                Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
    public static void counter() throws Exception {
        ID = Integer.parseInt(readFileAsString(idFilePath));
    }

}
