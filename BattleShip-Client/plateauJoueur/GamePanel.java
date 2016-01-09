package plateauJoueur;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


// Classe héritant de JPanel
// Elle est instancié pour permettre au joueur de jouer

@SuppressWarnings("serial")
public class GamePanel extends JPanel 
{
	
	
	/*-------------------------------------------------------------------------*/
	/*------------------------------Attributs----------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	private Arrive ar;								// 
	private int lines;								// Nombre de lignes du plateau
	private int columns;							// Nombre de colonnes du plateau
	private Button[][] plateau_perso_button;		// Plateau du joueur
	private Button[][] plateau_adversaire_button;	// Plateau de son adversaire
	private boolean joueur;							// True si joueur, False si c'est un spectateur
	
	
	/*-------------------------------------------------------------------------*/
	/*------------------------------Constructeur-------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	GamePanel(Arrive ar, boolean bool, String pseudo0, String pseudo1)
	{
		super();
		joueur = bool;
		this.ar = ar;
		lines = 8;
		columns = 8;
		JPanel pane_left = new JPanel();
		JPanel pane_right = new JPanel();
		pane_left.setLayout(new GridLayout(lines, columns));
		pane_right.setLayout(new GridLayout(lines, columns));
		plateau_perso_button = new Button[lines][columns];
		plateau_adversaire_button = new Button[lines][columns];
		if(!joueur)
		{
			pane_left.setBorder(new TitledBorder(new EtchedBorder(),pseudo0));
			pane_right.setBorder(new TitledBorder(new EtchedBorder(), pseudo1));
		}
		else
		{
			pane_left.setBorder(new TitledBorder(new EtchedBorder(),"You"));
			pane_right.setBorder(new TitledBorder(new EtchedBorder(),"Adversaire"));
		}
		
		for(int i=0;i<lines;i++) 
		{
			for(int j=0;j<columns;j++)
			{
				Button b = new Button(i,j);
				plateau_perso_button[i][j] = b;
				pane_left.add(b);
				b = new Button(i,j);
				if(joueur)
				{
					b.addActionListener(new ButtonListener(i,j,ar));
				}
				plateau_adversaire_button[i][j] = b;
				pane_right.add(b);
				
			}
		}
		this.setLayout(new GridLayout(1,2));
		this.add(pane_left);
		this.add(pane_right);
		
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(ar.get_plateau_perso()[i][j]==1)
				{
					plateau_perso_button[i][j].set_Text("B");
					
				}
				plateau_perso_button[i][j].setEnabled(false);
				if(joueur)
				{
					plateau_adversaire_button[i][j].setEnabled(true);
				}
				else
				{
					plateau_adversaire_button[i][j].setEnabled(false);
				}
				
			}
		}
		if(!joueur)
		{
			actualise_affichage();
		}
	}
	
	
	/*-------------------------------------------------------------------------*/
	/*---------------------------------Méthodes--------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	// Classe interne
	// Associé à chaque bouton
	// Lui permet de donner sa ligne et sa colonne pour l'idnetifier
	class ButtonListener implements ActionListener
	{
		private int i;
		private int j;
		private Arrive a;
		ButtonListener(int i, int j, Arrive a)
		{
			this.i=i;
			this.j=j;
			this.a=a;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(a.get_turn())
			{
				case_joue_GP(i, j);
			}
		}
	}
	
	
	
	
	// Transmet à l'instance de Arrive le coup joue
	// Il sera transmis ensuite vers le serveur
	public void case_joue_GP(int i, int j)
	{
		ar.case_joue(i,j);
	}
	
	
	
	// Actualise l'affichage en fonction des valeurs des plateaux
	// Quand le client est spectateur on affiche toute les cases
	// Dans le cas contraire, on affiche que ses propres cases et les cases jouées
	// (Sinon le joueur connait l'emplacement des bateaux adverses)
	public void actualise_affichage()
	{
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				if(ar.get_plateau_perso()[i][j]==1)
				{
					plateau_perso_button[i][j].set_Text("B");
				}
				else if(ar.get_plateau_perso()[i][j]==2)
				{
					plateau_perso_button[i][j].set_Text("B+T");
				}
				else if(ar.get_plateau_perso()[i][j]==-1)
				{
					plateau_perso_button[i][j].set_Text("O");
				}
				else if(ar.get_plateau_perso()[i][j]==0)
				{
					plateau_perso_button[i][j].set_Text(" ");
				}
				plateau_perso_button[i][j].setEnabled(false);
				if(ar.get_plateau_adversaire()[i][j]==2)
				{
					plateau_adversaire_button[i][j].set_Text("B+T");
					plateau_adversaire_button[i][j].setEnabled(false);
					
				}
				else if(ar.get_plateau_adversaire()[i][j]==-1)
				{
					plateau_adversaire_button[i][j].set_Text("O");
					plateau_adversaire_button[i][j].setEnabled(false);
				}
				else if(ar.get_plateau_adversaire()[i][j]==1&&joueur==false)
				{
					plateau_adversaire_button[i][j].set_Text("B");
				}
			}
		}
	}
	
	
	// Appele en fin de jeu pour empecher le joueur de pouvoir cliquer sur les boutons
	// On les désactive donc tous
	public void end_game()
	{
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				plateau_perso_button[i][j].setEnabled(false);
				plateau_adversaire_button[i][j].setEnabled(false);
			}
		}
	}
}
