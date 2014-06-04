package connection.dht.chord.channels;

import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;

import connection.dht.chord.Chord;
import connection.dht.chord.Key;
import connection.dht.chord.Node;

public class Dispatcher implements Runnable {
	
	private Node node;
	private ArrayBlockingQueue<String> queue;

	public Dispatcher(Node n, ArrayBlockingQueue<String> q) {
		node = n;
		queue = q;
	}

	public void run() {
		String message;
		
		while (true) {
			message = queue.poll();
			if (message != null) {
				this.process_message(message);
			}
		}
		
	}

	
	
	/****** Querying the CHORD ******/
	
	private void process_message(String message) {
		String[] frags = message.trim().split(" ");
		
		if (frags.length == 0)
			return;
		
		String[] address = frags[frags.length - 1].split(":");
		if (address.length != 2)
			return;

		if (frags[0].equalsIgnoreCase("query"))
			this.process_query(address[0], Integer.valueOf(address[1]), frags);
		else if (frags[0].equalsIgnoreCase("reply"))
			this.process_reply(address[0], Integer.valueOf(address[1]), frags);
		else if (frags[0].equalsIgnoreCase("reply"))
			System.out.println("+OK");
		else System.out.println("Wait what?! Message: "+ frags[0]);
	}
	
	
	private void process_query(String addr, int p, String[] frags) {
		
		if (frags[1].equalsIgnoreCase("successor")) {
			Key k = new Key(Integer.valueOf(frags[2]), addr);
			Node n = new Node(k, addr, p);
			
			System.out.println("SUCCESSOR REQ: RANGE (node <-> joined node) "+ node.id() +" <-> "+ n.id());
			
			Node successor = node.find_successor(n.id());
			
			System.out.println("ANSWER: "+ successor.id());
			
			Chord.reply_successor(n, successor);
			
		} else if (frags[1].equalsIgnoreCase("predecessor")) {
			Node n = new Node(addr, p);
			
			Chord.reply_predecessor(n);

		} else if (frags[1].equalsIgnoreCase("notify")) {
			Key k = new Key(Integer.valueOf(frags[2]), addr);
			Node n = new Node(k, addr, p);
			if (n.id() > node.id() && (node.getSuccessor() != null && n.id() < node.getSuccessor().id()))
				node.setSuccessor(n);
		}

	}
	
	private void process_reply(String addr, int p, String[] frags) {
		Key k = new Key(Integer.valueOf(frags[2]), addr);
		Node n = new Node(k, addr, p);
		
		if (frags[1].equalsIgnoreCase("notify")) {
			Chord.reply_notify(n);
		} else if (frags[1].equalsIgnoreCase("predecessor")) {
		
			/*
			 * if (x in (n, successor))
			 *   successor = x;
			 */
			System.out.println("PREDECESSOR REPLY: RANGE (node <-> predecessor) "+ node.id() +" <-> "+ n.id());
			if (n.id() > node.id() && (node.getSuccessor() != null && n.id() < node.getSuccessor().id()))
				node.setPredecessor(n);
			
		} else if (frags[1].equalsIgnoreCase("successor")) {
			System.out.println("SUCCESSOR REPLY: RANGE (node <-> successor) "+ node.id() +" <-> "+ n.id());
				node.setSuccessor(n);
			
			Chord.query_notify(n);
		}
	}
	
	
	
}
