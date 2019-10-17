package tallerUno.Payan;

import java.util.ArrayList;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
public class Bandera {

	private Main app;
	private PVector pos, vel, acel;
	private String tipo;
	private PImage[] poste;
	private float waves, ancho, alto;
	private Thread fisicasHilo, frameRateHilo;
	private ArrayList<Suelo> suelo;
	private boolean isDrawRunning;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	public Bandera(Main app, int x, int y) {

		this.app = app;
		pos = new PVector(x, y);
		vel = new PVector(0, 0);
		acel = new PVector(0, 0.34f);
		isDrawRunning = false;

		if (x == 40)
			tipo = "Sign";

		if (x == 1551)
			tipo = "Flag";

		if (tipo == "Sign") {
			poste = new PImage[1];
			poste[0] = app.loadImage("./data/Escenario/Suelo/letrero.png");
		}
		if (tipo == "Flag") {
			poste = new PImage[6];

			for (int i = 0; i < poste.length; i++) {
				poste[i] = app.loadImage("./data/Escenario/Suelo/bandera" + (i + 1) + ".png");
			}
		}

		waves = 0;

		// dimensiones

		ancho = 20;
		alto = 100;

		// hilos

		fisicasHilo = new Thread(new fisicas());
		fisicasHilo.start();
		frameRateHilo = new Thread(new frameRate());
		frameRateHilo.start();

	}// cierra la clase Bandera
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	void pintar(ArrayList<Suelo> suelo) {

		this.suelo = suelo;

		isDrawRunning = true;

		app.imageMode(PConstants.CENTER);

		if (tipo == "Sign")

			app.image(poste[0], pos.x, pos.y - 30, poste[0].width / 1.3f, this.poste[0].height / 1.3f);

		if (waves >= poste.length)

			waves = 0;

		if (tipo == "Flag")

			app.image(poste[(int) waves], pos.x + 45, pos.y - 60, 120, 220);

	} // cierra el metodo pintar

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	class fisicas implements Runnable {

		public void run() {

			while (true) {

				System.out.print("");

				if (isDrawRunning) {

					try {

						Thread.sleep(17);

						vel.y += acel.y;

						// aplica las fisicas a la posicion

						pos.y += vel.y;

						for (int i = 0; i < suelo.size(); i++) {

							Suelo sue = suelo.get(i);

							// la bandera para si está pisando suelo
							if (vel.y >= 0 && pos.y + alto / 2 > sue.getPos().y
									&& pos.y + alto / 2 < sue.getPos().y + 30
									&& pos.x - ancho / 2 <= sue.getPos().x + sue.getAncho()
									&& pos.x + ancho / 2 >= sue.getPos().x) {
								vel.y = 0;
								pos.y = sue.getPos().y - alto / 2;
							}
						}

					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				} // cierra la condicion de isDrawRunning
			} // cierra el While
		} // cierra el run
	}// cierra la clase fisicas
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	class frameRate implements Runnable {

		public void run() {
			while (true) {

				System.out.print("");

				if (isDrawRunning) {

					try {

						Thread.sleep(160);

						waves += 1;

					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				} // cierra la condicion isDrawing

			} // cierra el while

		}// cierra el run

	}// cierra la clase frameRate

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	// getter and setters

	public PVector getPos() {
		return pos;
	}

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

}// cierra la clase Bandera
