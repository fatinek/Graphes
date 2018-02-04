package core ;

/**
 * Permet de repr�senter un sommet(noeud) dans le graphe
 */

import java.util.ArrayList;

public class Sommet {
	
	protected int numero;
	
	// Tous les arcs partant du sommet
	protected ArrayList<Arc> arc_adj;
	// Sa position sur le graphe
	protected float longitude;
	
	protected float latitude;
	/**
	 * Permet d'instancier un sommet avec 
	 * @param numero Son num�ro d'identification sur le graphe
	 * @param longitude Sa longitude
	 * @param latitude Sa latitude
	 */
	public Sommet(int numero, float longitude, float latitude){
		
	    this.numero = numero;
	    this.longitude = longitude;
	    this.latitude = latitude;
	    this.arc_adj = new ArrayList<Arc>();
		
	}
	/**
	 * Permet � un sommet de se pr�senter
	 */
	public String toString(){
		String blabla = "Numero : " + this.numero + ", la longitude : " + this.longitude + ", la latitude : " + this.latitude + ", les arc : " + "\n" ;
	
		for(Arc ligne : arc_adj)
			blabla = blabla + ligne + "\n";
		
		return blabla;
	}
	
}