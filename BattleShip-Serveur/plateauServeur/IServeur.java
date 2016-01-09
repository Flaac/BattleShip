package plateauServeur;

import java.rmi.*;

import plateauJoueur.IArrive;

public interface  IServeur extends Remote 
{
	 void enregistrer(IArrive j)throws RemoteException;	
	 void emit_nouveau_message(String ID, String s) throws RemoteException;
	 void set_ready(int ID, boolean etat)throws RemoteException;
	 void emit_nouveau_message_ready(String s, boolean b)throws RemoteException;
	 void actualise_plateau(int ID, int i, int j, int val)throws RemoteException;
	 void case_joue(int i, int j)throws RemoteException;
	 void mode_spec_on(int ID)throws RemoteException;
}


// Les rôles des ces fonctions sont détaillés dans le fichier Serveur.java