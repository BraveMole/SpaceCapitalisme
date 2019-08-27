package gameConcepts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import commerce.Besoin;
import commerce.MarketPlace;
import commerce.Offre;
import gameObjets.HasInventory;
import gameObjets.Implantation;
import gameObjets.Trader;

public class Population implements Trader{
	private static float [] produitConsomme;
	private static float StockRessource =5;
	private static final double DistanceCoef =0.0000001;
	
	private int nbTravailleur;
	private Inventory inventory;
	private int[]prixachat;
	private Array<Besoin> listeBesoin;
	private Implantation implantation;
	
	public static void besoinInit(){
		produitConsomme = new float[Ressource.ressourcePossible.size()];
		String[] wholeFile = Gdx.files.internal("BesoinPop.txt").readString().split(";");
		int i=0;
		for (String s : wholeFile) {
			if (!s.isEmpty()){
				produitConsomme[i]=Float.parseFloat(s)/100f;
				i++;
			}
		}
	}

	public Population (int nbTravailleur,Implantation implantation) {
		this.nbTravailleur=nbTravailleur;
		this.inventory= new Inventory();
		this.implantation=implantation;
		this.listeBesoin= new Array<>();
		this.prixachat = new int[produitConsomme.length];
		for (int i=0;i<produitConsomme.length;i+=2) {
			this.prixachat[i]=Ressource.getBasicPrice((int)produitConsomme[i]);
		}
		MarketPlace.addTrader(this);
	}
	
	@Override
	public boolean receivingCargo(int ressourceId, float quantity) {
		if (quantity<0) {
			if(this.inventory.addRessource(ressourceId, quantity)) {
				this.addObjetVendu(quantity);
				return true;
			}
			else {
				return false;
			}
		}
		return this.inventory.addRessource(ressourceId, quantity);
	}
	@Override
	public boolean startingCargoExchange(int ressourceId, float quantity,HasInventory t) {
		if (t.receivingCargo(ressourceId, quantity)) { //La partie adverse peut faire la transfaction
			if(!this.receivingCargo(ressourceId, -quantity)) { //Si celui qui initie la transaction ne peut pas la faire, on l'annule
				t.receivingCargo(ressourceId, -quantity);
				return false;
			}
			return true;
		}
		return false;
	}
	@Override
	public void updateBesoin() {
		this.listeBesoin.clear();
		for (int i=0;i<produitConsomme.length;i++) {
			if ((Population.StockRessource*this.nbTravailleur * produitConsomme[i])
					-this.inventory.getRessource(i) >0) {
				this.listeBesoin.add(new Besoin(i,
						Population.StockRessource*this.nbTravailleur * Population.produitConsomme[i]
						-this.inventory.getRessource(i)
						, this.prixachat[i], this));
			}
		}
		
	}
	public int getNbTravailleur(){
		return this.nbTravailleur;
	}
	@Override
	public void updateOffre() {
	}

	@Override
	public void cycle() {
		for (int i = 0; i < Ressource.diverse; i++) {
			this.inventory.addRessource(i,this.nbTravailleur*Population.produitConsomme[i]);
		}
	}

	@Override
	public Offre getOffre() {
		return null;
	}
	@Override
	public Array<Besoin> getBesoin() {
		return this.listeBesoin;
	}
	@Override
	public float getX() {
		return this.implantation.getX();
	}

	@Override
	public float getY() {
		return this.implantation.getY();
	}
	@Override
	public double distance(Trader t) {
		return Math.sqrt(Math.pow((this.getX()-t.getX()),2)+Math.pow((this.getY()-t.getY()),2));
	}
	@Override
	public Implantation getImplatation() {
		return this.implantation;
	}
	@Override
	public void addObjetVendu(float nbobjetvendu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
	

}
