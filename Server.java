// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.net.*;
import java.sql.*;
import java.awt.*;
import javax.swing.*;


import java.awt.event.*;

// Server class
public class Server extends JFrame
{ 
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private static JTextArea jta = new JTextArea();
public static JButton jbe = new JButton("Exit");

public static Statement st;
public static Connection con;
public static ResultSet rs;


	public static void main(String[] args) throws IOException 
	{ 
		new Server();
	}

	public Server() throws IOException {
		
		setLayout(new BorderLayout());
		add(new JScrollPane(jta), BorderLayout.CENTER);
		add(jbe , BorderLayout.EAST);
	
		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		jbe.addActionListener(new ExitListener()); // Exit listener

		// server is listening on port 5056 
		ServerSocket ss = new ServerSocket(5056);

		jta.append("Server listening at " + ss + '\n');

		//Checking Connectivity for SQLDB and mapping result set to current students
		try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/assign1", "root", "root");
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs=st.executeQuery("select * from students");

			
			if(rs.next()){
			jta.append("Successfully Connected to SQL DB \n");
			}
			else { 
				jta.append("Result Set is empty in DB");
			}
    }
    catch(Exception e){
		jta.append("Error connecting to DB");

    }


		// running infinite loop for getting 
		// client request 
		while (true) 
		{ 
			Socket s = null; 
			
			try
			{ 
				// socket object to receive incoming client requests 
				s = ss.accept(); 
				
				jta.append("A new client is attempting to connect : " + s + '\n');

				// obtaining input and out streams 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				
				//Read user initial input ID
				String id = dis.readUTF();
				
				//SQL call for the specific user id
				st = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				String SELECT_USER = "SELECT * FROM students WHERE SID=" + id;
				
				//Setting rs.next to user to check if there is an valid user (true/false)
				//If so then get the first name of the user id
				rs = st.executeQuery(SELECT_USER);
				boolean user = rs.next();
				String username = rs.getString("FNAME");
				

				//Updating total users requests
				int requests = rs.getInt ("TOT_REQ");
				++requests;

				System.out.println(requests);
                    
                String str1 = Integer.toString(requests);

                //SQL command updating total requests 
				PreparedStatement stmt = con.prepareStatement("UPDATE students SET TOT_REQ=? WHERE SID=?"
				, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        stmt.setString(1, str1);
                        stmt.setString(2, id);
					stmt.executeUpdate();

				//Assigning new thread, passing socket, input, output, user name and text area for server
				Thread t = new ClientHandler(s, dis, dos, user, jta, username); 
				t.start();
				jta.append("Assigning new thread for this client \n");

			} 
			catch (Exception e){ 
				//close sockets
				s.close(); 
				ss.close();
				e.printStackTrace(); 
			} 
		} 
	} 
	private class ExitListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
		  System.exit(0);
		}
	  }
} 