package connection.dht.chord.channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;

import connection.dht.chord.Chord;
import connection.dht.chord.Node;

public class ChordChannel implements Runnable {

	private Node node;
	
	ArrayBlockingQueue<String> queue;
	
	public ChordChannel(Node node, ArrayBlockingQueue<String> queue) {
		this.node = node;
		this.queue = queue;
	}

	public void run() {
		DatagramSocket socket;
		byte[] buffer;
		DatagramPacket packet;

		while (true) {
			try {
				buffer = new byte[1024];
				
				packet = new DatagramPacket(buffer, buffer.length);
				
				System.out.println("Listening on port "+ node.port());
				
				socket = new DatagramSocket(node.port());

				socket.receive(packet);
				
				this.process_message(packet.getAddress(), packet.getPort(), packet.getData());

				socket.close();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void process_message(InetAddress addr, int port, byte[] buffer) {
		String message = new String(buffer);
		String[] messages = message.trim().split("\n\n");
		System.out.println("Got "+ messages.length + " messages");
		System.out.println("NODE "+ node.id() +" GOT MESSAGE:");
		
		for (int i = 0; i < messages.length; i++) {
			System.out.println("M#"+ i +": "+ messages[i]);
			queue.add(messages[i]);
		}
	}

}
