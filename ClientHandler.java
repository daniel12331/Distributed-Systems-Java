import java.io.*;

import javax.swing.JTextArea;

import java.net.*;


// ClientHandler class
class ClientHandler extends Thread 
{ 

	final DataInputStream dis; 
	final DataOutputStream dos; 
	final Socket s; 
	boolean user;
	JTextArea jta;
	String username;
	

	// Constructor 
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Boolean user, JTextArea jta, String username) 
	{ 
		this.s = s; 
		this.dis = dis; 
		this.dos = dos; 
		this.user = user;
		this.jta = jta;
		this.username = username;
	} 

	@Override
	public void run() 
	{ 
		String received; 
		String toreturn; 
		while (true) 
		{ 		 
			try {

				//When thread is run we send welcome message
					toreturn ="Client Validated, Welcome " + username + "!";
					dos.writeUTF(toreturn);
					received = dis.readUTF();
					
					//The radius process call
				if(received.equals("radius")){
					Double radiusRec = dis.readDouble();

					Double area = radiusRec * radiusRec * Math.PI;
						
					dos.writeDouble(area);
					dos.flush();				
				}


				//The exit process call
				if(received.equals("Exit")){
					System.out.println("Client " + this.s + " sends exit...");
					System.out.println("Closing this connection."); 

					String DisconnectMSG = "Connection Sucessfully closed!";
					dos.writeUTF(DisconnectMSG);
					//dos.flush();

					this.s.close(); 
					System.out.println("Connection closed."); 
					break; 				
				}			
			} catch ( IOException e) {	
				e.printStackTrace();
			}

			} 
		try
			{ 
				// closing resources and current thread
				this.dis.close(); 
				this.dos.close(); 
				Thread.currentThread().interrupt();	
				jta.append("Thread and socket closed for client: " + username + '\n'); 
			
		}catch(IOException e){ 
				e.printStackTrace(); 
			} 
		} 		
		
	} 

