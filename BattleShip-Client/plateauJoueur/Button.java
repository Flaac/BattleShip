package plateauJoueur;

import javax.swing.JButton;


// Classe héritant de Button
// L'intérêt est d'avoir deux attributs qui sont la ligne et la colonne sur
// laquelle il se trouve sur le plateau

@SuppressWarnings("serial")
public class Button extends JButton{
	private int i;
	private int j;
	Button(int i, int j)
	{
		super();
		this.i=i;
		this.j=j;
	}
	
	public int get_I()
	{
		return i;
	}
	
	public int get_J()
	{
		return j;
	}

	public void set_Text(String s)
	{
		this.setText(s);
	}

}
