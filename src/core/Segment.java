package core;

/**
 * Classe permettant de repr�senter un segment contenu dans les arcs
 *
 */

public class Segment{
	
	protected float longitude;
	
	protected float latitude;
	/**
	 * Permet d'instancier un segment avec
	 * @param longitude La longitude
	 * @param latitude  La latitude
	 */
	public Segment(float longitude, float latitude){
		
		this.longitude = longitude;
		
		this.latitude = latitude;
		
	}
	/**
	 * Permet � un segment de se pr�senter lui et ses caract�ristiques
	 */
	public String toString(){
		return "la latitude : " + this.latitude + ", la longitude : " + this.longitude;
	}
	
}