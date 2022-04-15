package Models;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.omg.CORBA.PRIVATE_MEMBER;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sun.util.logging.resources.logging;

public class ClientChat extends Application{

	public Socket socket ;
	public PrintWriter pw ;
	InputStream inputStream;
	InputStreamReader isr;
	BufferedReader bufferedReader;
	boolean isActive=true;
	Thread thread;
	public static void main(String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("client chat");
		BorderPane borderPane = new BorderPane();
		Label labelHost = new Label("Host :");
		TextField  textFieldHost = new TextField("localhost");
		Label labelPort = new Label("Port :");
		TextField  textFieldPort = new TextField("200");
		Button connecter = new Button("connecter");
		HBox hbox = new HBox();
		hbox.setSpacing(20);
		hbox.setPadding(new Insets(10));
		hbox.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));
		hbox.getChildren().addAll(labelHost,textFieldHost,labelPort,textFieldPort,connecter);
		borderPane.setTop(hbox);
		VBox vbox = new VBox();
		vbox.setSpacing(13);
		vbox.setPadding(new Insets(15));	
		ObservableList<String> listModel = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<>(listModel);
		borderPane.setCenter(listView);
		vbox.getChildren().add(listView);
		vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN, null, null)));
		borderPane.setCenter(vbox);
		Label message = new Label("message");
		TextField textFieldMessage = new TextField();
		textFieldMessage.setPrefSize(300, 30);
		Button envoyer = new Button("envoyer");
		Button deconnecter = new Button("deconnexion");
		HBox hbox2 = new HBox();
		hbox2.setSpacing(20);
		hbox2.setPadding(new Insets(10));
		hbox2.setBackground(new Background(new BackgroundFill(Color.BEIGE, null, null)));
		hbox2.getChildren().addAll(message,textFieldMessage, envoyer,deconnecter);
		borderPane.setBottom(hbox2);
		Scene scene = new Scene(borderPane, 600,400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		connecter.setOnAction((evt)->{
			String host = textFieldHost.getText();
			int port = Integer.parseInt(textFieldPort.getText());
			try {
				socket = new Socket(host, port);
				inputStream = socket.getInputStream();
				isr = new InputStreamReader(inputStream);
				bufferedReader = new BufferedReader(isr);
				pw = new PrintWriter(socket.getOutputStream(), true);
				Thread thread = new Thread(()->{
					while(isActive) {
						try {
							String response = bufferedReader.readLine();
							Platform.runLater(()->{
								listModel.add(response);
							});			
							}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				thread.start();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				
			}
			
		});
		envoyer.setOnAction((evt)->{
			String sms = textFieldMessage.getText();
			pw.println(sms);
			
			
		});
		
		deconnecter.setOnAction((evt)->
		{
			String deconnexion = "deconnecté";
			try {
				pw.println(deconnexion);
				isActive = false;
				System.exit(0);
			} catch (Exception e) {
				//e.printStackTrace();
			}
			primaryStage.close();
		});
		
		
	}
	
}
