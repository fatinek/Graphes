package core;

/**
 * Lance un message d'erreur avec le numéro du sommet lorsque celui-ci n'existe pas sur la 
 * carte actuelle.
 *
 */

public class SommetInexistantException extends Exception {

	public SommetInexistantException(int sommet){
		System.out.println("\nSommet nÂ°" + sommet + " est inexistant");
	}
	
}
