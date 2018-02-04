package core ;

import java.io.* ;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import base.Readarg ;

public class PccGen<E> extends Algo {

    // Numero des sommets et des zones origines et destinations
    protected int zoneOrigine ;
    protected int origine ;
    protected int zoneDestination ;
    protected int destination ;
    // Notre tas qui contient les noeuds explorés
    protected BinaryHeap tasExplorateur = new BinaryHeap<>();
    // La structure de nos noeuds labelisés 
    protected HashMap<Sommet,E> listLabel = new HashMap<Sommet, E>();
    // En distance ou en temps (0 ou 1)
    protected int choix;
    // Sommet actuel
    protected E sommetExplorer;
    // Le calcul du temps du chemin en temps ou en distance (0 ou 1)
    protected int choixChemin;
    
	/**
	 * Constructeur permettant d'instancier un PCC A*
	 * @param gr Le graphe sur lequel nous allons travailler
	 * @param sortie Fichier d'écriture de sortie
	 * @param readarg Pour récupérer les informations de l'utilisateur
	 */
    public PccGen(Graphe gr, PrintStream sortie, Readarg readarg) {
    	super(gr, sortie, readarg) ;

    		this.zoneOrigine = gr.getZone () ;
    		this.origine = readarg.lireInt ("Numero du sommet d'origine ? ");

    		// Demander la zone et le sommet destination.
    		this.zoneDestination= gr.getZone () ;
    		this.destination = readarg.lireInt ("Numero du sommet destination ? ");
    		// Demande le choix entre le temps et la distance pour les deux
    		this.choix = readarg.lireInt("En temps ou distance ? (0 ou 1)");
    		this.choixChemin = readarg.lireInt("En temps ou distance votre (calcul_cout_chemin) ? (0 ou 1)");
    
    }
    /**
     * Initialise toutes les caractéristiques nécessaires au lancement des algorithmes de plus
     * courts chemins
     * @param choix En temps (0) ou en distance(1)
     * @throws SommetInexistantException Si le sommet d'origine n'est pas le bon, message d'erreur
     */
    public void Init(int choix) throws SommetInexistantException{
    	
    }
    /**
     * L'algorithme de recherche de plus court en chemin. Il va comparer tous les arcs de chaque noeud du chemin
     * en se dirigeant vers la destination pour trouver celui avec le plus court chemin.
     * A* se dirigera plus rapidement vers la destination grâce à son estimateur.
     */
    public void run(){
    	// Active le timer pour le calcul du CPU
        long tempsCalcul = System.currentTimeMillis();
    	
        try {
        	// Initisalition
			Init(choix);
			
	    	System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;
	    	//Le maximum d'élement dans le tas, le nombre de sommet explorés et marqués
	    	int maxElementTas = 1; // Sommet d'origine présent
	    	int nbreSommetsExplores = 1; // Sommet d'origine présent
	    	int nbreSommetsMarques = 0; // Sommet d'origine non marqué
	    	
	    	((Label) sommetExplorer).setCout(0.0);
	    	// Le label du destination
        	E labelDest = listLabel.get(this.graphe.getSommetIndexe(destination));
        	
        	// Notre origine est inséré
	    	tasExplorateur.insert((Label)sommetExplorer);
	    	
	    	// Si nous ne sommes pas sur un chemin nul
			if(origine != destination){
				// Tant que nous n'avons pas marqué tous les sommets ou que la destination n'est pas atteinte
		    	while(!tasExplorateur.isEmpty() && !((Label) labelDest).isMarquage() ){
		    		// On prend le sommet avec le coût le plus petit et on le marque
		    		sommetExplorer = (E) tasExplorateur.deleteMin();
		    		((Label) sommetExplorer).setMarquage(true);
		    		
		    		nbreSommetsMarques++;
		    		// Pour tous les arcs de ce sommet
		    		for(Arc arcs : this.graphe.getSommetIndexe(((Label) sommetExplorer).getSommet_courant()).arc_adj){
		    			// On va comparer le coût de leur sommet de destination s'il n'est pas déjà marqué avec celui d'origine
		    			Label succ = (Label) listLabel.get(arcs.destination);
		    			if(!succ.isMarquage()){
		    				// En distance
		    				if(choix == 1){
		    					// Si son coût est supérieur, on lui donne le nouveau coût depuis l'origine
		    					// Puis ont l'insère dans le tas ou en le met à jour s'il n'existait pas
		    					// en précisant le sommet précédent.
			    				if(succ.getCout() > (((Label) sommetExplorer).getCout()+ arcs.longueur)){
			    					succ.setCout(((Label) sommetExplorer).getCout() + arcs.longueur);
			    					if(tasExplorateur.getIndex(succ) == null){
			    						tasExplorateur.insert(succ);	   
			    						nbreSommetsExplores++;
			    					}
			    					else
			    						tasExplorateur.update(succ);
		
			    					succ.setPere(((Label) sommetExplorer).getSommet_courant());
			    				}
		    				}
		    				// En temps en convertisant la longueur en temps grâce à la vitesse contenu dans le descripteur de l'arc
		    				else {
			    				if(succ.getCout() > (((Label) sommetExplorer).getCout() + (((double)(arcs.longueur*60))/((double)(1000*arcs.descripteur.vitesseMax()))))){
			    					succ.setCout(((Label) sommetExplorer).getCout() + (((double)(arcs.longueur*60))/((double)(1000*arcs.descripteur.vitesseMax()))));
			    					if(tasExplorateur.getIndex(succ) == null){
			    						tasExplorateur.insert(succ);
			    						nbreSommetsExplores++;
			    					}
			    					else
			    						tasExplorateur.update(succ);
			    					succ.setPere(((Label) sommetExplorer).getSommet_courant());
			    				}
		    				}
		    				
		    			}
		    				
		    		}
		    		// On met à jour le maximum qu'il y a eu dans le tas explorateur
		    		if(tasExplorateur.size() > maxElementTas)
		    			maxElementTas = tasExplorateur.size();
		    	}  
		    	// Dessine le résultat du chemin pour une comparaison visuelle
		    	try{
		    		this.dessinerResultatChemin();
		    	// Si les sommets ne sont pas connexes,
		    	//le coût du chemin est à l'infini (chemin entre deux îles par exemple)
		    	}catch(java.lang.OutOfMemoryError e){
		    		((Label) labelDest).setCout(Float.POSITIVE_INFINITY);
		    	}
		    	
			}
			// Si c'est un chemin nul, on marque la destination, on lui donne un coût de 0.
			else{
				((Label) labelDest).setCout(0.0);
				((Label) labelDest).setMarquage(true);
				tasExplorateur.insert(((Label)labelDest));
				nbreSommetsMarques++;
			}
			// Fin du timer pour le CPU
	    	tempsCalcul = System.currentTimeMillis() - tempsCalcul;
	    	
	    	System.out.println("\nCoÃ»t : " + ((Label) labelDest).getCout() + ", pÃ¨re : " + ((Label) labelDest).getPere() + ", maximum d'Ã©lements dans le tas : " + maxElementTas + ", nombre de sommets explorÃ©s : " + nbreSommetsExplores + ", nombre de sommets marquÃ©s : " + nbreSommetsMarques + ", temps de calcul : " + tempsCalcul + " ms.");
       // Si le sommet de destination n'est pas dans la carte actuelle,
	   // un message d'erreur est envoyé
        } catch (SommetInexistantException e1) {
        }catch(java.lang.IndexOutOfBoundsException e1){
        	try{
        		throw new SommetInexistantException(destination);
        	}catch(SommetInexistantException e2){
        		
        	}
        }
    }
    /**
     * Dessine le chemin trouvé grâce à l'algorithme pour avoir un résultat visuel
     */
    protected void dessinerResultatChemin(){
    	
    	E labelDest = listLabel.get(this.graphe.getSommetIndexe(destination));
    	Chemin cheminPlusCourt = new Chemin(0x400);
    	int pereTemp = ((Label) labelDest).getPere();
    	// On ajoute notre destination au chemin à dessiner
    	cheminPlusCourt.noeuds.add(((Label) labelDest).getSommet_courant());
    	//Tant que nous n'avons pas ajouter l'origine au chemin à dessiner
    	while(pereTemp != origine){
    		// On ajoute chaque noeud au chemin à dessiner
    		cheminPlusCourt.noeuds.add(pereTemp);
    		pereTemp = ((Label)listLabel.get(this.graphe.getSommetIndexe(pereTemp))).getPere();
    	}
    	// On ajoute l'origine et on inverse l'ordre de la liste pour commencer à dessiner
    	// à partir de l'origine
    	cheminPlusCourt.noeuds.add(origine);
    	Collections.reverse(cheminPlusCourt.noeuds);
    	// Dessin du chemin et calcul de son coût
    	graphe.dessinerChemin(cheminPlusCourt);
    	graphe.calculCoutChemin(cheminPlusCourt, choixChemin);
    }

}

