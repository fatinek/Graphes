package core;

/*
 * Permet de définir un label pour chaque sommet en sauvegardant son coût depuis l'origine, si l'algorithme est passé dessus,
 * son estimation vers la destination, le sommet précédent et son numéro d'identification.
 */

public class Label implements Comparable<Label> {
	
	private boolean marquage;
	private double cout;
	protected double estimation = 0.0; // Il n'y a pas d'estimation pour les Label Dijkstra
	private int pere;
	private int sommet_courant;
	/**
	 * Permet d'instancier un label pour un sommet.
	 * @param sommet Le sommet du label
	 */
	public Label(Sommet sommet){
		this.setMarquage(false);// Initialement jamais marqué
		this.setCout(Float.POSITIVE_INFINITY); // Il a donc un coût infini
		this.setPere(0); // Noeud précédent inconnu
		this.setSommet_courant(sommet.numero);
	}
	

	public boolean isMarquage() {
		return marquage;
	}

	public void setMarquage(boolean marquage) {
		this.marquage = marquage;
	}

	public double getCout() {
		return cout;
	}

	public void setCout(double cout) {
		this.cout = cout;
	}


	public int getPere() {
		return pere;
	}


	public void setPere(int pere) {
		this.pere = pere;
	}


	public int getSommet_courant() {
		return sommet_courant;
	}

	public void setSommet_courant(int sommet_courant) {
		this.sommet_courant = sommet_courant;
	}
	
	public double getEstimation(){
		return this.estimation;
	}

	public void setEstimation(double estimation){
		this.estimation = estimation;
	}
	/**
	 * Permet de comparer deux labels entre eux par rapport à leur coût
	 */
	@Override
	public int compareTo(Label o) {
		
		int temp = (int)( (this.getCout()+this.getEstimation()) - (o.getCout()+o.getEstimation()) );
		if(temp == 0)
			temp = (int)(this.getEstimation() - this.getEstimation());
				
		return temp;
		
	}
	
}
