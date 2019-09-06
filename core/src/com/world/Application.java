package com.world;

import com.badlogic.gdx.ApplicationAdapter;
import gameConcepts.Recette;
import gameConcepts.Ressource;
import test.TestWorldSettings;

public class Application extends ApplicationAdapter {
	World world;

	@Override
	public void create () {
		TestWorldSettings.test=true;
		this.ressourceInit();
		this.world = new World();
	}

	@Override
	public void resize(int width, int height) {
		this.world.resize(width, height);
	}

	private void ressourceInit() {
		Ressource.RessourceCreation();
		Recette.RecetteCreation();
	}

	@Override
	public void render () {
		this.world.draw();
		this.world.act();
	}

	@Override
	public void dispose () {

	}
}
