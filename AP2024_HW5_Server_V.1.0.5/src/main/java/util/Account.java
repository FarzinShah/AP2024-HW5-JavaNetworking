package util;

import org.mindrot.jbcrypt.BCrypt;

public class Account {
    private String username;
    private String hashedPassword;
    private String tokenUsername;
    private String tokenPassword;
    private Jwt jwtUtil;


    public Account(String username, String password) {
        this.username = username;
//        this.password = password;
        this.hashedPassword = hashPassword(password);
        jwtUtil= new Jwt();
        tokenPassword = jwtUtil.generateToken(password);
        tokenUsername=jwtUtil.generateToken(username);

    }

    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(plainPassword, salt);
    }
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getTokenPassword() {
        return tokenPassword; // صرفا برای اینکه زده باشم. فعلا نیازی برای استفاده ازش نمیبینم :todo
    }

    public String getTokenUsername() {
        return tokenUsername;
    }
}
