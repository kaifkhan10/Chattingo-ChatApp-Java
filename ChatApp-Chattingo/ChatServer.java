import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("🔌 Chattingo Server started on port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private String nickname;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("ENTER_NICKNAME");
                nickname = in.readLine();

                synchronized (clients) {
                    clients.put(nickname, out);
                    sendUserList();
                }

                broadcast("📢 " + nickname + " joined the chat!");

                String input;
                while ((input = in.readLine()) != null) {
                    input = EncryptionUtil.decrypt(input);

                    if (input.startsWith("/pm ")) {
                        String[] parts = input.split(" ", 3);
                        if (parts.length == 3 && clients.containsKey(parts[1])) {
                            sendPrivate(parts[1], "[Private] " + nickname + ": " + parts[2]);
                        } else {
                            out.println(EncryptionUtil.encrypt("X User not found."));
                        }
                    } else {
                        broadcast(nickname + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println("X " + nickname + " disconnected.");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
                synchronized (clients) {
                    clients.remove(nickname);
                    broadcast("👋 " + nickname + " left the chat.");
                    sendUserList();
                }
            }
        }

        private void broadcast(String msg) {
            String encrypted = EncryptionUtil.encrypt(msg);
            synchronized (clients) {
                for (PrintWriter writer : clients.values()) {
                    writer.println(encrypted);
                }
            }
        }

        private void sendPrivate(String toUser, String msg) {
            PrintWriter writer = clients.get(toUser);
            if (writer != null) {
                writer.println(EncryptionUtil.encrypt(msg));
            }
        }

        private void sendUserList() {
            StringBuilder sb = new StringBuilder("USER_LIST:");
            for (String name : clients.keySet()) {
                sb.append(name).append(",");
            }
            String list = sb.toString();
            for (PrintWriter writer : clients.values()) {
                writer.println(EncryptionUtil.encrypt(list));
            }
        }
    }
}
