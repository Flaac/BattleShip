package plateauServeur;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;

// Class LanceSerceur
// Lance le serveur
public class LanceServeur {
	public static void main(String[] args) {
		Serveur serveur;
		try {
			LocateRegistry.createRegistry(2005);
			serveur  = new Serveur();
			Naming.rebind("//localhost:2005/Serveur",serveur);
			System.out.println("Serveur enregistre dans Registry");
		} catch (Exception ex) {
			System.out.println("CompteurServeur.main : exception " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
