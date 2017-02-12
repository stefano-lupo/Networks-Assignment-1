

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class MyServer extends Thread{
	private ServerSocket serverSocket;
	private static int portNum = 8084;
	
	private ArrayList<Frame> frames;
	private Frame frame;
	

	public MyServer(int port) throws IOException {
		frames = new ArrayList<>();
		System.out.println("-------------------------Server--------------------------------");
		serverSocket = new ServerSocket(portNum);
//		serverSocket.setSoTimeout();
	}


	public void run() {
		while(true) {
			try{
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				System.out.println();
				
				// Create Data input stream
				DataInputStream dataInputStream = new DataInputStream(server.getInputStream());
				DataOutputStream dataOutputStream = new DataOutputStream(server.getOutputStream());
				
				// Welcome client to Server
				dataOutputStream.writeUTF("SERVER: You have succesfully connected");
				
				// Create object input stream from this data stream
				ObjectInputStream objectInputStream = new ObjectInputStream(dataInputStream);
				
				// Try accessing the object
				try {
					System.out.println("Waiting for frame from client..");
					frame = (Frame)objectInputStream.readObject();
					frames.add(frame);
					System.out.println("Frame Received from client - Checking CRC");
					dataOutputStream.writeUTF("SERVER: Frame Received - Checking CRC");
					
					if(frame.checkCRC()){
						dataOutputStream.writeUTF("SERVER: No CRC error found");
					} else {
						dataOutputStream.writeUTF("CRC found an error - Retransmit");
					}
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				System.out.println("Data Received = " + frame.getData());				
				System.out.println();
				System.out.println();
				server.close();

			} catch (SocketTimeoutException s){
				System.out.println("Socket timeout");
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			Thread t = new MyServer(8083);
			t.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}


