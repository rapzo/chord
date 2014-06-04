package connection.dht.chord;

import java.util.HashMap;


public class Node {
	
	private Key key;

	private String host;
	private int port;
	
	private HashMap<Key, Node> fingers;
	
	private Node predecessor;
	
	private Node successor;
	
	private boolean connected;
	
	// private HashMap<String, String> cache;
	
	
	public Node(String host, int port) {
		this.key = null;
		
		this.host = host;
		this.port = port;
		
		this.predecessor = null;
		this.successor = null;
		
		this.connected = false;
	}

	public Node(Key key, String host, int port) {
		this.key = key;
		
		this.host = host;
		this.port = port;
		
		this.predecessor = null;
		this.successor = this;
		
		this.fingers = new HashMap<Key, Node>();
		
		this.connected = false;
	}
	
	public Node(Key key, String host, int port, Node p, Node s) {
		this.key = key;
		
		this.host = host;
		this.port = port;
		
		this.predecessor = p;
		this.successor = s;
		
		// this.fingers = new FingerTable();
		this.fingers = new HashMap<Key, Node>();
	}
	
	
	public Node getSuccessor() { return this.successor; }
	
	public Node getPredecessor() { return this.predecessor; }
	
	public void setSuccessor(Node node) { this.successor = node; }
	
	public void setPredecessor(Node node) { this.predecessor = node; }
	
	public boolean isConnected() {
		if (this.connected)
			return true;
		if (
			this.successor != null && this.id() != this.successor.id() ||
			this.predecessor != null && this.id() != this.predecessor.id()
		)
			return true;
		return false;
	}
	
	public Key key() { return this.key; }

	public Integer id() { return this.key.id(); }
	
	public String host() { return this.host; }
	
	public int port() { return this.port; }
	
	
	public Node find_successor(Integer id) {
		if (id > this.id() && successor == null)
			return successor;
		if (id > this.id() && successor.id() < id)
			return successor;
		
		/*
		 *
		Node n = closest_preceding_node(id);
		if (n != null)
			return n.find_successor(id);
		return this;
		*
		*/
		
		return this;
	}
	
	public Node closest_preceding_node(Integer id) {
		if (fingers.isEmpty())
			return this;
		
		Key[] keys = (Key[]) fingers.keySet().toArray();
		
		for (int i = keys.length - 1; i >= 0; i--) {
			if (this.id() < id && keys[i].id() < id)
				return fingers.get(keys[i]);
		}
		
		return this;
	}
	
	public boolean in_range(Node n, boolean open) {
		if (n.id() > this.id() && this.successor == null)
			return true;
		if (open)
			return (n.id() > this.id() && n.id() < this.successor.id());
		return (n.id() > this.id() && n.id() <= this.successor.id());
	}

}
