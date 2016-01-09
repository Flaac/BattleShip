package plateauServeur;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Arrays;

import plateauJoueur.IArrive;

@SuppressWarnings("serial")
class  Serveur extends UnicastRemoteObject implements IServeur {

	/*-------------------------------------------------------------------------*/
	/*------------------------------Attributs----------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	private ArrayList<IArrive> list_IA_Co = new ArrayList<IArrive>();  			// Liste des joueurs connectés
	private ArrayList<IArrive> list_IA_Ready = new ArrayList<IArrive>();		// Liste des joueurs prêts à jouer
	private ArrayList<IArrive> list_IA_Playing = new ArrayList<IArrive>();		// Liste des joueurs qui sont en train de jouer
	private ArrayList<IArrive> list_IA_Spec = new ArrayList<IArrive>();			// Liste des joueurs spectateurs
	private int[][][] list_Plateau_Playing;										// Valeur des plateaux du jeu en cours
	private int nb_ready_toplay = 0;											// Nombre de personnes prêtes à jouer
	private int lim_coup = 0;													// Nombre de 'touché' pour gagner la partie
	private int joueur1,joueur2;				// Nombre de 'touché' par joueur
	private int tour = 0;						// Designe le joueurs qui doit jouer
	private boolean partie_en_cours = false;	// Vrai si une partie est cours, faux sinon
	
	/*-------------------------------------------------------------------------*/
	/*------------------------------Constructeur-------------------------------*/
	/*-------------------------------------------------------------------------*/
	Serveur() throws RemoteException 
	{
		System.out.println("Serveur installe");
	}
	
	
	/*-------------------------------------------------------------------------*/
	/*---------------------------------Méthodes--------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	// Enregistre les personnes qui se connecte au fur et à mesure
	// Vérifie en même temps si une partie est en cours pour pouvoir la regarder
	
	public void enregistrer(IArrive j)throws RemoteException
	{
		list_IA_Co.add(j);
		System.out.println(partie_en_cours);
		if(partie_en_cours)
		{
			j.init_mode_spec();
		}
	}
	
	
	
	// Menu d'accueil : Transmet un message envoyé par une personne aux autres personnes connectés
	public void emit_nouveau_message(String pseudo, String s) throws RemoteException
	{
		for(int i=0;i<list_IA_Co.size();i++)
		{
			((IArrive) list_IA_Co.get(i)).nouveau_message(pseudo,s);
		}
	}
	
	
	// Menu Accueil : Fait changer de menu au gens prêt à choisir leurs bateaux
	// Dès qu'un joueur se déclare prêt on l'ajoute à list_IA_Ready
	// On compare si un autre joueur est prêt avec les mêmes réglages
	// Si il n'est plus prêt on le retire la liste
	public void set_ready(int ID, boolean etat)throws RemoteException
	{
		if(etat==true) // Player is ready to choose boats		
		{
			for(int i=0;i<list_IA_Co.size();i++)
			{
				int a = ((IArrive) list_IA_Co.get(i)).get_ID();
				if(a==ID)
				{
					list_IA_Ready.add((IArrive) list_IA_Co.get(i));
				}
			}
			int l = list_IA_Ready.size();
			if(partie_en_cours==false)
			{
				if(l>1)
				{
					for(int i=0;i<l-1;i++)
					{
						for(int j=i+1;j<l;j++)
						{
							if(isEquals(((IArrive) list_IA_Ready.get(i)).get_parameter(),(((IArrive) list_IA_Ready.get(j)).get_parameter())))
							{
								list_IA_Playing.add((IArrive) list_IA_Ready.get(i));
								list_IA_Playing.add((IArrive) list_IA_Ready.get(j));
								lim_coup = get_longueur(((IArrive) list_IA_Playing.get(0)).get_parameter());
								list_IA_Ready.remove(j);
								list_IA_Ready.remove(i);
								((IArrive) list_IA_Playing.get(0)).choix_bateau();
								((IArrive) list_IA_Playing.get(1)).choix_bateau();
								init_plateau_serveur();
								
							}
						}
					}
				}
			}
			
		}
		else //Player is not ready anymore
		{
			for(int i=0;i<list_IA_Ready.size();i++)
			{
				if(((IArrive) list_IA_Ready.get(i)).get_ID()==ID)
				{
					list_IA_Ready.remove(i);
				}
			}
		}
	}
	
	// Menu Accueil : Initialise les plateaux pour le menu choice des joueurs
	public void init_plateau_serveur()
	{
		list_Plateau_Playing = new int[2][8][8];
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				list_Plateau_Playing[0][i][j] = 0;
				list_Plateau_Playing[1][i][j] = 0;
			}
		}
	}

	// Menu Choice : Actualise les postions des bateaux en direct qui sont en train d'être posé
	// Transmet la modif à son adversaire
	public void actualise_plateau(int ID, int i, int j, int val)throws RemoteException
	{
		if(((IArrive) list_IA_Playing.get(0)).get_ID()==ID)
		{
			list_Plateau_Playing[0][i][j] = val;
			((IArrive) list_IA_Playing.get(1)).modif_adv(i,j,val);
		}
		else if(((IArrive) list_IA_Playing.get(1)).get_ID()==ID)
		{
			list_Plateau_Playing[1][i][j] = val;
			((IArrive) list_IA_Playing.get(0)).modif_adv(i,j,val);
		}
		else
		{
			System.out.println("Joueur non trouvé : /serveur.java/actualise_plateau");
		}
	}
	
	// Menu Choice : Emet le message est prêt ou non et actualise les listes en fonction
	// lance le jeu quand tout le monde est prêt
	// Active un bouton aux autes joueurs pour qu'ils puissent suivre la partie en mode spectateur
	public void emit_nouveau_message_ready(String s, boolean b) throws RemoteException
		{
			for(int i=0;i<list_IA_Playing.size();i++)
			{
				((IArrive) list_IA_Playing.get(i)).nouveau_message_ready(s);
			}
			if(b)
			{
				nb_ready_toplay++;
				if(nb_ready_toplay==2) // Beginning of the game
				{
					((IArrive) list_IA_Playing.get(0)).launch_game(true);
					((IArrive) list_IA_Playing.get(1)).launch_game(false);
					System.out.println("Ca part !");
					partie_en_cours = true;
					//Activation pour les autres joueurs de regarder la partie
					int ID1, ID0;
					ID0 = ((IArrive) list_IA_Playing.get(0)).get_ID();
					ID1 = ((IArrive) list_IA_Playing.get(1)).get_ID();
					for(int i=0;i<list_IA_Co.size();i++)
					{
						if(((IArrive) list_IA_Co.get(i)).get_ID()!=ID0&&((IArrive) list_IA_Co.get(i)).get_ID()!=ID1)
						{
							((IArrive) list_IA_Co.get(i)).init_mode_spec();
						}
					}
				}
			}
			else
			{
				nb_ready_toplay--;
			}
		}
	
	
	// Méthode utilisé dans la méthode précédente qui permet de comparer deux tableaux de même taille
	// pour savoir si ils contiennent les mêmes éléments
	public boolean isEquals(int[] a, int[] b)
	{
		Arrays.sort(a);
		Arrays.sort(b);
		for(int i=0;i<a.length;i++)
		{
			if(a[i]!=b[i])
			{
				return false;
			}
		}
		return true;
	}
	
	
	// Renvoie la somme des termes d'un tableau d'entier positif
	public int get_longueur(int[] a)
	{
		int somme = 0;
		for(int i=0;i<a.length;i++)
		{
			somme += a[i];
		}
		return somme;
	}
	
	// Lorsqu'une case est jouée
	// Analyse si un bateau était là
	// Transmis au joueur : la case jouée et un message pour dire si le bateau a été touché, le changement de tour
	// Transmis au spectateurs : la case jouée
	// Vérifir si un des joueurs a gagné, le cas écheant :
	//		Arrete le jeu et enlève les spectateurs de la liste
	// 		Dis qui a gagné aux spectateurs et aux joueurs
	// 		Appel de la fonction reinit()
	public void case_joue(int i, int j)throws RemoteException
	{
		String s="error";
		int val = 0;
		if(tour==0)
		{
			if(list_Plateau_Playing[1][i][j]==0)
			{
				// A l'eau
				list_Plateau_Playing[1][i][j]=-1;
				val = -1;
				s = "A l'eau !";				
			}
			else if(list_Plateau_Playing[1][i][j]==1)
			{
				// Touché !
				joueur2++;
				list_Plateau_Playing[1][i][j]=2;
				val=2;

				s = "Touché !";
			}
			((IArrive) list_IA_Playing.get(1)).case_joue_serveur(true,i,j,val);
			((IArrive) list_IA_Playing.get(0)).case_joue_serveur(false,i,j,val);
			for(int k=0;k<list_IA_Spec.size();k++)
			{
				((IArrive) list_IA_Spec.get(k)).case_joue_spec(1, i, j, val);
			}
			tour=1;
		}
		else
		{
			if(list_Plateau_Playing[0][i][j]==0)
			{
				// A l'eau
				list_Plateau_Playing[0][i][j]=-1;
				val=-1;
				s = "A l'eau !";
			}
			else if(list_Plateau_Playing[0][i][j]==1)
			{
				// Touché !
				joueur1++;
				val = 2;
				list_Plateau_Playing[0][i][j]=2;
				s = "Touché !";
			}
			tour=0;
			((IArrive) list_IA_Playing.get(1)).case_joue_serveur(false,i,j,val);
			((IArrive) list_IA_Playing.get(0)).case_joue_serveur(true,i,j,val);
			for(int k=0;k<list_IA_Spec.size();k++)
			{
				((IArrive) list_IA_Spec.get(k)).case_joue_spec(0, i, j, val);
			}
			
		}
		if(joueur1==lim_coup||joueur2==lim_coup)
		{
			
			String s1 = "error";
			if(joueur1==lim_coup)
			{
				s1 = ((IArrive) list_IA_Playing.get(tour+1)).get_pseudo()+" a gagné !";
			}
			else
			{
				s1 = ((IArrive) list_IA_Playing.get(tour-1)).get_pseudo()+" a gagné !";
			}
			for(int h=0;h<list_IA_Spec.size();h++)
			{
				((IArrive) list_IA_Spec.get(h)).nouveau_message("null",s1);
				((IArrive) list_IA_Spec.get(h)).end_spec();
			}
			((IArrive) list_IA_Playing.get(0)).nouveau_message_ready(s1);
			((IArrive) list_IA_Playing.get(1)).nouveau_message_ready(s1);
			((IArrive) list_IA_Playing.get(0)).end_game();
			((IArrive) list_IA_Playing.get(1)).end_game();
			reinit();
		}
		else
		{
			try{
				((IArrive) list_IA_Playing.get(0)).nouveau_message_ready(s);
				((IArrive) list_IA_Playing.get(1)).nouveau_message_ready(s);
				((IArrive) list_IA_Playing.get(0)).chgt_tour();
				((IArrive) list_IA_Playing.get(1)).chgt_tour();}
				catch(Exception e){};
		}
	}
	
	
	// Permet de reinitiliser les listes et valeurs des constantes pour pouvoir refaire une autre partie
	public void reinit()
	{
		list_IA_Playing.remove(1);
		list_IA_Playing.remove(0);
		int l = list_IA_Spec.size();
		for(int i=0;i<l;i++)
		{
			list_IA_Spec.remove(0);
		}
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				list_Plateau_Playing[0][i][j]=0;
				list_Plateau_Playing[1][i][j]=0;
			}
		}
		nb_ready_toplay = 0;
		tour = 0;
		lim_coup = 0;
		joueur1=0;
		joueur2=0;
		partie_en_cours = false;
	}
	
	
	// Lorsqu'une personne décide d'être spectateur, on lui transmet les données de la partie qui sont :
	// Nom des joueurs et valeurs des plateaux
	public void mode_spec_on(int ID)throws RemoteException
	{
		for(int i=0;i<list_IA_Co.size();i++)
		{
			if(list_IA_Co.get(i).get_ID()==ID)
			{
				list_IA_Spec.add((IArrive) list_IA_Co.get(i));
				list_IA_Co.get(i).update_spec(list_Plateau_Playing, ((IArrive) list_IA_Playing.get(0)).get_pseudo(), ((IArrive) list_IA_Playing.get(1)).get_pseudo());
			}
		}
	}
	
}
