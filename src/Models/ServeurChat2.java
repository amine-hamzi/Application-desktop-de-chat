package Models;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

//import server.ServeurChat.Conversation;
public class ServeurChat2 extends Thread {

	public int numeroClient = 0 ;
	public  Conversation client ;
	
	public List<Conversation> listeClients = new ArrayList<>();
	public static void main(String [] args )
	{
		ServeurChat2 s = new ServeurChat2();
		s.start();
	}
	
	public void run() {
		try {
			System.out.println("demarrage de serveur");
			ServerSocket ss = new ServerSocket(200);
			while(true)
			{
				System.out.println("attend des clients");
				Socket socket = ss.accept();
				++numeroClient ; 
				System.out.println("creer un thread pour chaque socket");
				client = new Conversation(socket, numeroClient);
				listeClients.add(client);
				client.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	class Conversation extends Thread
	{
		protected  Socket socketClient ;
		protected int numeroClient ;
		boolean threadActive = true;
		public Conversation(Socket socket , int numeroClient)
		{
			this.socketClient = socket ;
			this.numeroClient = numeroClient ;
		}
	
		
		public Socket getSocket() {
			return socketClient;
		}


		public void setSocket(Socket socket) {
			this.socketClient = socket;
		}


		public int getNumeroClient() {
			return numeroClient;
		}


		public void setNumeroClient(int numeroClient) {
			this.numeroClient = numeroClient;
		}


		public void broadcastMessage(String message, Socket socket , int destination , int source)
		{
			
			try {
				for(Conversation client : listeClients)
				{
					if(client.socketClient!= socket)
					{
						if(client.numeroClient == destination || destination == -1)
						{
							if(!message.equals("deconnecté"))
							{
								PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(),true);
								//printWriter.println(message);
								printWriter.println("le client "+source+" vous envoyer : "+message);
							}
							else
							{
								PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(),true);
								//printWriter.println(message);
								printWriter.println("le client  "+source+" est deconnecté ..... ");
								threadActive = false;
								
							}
						}
					}
			
		
			
				} 
			}
					catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		public void run() {
			try {
				String ipClient ; 
				//lire une chaine de caractere
				InputStream is = socketClient.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				//envoyer une chaine 
				OutputStream os = socketClient.getOutputStream();
				PrintWriter pw = new PrintWriter(os,true);
				ipClient = socketClient.getRemoteSocketAddress().toString();
				System.out.println("connexion  client "+numeroClient+"  d'IP : "+ipClient);
			    pw.println("vous etez le client de numero :"+numeroClient);
				//pw.println(ipClient);
				
			    while(threadActive) {
					String req = br.readLine();
					if(req.contains("=>")) {
						String[] requestParams = req.split("=>");
						if(requestParams.length == 2)
						{
						String message = requestParams[1];
						int destination = Integer.parseInt(requestParams[0]);
						broadcastMessage(message, socketClient, destination,numeroClient );
						}
					}
					else {
						broadcastMessage(req, socketClient, -1 , numeroClient);
					}
				}
				
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
		}
	}
}
