package gameObjets;

import com.badlogic.gdx.utils.Array;
import commerce.Besoin;
import commerce.MarketPlace;
import commerce.Offre;
import gameConcepts.Inventory;
import gameConcepts.Recette;
import gameConcepts.Ressource;
import gameUi.InterfaceIndustrie;

import java.util.Random;

import static gameConcepts.Ressource.diverse;

public class Industrie implements Trader{

	private Recette recette;
	private Inventory inventory;
	private int prixvente;
	private Array<Besoin> listeBesoin;
	private Offre offre;
	private int nbobjetsproduits;
	private int nbobjetvendu;
	private Implantation implatation;
	private Array<Besoin> listeDemande;
	private int id;
	private InterfaceIndustrie inter;
	private Ressource ressourceProduite;

	private static final float EnoughSaleCoef = 1;
	private static final float maxCostOfMats=0.7f;
	private static final float LittleRaise =1.1f;
	private static final float NotEnoughSaleCoef = 4f;
	private static final float LittleReduction =0.9f;
	private static final float ReallyNotEnoughSaleCoef = 6f;
	private static final float BigReduction =0.7f;
	private static final float ReserveMatierePremiere = 5f;
	private static final float DistanceCoef =0.0000001f;
	private static int nbIndustrie=0;

	Industrie(Ressource ressourceProduite, int puissanceindustrielle, Implantation implantation) {
		Random rand = new Random();
		this.ressourceProduite = ressourceProduite;
		this.id= Industrie.nbIndustrie;
		Industrie.nbIndustrie++;
		this.inventory=new Inventory();
		this.implatation=implantation;
        this.listeBesoin= new Array<>();
		this.prixvente=(int) (ressourceProduite.getBasicprice()*(rand.nextFloat()/2+0.75f));
		this.recette= ressourceProduite.getRecette();
		if (recette!=null){
			this.nbobjetsproduits= puissanceindustrielle/this.recette.getCoutmanoeuvre();
            for (int i=0;i<diverse;i++) {
                if (recette.getMatpremiere()[i]!=0){
                    listeBesoin.add(new Besoin(i,0, Ressource.getBasicPrice(i), this));
                }
            }
		}
		else{
			if (ressourceProduite.getFacilitExtraction()==0){
				System.out.println(ressourceProduite);
			}
			this.nbobjetsproduits = (int)(puissanceindustrielle/ ressourceProduite.getFacilitExtraction());
		}
		this.offre=new Offre(ressourceProduite.getId(),0,0,this);
        this.listeDemande= new Array<>();
		MarketPlace.addTrader(this);
		this.nbobjetvendu=0;
	}

	public int getId() {
		return this.id;
	}

	public void updateBesoin() {
		if (this.recette!=null) {
			int prixmattotalestime=0;
			float stockmanquant;
			for (int i=0;i<this.listeBesoin.size;i++) {
				prixmattotalestime+=recette.getMatpremiere()[this.listeBesoin.get(i).getRessourceId()]*this.listeBesoin.get(i).getPrixmax();
			}
            if (prixmattotalestime>maxCostOfMats*prixvente*nbobjetsproduits){
                float scalePriceFactor =maxCostOfMats*prixvente*nbobjetsproduits/prixmattotalestime;
                for (int i=0;i<this.listeBesoin.size;i++) {
                    this.listeBesoin.get(i).scalePrixMax(scalePriceFactor);
                }
            }
			for (int i=0;i<this.listeBesoin.size;i++) {
				stockmanquant = nbobjetsproduits*recette.getMatpremiere()[this.listeBesoin.get(i).getRessourceId()]* Industrie.ReserveMatierePremiere-this.inventory.getRessource(this.listeBesoin.get(i).getRessourceId());
				if (stockmanquant<0){
					stockmanquant=0;
				}
                this.listeBesoin.get(i).setQuantity(stockmanquant);
			}
		}
	}

	public void addObjetVendu(float nbobjetvendu) {
		this.nbobjetvendu+=nbobjetvendu;
	}

	public void updateOffre() {
	    float objetsavendre=this.inventory.getRessource(ressourceProduite.getId())-this.nbobjetvendu;
		if (objetsavendre> Industrie.ReallyNotEnoughSaleCoef*this.nbobjetsproduits) {
			this.prixvente*= Industrie.BigReduction; // Si on a vraiment trop de stock le prix s'effondre
		}
		else if (objetsavendre> Industrie.NotEnoughSaleCoef*this.nbobjetsproduits) {
			this.prixvente*= Industrie.LittleReduction; // Si on a un peu de mal � vendre on baisse le prix
		}
		else if (objetsavendre< Industrie.EnoughSaleCoef*this.nbobjetsproduits) {
			this.prixvente*= Industrie.LittleRaise; //Si nos stocks de produits � vendre sont faible, on peut augmenter le prix
		}
		offre.setPrix(this.prixvente);
		offre.setQuantity(objetsavendre);
	}

	public Offre getOffre() {
		return this.offre;
	}

	public Array<Besoin> getBesoin(){
		return this.listeBesoin;
	}

	public void cycle() {
		int maxproduction = this.nbobjetsproduits;
		for (int i=0;i<this.listeBesoin.size;i++) {
		    if ((this.inventory.getRessource(this.listeBesoin.get(i).getRessourceId())/recette.getMatpremiere()[this.listeBesoin.get(i).getRessourceId()])<maxproduction) {
		        maxproduction=(int)(this.inventory.getRessource(this.listeBesoin.get(i).getRessourceId())/recette.getMatpremiere()[this.listeBesoin.get(i).getRessourceId()]);
		    }
		}
		for (int i=0;i<this.listeBesoin.size;i++) {
		    this.addRessource(this.listeBesoin.get(i).getRessourceId(), recette.getMatpremiere()[this.listeBesoin.get(i).getRessourceId()]*maxproduction);
		}
		this.addRessource(ressourceProduite.getId(),maxproduction);
		this.updateOffre();
		this.updateBesoin();
	}


	private boolean addRessource(int ressourceId, float quantity){
		if (this.inventory.addRessource(ressourceId, quantity)) {
			if (this.inter!=null) {
				this.inter.refreshInventory();
			}
			return true;
		}
		return false;
	}

	public void setInterface(InterfaceIndustrie inter){
		this.inter=inter;
	}

	@Override
	public boolean receivingCargo(int ressourceId, float quantity) {
		if (quantity<0) {
			if(this.addRessource(ressourceId, quantity)) {
				this.addObjetVendu(quantity);
				return true;
			}
			else {
				return false;
			}
		}
		return this.addRessource(ressourceId, quantity);
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
	public float getX() {
		return this.implatation.getX();
	}

	@Override
	public float getY() {
		return this.implatation.getY();
	}

	@Override
	public double distance(Trader t) {
		return Math.sqrt(Math.pow((this.getX()-t.getX()),2)+Math.pow((this.getY()-t.getY()),2));
	}

	@Override
	public Implantation getImplatation() {
		return this.implatation;
	}

	public String toString() {
		return this.ressourceProduite.getName();
	}

	@Override
	public Inventory getInventory() {
		return this.inventory;
	}





}
