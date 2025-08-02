public class EncryptionUtil {
    private static final int SHIFT = 3;

    public static String encrypt(String msg) {
        StringBuilder sb = new StringBuilder();
        for (char c : msg.toCharArray()) {
            sb.append((char) (c + SHIFT));
        }
        return sb.toString();
    }

    public static String decrypt(String msg) {
        StringBuilder sb = new StringBuilder();
        for (char c : msg.toCharArray()) {
            sb.append((char) (c - SHIFT));
        }
        return sb.toString();
    }
}
