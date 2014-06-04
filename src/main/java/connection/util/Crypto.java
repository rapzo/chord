package connection.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Crypto {
	
	private static String secret = "ER379TNm-dbdq1,.GAHaeer,aewdr123D+121!";
	
	/**
	 * 
	 * @param pieces
	 * @return
	 * @throws Exception
	 */
	public static String generateSha1(String piece) throws NoSuchAlgorithmException {
		MessageDigest md;
	
		md = MessageDigest.getInstance("SHA-1");
		
		md.update(piece.getBytes());
		
		md.update(Crypto.secret.getBytes());

		byte[] hash = md.digest();
		StringBuffer hexString = new StringBuffer();

		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		

		return hexString.toString();
	}
}
