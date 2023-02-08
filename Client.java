// Save file as Client.java

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

// Client class
public class Client extends JFrame
{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException 
	{ 

		JFrame f = new JFrame();
		JPanel panel= new JPanel();
		f.setVisible(true);
		f.add(panel);
		f.setTitle("Client");
		

        JLabel studentidlabel = new JLabel("Enter Student ID: ");
		JLabel processlabel = new JLabel("");
		JLabel emptyblock = new JLabel("");
		JLabel radiusLabel = new JLabel("");



		JTextField id = new JTextField(6);
		JTextField sendradiustextfield = new JTextField(6);


		JButton send = new JButton("Send");
		JButton disconnect = new JButton("Disconnect");
		JButton sendradiusbtn = new JButton("Send Radius");


		
		
		
		panel.add(studentidlabel);
		panel.add(id);
		panel.add(send);	
		panel.add(disconnect);
		panel.add(processlabel);
		panel.add(emptyblock);
		panel.add(sendradiusbtn);
		panel.add(sendradiustextfield);
		panel.add(radiusLabel);
		disconnect.setVisible(false);
		sendradiusbtn.setVisible(false);
		sendradiustextfield.setVisible(false);

		panel.setLayout(new GridLayout(5, 0));

		f.setSize(800,200);
		
	
	
	//Only run when a user clicks send.	
	send.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent arg0){

			try {
				// getting localhost ip 
				InetAddress ip = InetAddress.getByName("localhost"); 
		
				// establish the connection with server port 5056 
				Socket socket = new Socket(ip, 5056); 
		
				// obtaining input and out streams 
				DataInputStream dis = new DataInputStream(socket.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream()); 
		
				//Listening and getting ID from client.
				String textfromfield = id.getText().trim();
				dos.writeUTF(textfromfield);
				
				//Getting call back from server verifying successful connection
				String fromserver = dis.readUTF();
				processlabel.setText(fromserver);
				
				
				//Formating GUI 
				if(fromserver != null){
					studentidlabel.setVisible(false);
					id.setVisible(false);
					send.setVisible(false);
					disconnect.setVisible(true);
					sendradiusbtn.setVisible(true);
					sendradiustextfield.setVisible(true);
					radiusLabel.setVisible(true);

				}
				
				//Only runs when a user clicks disconnect
				disconnect.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0){

						
						try {
							//Send a Exit call to the server allowing the server know that there is a closing process
							String exitcall = "Exit";
							dos.writeUTF(exitcall); 
							
							//Getting call back from server
							String fromserver = dis.readUTF();
							processlabel.setText(fromserver);

							//Reverting GUI back to original launch
							if(fromserver != null){
								studentidlabel.setVisible(true);
								id.setVisible(true);
								send.setVisible(true);
								disconnect.setVisible(false);
								sendradiusbtn.setVisible(false);
								sendradiustextfield.setVisible(false);
								radiusLabel.setVisible(false);
							}
							//CLosing socket and input/output streams
							socket.close();
							dis.close(); 
							dos.close(); 
							
						} catch (IOException e) {
						} 

						}
					}); 

					//Only run when a user clicks the send radius button
					sendradiusbtn.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent arg0){
	
							try {
								//Sends the message radius call to process the radius the user has sent
								String radiuscall = "radius";
								dos.writeUTF(radiuscall); 
								
								//Taking radius from textfield and converting to double and sending it to be calculated
								Double radius = Double.parseDouble(sendradiustextfield.getText().trim());
								dos.writeDouble(radius); 
								dos.flush();
								
								//Getting double back from server and converting to string to be displayed in radius area label
								Double areafromserver = dis.readDouble();
								String s1 = Double.toString(areafromserver);

								radiusLabel.setText("Radius is : " + s1);

							} catch (IOException e) {
								processlabel.setText("Issue sending radius");
							} 
	
							}
						}); 
			} catch (Exception e) {
				processlabel.setText("Unable to connect with server / Check student ID");
			} 
	}});				 
	}
} 