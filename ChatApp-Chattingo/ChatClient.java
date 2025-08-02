import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatClient extends Application {
    private String nickname;
    private PrintWriter out;
    private VBox messageBox;
    private ListView<String> userListView = new ListView<>();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
        stage.setTitle("💬 Chattingo");

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter Nickname");
        dialog.setHeaderText("Welcome to Chattingo!");
        dialog.setContentText("Nickname:");
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;
        nickname = result.get();

        BorderPane root = new BorderPane();
        messageBox = new VBox(10);
        messageBox.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(messageBox);
        scrollPane.setFitToWidth(true);

        TextField inputField = new TextField();
        inputField.setPromptText("Type your message...");
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !inputField.getText().isEmpty()) {
                sendMessage(inputField.getText());
                inputField.clear();
            }
        });

        userListView.setPrefWidth(120);
        VBox rightPanel = new VBox(new Label("🟢 Online"), userListView);
        rightPanel.setPadding(new Insets(10));

        root.setTop(new Label("💬 Chattingo"));
        root.setCenter(scrollPane);
        root.setBottom(inputField);
        root.setRight(rightPanel);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();

        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 12345)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                if ("ENTER_NICKNAME".equals(in.readLine())) {
                    out.println(nickname);
                }

                String line;
                while ((line = in.readLine()) != null) {
                    String msg = EncryptionUtil.decrypt(line);
                    if (msg.startsWith("USER_LIST:")) {
                        String[] users = msg.substring(10).split(",");
                        Platform.runLater(() -> {
                            userListView.getItems().setAll(users);
                        });
                    } else {
                        Platform.runLater(() -> addMessageBubble(msg));
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> addMessageBubble("X Disconnected from server"));
            }
        }).start();
    }

    private void sendMessage(String text) {
        if (out != null) {
            out.println(EncryptionUtil.encrypt(text));
        }
    }

    private void addMessageBubble(String msg) {
        boolean isSelf = msg.startsWith(nickname + ":") || msg.contains("[Private] " + nickname + ":");

        HBox bubbleBox = new HBox();
        Label bubble = new Label(msg);
        bubble.setWrapText(true);
        bubble.setPadding(new Insets(5));
        bubble.setMaxWidth(350);
        bubble.setStyle("-fx-background-color: " + (isSelf ? "#DCF8C6" : "#E5E5EA") +
                        "; -fx-background-radius: 10; -fx-font-size: 14px;");

        Label timeLabel = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        timeLabel.setPadding(new Insets(0, 5, 0, 5));

        if (isSelf) {
            bubbleBox.setAlignment(Pos.CENTER_RIGHT);
            bubbleBox.getChildren().addAll(timeLabel, bubble);
        } else {
            bubbleBox.setAlignment(Pos.CENTER_LEFT);
            bubbleBox.getChildren().addAll(bubble, timeLabel);
        }

        messageBox.getChildren().add(bubbleBox);
    }
}
