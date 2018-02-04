package core ;

/**
 * Permet d'ex�cuter l'algorithme d'A* en l'initialisant et emp�chant les erreurs
 */

import java.io.* ;
import java.util.ArrayList;

import base.Readarg ;

public class PccStar extends PccGen<LabelStar> {

	/**
	 * Constructeur permettant d'instancier un PCC A*
	 * @param gr Le graphe sur lequel nous allons travailler
	 * @param sortie Fichier d'�criture de sortie
	 * @param readarg Pour r�cup�rer les informations de l'utilisateur
	 */
    public PccStar(Graphe gr, PrintStream sortie, Readarg readarg) {
    	super(gr, sortie, readarg) ;
    }

    /**
     * Initialise tous les param�tres n�cessaires � l'ex�cution d'A*
     */
    public void Init(int choix) throws SommetInexistantException{
    	// On r�cup�re tous les sommets du graphe pour les lab�lis�s
    	ArrayList<Sommet> listeSommet = this.graphe.getSommets();
    	try{
    		// En distance
    		if(choix == 1){
    			// On cr�e un label pour chaque sommet
    			for(int i=0;i<listeSommet.size();i++)
    				listLabel.put(this.graphe.getSommetIndexe(i),new LabelStar(this.graphe.getSommetIndexe(i),this.graphe.getSommetIndexe(destination)));

    			this.sommetExplorer = new LabelStar(this.graphe.getSommetIndexe(this.origine),this.graphe.getSommetIndexe(destination));
    		// En temps
    		}else{
    			// On cr�e un label pour chaque sommet

    			for(int i=0;i<listeSommet.size();i++)
    				listLabel.put(this.graphe.getSommetIndexe(i),new LabelStar(this.graphe.getSommetIndexe(i),this.graphe.getSommetIndexe(destination),true));

    			this.sommetExplorer = new LabelStar(this.graphe.getSommetIndexe(this.origine),this.graphe.getSommetIndexe(destination),true);
    		}
    	}
    	// Si le noeud d'origine n'est pas dans cette carte, on envoie un message d'erreur
    	catch(java.lang.IndexOutOfBoundsException e1){
				throw new SommetInexistantException(origine);
        }
    	
    }
   
}
