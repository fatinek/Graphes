package core;

/**
 * Lance un message d'erreur avec le num�ro du sommet lorsque celui-ci n'existe pas sur la 
 * carte actuelle.
 *
 */

public class SommetInexistantException extends Exception {

	public SommetInexistantException(int sommet){
		System.out.println("\nSommet n°" + sommet + " est inexistant");
	}
	
}
