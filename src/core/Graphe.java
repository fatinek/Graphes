package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.io.* ;
import java.util.ArrayList;
import java.util.Iterator;

import base.* ;

public class Graphe {

	// Nom de la carte utilisee pour construire ce graphe
	private final String nomCarte ;

	// Fenetre graphique
	private final Dessin dessin ;

	// Version du format MAP utilise'.
	private static final int version_map = 4 ;
	private static final int magic_number_map = 0xbacaff ;

	// Version du format PATH.
	private static final int version_path = 1 ;
	private static final int magic_number_path = 0xdecafe ;

	// Identifiant de la carte
	private int idcarte ;

	// Numero de zone de la carte
	private int numzone ;

	/*
	 * Ces attributs constituent une structure ad-hoc pour stocker les informations du graphe.
	 * Vous devez modifier et ameliorer ce choix de conception simpliste.
	 */
	// Chaque contiendra tous ses sommets et tous les descripteurs de ses arcs.
	private ArrayList<Descripteur> descripteurs = new ArrayList<Descripteur>() ;
	private ArrayList<Sommet> sommets = new ArrayList<Sommet>();
	/**
	 * Récupère un sommet à partir de son numéro d'identification
	 * @param numero d'identification
	 * @return Le sommet
	 */
	public Sommet getSommetIndexe(int numero){
		return sommets.get(numero);
	}
	/**
	 * Récupère la liste de tous les sommets du graphe
	 * @return La liste des sommets
	 */
	public ArrayList<Sommet> getSommets(){
		return sommets;
	}

	// Deux malheureux getters.
	public Dessin getDessin() { return dessin ; }
	public int getZone() { return numzone ; }

	// Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
	public Graphe (String nomCarte, DataInputStream dis, Dessin dessin) {

		this.nomCarte = nomCarte ;
		this.dessin = dessin ;
		Utils.calibrer(nomCarte, dessin) ;

		// Lecture du fichier MAP. 
		// Voir le fichier "FORMAT" pour le detail du format binaire.
		try {

			// Nombre d'aretes
			int edges = 0 ;

			// Verification du magic number et de la version du format du fichier .map
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_map, version, version_map, nomCarte, ".map") ;

			// Lecture de l'identifiant de carte et du numero de zone, 
			this.idcarte = dis.readInt () ;
			this.numzone = dis.readInt () ;

			// Lecture du nombre de descripteurs, nombre de noeuds.
			int nb_descripteurs = dis.readInt () ;
			int nb_nodes = dis.readInt () ;

			// Nombre de successeurs enregistrÃƒÂ©s dans le fichier.
			int[] nsuccesseurs_a_lire = new int[nb_nodes] ;

			// En fonction de vos choix de conception, vous devrez certainement adapter la suite.

			// Lecture des noeuds
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture du noeud numero num_node

				this.sommets.add(new Sommet(num_node, ((float)dis.readInt ()) / 1E6f, ((float)dis.readInt ()) / 1E6f));
				nsuccesseurs_a_lire[num_node] = dis.readUnsignedByte() ;
			}

			Utils.checkByte(255, dis) ;

