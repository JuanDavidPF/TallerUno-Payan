package tallerUno.Payan;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

public class Pantalla {

	// variables e instancias

	private Main app;
	private PFont fuente;
	private String nivel;
	private PImage nubes, instrucciones, logo, fondo, montana, help, jugar, back, puntuacion, jdpLogo;
	private float nubes1x, nubes2x, logoY, logoYMul, logoZoom, logoMul, fuenteZoom, fuenteMul, aumento, crecimiento,
			unoPuntaje, dosPuntaje;;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public Pantalla(Main app) {

		this.app = app;
		fuente = app.createFont("./data/Fonts/Kidstar.ttf", 32);
		nivel = "Espera";

		// nubes del main
		nubes = app.loadImage("./data/Escenario/Nubes/nubes.png");
		nubes1x = -100f;
		nubes2x = 3900f;

		// instrucciones
		instrucciones = app.loadImage("./data/Escenario/Iconos/instrucciones.png");

		// logo
		jdpLogo = app.loadImage("./data/Escenario/Iconos/jdpLogo.png");
		logo = app.loadImage("./data/Escenario/Iconos/Logo.png");
		logoY = 200f;
		logoYMul = 0.2f;
		logoZoom = 1f;
		logoMul = 0.0025f;

		// fondo
		fondo = app.loadImage("./data/Escenario/Nubes/fondo.png");
		montana = app.loadImage("./data/Escenario/Nubes/montana.png");

		// marca
		fuenteZoom = 25f;
		fuenteMul = 0.2f;

		// botones
		help = app.loadImage("./data/Escenario/Iconos/help.png");
		jugar = app.loadImage("./data/Escenario/Iconos/jugar.png");
		back = app.loadImage("./data/Escenario/Iconos/back.png");

		// puntaje
		puntuacion = app.loadImage("./data/Escenario/Iconos/score.png");
		aumento = 0;
		crecimiento = 0;
		unoPuntaje = 0;
		dosPuntaje = 0;

	}// ciera el construrctor de Pantalla

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void pintar() {

		switch (nivel) {

		case "Main":
			app.imageMode(PConstants.CORNER);

			// fondos del juego
			fondos();

			// logo
			app.imageMode(PConstants.CENTER);
			app.image(logo, app.width / 2, logoY, logo.width * logoZoom, logo.height * logoZoom);

			logoZoom += logoMul * 2.5;
			if (logoZoom > 1.5f)
				logoMul *= -1f;
			if (logoZoom < 1f)
				logoMul *= -1f;

			logoY += logoYMul;
			if (logoY > 250f)
				logoYMul *= -1f;
			if (logoY < 150f)
				logoYMul *= -1f;

			// marca
			app.fill(255);
			app.textFont(fuente, fuenteZoom);
			app.textAlign(PConstants.CENTER);
			if (app.mouseX > 925 && app.mouseX < 1065 && app.mouseY > 625 && app.mouseY < 650) {
				fuenteZoom += fuenteMul;
				if (fuenteZoom > 35f)
					fuenteMul *= -1f;
				if (fuenteZoom < 25f)
					fuenteMul *= -1f;
			}

			app.image(jdpLogo, 1120, 630);
			app.text("JuanPayan", 1000, 650);

			// botones
			app.image(jugar, app.width / 2 - 200, 550);
			if (app.mouseX > 303 && app.mouseX < 489 && app.mouseY > 513 && app.mouseY < 585)
				app.image(jugar, app.width / 2 - 200, 550, jugar.width * 1.2f, jugar.height * 1.2f);

			app.image(help, app.width / 2 + 200, 550);
			if (app.mouseX > 700 && app.mouseX < 890 && app.mouseY > 511 && app.mouseY < 585)
				app.image(help, app.width / 2 + 200, 550, jugar.width * 1.2f, jugar.height * 1.2f);

			break;// cierra el case main

		case "Help":
			app.imageMode(PConstants.CORNER);
			app.image(fondo, 0, 0);
			app.image(instrucciones, 0, 0);
			app.imageMode(PConstants.CENTER);
			app.image(back, 100, 600);
			if (PApplet.dist(app.mouseX, app.mouseY, 100, 600) < 85)
				app.image(back, 100, 600, 170, 170);
			break;// cierra el case Help

		case "Juego":

			// fondos del juego
			fondos();

			break;// cierra el case Juego

		case "Game Over":
			app.imageMode(PConstants.CORNER);
			app.image(fondo, 0, 0);
			break;

		}// cierra el switch de nivel

	}// cierra el metodo pintar
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void fondos() {
		app.imageMode(PConstants.CORNER);
		app.image(fondo, 0, 0);

		// bucle de nubesitas

		app.imageMode(PConstants.CENTER);

		if (nivel == "Main") {
			app.image(nubes, nubes1x, 400);
			app.image(nubes, nubes2x, 400);
		}

		if (nivel == "Juego") {
			app.image(nubes, nubes1x, 20);
			app.image(nubes, nubes2x, 20);
		}

		nubes1x -= 1;
		nubes2x -= 1;

		if (nubes1x <= -4000) {
			nubes1x = 4000;
		}
		if (nubes2x <= -4000) {
			nubes2x = 4000;
		}

		// montañas

		if (nivel == "Juego") {
			app.imageMode(PConstants.CORNER);
			app.image(montana, -100, 400);

		}

	} // cierra el metodo nubes
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void puntajes(float ronda, boolean unoGano, boolean dosGano) {

		if (aumento < 1) {
			aumento += 0.05;
		}

		app.image(puntuacion, app.width / 2, app.height / 2, puntuacion.width * aumento, puntuacion.height * aumento);

		if (aumento >= 1) {
			app.fill(255);
			aumento = 1;
			app.textSize(80);
			app.text("Puntuacion Parcial", app.width / 2, 125);
			app.textSize(70);
			app.text("Ronda " + (int) ronda + " de 10", app.width / 2, 200);
			app.textSize(40);
			app.text("Oveja Uno:", 200, 300);
			app.text("Oveja DOs:", 200, 450);

			if (unoGano && crecimiento < 100) {
				unoPuntaje += 1;
			}

			if (dosGano && crecimiento < 100) {
				dosPuntaje += 1;
			}
			crecimiento += 1;

			app.text((int) unoPuntaje, 200, 350);
			app.text((int) dosPuntaje, 200, 500);
			app.noStroke();
			app.fill(195, 192, 0);
			app.rect(400, 300, PApplet.map(unoPuntaje, 0, 1000, 0, 600), 75);

			app.fill(0, 148, 193);
			app.rect(400, 450, PApplet.map(dosPuntaje, 0, 1000, 0, 600), 75);

			app.fill(255);
			app.textSize(40);
			if (app.mouseX > 525 && app.mouseY > 588 && app.mouseX < 660 && app.mouseY < 615)
				app.textSize(45);
			app.text("Continuar", app.width / 2, 600);

		}

	} // cierra le metodo puntajes

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	// getters and setters
	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public float getAumento() {
		return aumento;
	}

	public void setAumento(int aumento) {
		this.aumento = aumento;
	}

	public void setCrecimiento(int crecimiento) {
		this.crecimiento = crecimiento;
	}

	public float getCrecimiento() {
		return crecimiento;
	}

	public void setUnoPuntaje(float unoPuntaje) {
		this.unoPuntaje = unoPuntaje;
	}

	public float getUnoPuntaje() {
		return unoPuntaje;
	}

	public float getDosPuntaje() {
		return dosPuntaje;
	}

	public void setDosPuntaje(float dosPuntaje) {
		this.dosPuntaje = dosPuntaje;
	}

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

}// cierra la clase Pantalla
