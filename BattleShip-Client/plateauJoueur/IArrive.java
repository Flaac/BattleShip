package plateauJoueur;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IArrive extends Remote{
	public void nouveau_message(String ID, String s) throws RemoteException;
	public int get_ID()throws RemoteException;
	public String get_pseudo()throws RemoteException;
	public int[] get_parameter() throws RemoteException;
	public void choix_bateau()throws RemoteException;
	public void nouveau_message_ready(String s)throws RemoteException;
	public void launch_game(boolean b)throws RemoteException;
	public void modif_adv(int i, int j, int val)throws RemoteException;
	public void chgt_tour()throws RemoteException;
	public void case_joue_serveur(boolean b, int i, int j, int val)throws RemoteException;
	public void end_game()throws RemoteException;
	public void init_mode_spec() throws RemoteException;
	public void update_spec(int[][][] a, String pseudo0, String pseudo1) throws RemoteException;
	public void case_joue_spec(int a, int i, int j, int val) throws RemoteException;
	public void end_spec() throws RemoteException;
}

//Les rôles des ces fonctions sont détaillés dans le fichier Arrive.java