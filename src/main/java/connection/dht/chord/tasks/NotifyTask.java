package connection.dht.chord.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import connection.dht.chord.Node;

public class NotifyTask implements Runnable {
	
	private Node destiny;
	
	private String query;
	
	
	public NotifyTask(Node d, String q) {
		this.destiny = d;
		this.query = q;
	}

	public void run() {
		DatagramSocket s;
		
		try {
			s = new DatagramSocket();
			
			DatagramPacket packet = new DatagramPacket(
				query.toString().getBytes(),
				query.toString().getBytes().length,
				InetAddress.getByName(destiny.host()),
				destiny.port()
			);
			
			System.out.println("Sending message to "+ destiny.host() + ":" + destiny.port());
			s.send(packet);
			System.out.println("Sent!");

			s.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
