package connection.dht.chord.tasks;

import connection.dht.chord.Node;

public abstract class Task implements Runnable {

	protected Node node;
	
	public Task(Node n) {
		node = n;
	}

	@Override
	public void run() {

	}

}
