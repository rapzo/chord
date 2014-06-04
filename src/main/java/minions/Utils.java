package minions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author Rui Pedro
 *
 */
public class Utils {
	public static String generateSha1(File file) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
		byte[] dataBytes = new byte[1024];

		int nread = 0; 

		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		};
		fis.close();
		
		byte[] mdbytes = md.digest();

		//convert the byte to hex format
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
	
	
	/**
	 * Método para gerar uma hash única usando o algoritmo sha256
	 * @param file
	 * @return String com a hash gerada a partir de metadata do ficheiro fornecido e um timestamp. 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String generateSha256(File file) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			
			// Gerar sal com nome do ficheiro
			md.update(file.getName().getBytes());
			
			// Adicionar canonicalpath porque diz ser única :-)
			md.update(file.getCanonicalPath().getBytes());
			
			// Adiciona o tamanho do ficheiro
			md.update(new String(new Long(file.length()).toString()).getBytes());
			
			// E a última atualização para garantir que a mais recente versão não tem o mesmo id
			md.update(new String(new Long(file.lastModified()).toString()).getBytes());

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
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Método para gerar um número aleatório, neste caso, para gerar os tempos de espera das threads.
	 * Source: http://stackoverflow.com/questions/363681/generating-random-numbers-in-a-range-with-java
	 * @param min O limite mínimo do intervalo.
	 * @param max O limite máximo do intervalo.
	 * @return Um número aleatório entre min e max.
	 */
	public static int randomize(int min, int max) {
		Random seed = new Random();
		
		int r = seed.nextInt((max - min) + 1) + min;
		
		return r;
	}

	
	public static byte[] wrapChunkBody(String[] chunks) {
		StringBuffer sf = new StringBuffer();
		
		for (String s: chunks) {
			sf.append(s);
		}
		
		return sf.toString().getBytes();
	}
}
