package plateauJoueur;

import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import plateauServeur.IServeur;


// Classe héritant de JPanel
// Classe principale du joueur

@SuppressWarnings("serial")
class  Arrive extends JPanel implements IArrive {
	
	/*-------------------------------------------------------------------------*/
	/*------------------------------Attributs----------------------------------*/
	/*-------------------------------------------------------------------------*/
	
   private IServeur serveur = null;			// Interface du serveur avec lequel on communique
   private int ID;							// Caractérise un client parmi les autres
   private String pseudo = "Inconnu"; 		// Pseudo du joueur
   private boolean first_chat = false;		// Bool pour savoir si le joueur a déjà parlé
   private boolean is_ready_toplay = false;	// Pour savoir si le joueur est prêt à jouer
   private int[][] plateau_perso;			// Plateau du joueur
   private int[][] plateau_adversaire;		// Plateau de l'adversaire
   private int[] tab_parameter;				// Paramètres de jeu : nombre de bateaux et longueur
   private boolean myturn;					// Savoir au tour de quelle joueur
   private GamePanel gp;					// Panel centrale quand on joue
   private Choice cp;						// Panel centrale quand on choisis l'emplacement des bateaux
   
   JTextArea zone_chat = new JTextArea(15,30);
   JTextArea zone_news = new JTextArea(15,25);
   
   JButton button_ready_toplay = new JButton("Je suis prêt à jouer !");
   JButton button_end_game = new JButton("");
   JLabel label_north = new JLabel(" YOLO ");
   
   
   JPanel pane_north = new JPanel();
   JPanel pane_center = new JPanel();
   JPanel pane_south = new JPanel();
   JPanel pane_east = new JPanel();
   JPanel pane_west = new JPanel();
   JPanel pane_south_right = new JPanel();
   JPanel pane_south_left = new JPanel();
   
   
	/*-------------------------------------------------------------------------*/
	/*------------------------------Constructeur-------------------------------*/
	/*-------------------------------------------------------------------------*/
   
   
   Arrive(IServeur serveur) 
	{
	   super();
	   
	   long now = System.currentTimeMillis();
	   ID = (int)((now/1000)%2000);
	   System.out.println(ID);
	   this.serveur = serveur;
	   int port = 2006+ID;
	   System.out.println(port);
	   try { UnicastRemoteObject.exportObject(this,port); }
	   catch (Exception ex) {System.out.println("zefzef");ex.printStackTrace();};
	   try { serveur.enregistrer(this); }
	   catch (RemoteException ex) {
		   System.out.println("Remote exception : Enregistrer");
		   ex.printStackTrace();
	   }
	   init();
   }

	/*-------------------------------------------------------------------------*/
	/*---------------------------------Méthodes--------------------------------*/
	/*-------------------------------------------------------------------------*/
   
