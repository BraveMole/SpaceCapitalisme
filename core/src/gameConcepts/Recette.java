package gameConcepts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class Recette {
	private static Array<Recette> PossibleRecette;
	final private int[] matpremiere;
	final private int coutmanoeuvre;
	final private int idRessourceProduite;

	private Recette(String recette[]) {
		this.idRessourceProduite=Integer.parseInt(recette[1]);
		this.coutmanoeuvre=Integer.parseInt(recette[2]);
		matpremiere = new int[Ressource.diverse];
		for (int i=3;i<recette.length;i++){
			if (!recette[i].isEmpty()) {
				matpremiere[i-3] = Integer.parseInt(recette[i]);
			}
		}
		Ressource.ressourcePossible.get(idRessourceProduite).setRecette(this);
	}
	
	public int[] getMatpremiere() {
		return matpremiere;
	}
	public int getCoutmanoeuvre() {
		return coutmanoeuvre;
	}
	public static void RecetteCreation(){
		Recette.PossibleRecette = new Array<>();
		String[] wholeFile = Gdx.files.internal("Recette.txt").readString().split("/");
			String[] wholeLine;
			for (int i = 0;i<wholeFile.length-1;i++){
				wholeLine = wholeFile[i].strip().split(";");
				Recette.PossibleRecette.add(new Recette(wholeLine));
			}
	}

	public int getIdRessourceProduite() {
		return idRessourceProduite;
	}

}
