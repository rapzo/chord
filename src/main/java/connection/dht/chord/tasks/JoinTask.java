package connection.dht.chord.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.Callable;

import connection.dht.chord.Node;

public class JoinTask implements Callable<Node> {
	
	private Node node;
	private Node destiny;
	private String username;
	private String password;

	public JoinTask(Node node, Node destiny) {
		this.node = node;
		this.destiny = destiny;
	}
	
	public JoinTask(Node node, Node destiny, String username, String password) {
		this(node, destiny);
		this.username = username;
		this.password = password;
	}

	public Node call() throws Exception {
		DatagramSocket s;
		StringBuilder message = new StringBuilder();
		
		try {
			s = new DatagramSocket();
			message
				.append("QUERY")
				.append("JOIN")
				.append(" ");
			
			if (this.username != null && this.password != null) {
				message
					.append(this.username)
					.append(this.password);
			}
			
			message
				.append(node.host())
				.append(" ")
				.append(node.port())
				.append("\n\n");
			
			System.out.println(destiny.host() + ":" + destiny.port());
			System.out.println(message);
			
			
			DatagramPacket msg = new DatagramPacket(
				message.toString().getBytes(),
				message.toString().getBytes().length,
				InetAddress.getByName(destiny.host()),
				destiny.port()
			);
			
			s.send(msg);
			
			System.out.print(message);
			
			s.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return node;
	}

}
