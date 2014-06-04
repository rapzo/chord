package connection.dht.chord;

import java.security.NoSuchAlgorithmException;

import connection.util.Crypto;

public class Key {
	
	private Integer keyspace;
	
	private String seed;
	
	private String hash;
	
	public Key(Integer i, String h) {
		keyspace = i;
		seed = h;
		try {
			hash = Crypto.generateSha1(seed + keyspace.toString());
		} catch (NoSuchAlgorithmException e) {
			hash = h;
		}
	}
	
	public Integer id() { return this.keyspace; }
	
	public String hash() { return this.hash; }

}
