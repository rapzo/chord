/**
 * 
 */
package connection.util;

/**
 * Classe que gera as mensagens para não andar com plain strings de um lado para o outro
 * e para não andar com altas construções sempre que se manda uma mensagem.
 * @author ruipedro
 *
 */
public class Message {
	public static String build(String key, String[] args) {
		String template = new String("%s %s");
		StringBuilder tmp = new StringBuilder();
		
		for (int i = 0; i < args.length; i++) {
			tmp.append(args[i]);
			if (i != (args.length - 1)) {
				tmp.append(" ");
			}
		}
		/* isto é OS dependent... não parece boa ideia
		tmp
			.append(System.getProperty("line.separator"))
			.append(System.getProperty("line.separator"));
		*/
		// good'ol \n
		tmp.append("\n\n");
		return String.format(template, key.toUpperCase(), tmp.toString());
	}
}
