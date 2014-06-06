package connection.dht.chord;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import connection.dht.chord.channels.ChordChannel;
import connection.dht.chord.channels.Dispatcher;
import connection.dht.chord.tasks.DebugTask;
import connection.dht.chord.tasks.FixFingersTask;
import connection.dht.chord.tasks.NotifyTask;
import connection.dht.chord.tasks.StabilizeTask;
import connection.dht.chord.tasks.Task;


public class Chord {
	
	public final static int BITS = 8;
	
	public static Chord chord;
	
	public static Node node;
	
	private String host;
	
	private int port;
	
	private Boolean virtual;
	
	private Boolean proxy;
	
	private HashMap<Key, Node> nodes;
	
	private HashMap<String, Key> dht;
	
	ExecutorService executor;
	
	ScheduledExecutorService operator;
	
	HashMap<String, Runnable> channels;
	
	HashMap<String, Runnable> schedule;
	
	// HashMap<String, Callable<Node>> tasks;
	
	
	ArrayBlockingQueue<String> messages;
	
	ArrayBlockingQueue<Task> jobs;
	
	
	/****** Singleton initialization ******/
	private Chord(String host, int port) {
		this.host = host;
		this.port = port;
		
		this.virtual = false;
		this.proxy = false;

		nodes = new HashMap<Key, Node>();
		
		dht = new HashMap<String, Key>();
		
		messages = new ArrayBlockingQueue<String>(10, true);
		
		jobs = new ArrayBlockingQueue<Task>(10, true);
		
		channels = new HashMap<String, Runnable>();		
		
		schedule = new HashMap<String, Runnable>();
		
		// tasks = new HashMap<String, Callable<Node>>();
	}
	
	private Chord(Node n) {
		this(n.host(), n.port());
		
		node = n;
	}
	
	public static void create(String host, int port, boolean spawns) {
		chord = new Chord(new Node(new Key(0, host), host, port));
		
		chord.start();
	}
	
	public static void join(String host, int port, String saddr, int sport) {
		chord = new Chord(new Node(host, port));
		
		chord.start();
		
		Node n = new Node(saddr, sport);

		query_successor(n);
		
		/* for thinking later!!
		 *
		ExecutorService tasker = Executors.newSingleThreadExecutor();
		tasker.execute(new NotifyTask(n, query_successor(node)));
		
		ExecutorService task = Executors.newSingleThreadExecutor();
		Callable<Node> callable = new JoinTask(node, n);
		Future<Node> a = task.submit(callable);

		try {
			System.out.println("FUTURE! "+ a.get().host() +":"+ a.get().port());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		*/
	}
	
	private void start() {
		chord.start_channels();
		chord.start_tasks();
	}
	
	private void start_channels() {
		channels.put("chord", new ChordChannel(node, messages));
		channels.put("dispatcher", new Dispatcher(node, messages));
		
		executor = Executors.newFixedThreadPool(chord.channels.size());
		
		// executor.execute(channels.get("debug"));
		executor.execute(channels.get("chord"));
		executor.execute(channels.get("dispatcher"));
	}
	
	private void start_tasks() {
		schedule.put("fix_fingers", new FixFingersTask(node));
		schedule.put("stabilize", new StabilizeTask(node));
		schedule.put("debug", new DebugTask(node));
		
		operator = Executors.newScheduledThreadPool(1);
		
		operator.scheduleAtFixedRate(schedule.get("stabilize"), 0, 4, TimeUnit.SECONDS);
		operator.scheduleAtFixedRate(schedule.get("debug"), 0, 5, TimeUnit.SECONDS);
	}
	
	
	private void restart_channels() {
		executor.shutdown();
		
		chord.start_channels();
	}

	/*
	public void add_task(String key, Callable<Node> r) {
		chord.tasks.put(key, r);
	}
	
	*/
	
	public void add_channel(String key, Runnable r) {
		chord.channels.put(key, r);
	}
	
	private static Integer generate_id() {
		return new SecureRandom().nextInt((int) Math.pow(2, Chord.BITS));
	}
	
	/************ DHT *******************/
	
	public boolean push_node(Key k, Node n) {
		if (chord.nodes.get(k) == null) {
			nodes.put(k, n);
			return true;
		}
		return false;
	}
	
	public Node find_node(Node n) {
		return chord.nodes.get(node.key());
	}
	
	
	/****** Querying the CHORD ******/
	
	private void notify(Node n, String query) {
		
		ExecutorService tasker = Executors.newSingleThreadExecutor();
		tasker.execute(new NotifyTask(n, query));

	}
	
	public static Node find_successor(Node n) {
		
		/**
		 * Testings
		 *
		ScheduledExecutorService taskor = Executors.newScheduledThreadPool(1);
		NotifyTask task = new NotifyTask(n, chord.query_predecessor(node));
		final ScheduledFuture<?> future = taskor.scheduleAtFixedRate(task, 0, 4, TimeUnit.SECONDS);
		taskor.schedule(new Runnable () {
			public void run() { future.cancel(true); }
		}, 4 * 5, TimeUnit.SECONDS);
		*/

		return n;
	}
	
	public static void query_join(Node n) {
		StringBuilder query = new StringBuilder();
		
		query
			.append("QUERY")
			.append(" ")
			.append("JOIN")
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}
	
	public static void query_successor(Node n) {
		StringBuilder query = new StringBuilder();
		
		query
			.append("QUERY")
			.append(" ")
			.append("SUCCESSOR")
			.append(" ")
			.append(node.id())
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}
	
	public static void query_predecessor(Node n) {
		StringBuilder query = new StringBuilder();
		
		query
			.append("QUERY")
			.append(" ")
			.append("PREDECESSOR")
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}
	
	public static void query_notify(Node n) {
		StringBuilder query = new StringBuilder();
		
		query
			.append("QUERY")
			.append(" ")
			.append("NOTIFY")
			.append(" ")
			.append(node.id())
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}
	
	public static void reply_join(Node n) {
		StringBuilder query = new StringBuilder();
		
		query
			.append("REPLY")
			.append(" ")
			.append("JOIN")
			.append(" ")
			.append(n.id())
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}
	
	public static void reply_successor(Node n, Node successor) {
		StringBuilder query = new StringBuilder();
		
		query
			.append("REPLY")
			.append(" ")
			.append("SUCCESSOR")
			.append(" ")
			.append(successor.id())
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}
	
	public static void reply_predecessor(Node n) {
		StringBuilder query = new StringBuilder();
		Node predecessor = node.getPredecessor();
		
		query
			.append("REPLY")
			.append(" ")
			.append("PREDECESSOR")
			.append(" ")
			.append(predecessor.id())
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}
	
	public static void reply_notify(Node n) {
		StringBuilder query = new StringBuilder();
		
		query
			.append("REPLY")
			.append(" ")
			.append("NOTIFY")
			.append(" ")
			.append(node.host())
			.append(":")
			.append(node.port())
			.append("\n\n");
		
		chord.notify(n, query.toString());
	}

	
	/*************** Not used ***************/
	
	public boolean isVirtual() { return chord.virtual; }
	
	public boolean isProxy() { return chord.proxy; }
}
