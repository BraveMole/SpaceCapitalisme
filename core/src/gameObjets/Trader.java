package gameObjets;

import com.badlogic.gdx.utils.Array;
import commerce.Besoin;
import commerce.Offre;
import gameConcepts.Inventory;

public interface Trader extends HasInventory{
	void updateBesoin();
	void updateOffre();
	void cycle();
	Offre getOffre();
	Array<Besoin> getBesoin();
	float getX();
	float getY();
	double distance(Trader t);
	Implantation getImplatation();
	void addObjetVendu(float nbobjetvendu);

}
