package plateauJoueur;


// Classe h�ritant de JFrame
// Permet d'afficher les r�gles �l�mentaires du jeu

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class RulesFrame extends JFrame 
{
	RulesFrame()
	{
		this.setTitle("R�gles du jeu");
		this.setSize(800, 500);
		JPanel rule_pane = (JPanel)this.getContentPane();
		JTextPane textarea = new JTextPane();
		rule_pane.setLayout(new BorderLayout());
		rule_pane.add(textarea);
		textarea.setContentType("text/html");
		textarea.setEditable(false);
		textarea.setText("<html> Les r�gles du <strong>jeu</strong> sont : <br> <lu> <li> Deux joueurs s'affrontent en tour par tour.  </li> <li> Au d�but de la partie, chacun des joueurs dispose ses bateaux sur la grille comme il l'entend </li><li> Au tour par tour, les joueurs visent une case de la grille, qui indique 'Dans l'eau' si aucun bateau adverse ne s'y trouve et 'Touch�' s'il y en a un. C'est alors au tour de l'adversaire.  </li> <li> La partie se termine lorsqu'un des deux joueurs a coul� la totalit� des bateaux adverses.</li></lu></html>");	
	}
}
