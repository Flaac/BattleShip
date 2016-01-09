package plateauJoueur;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;

// Classe héritant de JFrame
// Permet d'afficher quelques commentaires sur l'application


@SuppressWarnings("serial")
public class AboutFrame extends JFrame
{
	AboutFrame()
	{
		this.setTitle("A propos");
		this.setSize(800, 500);
		JPanel about_pane = (JPanel)this.getContentPane();
		JTextPane textarea = new JTextPane();
		about_pane.setLayout(new BorderLayout());
		about_pane.add(textarea);
		textarea.setContentType("text/html");
		textarea.setEditable(false);
		textarea.setText("<html> <h1> BattleShip-Online </h1> <p> Cette application a été réalisée par : </p> <lu> <li> ABGRALL Corentin  </li> <li> L'OLLIVIER Brendan</li><li> CABALL Rémy</li><li> Chandail  </li></lu></html>");
	}
}
