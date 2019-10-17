package tallerUno.Payan;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class Elemento {

	private Main app;
	private float animacion, ancho, alto;
	private String tipo;
	private boolean explota, activada;
	private PImage[] boom, ele;
	private PVector pos;
	private Thread framerateHilo;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	public Elemento(Main app, float x, float y, String tipo) {
		this.app = app;
		this.tipo = tipo;
		explota = false;

		boom = new PImage[5];

		for (int i = 0; i < boom.length; i++) {
			boom[i] = app.loadImage("./data/Escenario/Armas/explosion" + (i + 1) + ".png");
		}

		switch (tipo) {

		case "Mina":

			ele = new PImage[3];

			for (int i = 0; i < ele.length; i++) {
				ele[i] = app.loadImage("./data/Escenario/Armas/mina" + (i + 1) + ".png");
			}

			ancho = 100;
			alto = 50;
			activada = false;
			y += 50 - alto / 2;
			break;// cierra el case de Mina

		case "Andamio":
			ele = new PImage[1];
			ele[0] = app.loadImage("./data/Escenario/Armas/andamio.png");
			ancho = 100;
			alto = 35;
			y += 50 - alto / 2;
			break;// cierra el case de Andamio
		}// cierra el switch de tipo de arma, trampa o plataforma

		pos = new PVector(x, y);

		// hilo

		framerateHilo = new Thread(new framerate());
		framerateHilo.start();

	}// cierra el constructor de elemeto
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void pintar() {

		app.imageMode(PConstants.CENTER);

		switch (this.tipo) {

		case "Andamio":
			app.image(ele[0], pos.x, pos.y);
			break;// cierra el case Andamio

		case "Mina":
			if (animacion > 1 && explota == false)
				animacion = 0;
			if (activada == false)
				app.image(ele[(int) animacion], pos.x, pos.y - 5);
			if (activada)
				app.image(ele[2], pos.x, pos.y);
			break;// cierra el case Mira
		} // cierra el switch de tipo

		if (animacion > 4) {
			explota = false;
		}
		if (explota) {
			app.image(boom[(int) animacion], pos.x, pos.y);
		}
	}
	// cierra el metodo pintar
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	class framerate implements Runnable {
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

		public void run() {
			while (true) {
				try {
					Thread.sleep(120);
					animacion += 1;
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}// cierra el run

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	}// cierra la clase framerate

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
//getters and setters

	public PVector getPos() {
		return pos;
	}

	public float getAlto() {
		return alto;
	}

	public float getAncho() {
		return ancho;
	}

	public String getTipo() {
		return tipo;
	}

	public boolean isActivada() {
		return activada;
	}

	public void setExplota(boolean explota) {
		this.explota = explota;
	}

	public void setActivada(boolean activada) {
		this.activada = activada;
	}

}// cierra la clase elemento
