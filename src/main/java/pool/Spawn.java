package pool;

import java.io.File;
import java.util.ArrayList;

import connection.dht.chord.Chord;

import minions.*;

/**
 * @author Rui Pedro
 *
 */
public class Spawn {
	private static Spawn pool;
	
	private ArrayList<Target> targets;
	
	private Spawn(String origin) {
		targets = new ArrayList<Target>();
		this.traverse(new File(origin));
	}
	
	public static Spawn launch() {
		if (pool == null) {
			pool = new Spawn(System.getProperty("user.dir"));
		}
		return pool;
	}
	
	private final void traverse(File path) {
		File[] files = path.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				traverse(files[i]);
			} else {
				this.targets.add(new Target(files[i].getAbsolutePath()));
			}
		}
	}
	
	public static void debug() {
		pool.print();
	}
	
	private void print() {
		for (Target file: this.targets) {
			System.out.println(file.getPath());
		}
	}
	
	public static void main(String[] args) {
		Spawn pool = Spawn.launch();
		// pool.print();

	
		if (args.length == 4) {				
			Chord.join(args[0], Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]));
		} else {
			Chord.create(args[0], Integer.valueOf(args[1]), true);
		}
	}
}
