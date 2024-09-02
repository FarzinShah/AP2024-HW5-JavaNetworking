//package auth_server;
//
//import org.mindrot.jbcrypt.BCrypt;
//
//public class PasswordUtil {
//    public static PasswordUtil instance;
//    // Hash کردن رمز عبور
//    public static String hashPassword(String plainPassword) {
//        String salt = BCrypt.gensalt(12); // ایجاد salt با قدرت ۱۲ (هرچه بالاتر، امنیت بیشتر ولی سرعت کمتر)
//        return BCrypt.hashpw(plainPassword, salt);
//    }
//
//    // بررسی صحت رمز عبور
//    public static boolean checkPassword(String plainPassword, String hashedPassword) {
//        return BCrypt.checkpw(plainPassword, hashedPassword);
//    }
//    public static PasswordUtil getInstance(){
//
//    }
//
//    public static void main(String[] args) {
//        String username = "user1";
//        String password = "mySecurePassword";
//
//        // Hash کردن رمز عبور
//        String hashedPassword = hashPassword(password);
//        System.out.println("Hashed Password: " + hashedPassword);
//
//        // بررسی صحت رمز عبور
//        boolean isPasswordCorrect = checkPassword(password, hashedPassword);
//        System.out.println("Password is correct: " + isPasswordCorrect);
//    }
//}