   // Initialisation de toute les variables et construction de la fenêtre
   // init() est lancé à la fin du constructeur
	public void init()
	{
		// North
		label_north.setText("Bienvenue sur BattleShip-Online");
		pane_north.add(label_north);
		
		
		//West
		pane_west_setup();
		
		//South
		pane_south_right = new JPanel();
		pane_south_left = new JPanel();
		pane_south_right.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pane_south_left.setLayout(new FlowLayout(FlowLayout.LEFT));
		pane_south.setLayout(new GridLayout(1,2));
		JButton button_rules = new JButton("Règles");
		button_rules.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				RulesFrame frame_rules = new RulesFrame();
				frame_rules.setVisible(true);
			}
		});
		JButton button_quit = new JButton("Quitter");
		button_quit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		JButton button_about = new JButton("A propos");
		button_about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				AboutFrame frame_about = new AboutFrame();
				frame_about.setVisible(true);
			}
		});
		pane_south_right.add(button_rules);
		pane_south_right.add(button_about);
		pane_south_right.add(button_quit);
		pane_south.add(pane_south_left);
		pane_south.add(pane_south_right);
		
		
		//South mais pour le menu Choice
		button_ready_toplay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(is_ready_toplay==false)
				{
					try {
						serveur.emit_nouveau_message_ready(pseudo+" est prêt", true);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					System.out.println("Appel button ready toplay, 1");
					is_ready_toplay=true;
				}
				else
				{
					try {
						serveur.emit_nouveau_message_ready(pseudo+" place ses bateaux !", false);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					System.out.println("Appel button ready toplay, 2");
					is_ready_toplay=false;
				}

			}
		});
		
		//Center
		pane_center.setBorder(new TitledBorder(new EtchedBorder(), "Play Area"));
		pane_center.setLayout(new GridLayout(1,1));
		
		
		//East
		JPanel pane_east_south = new JPanel();
		pane_east.setLayout(new BorderLayout());
		pane_east_south.setLayout(new FlowLayout());
		JButton button_submit = new JButton("Submit");
		JTextField zone_submit = new JTextField(33);
		zone_submit.setText("Pseudo");
		JScrollPane scroll = new JScrollPane(zone_chat);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        zone_chat.setEditable(false);
		zone_chat.append("Chat"+"\n");
		pane_east_south.add(zone_submit);
		pane_east_south.add(button_submit);
		pane_east.add("Center",scroll);
		pane_east.add("South",pane_east_south);
		pane_east.setBorder(new TitledBorder(new EtchedBorder(), "Chat Area"));
		ActionListener send= new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String s = zone_submit.getText();
				zone_submit.setText("");
				if(first_chat==true)
				{
					try {
						serveur.emit_nouveau_message(pseudo,s);
					} catch (RemoteException e1) {
						System.out.println("Probleme emission mess");
						e1.printStackTrace();
					}
				}
				else
				{
					pseudo = s;
					try {
						serveur.emit_nouveau_message("null",s+" vient de se connecter !");
					} catch (RemoteException e1) {
						System.out.println("First message error");
						e1.printStackTrace();
					}
					first_chat = true;
				}

			}
		};
		zone_submit.addActionListener(send);
		button_submit.addActionListener(send);

		
		//Final
		this.setLayout(new BorderLayout());
		this.add("Center",pane_center);
		this.add("North",pane_north);
		this.add("East",pane_east);
		this.add("West",pane_west);
		this.add("South",pane_south);
		

	}

	// Permet d'ajouter une chaine de caractère dans la zone de chat chez chaque joueur
	public void nouveau_message(String pseu, String s) throws RemoteException
	{
		if("null".equals(pseu))
		{
			zone_chat.append(s+"\n");
		}
		else
		{
			zone_chat.append(pseu+" : "+s+"\n");
		}
		
	}
	
	
	//Renvoie ID du joueur
	public int get_ID()
	{
		return ID;
	}
	
	// Permet de changer de panel pour permettre au joueur de choisir l'emplacement des bateaux
	public void choix_bateau()
	{
		//Reinit JPanel
		pane_center.removeAll();
		pane_east.removeAll();
		pane_west.removeAll();
		
		//Default parameters
		int lines = 8;
		int columns = 8;
		plateau_perso = new int[lines][columns];
		plateau_adversaire = new int[lines][columns];
		for(int i=0;i<lines;i++)
		{
			for(int j=0;j<columns;j++)
			{
				plateau_perso[i][j]=0;
				plateau_adversaire[i][j]=0;
			}
		}

		
		//Add plateau_principal
		
		cp = new Choice(this);
		pane_center.add(cp);
		
		//North
		label_north.setText("Placez vos bateaux");
		
		//West - Parameters
		JLabel label_paramters = new JLabel("Toutes les options");
		pane_west.add(label_paramters);
		
		
		
		//East - News
		zone_news.append("News"+"\n");
		JScrollPane scroll = new JScrollPane(zone_news);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        zone_news.setEditable(false);
		pane_east.add(scroll);
		
		//South

		JButton button_retry = new JButton("Réinitialiser la position des bateaux");
		button_retry.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				cp.reinit_bateau();
				System.out.println("Ooutoutotut");
			}
		});
		button_ready_toplay.setEnabled(false);
		pane_south_left.add(button_ready_toplay);
		pane_south_left.add(button_retry);

		//Final
		this.setLayout(new BorderLayout());
		this.add("Center",pane_center);
		this.add("North",pane_north);
		this.add("East",pane_east);
		this.add("West",pane_west);
		this.add("South",pane_south);
		this.revalidate();
	}
	
	
	// Permt de finir de finir de lancer une partie
	public void end_choice()
	{
		button_ready_toplay.setEnabled(true);
	}
	
	// Seulement pendant Choice
	// Transmet les modifications du plateau au serveur 
	public void modif(int i, int j, int val)
	{
		plateau_perso[i][j] = val;
		try {
			serveur.actualise_plateau(ID, i, j, val);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	
	// Même chose mais pour l'adversaire
	public void modif_adv(int i, int j, int val)throws RemoteException
	{
		plateau_adversaire[i][j] = val;
	}
	
	// En cours de jeu
	// Transmission de la case jouée au serveur
	// Case joue par nous
	public void case_joue(int i, int j)
	{
		try {
			serveur.case_joue(i,j);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	// Recoit les modifications de plateau du serveur
	// Actualise ensuite l'affichage
	public void case_joue_serveur(boolean b, int i, int j, int val)throws RemoteException
	{
		if(b)
		{
			plateau_perso[i][j]=val;
		}
		else
		{
			plateau_adversaire[i][j]=val;
		}
		gp.actualise_affichage();
	}
	
	
	
	public int[][] get_plateau_perso()
	{
		return plateau_perso;
	}
	
	public int[][] get_plateau_adversaire()
	{
		return plateau_adversaire;
	}
	
	//Ajout d'un message pendant le jeu dans un zone de chat
	public void nouveau_message_ready(String s)throws RemoteException
	{
		zone_news.append(s+"\n");
	}
	
	// Permet de changer de Panel centrale 
	// Lance le jeu cote client
	public void launch_game(boolean b) throws RemoteException
	{		
		System.out.println(ID+" : joue !");
		myturn = b;
		if(b)
		{
			label_north.setText(pseudo+" - A vous de jouer !");
		}
		else
		{
			label_north.setText(pseudo+" - A votre adversaire de jouer !");
		}
		pane_center.removeAll();
		pane_south_left.removeAll();
		//pane_south_left.setLayout(new FlowLayout(FlowLayout.LEFT));
		pane_center.setLayout(new GridLayout(1,1));
		gp = new GamePanel(this, true, "", "");
		pane_center.add(gp);
		
		
		button_end_game = new JButton("Fin du jeu");
		button_end_game.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				reinit();
			}
		});
		button_end_game.setEnabled(false);
		pane_south_left.add(button_end_game);
		this.revalidate();
		pane_south_left.revalidate();
	} 
	
	// Actualise les changements de tour
	public void chgt_tour()throws RemoteException
	{
		if(myturn)
		{
			myturn = false;
			label_north.setText(pseudo+" - C'est à votre adversaire de jouer !");
		}
		else
		{
			myturn = true;
			label_north.setText(pseudo+" - C'est à vous de jouer !");
		}
	}
	
	// Transmet la méthode de fin de jeu au panel principal
	public void end_game()
	{
		gp.end_game();
		button_end_game.setEnabled(true);
		
	}
	
	public boolean get_turn()
	{
		return myturn;
	}
	
	public String get_pseudo()
	{
		return pseudo;
	}
	
	public int[] get_parameter()
	{
		return tab_parameter;
	}
	
	
	// Une fois le jeu fini il faut reinitialiser les autres variables pour permettre de relancer une partie ensuite
	public void reinit()
	{
		pane_center.removeAll();
		pane_east.removeAll();
		pane_west.removeAll();
		//Center
		//South
		//pane_south_setup();
		pane_south_left.removeAll();
		//East
		pane_east_setup();
		//West
		pane_west_setup();
		//North
		label_north.setText("Bienvenue sur BattleShip-Online");
		
		zone_news.setText("");
		
		
		//Reinit valeur
		is_ready_toplay = false;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				plateau_perso[i][j]=0;
				plateau_adversaire[i][j]=0;
			}
		}
		this.revalidate();
	}
	
	
	// Mettre en place le panel east
	public void pane_east_setup()
	{
		JPanel pane_east_south = new JPanel();
		pane_east.setLayout(new BorderLayout());
		pane_east_south.setLayout(new FlowLayout());
		JButton button_submit = new JButton("Submit");
		JTextField zone_submit = new JTextField(33);
		JScrollPane scroll = new JScrollPane(zone_chat);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        zone_chat.setEditable(false);
		pane_east_south.add(zone_submit);
		pane_east_south.add(button_submit);
		pane_east.add("Center",scroll);
		pane_east.add("South",pane_east_south);
		pane_east.setBorder(new TitledBorder(new EtchedBorder(), "Chat Area"));
		ActionListener send = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String s = zone_submit.getText();
				zone_submit.setText("");
				if(first_chat==true)
				{
					try {
						serveur.emit_nouveau_message(pseudo,s);
					} catch (RemoteException e1) {
						System.out.println("Probleme emission mess");
						e1.printStackTrace();
					}
				}
				else
				{
					pseudo = s;
					try {
						serveur.emit_nouveau_message("null",s+" vient de se connecter !");
					} catch (RemoteException e1) {
						System.out.println("First message error");
						e1.printStackTrace();
					}
					first_chat = true;
				}

			}
		};
		zone_submit.addActionListener(send);
		button_submit.addActionListener(send);
	}
	
	// Mettre en place le panel west
	public void pane_west_setup()
	{
		JPanel pane_west_center = new JPanel();
		JPanel pane_west_south = new JPanel();
		pane_west.setLayout(new BorderLayout());
		pane_west_south.setLayout(new FlowLayout());
		pane_west_center.setLayout(new GridLayout(6,2));
		JButton button_ready = new JButton("Ready !");
		SpinnerNumberModel model1 = new SpinnerNumberModel(5.0, 1.0, 5.0, 1.0);  
		JSpinner spin_nb_boat = new JSpinner(model1);
		JLabel label_nb_boat = new JLabel("Nb de bateaux : ");
		spin_nb_boat.setPreferredSize(new Dimension(80,20));
		JSpinner spin_boat1 = new JSpinner(new SpinnerNumberModel(5.0, 2.0, 5.0, 1.0));
		JLabel label_boat1 = new JLabel("Taille bateau 1 :  ");
		spin_nb_boat.setPreferredSize(new Dimension(80,20));
		JSpinner spin_boat2 = new JSpinner(new SpinnerNumberModel(4.0, 2.0, 5.0, 1.0));
		JLabel label_boat2 = new JLabel("Taille bateau 2 : ");
		spin_nb_boat.setPreferredSize(new Dimension(80,20));
		JSpinner spin_boat3 = new JSpinner(new SpinnerNumberModel(3.0, 2.0, 5.0, 1.0));
		JLabel label_boat3 = new JLabel("Taille bateau 3 : ");
		spin_nb_boat.setPreferredSize(new Dimension(80,20));
		JSpinner spin_boat4 = new JSpinner(new SpinnerNumberModel(3.0, 2.0, 5.0, 1.0));
		JLabel label_boat4 = new JLabel("Taille bateau 4 : ");
		JSpinner spin_boat5 = new JSpinner(new SpinnerNumberModel(2.0, 2.0, 5.0, 1.0));
		JLabel label_boat5 = new JLabel("Taille bateau 5 : ");
		
		
		spin_nb_boat.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e)
			{
				double longueur = (double) spin_nb_boat.getValue();
				int a= (int) Math.floor(longueur);
				switch(a)
				{
				case 1:
					spin_boat2.setEnabled(false);
					spin_boat3.setEnabled(false);
					spin_boat4.setEnabled(false);
					spin_boat5.setEnabled(false);
					break;
				case 2:
					spin_boat2.setEnabled(true);
					spin_boat3.setEnabled(false);
					spin_boat4.setEnabled(false);
					spin_boat5.setEnabled(false);
					break;
				case 3:
					spin_boat2.setEnabled(true);
					spin_boat3.setEnabled(true);
					spin_boat4.setEnabled(false);
					spin_boat5.setEnabled(false);
					break;
				case 4:
					spin_boat2.setEnabled(true);
					spin_boat3.setEnabled(true);
					spin_boat4.setEnabled(true);
					spin_boat5.setEnabled(false);
					break;
				case 5:
					spin_boat2.setEnabled(true);
					spin_boat3.setEnabled(true);
					spin_boat4.setEnabled(true);
					spin_boat5.setEnabled(true);
					break;
				default:
					System.out.println(a);
					break;
				}
			}
		});
		spin_nb_boat.setPreferredSize(new Dimension(80,20));
		pane_west_center.add(label_nb_boat);
		pane_west_center.add(spin_nb_boat);
		pane_west_center.add(label_boat1);
		pane_west_center.add(spin_boat1);
		pane_west_center.add(label_boat2);
		pane_west_center.add(spin_boat2);
		pane_west_center.add(label_boat3);
		pane_west_center.add(spin_boat3);
		pane_west_center.add(label_boat4);
		pane_west_center.add(spin_boat4);
		pane_west_center.add(label_boat5);
		pane_west_center.add(spin_boat5);

		pane_west_south.add(button_ready);
		pane_west.add("Center",pane_west_center);
		pane_west.add("South",pane_west_south);
		
		pane_west.setBorder(new TitledBorder(new EtchedBorder(), "Options Area"));
		button_ready.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(spin_nb_boat.isEnabled()==true)
				{
					spin_nb_boat.setEnabled(false);
					tab_parameter = new int[5];
					double l = (double) spin_nb_boat.getValue();
					int l1 = (int) Math.floor(l);
					l = (double) spin_boat1.getValue();
					int a1 = (int) Math.floor(l);
					l = (double) spin_boat2.getValue();
					int a2 = (int) Math.floor(l);
					l = (double) spin_boat3.getValue();
					int a3  = (int) Math.floor(l);
					l = (double) spin_boat4.getValue();
					int a4  = (int) Math.floor(l);
					l = (double) spin_boat5.getValue();
					int a5 = (int) Math.floor(l);
					tab_parameter[0] = a1;
					tab_parameter[1] = a2;
					tab_parameter[2] = a3;
					tab_parameter[3] = a4;
					tab_parameter[4] = a5;
					for(int i=0;i<5;i++)
					{
						if(l1<=i)
						{
							tab_parameter[i]=0;
						}
					}
					try 
					{
						serveur.emit_nouveau_message("null",pseudo+" est prêt à jouer à une partie avec 5 bateaux qui ont ");
						serveur.emit_nouveau_message("null","pour longueurs respectives "+tab_parameter[0]+", "+tab_parameter[1]+", "+tab_parameter[2]+", "+tab_parameter[3]+" et "+tab_parameter[4]);
						serveur.set_ready(ID, true);
					} 
					catch (RemoteException e1) 
					{
						e1.printStackTrace();
					} 
				}
				else
				{
					spin_nb_boat.setEnabled(true);
					try {
						serveur.emit_nouveau_message("null",pseudo+" règle ses paramètres !");
						serveur.set_ready(ID, false);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	// Active un bouton pour permettre au joueur
	// de lancer le mode spectateur
	public void init_mode_spec() throws RemoteException
	{
		JButton button_spec = new JButton("Regarder la partie en cours");
		button_spec.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					serveur.mode_spec_on(ID);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				pane_south_left.remove(button_spec);
			}
		});
		pane_south_left.setLayout(new FlowLayout(FlowLayout.LEFT));
		pane_south_left.add(button_spec);
		pane_south.removeAll();
		pane_south.add(pane_south_left);
		pane_south.add(pane_south_right);
		pane_south.revalidate();
	}
	
	// Charge les données quand le client rejoint une partie en cours
	public void update_spec(int[][][] tableau, String pseudo0, String pseudo1) throws RemoteException
	{
		
		pane_south_left.revalidate();
		plateau_perso = new int[8][8];
		plateau_adversaire = new int[8][8];
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
			{
				plateau_perso[i][j] = tableau[0][i][j];
				plateau_adversaire[i][j] = tableau[1][i][j];
			}
		}
		gp = new GamePanel(this, false, pseudo0, pseudo1);
		pane_center.add(gp);
		this.revalidate();
	}
	
	// Enleve les panel inutile quand la partie de termine pour le spectateur
	public void end_spec() throws RemoteException
	{
		pane_center.removeAll();
		pane_center.revalidate();
	}
	
	// Actualise le plateau lorsque le client est en mode spectateur
	public void case_joue_spec(int a, int i, int j, int val) throws RemoteException
	{
		if(a==0)
		{
			plateau_perso[i][j] = val;
		}
		else
		{
			plateau_adversaire[i][j] = val;
		}
		gp.actualise_affichage();
	}
 }