			// Lecture des descripteurs
			for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) {
				// Lecture du descripteur numero num_descr
				descripteurs.add(new Descripteur(dis)) ;
			}

			Utils.checkByte(254, dis) ;

			// Lecture des successeurs
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture de tous les successeurs du noeud num_node
				
				for (int num_succ = 0 ; num_succ < nsuccesseurs_a_lire[num_node] ; num_succ++) {
					// zone du successeur
					int succ_zone = dis.readUnsignedByte() ;

					
					// numero de noeud du successeur
					int dest_node = Utils.read24bits(dis) ;

					// descripteur de l'arete
					int descr_num = Utils.read24bits(dis) ;
					
					// longueur de l'arete en metres
					int longueur  = dis.readUnsignedShort() ;
					

					
					// Nombre de segments constituant l'arete
					int nb_segm   = dis.readUnsignedShort() ;
					
					edges++ ;
					
					Arc arc = new Arc(sommets.get(dest_node), descripteurs.get(descr_num), longueur);

					Couleur.set(dessin, arc.descripteur.getType()) ;

					float current_long = this.sommets.get(num_node).longitude ;
					float current_lat  = this.sommets.get(num_node).latitude ;

					// Chaque segment est dessine'
					for (int i = 0 ; i < nb_segm ; i++) {
						arc.segments.add(new Segment((dis.readShort()) / 2.0E5f, (dis.readShort()) / 2.0E5f));
						dessin.drawLine(current_long, current_lat, (current_long + arc.segments.get(i).longitude), (current_lat + arc.segments.get(i).latitude)) ;
						current_long += arc.segments.get(i).longitude ;
						current_lat  += arc.segments.get(i).latitude ;
					}

					// Le dernier trait rejoint le sommet destination.
					// On le dessine si le noeud destination est dans la zone du graphe courant.
					if (succ_zone == numzone) {
						dessin.drawLine(current_long, current_lat, arc.destination.longitude, arc.destination.latitude) ;
					}
					
					this.sommets.get(num_node).arc_adj.add(arc);
					
					if(!descripteurs.get(descr_num).isSensUnique()){
						this.sommets.get(dest_node).arc_adj.add(new Arc(sommets.get(num_node), descripteurs.get(descr_num), longueur));
					}
				}
			}

			Utils.checkByte(253, dis) ;

			System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, " 
					+ nb_descripteurs + " descripteurs.") ;

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

	// Rayon de la terre en metres
	private static final double rayon_terre = 6378137.0 ;

	/**
	 *  Calcule de la distance orthodromique - plus court chemin entre deux points Ã  la surface d'une sphÃ¨re
	 *  @param long1 longitude du premier point.
	 *  @param lat1 latitude du premier point.
	 *  @param long2 longitude du second point.
	 *  @param lat2 latitude du second point.
	 *  @return la distance entre les deux points en metres.
Voulez-vous une sortie graphique (0 = non, 1 = oui) ? chemin_0x100_2_139
	 *  Methode ÃƒÂ©crite par Thomas Thiebaud, mai 2013
	 */
	public static double distance(double long1, double lat1, double long2, double lat2) {
		double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
		double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
		double cosLong = Math.cos(Math.toRadians(long2-long1));
		return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
	}

	/**
	 *  Attend un clic sur la carte et affiche le numero de sommet le plus proche du clic.
	 *  A n'utiliser que pour faire du debug ou des tests ponctuels.
	 *  Ne pas utiliser automatiquement a chaque invocation des algorithmes.
	 */
	public void situerClick() {

		System.out.println("Allez-y, cliquez donc.") ;

		if (dessin.waitClick()) {
			float lon = dessin.getClickLon() ;
			float lat = dessin.getClickLat() ;

			System.out.println("Clic aux coordonnees lon = " + lon + "  lat = " + lat) ;

			// On cherche le noeud le plus proche. O(n)
			float minDist = Float.MAX_VALUE ;
			int   noeud   = 0 ;

			for (int num_node = 0 ; num_node < this.sommets.size() ; num_node++) {
				float londiff = (this.sommets.get(num_node).longitude - lon) ;
				float latdiff = (this.sommets.get(num_node).latitude - lat) ;
				float dist = londiff*londiff + latdiff*latdiff ;
				if (dist < minDist) {
					noeud = num_node ;
					minDist = dist ;
				}
			}

			System.out.println("Noeud le plus proche : " + noeud) ;
			System.out.println() ;
			dessin.setColor(java.awt.Color.red) ;
			dessin.drawPoint(this.sommets.get(noeud).longitude, this.sommets.get(noeud).latitude, 5) ;
		}
	}
	/**
	 * Permet de dessiner un chemin à partir de tous les sommets de celui-ci
	 * @param chemin le chemin à dessiner
	 */
	public void dessinerChemin(Chemin chemin){
		
		dessin.setColor(java.awt.Color.BLUE);
		
		// Pour chaque noeud du chemin on va le relier au suivant en suivant la courbe grâce
		// aux segments
		for(int i = 0; i<chemin.noeuds.size();i++){
				// Si c'est le noeud de destination, on ne le relie à personne et on dessinne un point
				if(chemin.noeuds.size()-1 == i)
					this.dessin.drawPoint(sommets.get(chemin.noeuds.get(i)).longitude, sommets.get(chemin.noeuds.get(i)).latitude, 5);
				else{	
					
					for(Arc arcs : sommets.get(chemin.noeuds.get(i)).arc_adj){
						// Pour tous les arcs de ce sommet, on regarde celui qui correspond
						// au noeud suivant du chemin
						if(arcs.destination == sommets.get(chemin.noeuds.get(i+1))){
							int size = arcs.segments.size();
								// On desssine tous les segments de l'arc
								if (size > 1){
								
								float longitude = sommets.get(chemin.noeuds.get(i)).longitude;
								float latitude = sommets.get(chemin.noeuds.get(i)).latitude;
								for(int f=0;f<size;f++){
									this.dessin.drawLine(longitude,latitude, longitude + arcs.segments.get(f).longitude,latitude + arcs.segments.get(f).latitude) ;
									longitude += arcs.segments.get(f).longitude;
									latitude += arcs.segments.get(f).latitude;
								}
								this.dessin.drawLine(longitude,latitude,sommets.get(chemin.noeuds.get(i+1)).longitude,sommets.get(chemin.noeuds.get(i+1)).latitude);
							
							}	
							else
								this.dessin.drawLine(sommets.get(chemin.noeuds.get(i)).longitude, sommets.get(chemin.noeuds.get(i)).latitude, sommets.get(chemin.noeuds.get(i+1)).longitude,sommets.get(chemin.noeuds.get(i+1)).latitude) ;
						
							break;
							
						}
					
					}
					
				}
		
		}
		


	}
	/**
	 * Permet de calculer le coût d'un chemin donné grâce à ses noeuds
	 * @param chemin Le chemin à évaluer
	 * @param choixChemin En temps (0) ou en distance (1)
	 */
	public void calculCoutChemin(Chemin chemin, int choixChemin){
		
		double temps = 0;
		double TempsMinArc;
		// On va parcourir tout le chemin grâce à ses noeuds
		for(int i = 0; i<chemin.noeuds.size() - 1;i++){
			// Pour le débug
			//System.out.print(chemin.noeuds.get(i) + " -> ");
			
			TempsMinArc = 1000000;
			// Pour le temps
			if (choixChemin==0) {
				// Pour chaque noeud on cherme son arc le reliant au noeud suivant et
				// grâce à cela on lui calcule le temps pour le parcourir
				// grâce à la vitesse et la longueur sur l'arc en m/s
				for(Arc arc : sommets.get(chemin.noeuds.get(i)).arc_adj){
					if( sommets.get(chemin.noeuds.get(i+1)) == arc.destination){
					
						if(TempsMinArc > ((double)(arc.longueur*60))/((double)(1000*arc.descripteur.vitesseMax()))) {
							TempsMinArc = ((double)(arc.longueur*60))/((double)(1000*arc.descripteur.vitesseMax()));
						}

					//System.out.print(chemin.noeuds.get(i+1) + " (" + arc.longueur + ")");
					}
				}
			}
			// Pour la distance
			else {
				for(Arc arc : sommets.get(chemin.noeuds.get(i)).arc_adj){
					if( sommets.get(chemin.noeuds.get(i+1)) == arc.destination){
						
						if(TempsMinArc > arc.longueur) {
							TempsMinArc = arc.longueur;
						}

					//System.out.print(chemin.noeuds.get(i+1) + " (" + arc.longueur + ")");
					}
				}
			}
				
			
			temps += TempsMinArc;
			
			//System.out.println();
		}
		
		System.out.println("Cout : " + temps);
		
	}
	

	/**
	 *  Charge un chemin depuis un fichier .path (voir le fichier FORMAT_PATH qui decrit le format)
	 *  Verifie que le chemin est empruntable et calcule le temps de trajet.
	 */
	public void verifierChemin(DataInputStream dis, String nom_chemin) {

		try {
			
			// Verification du magic number et de la version du format du fichier .path
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

			// Lecture de l'identifiant de carte
			Chemin chemin = new Chemin(dis.readInt ()) ;

			if (chemin.getIdCarte() != this.idcarte) {
				System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
				System.exit(1) ;
			}

			int nb_noeuds = dis.readInt () ;

			// Origine du chemin
			int first_zone = dis.readUnsignedByte() ;
			int first_node = Utils.read24bits(dis) ;

			// Destination du chemin
			int last_zone  = dis.readUnsignedByte() ;
			int last_node = Utils.read24bits(dis) ;

			System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

			// Tous les noeuds du chemin
			for (int i = 0 ; i < nb_noeuds ; i++) {
				chemin.zones.add(dis.readUnsignedByte()) ;
				chemin.noeuds.add(Utils.read24bits(dis)) ;
				System.out.println(" --> " + chemin.zones.get(i) + ":" + chemin.noeuds.get(i)) ;
			}

			if ((chemin.zones.get(nb_noeuds-1) != last_zone) || (chemin.noeuds.get(nb_noeuds-1) != last_node)) {
				System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
				System.exit(1) ;
			}
			
			calculCoutChemin(chemin, 0);
			dessinerChemin(chemin);
			
		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

}
