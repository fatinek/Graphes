package core;

/**
 * Hérite de label en donnant une vraie estimation puisque utilisé avec l'algorithme d'A*
 */

public class LabelStar extends Label {
	
	/**
	 * Instancie un labelStar pour une estimation en distance
	 * @param sommet Sommet courant
	 * @param destination Le sommet de destination afin de régler l'estimation
	 */
	public LabelStar(Sommet sommet, Sommet destination) {
		super(sommet);
		this.estimation = Graphe.distance(sommet.longitude, sommet.latitude, destination.longitude, destination.latitude);
	}
	/**
	 * Instancie un labelStar pour une estimation en temps avec une vitesse de 60km/h
	 * @param sommet Sommet courant
	 * @param destination Le sommet de destination afin de régler l'estimation
	 * @param temps Pour savoir qu'on veut une estimation en temps
	 */
	public LabelStar(Sommet sommet, Sommet destination, boolean temps){
		super(sommet);
		this.estimation = ((double)((Graphe.distance(sommet.longitude, sommet.latitude, destination.longitude, destination.latitude))))/((double)(60*1000/60));
	}
	
	

}
