package gameObjets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.world.World;
import commerce.HashmapMegaTrade;
import commerce.MarketPlace;
import commerce.Trade;
import gameConcepts.MapIndex;
import gameConcepts.Population;
import gameConcepts.Ressource;
import gameConcepts.SuperActor;

import java.util.Random;

import static commerce.MarketPlace.getXindex;
import static commerce.MarketPlace.getYindex;

public abstract class Implantation extends SuperActor {
	public HashmapMegaTrade getListeMegaTrade() {
		return listeMegaTrade;
	}
	private HashmapMegaTrade listeMegaTrade = new HashmapMegaTrade();
	private Array<Industrie> listeIndustrie = new Array<>();
	private Array<Ressource> ressourcePrimaireDisponible;
	private Array<Ressource> ressourceProduite;
	private int id;
	private Population population;
	private Sprite sprite;
	private float size;
	private Texture close;
	private Texture far;
	private static float farTextureZoom=3.5f;
	private final String name;
	private final boolean habitable;

	static void generateRessource(Implantation implantation){
		Random rand = new Random();
		if (implantation.isHabitable()) {
			for (int i=0;i<Ressource.nbprimaire;i++){
				if (rand.nextFloat()< Ressource.ressourcePossible.get(i).getProfusionhabitable()) {
					implantation.addRessourcePrimaire(i);
				}
			}
		}
		else{
			for (int i=0;i<Ressource.nbprimaire;i++){
				if (rand.nextFloat()< Ressource.ressourcePossible.get(i).getProfusionnonhabitable()) {
					implantation.addRessourcePrimaire(i);
				}
			}
		}
	}

	static void generateIndustries(Implantation implantation){
		Random rand = new Random();
		for (Ressource ressource : Ressource.ressourcePossible) {
			if ((!ressource.isPrimaire()&& implantation.isHabitable() && rand.nextFloat()>0.9f)||(ressource.isPrimaire() && rand.nextFloat()>0.4f)) {
				implantation.addIndustrie(ressource);
			}
		}
	}


	Implantation(Sprite sprite,int size,float x, float y,Texture close, Texture far, int population, String name,boolean habitable) {
		this.size=size;
		this.close=close;
		this.far=far;
		this.habitable=habitable;
		this.name=name;
		this.sprite=sprite;
		this.ressourcePrimaireDisponible = new Array<>();
		this.ressourceProduite=new Array<>();
		this.id = World.nbImplantationCree;
		World.nbImplantationCree++;
		sprite.setBounds(x - (size / 4), y + (size / 4),size,size);
		this.setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(),sprite.getHeight());
		this.position = new Vector2(x,y);
		if (population > 0) {
			this.population = new Population(population,  this);
		}
		MarketPlace.addImplantation(this);
	}

	public Array<Ressource> getRessourcePrimaireDisponible() {
		return ressourcePrimaireDisponible;
	}

	private void addRessourcePrimaire(int idRessource){
		this.ressourcePrimaireDisponible.add(Ressource.ressourcePossible.get(idRessource));
	}

	private void addIndustrie(Ressource ressource){
		if (ressource.isPrimaire()){
			if (this.ressourcePrimaireDisponible.contains(ressource,true)){
				this.ressourcePrimaireDisponible.removeValue(ressource,true);
				this.ressourceProduite.add(ressource);
				this.listeIndustrie.add(new Industrie(ressource,50,this));
			}
		}
		else if (!ressourceProduite.contains(ressource,true)){
			this.listeIndustrie.add(new Industrie(ressource,50,this));
			this.ressourceProduite.add(ressource);
		}
	}


	public void addTrade(Trade t) {
		t.getVendeur().addObjetVendu(t.getQuantity());
		if (t.getAcheteur().getImplatation()==this){
			t.getAcheteur().startingCargoExchange(t.getRessourceId(),-t.getQuantity(),t.getVendeur());
		}
		else {
			this.listeMegaTrade.addTrade(t);
		}
	}

	public int getId() {
		return this.id;
	}

	public Population getPopulation() {
		return this.population;
	}

	public String toString(){
		return this.name;
	}


	public float getX() {
		return this.position.x;
	}

	public float getY() {
		return this.position.y;
	}

	public Array<Industrie> getListeIndustrie() {
		{
			return this.listeIndustrie;
		}
	}

	private boolean isHabitable() {
		return habitable;
	}

	private void textureSwitch() {
		texturezoomlevel=zoomlevel;
		switch (zoomlevel){
			case(0):
			case(1):
				this.setTouchable(Touchable.enabled);
				sprite.setRegion(close);
				sprite.setBounds(this.position.x-size/2,this.position.y-size/2,size,size);
				this.setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(),sprite.getHeight());
				break;
			case(2):
				this.setTouchable(Touchable.enabled);
				sprite.setRegion(far);
				sprite.setBounds(this.position.x-size*farTextureZoom/2,this.position.y-size*farTextureZoom/2,size*farTextureZoom,size*farTextureZoom);
				this.setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(),sprite.getHeight());
				break;
			case(3):
				this.setTouchable(Touchable.disabled);
				break;
		}

	}



	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		if (zoomlevel!=texturezoomlevel){
			this.textureSwitch();
		}
		if (zoomlevel<3) {
			this.sprite.draw(batch);
		}
	}
}
