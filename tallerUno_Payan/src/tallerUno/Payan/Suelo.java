package tallerUno.Payan;

import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
public class Suelo {

	private Main app;
	private PVector pos;
	private int poblacionPasto, poblacionPiedra;
	private float ancho, alto;
	private float[] pastoX;
	private PImage tierra, cesped, piedra, pasto;
	private PVector[] piedraPos, piedraTam;
	private boolean fertil;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	public Suelo(Main app, float x, float y) {

		this.app = app;
		pos = new PVector(x, y);
		ancho = 100;
		alto = 100;
		tierra = app.loadImage("./data/Escenario/Suelo/tierra.png");
		cesped = app.loadImage("./data/Escenario/Suelo/cesped.png");
		piedra = app.loadImage("./data/Escenario/Suelo/piedra.png");
		pasto = app.loadImage("./data/Escenario/Suelo/pasto.png");

		/////////////// decoracion del bloque de suelo

		fertil = true;

		poblacionPasto = (int) app.random(1, 7);
		pastoX = new float[poblacionPasto];

		poblacionPiedra = (int) app.random(5, 10);
		piedraPos = new PVector[poblacionPiedra];
		piedraTam = new PVector[poblacionPiedra];

		// decoracion con pasto

		for (int i = 0; i < poblacionPasto; i++) {
			pastoX[i] = (int) app.random(pos.x, pos.x + ancho);
		}

		// decoracion con piedras

		for (int i = 0; i < poblacionPiedra; i++) {
			piedraPos[i] = new PVector((int) app.random(pos.x + 5, pos.x + ancho - 5),
					(int) app.random(pos.y + 5, pos.y + alto - 5));

			piedraTam[i] = new PVector((int) app.random(5, 15), (int) app.random(5, 15));
		}
	}// cierra el constructor de suelo

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	public void pintar(ArrayList<Suelo> suelo) {

		app.imageMode(PConstants.CORNER);

		// detecta si puede poner cesped en el bloque o no
		for (int i = 0; i < suelo.size(); i++) {

			Suelo sue = suelo.get(i);

			if (pos.y == sue.pos.y + sue.alto && pos.x == sue.pos.x) {
				fertil = false;
			}
		}

		// pone la tierra
		app.image(tierra, pos.x - 2, pos.y - 2, ancho + 2, alto + 2);

		// decora el bloque con piedras
		app.imageMode(PConstants.CENTER);

		for (int i = 0; i < poblacionPiedra; i++) {
			app.image(piedra, piedraPos[i].x, piedraPos[i].y, piedraTam[i].x, piedraTam[i].y);
		}

		// decora el bloque con pasto
		for (int i = 0; i < poblacionPasto; i++) {
			if (fertil) {
				app.image(pasto, pastoX[i], pos.y - 5, 14, 15);
			}
		}

		app.imageMode(PConstants.CORNER);

		// pone el cesped
		if (fertil)
			app.image(this.cesped, pos.x - 1, pos.y - 1, ancho + 1, 40);
	}// cierra el metodo pintar
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
// getters and setters

	public PVector getPos() {
		return pos;
	}

	public float getAncho() {
		return ancho;
	}

	public float getAlto() {
		return alto;
	}

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
}// cierra la clase suelo
