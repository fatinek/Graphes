package core;

/* Permet d'avoir le chemin voulu sur une carte grâce à ses noeuds et les zones de ces noeuds.
 */

import java.util.ArrayList;

public class Chemin {

	private int idCarte;
	
	protected ArrayList<Integer> noeuds = new ArrayList<Integer>();
	
	protected ArrayList<Integer> zones = new ArrayList<Integer>();
	
	public int getIdCarte(){
		return this.idCarte;
	}
	
	public Chemin(int id_carte){
		this.idCarte = id_carte;
	}
	
	
}
