package gameConcepts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import commerce.Besoin;
import commerce.MarketPlace;
import commerce.Offre;
import gameObjets.HasInventory;
import gameObjets.Implantation;
import gameObjets.Trader;
import test.TestWorldSettings;

public class Population implements Trader{
	private static float [] produitConsomme;
	private static int [] prioriteAchat;
	private static int nbBesoin=0;
	private int nbTravailleur;
	private Inventory inventory;
	private Array<Besoin> listeBesoin;
	private Implantation implantation;
	private static int stockPopulation = 10;
	private static int stockBas =5;
	private static int stockHaut=2;
	private float tresorie;

	public static void initPopBesoin(){
		if (TestWorldSettings.test){
			produitConsomme=TestWorldSettings.produitConsommePop();
			prioriteAchat=TestWorldSettings.prioriteAchatPop();
			for (float v : produitConsomme) {
				if (v>0) {
					nbBesoin++;
				}
			}
		}
		else {
			produitConsomme = new float[Ressource.ressourcePossible.size];
			prioriteAchat = new int[Ressource.ressourcePossible.size];
			String[] wholeFile = Gdx.files.internal("BesoinPop.txt").readString().split(";");
			int i = 0;
			String[] s2;
			for (String s : wholeFile) {
				if (!s.isEmpty()) {
					s2 = s.split("-");
					produitConsomme[i] = Float.parseFloat(s2[0]) / 100f;
					prioriteAchat[i] = Integer.parseInt(s2[1].strip());
					nbBesoin++;
				}
				i++;
			}
		}
	}

	private void besoinInit(){
		this.listeBesoin.setSize(nbBesoin);
		for (int i = 0; i < produitConsomme.length; i++) {
			if (produitConsomme[i]!=0) {
				this.listeBesoin.set(prioriteAchat[i], new Besoin(i, 0, Ressource.getBasicPrice(i)*2, this));
			}
		}
	}

	public Population (int nbTravailleur,Implantation implantation) {
		this.nbTravailleur=nbTravailleur;
		this.inventory= new Inventory();
		this.implantation=implantation;
		this.listeBesoin= new Array<>();
		this.besoinInit();
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
		float stockmanquant;int j=0;
		for (int i = 0; i < produitConsomme.length; i++) {
			if (produitConsomme[i] != 0) {
				stockmanquant = Math.max(produitConsomme[i]*stockPopulation*nbTravailleur-this.inventory.getRessource(i),0);
				if (stockmanquant>produitConsomme[i]*stockBas *nbTravailleur){
                    listeBesoin.get(prioriteAchat[i]).scalePrixMax(1.1f);
				}
				else if(stockmanquant<produitConsomme[i]*stockHaut*nbTravailleur){
				    listeBesoin.get(prioriteAchat[i]).scalePrixMax(0.9f);
                }
				listeBesoin.get(prioriteAchat[i]).setQuantity((float)Math.ceil(stockmanquant));
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
		this.updateBesoin();
		for (int i = 0; i < Ressource.diverse; i++) {
			//this.inventory.addRessource(i,this.nbTravailleur*Population.produitConsomme[i]);
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
