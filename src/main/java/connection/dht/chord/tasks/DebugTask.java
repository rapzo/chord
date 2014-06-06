package connection.dht.chord.tasks;

import connection.dht.chord.Node;

public class DebugTask extends Task {
	
	public DebugTask(Node node) {
		super(node);
	}

	public void run() {
		Node predecessor = node.getPredecessor(), successor = node.getSuccessor();
		
		System.out.println("--------------------------------------------------------------------");
		System.out.println("- Node: "+ node.id() +" ("+ node.id() +")");
		
		if (successor != null)
			System.out.println("- Successor: "+ node.getSuccessor().id() +" ("+ node.getSuccessor().id() +")");
		else System.out.println("- Successor: NULL");
		
		if (predecessor != null)
			System.out.println("- Predecessor: "+ node.getPredecessor().id() +" ("+ node.getPredecessor().id() +")");
		else System.out.println("- Predecessor: NULL");
		
		System.out.println("--------------------------------------------------------------------");
	}

}
