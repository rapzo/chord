package connection.dht.chord.tasks;

import connection.dht.chord.Chord;
import connection.dht.chord.Node;

public class StabilizeTask implements Runnable {
	
	private Node node;

	public StabilizeTask(Node n) {
		node = n;
	}

	public void run() {
		Node successor;
		
		successor = node.getSuccessor();
		
		if (successor != null && node.id() != successor.id()) {
			Chord.query_predecessor(successor);
		}
	}

}
