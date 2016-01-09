package plateauJoueur;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;


// Classe héritant de JPanel
// Elle est instanciée pour permettre au joueur de choisir où placer ses bateaux


@SuppressWarnings("serial")
public class Choice extends JPanel {

	/*-------------------------------------------------------------------------*/
	/*------------------------------Attributs----------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	private int lines;						// Nombre de lignes du plateau
	private int columns;					// Nombre de colonnes du plateau
	private Button[][] list_button;			// Matrice de boutton sur laquelle l'utilisateur clique pour placer ses bateaux
	private boolean first_place = true;		// 
	private int old_i;						// Ligne où le dernier bateau a été placé 
	private int old_j;						// Colonne où le dernier bateau a été placé
	private int longueur_bateau_encours;	// Longueur du bateau qu'on est en train de placer
	private int numero_bateau_encours;		// Indice du bateau qu"on est en train de placer
	private int[] choix_bateau;				// Tableau contenant les longueur des bateaux
	private Arrive ar;						// Classe principale du client
	
	/*-------------------------------------------------------------------------*/
	/*------------------------------Constructeur-------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	Choice(Arrive ar)
	{
		super();
		this.ar=ar;
		this.lines = 8;
		this.columns = 8;
		int l = ar.get_parameter().length;
		while(ar.get_parameter()[l-1]==0)
		{
			l--;
		}
		System.out.println(l);
		choix_bateau = new int[l];
		for(int i=0;i<l;i++)
		{
			choix_bateau[i] = ar.get_parameter()[i];
		}
		
		
		this.setLayout(new GridLayout(lines,columns));
		list_button = new Button[lines][columns];
				
				
		for(int i=0;i<lines;i++) 
		{
			for(int j=0;j<columns;j++)
			{
				Button b = new Button(i,j);
				b.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						place_bateau(b.get_I(), b.get_J());
					}
				});
				this.add(b);
				list_button[i][j] = b;
			}
		}
		numero_bateau_encours=0;
		longueur_bateau_encours = 0;
	}
	
	/*-------------------------------------------------------------------------*/
	/*---------------------------------Méthodes--------------------------------*/
	/*-------------------------------------------------------------------------*/
	
	
	// Désactive tous les boutons à l'écran
	public void desactive_all()
	{
		for(int i=0;i<lines;i++) 
		{
			for(int j=0;j<columns;j++)
			{
				list_button[i][j].setEnabled(false);
			}
		}
	}
	
	// Désactive les cases où un bateau est déjà présent
	public void desactive_bateau()
	{
		for(int i=0;i<lines;i++)
		{
			for(int j=0;j<columns;j++)
			{
				if(ar.get_plateau_perso()[i][j]==1)
				{
					list_button[i][j].setEnabled(false);
				}
				
			}
		}
	}
	
	// Désactive les cases où un bateau est déjà présent
	// Et en plus active les autres cases
	public void actualise_bateau()
	{
		for(int i=0;i<lines;i++)
		{
			for(int j=0;j<columns;j++)
			{
				if(ar.get_plateau_perso()[i][j]==1)
				{
					list_button[i][j].setEnabled(false);
				}
				else
				{
					list_button[i][j].setEnabled(true);
				}
				
			}
		}
	}
	
	// Permet de changer de bateau
	// Une fois un bateau placé, on passe au prochain bateau
	public void longueur_bat()
	{
		longueur_bateau_encours++;
		if(longueur_bateau_encours==choix_bateau[numero_bateau_encours])
		{
			numero_bateau_encours++;
			longueur_bateau_encours=0;
			first_place=true;
			actualise_bateau();
			if(numero_bateau_encours>=choix_bateau.length)
			{
				desactive_all();
				ar.end_choice();
			}
		}
	}
	
	// Gere les boutons à activer quand on place le bateau
	public void place_bateau(int i, int j)
	{
		ar.modif(i,j,1); 
		if(first_place==true)
		{
			desactive_all();
			if(i>0)
			{
				list_button[i-1][j].setEnabled(true);				
			}
			if(i<lines-1)
			{
				list_button[i+1][j].setEnabled(true);				
			}
			if(j>0)
			{
				list_button[i][j-1].setEnabled(true);				
			}
			if(j<columns-1)
			{
				list_button[i][j+1].setEnabled(true);				
			}
			first_place=false;
		}
		else
		{
			list_button[i][j].setEnabled(false);
			if(old_j==j)
			{
				if(old_j>0)
				{
					list_button[old_i][old_j-1].setEnabled(false);				
				}
				if(old_j<columns-1)
				{
					list_button[old_i][old_j+1].setEnabled(false);				
				}
				if(i>old_i)
				{
					if(i<lines-1)
					{
						list_button[i+1][j].setEnabled(true);
					}
					
				}
				else
				{
					if(i>0)
					{
						list_button[i-1][j].setEnabled(true);
					}
				}
			}
			else
			{
				if(old_i>0)
				{
					list_button[old_i-1][old_j].setEnabled(false);				
				}
				if(old_i<columns-1)
				{
					list_button[old_i+1][old_j].setEnabled(false);				
				}
				if(j>old_j)
				{
					if(j<columns-1)
					{
						list_button[i][j+1].setEnabled(true);
					}
					
				}
				else
				{
					if(j>0)
					{
						list_button[i][j-1].setEnabled(true);
					}
				}
			}
		}
		old_i=i;
		old_j=j;
		desactive_bateau();
		longueur_bat();
	}

	// 
	public void reinit_bateau()
	{
		System.out.println("Finir de coder reinit");
	}
}
