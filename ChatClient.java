package chat;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.io.DataOutputStream;



public class ChatClient extends JFrame {
  // IO streams
  DataOutputStream toServer = null;
  DataInputStream fromServer = null;
  JTextField jtf = new JTextField();;
  JTextArea jta = new JTextArea();
  Socket socket = null;
  boolean exit=true;
  listenMessage m;
  int count=0;
  
  public ChatClient() {
	  add(new JScrollPane(jtf));
	  JPanel p = new JPanel();
	    p.setLayout(new BorderLayout());
	    p.add(new JLabel("Type Ur Msg"), BorderLayout.WEST);
	    jtf.setPreferredSize(new Dimension(10,50));
	    p.add(jtf, BorderLayout.CENTER);
	    jtf.setHorizontalAlignment(JTextField.LEFT);
	    jtf.addActionListener(new TextFieldListener());
	    
	    //JScrollPane scrollPane = new JScrollPane(jtf);
	    //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  

	    //p.add(scrollPane);
	
	    setLayout(new BorderLayout());
	    add(p, BorderLayout.SOUTH);
	    
	    add(new JScrollPane(jta), BorderLayout.CENTER);

	    setTitle("ChatClient");
	    setSize(500, 300);
	    this.addWindowListener(new WindowAdapter() {
			   public void windowClosing(WindowEvent evt) {
			     close();
			   }
			  });
	    setVisible(true);
	    createMenu();
	    
  }
  private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener((e) ->{
			close();
		});
		JMenuItem openItem = new JMenuItem("Connect");
		openItem.addActionListener(new OpenConnectionListener());
		menu.add(openItem);
		menu.add(exitItem);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
  
  class OpenConnectionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			
			if(count==0) {
			socket = new Socket("localhost", 9898);
			jta.append("Connected"+"\n");
			Thread t= new Thread();
			m= new listenMessage();
			System.out.println("New thread started");
			count=1;
			}
			else {
				jta.append("Already connected!!!"+"\n");
		} 
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			jta.append("connection Failure"+"\n");
		}
	}
	  
  }
  
  class TextFieldListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
	    try {
		   
		      toServer = new DataOutputStream(socket.getOutputStream());
		    }
		    catch (IOException ex) {
		      jta.append(ex.toString() + '\n');
		    }
	    
	    try {
	        String str = jtf.getText().trim();
	        jta.append("Me:"+str+"\n");
	        toServer.writeUTF(str);
	        jtf.setText("");
	        toServer.flush();
	      }
	      catch (IOException ex) {
	        System.err.println(ex);
	      }	    
	}
  }
	  

  public static void main(String[] args) {
    ChatClient c = new ChatClient();
    c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    c.setVisible(true);
  }
  
  public void close() {
	  count=0;
	  if(socket==null)
		  System.exit(0);
	 try {
		 
		 toServer = new DataOutputStream(socket.getOutputStream());
		  toServer.writeUTF("exit");
		  exit=false;
		  
		  socket.close();
		System.exit(0);
	  }catch(Exception e1) {
			System.err.println("error");
  }		 
  }


class listenMessage extends Thread{
	
	listenMessage()
	{
		this.start();
	}
	public void run() {
	synchronized (socket){
	while(exit) {
	try {
			fromServer = new DataInputStream(socket.getInputStream());
	}
	catch (IOException e){
		System.err.println(e);
	}
	try {
    	String s= fromServer.readUTF();
    	jta.append(s);
    	jta.append("\n");
    	jtf.setText("");
    }
    
    catch(IOException ex) {
    	jta.append(ex.toString()+"\n");
    }}
}}
}}
