package com.example.lab20a;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientApp extends Application{
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private boolean authorized;

    private TextArea chatArea;
    private TextField loginField;
    private TextField passField;
    private TextField msgField;
    private Button authButton;

    public static void main(String[] args) {
        Application.launch(ClientApp.class, args);
    }

    public void start(Stage primaryStage) throws Exception{
        VBox root = new VBox();

        loginField = new TextField();
        loginField.setPromptText("Login");

        passField = new TextField();
        passField.setPromptText("Password");

        authButton = new Button("Authorize");
        authButton.setOnAction(e -> onAuthClick());

        chatArea = new TextArea();
        chatArea.setEditable(false);

        msgField = new TextField();
        msgField.setPromptText("Напиши свое сообщение: ");
        Button sendButton = new Button("Send");
        sendButton.setOnAction(this::onSendClick);

        root.getChildren().addAll(loginField, passField, authButton, chatArea, msgField, sendButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Client App");
        primaryStage.setScene(scene);
        primaryStage.show();

        ClientApp();
    }

    public void ClientApp() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true){
                            String strFromServer = in.readUTF();
                            if(strFromServer.startsWith("/authok")){
                                setAuthorized(true);
                                break;
                            }
                            chatArea.appendText(strFromServer + "\n");
                        }
                        while (true){
                            String strFromServer = in.readUTF();
                            if(strFromServer.equalsIgnoreCase("/end")){
                                break;
                            }
                            chatArea.appendText(strFromServer);
                            chatArea.appendText("\n");
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onAuthClick(){
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void onSendClick(ActionEvent actionEvent) {
        try {
            String msg = msgField.getText();
            out.writeUTF(msg);
            msgField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        loginField.setDisable(authorized);
        passField.setDisable(authorized);
    }
}