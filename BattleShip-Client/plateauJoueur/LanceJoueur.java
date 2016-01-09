package plateauJoueur;
import java.awt.*;
import java.rmi.*;
import javax.swing.JFrame;
import plateauServeur.IServeur;

// Classe permettant de lancer un joueur
// Pour modéliser plusieurs il faut la lancer plusieurs fois


public class LanceJoueur {
	public static void main(String[] args) {
		JFrame f = new JFrame("BattleShip-Online");
		f.setSize(1200, 300);
		Container c = f.getContentPane();
		IServeur serveur;
		try 
		{
			serveur = (IServeur) Naming.lookup("//localhost:2005/Serveur");
		}
		catch (Exception ex) 
		{
			System.out.println("Exception ; " + ex.getMessage());
			//ex.printStackTrace();
			return;
		}
		Arrive ar = new Arrive(serveur);
		c.add(ar);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}