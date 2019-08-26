package com.world;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import commerce.MarketPlace;
import gameConcepts.Population;
import gameConcepts.Recette;
import gameConcepts.Ressource;

public class Application extends ApplicationAdapter {
	World world;

	@Override
	public void create () {
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
		Population.besoinInit();
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
