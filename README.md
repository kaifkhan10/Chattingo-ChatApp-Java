# 💬 Chattingo

**Chattingo** is a Java-based encrypted chat application using Java Sockets and JavaFX. It supports real-time messaging, private chats, user list display, and features a modern chat UI.

---

## 📁 Project Structure

Chattingo/
├── ChatServer.java # Server-side logic with multi-client handling and encryption
├── EncryptionUtil.java # Utility for Caesar cipher-based message encryption
└── ChatClient.java # JavaFX-based client with GUI and chat feature

## 🚀 Getting Started

### ✅ Prerequisites

- Java JDK 8 or above
- JavaFX SDK (configured in your IDE or build path)
- IDE like IntelliJ IDEA or Eclipse

---

### 🖥️ Running the Application

#### Step 1: Start the Server

javac ChatServer.java
java ChatServer
Step 2: Start the Client

javac ChatClient.java
java ChatClient

🔧 If JavaFX is not globally configured, use:
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml ChatClient
Replace /path/to/javafx-sdk/ with your actual JavaFX SDK path.

✨ Features
🔒 Encrypted messaging (simple Caesar cipher)

💬 Group chat for all connected users

📩 Private messaging using /pm <username> <message>

🟢 Live online user list like WhatsApp

🧑‍🎨 Modern GUI with message bubbles and timestamps

↔️ Messages are aligned (sent → right, received ← left)

🕒 Time shown beside each message

🧪 Usage
To send a private message:
/pm username your_message_here

Example:
/pm Alice Hello, this is a private message!

👨‍💻 Developer
Author: Kaif Khan
Language: Java (JavaFX + Socket Programming)

📄 License
This project is open-source and available for educational and personal use.

yaml
Copy
Edit
