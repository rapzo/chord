package connection.dht.chord.tasks;

import connection.dht.chord.Chord;
import connection.dht.chord.Node;

public class StabilizeTask extends Task {

	public StabilizeTask(Node node) {
		super(node);
	}

	public void run() {
		Node successor;
		
		successor = node.getSuccessor();
		
		if (successor != null && node.id() != successor.id()) {
			Chord.query_predecessor(successor);
		}
	}

}
