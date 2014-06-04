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

	public JoinTask(Node node, Node destiny) {
		this.node = node;
		this.destiny = destiny;
	}

	public void run() {
		DatagramSocket s;
		StringBuilder message = new StringBuilder();
		
		try {
			s = new DatagramSocket();
			message
				.append("REQUEST ")
				.append("JOIN ")
				.append(node.id())
				.append(" ")
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
	}

	public Node call() throws Exception {
		this.run();
		return node;
	}

}
