package core;

/*Cette classe permet de définir les routes de nos cartes sous formes d'arcs.
 * On y retrouve sa destination, sa longueur, son descripteur et tous ses segments permettant de le retracer.
 */

import java.util.ArrayList;

import base.Descripteur;

public class Arc{
	
	protected Sommet destination;
	
	protected int longueur;
	
	protected Descripteur descripteur;
	
	protected ArrayList<Segment> segments;
	/**
	 * Constructeur permettant d'initialiser l'arc
	 * @param destination 
	 * @param descripteur
	 * @param longueur
	 */
	public Arc(Sommet destination, Descripteur descripteur,  int longueur){
		
		this.destination = destination;
		
		this.longueur = longueur;
		
		this.descripteur = descripteur;
		this.segments = new ArrayList<Segment>();
		
	}
	/**
	 * Permet à un arc de se présenter avec ses caractéristiques
	 */
	public String toString(){
		String temp = "Destination : " + this.destination.numero + ", les segments : " + "\n" ;
	
		for(Segment ligne : segments)
			temp = temp + ligne + "\n";
		
		return temp;
	}
	
}