package chat;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.*;



public class ChatServer extends JFrame implements Runnable {

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	private int clientNo=0;
	private JTextArea ta;
	private List<HandleAClient> clients=Collections.synchronizedList(new ArrayList<HandleAClient>());
	ServerSocket serverSocket;
	boolean exit=true;
	  
	
	public ChatServer() {
		super("Chat Server");
		ta = new JTextArea(10,10);
		JScrollPane sp = new JScrollPane(ta);
		this.add(sp);
		this.setSize(ChatServer.WIDTH, ChatServer.HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		this.setVisible(true);
		Thread t = new Thread(this);
		  t.start();
		
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) -> {System.exit(0); close();});
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	

	public static void main(String[] args) {
		new ChatServer();
	}

	@Override
	public void run() {
		try {
	        // Create a server socket
	        serverSocket = new ServerSocket(9898);
	        ta.append("ChatServer started at " 
	          + new Date() + '\n');
	    
	        while (true) {
	          Socket socket = serverSocket.accept();
	          clientNo++;
	          HandleAClient newClient = new HandleAClient(socket, clientNo);
              clients.add(newClient);
	          
	          ta.append("Starting thread for client " + clientNo +
	              " at " + new Date() + '\n');

	            InetAddress inetAddress = socket.getInetAddress();
	            ta.append("Client " + clientNo + "'s host name is "
	              + inetAddress.getHostName() + "\n");
	            ta.append("Client " + clientNo + "'s IP Address is "
	              + inetAddress.getHostAddress() + "\n");
	          
	          // Create and start a new thread for the connection
	          new Thread(new HandleAClient(socket, clientNo)).start();
	        }
	        }
		
	      catch(IOException ex) {
	        System.err.println(ex);
	      }
		    
	  }
	
	public void close() {
		try {
		serverSocket.close();
	}catch(Exception e) {
		System.out.println(e.toString()+"\n");
	}
	}
		
	
	
	
	class HandleAClient implements Runnable {
	    private Socket socket; // A connected socket
	    private int clientNum;
	    String str;
	    
	    /** Construct a thread */
	    public HandleAClient(Socket socket, int clientNum) {
	      this.socket = socket;
	      this.clientNum = clientNum;
	    }

	    /** Run a thread */
	    public void run() {
	      try {
	    	  
	        DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
	       
	          while (true) {
	        	
	        	  
	          str = inputFromClient.readUTF();
	          System.out.println(str);
	          if(str.equals("exit")) 
	          {
	          ta.append("Closing connection for client " + this.clientNum + " " +
		              str + '\n');
	          
	  			removeUser(this);
	         break;
	          }
	        	  
	          new SendMessage(clients, str, clientNum);
	          
	          ta.append("Msg received from client: " + this.clientNum + ": " +
	              str + '\n');
	          
	        }
	      }
	      catch(IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	    
	    public void removeUser(HandleAClient c) {
	    	
	    	clients.remove(c);
	    	System.out.println("Quit");
	    	try{
	    		this.socket.close();
	    		new SendMessage(clients, str, clientNum);
	    	}catch(Exception e){
	    		System.out.println(e);
	    		
	    	}
	    }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(clientNum, socket);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HandleAClient other = (HandleAClient) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return clientNum == other.clientNum && Objects.equals(socket, other.socket);
		}

		private ChatServer getEnclosingInstance() {
			return ChatServer.this;
		}
	  }
	 public class SendMessage extends Thread{
	        protected List<HandleAClient> clients;
	        String str;
	        int clientNum;

	        public SendMessage(List<HandleAClient> clients, String str, int clientNum) {
	            this.clients = clients;
	            this.str=str;
	            this.clientNum=clientNum;
	            this.start();
	        }

	        public void run() {
	            try {
	             for (HandleAClient client : clients) {
	            	 if(client.clientNum!=clientNum)
	            	 {
	            	 Socket s=client.socket;
	            	 if (s!=null){
	            	 DataOutputStream outputToClient = new DataOutputStream(s.getOutputStream());
	            	 outputToClient.writeUTF(clientNum+": "+str);
	                  }
	            	 }
	             }
	                }catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	 }
	}



