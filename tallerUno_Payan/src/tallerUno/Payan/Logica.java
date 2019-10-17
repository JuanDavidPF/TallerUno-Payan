package tallerUno.Payan;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
public class Logica implements Observer {

	// Variables e Instancias
	private Main app;
	private Pantalla pan;
	private PImage cursorUno, cursorDos, jackpot, mouse, oveja1, oveja2;
	private PImage[] loading;
	private boolean coger, poner, finalCountdown;
	private Pick elementoUno, elementoDos;
	private ArrayList<Pick> objetos;
	private ArrayList<Suelo> suelo;
	private ArrayList<Cuadricula> cuadricula;
	private ArrayList<Elemento> elementos;
	private Thread conteoRegresivoHilo;
	private Bandera bandera, letrero;
	private Jugador jugadorUno, jugadorDos;
	private PVector origenCam, mouseCam;
	private float distancia, distanciaMetaUno, distanciaMetaDos, ronda, turnoCoger, turnoPoner, contadorCorrector, zoom;
	private int cuenta;
	private Comunicacion com;
	private int loadingCon, readyCon;
	private boolean readyOne, readyTwo;
	private String clientSelUno, clientSelDos;

	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	public Logica(Main app) {

		this.app = app;

		// instancias
		pan = new Pantalla(app);

		// Strings
		clientSelUno = "";
		clientSelDos = "";

		// numericas
		ronda = 1;
		turnoCoger = 1;
		turnoPoner = 1;
		contadorCorrector = 0;
		cuenta = 4;
		loadingCon = 0;

		// imagenes
		cursorUno = app.loadImage("./data/Escenario/Iconos/cursorUno.png");
		cursorDos = app.loadImage("./data/Escenario/Iconos/cursorDos.png");
		jackpot = app.loadImage("./data/Escenario/Iconos/jackpot.png");
		mouse = app.loadImage("./data/Escenario/Iconos/mouse.png");
		oveja1 = app.loadImage("./data/Jugadores/Oveja/quietoD1.png");
		oveja2 = app.loadImage("./data/Jugadores/Oveja/quietoI1.png");
		loading = new PImage[8];

		for (int i = 0; i < 8; i++) {

			loading[i] = app.loadImage("./data/Escenario/Iconos/loading" + i + ".png");

		}

		// booleans
		coger = false;
		poner = false;
		finalCountdown = false;

		// arraylist
		objetos = new ArrayList<Pick>();
		suelo = new ArrayList<Suelo>();
		cuadricula = new ArrayList<Cuadricula>();
		elementos = new ArrayList<Elemento>();

		// conexion

		com = Comunicacion.getSingleton();
		com.addObserver(this);
		Thread t = new Thread(com);
		t.start();

	}// cierra el constructor de logica

	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	public void pintar() {

		pan.pintar();

		// juego

		switch (pan.getNivel()) {
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		case "Espera":
			app.cursor(PConstants.ARROW);

			if (app.frameCount % 5 == 0) {
				loadingCon += 1;
			}

			if (loadingCon == 8)
				loadingCon = 0;

			app.background(0);
			app.textAlign(PConstants.CENTER, PConstants.CENTER);
			app.textSize(30);
			app.text("Esperando Jugadores (" + com.getClientes().size() + "/2).", app.width / 2, app.height / 2);

			if (app.mouseX > 465 && app.mouseX < 724 && app.mouseY > 595 && app.mouseY < 615) {
				app.textSize(34);
			}

			app.text("Iniciar juego local", app.width / 2, 600);
			app.textSize(30);
			app.text(app.mouseX + ", " + app.mouseY, app.mouseX, app.mouseY);
			app.imageMode(PConstants.CENTER);
			app.image(loading[loadingCon], app.width / 2, 200);

			if (com.getClientes().size() >= 1) {

				if (readyOne == false) {
					app.fill(255, 0, 0);
					app.text("NO LISTO", 200, 150);
				} else {
					app.fill(0, 255, 0);
					app.text("LISTO", 200, 150);
				}
				app.fill(255);
				app.image(oveja1, 200, 400);
				app.text("Jugador 1 Conectado", 200, 620);

			}

			if (com.getClientes().size() == 2) {

				if (readyTwo == false) {
					app.fill(255, 0, 0);
					app.text("NO LISTO", 1000, 150);
				} else {

					app.fill(0, 255, 0);
					app.text("LISTO", 1000, 150);
				}

				app.fill(255);
				app.image(oveja2, 1000, 400);
				app.text("Jugador 2 Conectado", 1000, 620);
			}

			if (readyOne == false || readyTwo == false) {
				readyCon = 3;

			}

			if (readyOne && readyTwo) {
				app.textSize(70);
				app.text(readyCon, app.width / 2, 500);
				if (app.frameCount % 60 == 0) {
					readyCon -= 1;
					if (readyCon == 0) {
						pan.setNivel("Main");
						readyOne = false;
						readyTwo = false;
						readyCon = 3;
						com.getClientes().get(0).enviar("Nivel,Main");
						com.getClientes().get(1).enviar("Nivel,Main");
					}
				}
			}

			break;
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		case "Juego":

			// contador corrector
			if (poner) {
				contadorCorrector += 1;
			}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////aplica la camara a todo///////////////////////////

			app.pushMatrix();
			camara();

			// pintar los suelos

			for (int i = 0; i < suelo.size(); i++) {
				suelo.get(i).pintar(suelo);
			}

			// pintar el letrero

			letrero.pintar(suelo);

			// pintar la bandera

			bandera.pintar(suelo);

			// pintar los elementos

			for (int i = 0; i < elementos.size(); i++) {
				elementos.get(i).pintar();
			}

			// pintar la cuadricula

			for (int i = 0; i < cuadricula.size(); i++) {
				if (poner && finalCountdown == false) {
					cuadricula.get(i).pintar();
				}
			}

			// pintar los jugadores

			if (poner == false && coger == false) {

				jugadorUno.pintar(suelo, jugadorDos, elementos, bandera);
				jugadorDos.pintar(suelo, jugadorUno, elementos, bandera);

				jugadorUno = this.jugadorDos.getAdversario();
				jugadorDos = this.jugadorUno.getAdversario();
			}

			if (poner && finalCountdown == false) {

				// mouse
				app.imageMode(PConstants.CORNER);
				app.image(mouse, (float) (app.mouseX / zoom - origenCam.x), (float) (app.mouseY / zoom - origenCam.y),
						mouse.width * 2, mouse.height * 2);
				app.imageMode(PConstants.CENTER);

				if (turnoPoner == 1)
					app.image(cursorUno, (float) (app.mouseX / zoom - origenCam.x + 70),
							(float) (app.mouseY / zoom - origenCam.y + 70));
				if (turnoPoner == 2)
					app.image(cursorDos, (float) (app.mouseX / zoom - origenCam.x + 70),
							(float) (app.mouseY / zoom - origenCam.y + 70));

				// dibuja el hover de lo que va a poner cada jugador

				if (mouseCam.x > 490 && mouseCam.x < 1300 && mouseCam.y > 95 && mouseCam.y < 800) {

					if (turnoPoner == 1) {
						app.image(elementoUno.getHover(), (int) (PApplet.map(mouseCam.x, 490, 1300, 0, 8)) * 100 + 550,
								(int) (PApplet.map(mouseCam.y, 95, 800, 0, 7)) * 100 + 150);
					} else if (turnoPoner == 2) {
						app.image(this.elementoDos.getHover(),
								(int) (PApplet.map(mouseCam.x, 490, 1300, 0, 8)) * 100 + 550,
								(int) (PApplet.map(mouseCam.y, 95, 800, 0, 7)) * 100 + 150);

					}
				}
			}
			// se cierra la camara
			app.popMatrix();

			// maquina de casino

			if (coger)
				app.image(jackpot, app.width / 2, app.height / 2 + 50);

			// pintar los elementos aleatorias

			for (int i = 0; i < objetos.size(); i++) {
				objetos.get(i).pintar();
			}

			if (coger) {
				// mouse
				app.imageMode(PConstants.CORNER);
				app.image(mouse, app.mouseX, app.mouseY, mouse.width * 2, mouse.height * 2);
				app.imageMode(PConstants.CENTER);

				if (turnoCoger == 1)
					app.image(cursorUno, app.mouseX + 70, app.mouseY + 70, cursorUno.width / 2, cursorUno.height / 2);
				if (turnoCoger == 2)
					app.image(cursorDos, app.mouseX + 70, app.mouseY + 70, cursorDos.width / 2, cursorDos.height / 2);
			}

			// pinta la cuenta regresiva
			if (finalCountdown) {
				app.textAlign(PConstants.CENTER, PConstants.CENTER);
				app.textSize(300);
				app.text(cuenta - 1, app.width / 2, app.height / 2);
			}

			// pinta los puntajes parciales
			if ((jugadorUno.isGano() || jugadorUno.isCaido() || jugadorUno.isNoqueado())
					&& (jugadorDos.isGano() || jugadorDos.isCaido() || jugadorDos.isNoqueado())) {

				pan.puntajes(ronda, jugadorUno.isGano(), jugadorDos.isGano());

				// mouse
				app.imageMode(PConstants.CORNER);
				app.image(mouse, app.mouseX, app.mouseY, mouse.width * 2, mouse.height * 2);
				app.imageMode(PConstants.CENTER);
			}

			// se acabo el nivel

			if (ronda == 11)
				pan.setNivel("Game Over");

			break;// cierra el caso juego

		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////

		case "Game Over":

			app.textSize(70);
			app.text("GAME OVER", app.width / 2, 100);

			app.textSize(80);
			if (pan.getUnoPuntaje() == pan.getDosPuntaje()) {
				app.text("EMPATE", app.width / 2, 200);

				jugadorUno.setEstado("Quieto");
				jugadorDos.setEstado("Quieto");
			}

			if (pan.getUnoPuntaje() > pan.getDosPuntaje()) {
				app.text("VICTORIA \n JUGADOR 1 ", app.width / 2, 250);
				jugadorUno.setEstado("Quieto");

				jugadorDos.setEstado("Noqueado");
			}
			if (pan.getUnoPuntaje() < pan.getDosPuntaje()) {
				app.text("VICTORIA \n JUGADOR 2 ", app.width / 2, 250);
				jugadorDos.setEstado("Quieto");
				jugadorUno.setEstado("Noqueado");
			}

			app.textSize(70);
			if (app.mouseX > 488 && app.mouseX < 711 && app.mouseY > 575 && app.mouseY < 625)
				app.textSize(90);

			app.text("Reiniciar", app.width / 2, 600);

			app.pushMatrix();
			app.translate(125, -625);
			app.scale(3);
			jugadorUno.pintar(suelo, jugadorDos, elementos, bandera);
			jugadorDos.pintar(suelo, jugadorDos, elementos, bandera);
			app.popMatrix();
			jugadorUno.reiniciar();
			jugadorDos.reiniciar();
			app.textSize(50);
			app.text("Jugador 1", 250, 600);
			app.text("Jugador 2", 950, 600);
			app.text("Score: " + (int) pan.getUnoPuntaje(), 250, 650);
			app.text("Score: " + (int) pan.getDosPuntaje(), 950, 650);

			// mouse
			app.imageMode(PConstants.CORNER);
			app.image(mouse, app.mouseX, app.mouseY);
			app.imageMode(PConstants.CENTER);

			break;

		default:

			// mouse
			app.imageMode(PConstants.CORNER);
			app.image(mouse, app.mouseX, app.mouseY, mouse.width * 1.5f, mouse.height * 1.5f);
			app.imageMode(PConstants.CENTER);

			break;
		}// cierra el switch de pan nivel

///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

// Desiciones de los clientes
		if (pan.getNivel() == "Main") {
			switch (clientSelUno) {

			case "Juego":
				app.image(cursorUno, 400, 460);
				break;

			case "Instrucciones":
				app.image(cursorUno, 800, 460);
				break;

			}// cierra el switch de clientUno

			switch (clientSelDos) {

			case "Juego":
				app.image(cursorDos, 400, 635);
				break;

			case "Instrucciones":
				app.image(cursorDos, 800, 635);
				break;

			}// cierra el switch de clientDos

// toma desiciones

			if (clientSelUno.equals(clientSelDos) && clientSelUno.equals("Instrucciones")) {
				clientSelUno = "";
				clientSelDos = "";
				pan.setNivel("Help");
				com.getClientes().get(0).enviar("Nivel,Help");
				com.getClientes().get(1).enviar("Nivel,Help");
			} else if (clientSelUno.equals(clientSelDos) && clientSelUno.equals("Juego")) {
				clientSelUno = "";
				clientSelDos = "";
				pan.setNivel("Help");
				com.getClientes().get(0).enviar("Nivel,Escoger");
				com.getClientes().get(1).enviar("Nivel,Escoger");
				creacion();
			}
		}
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
		if (pan.getNivel() == "Help") {

			if (clientSelUno.equals("Main"))
				app.image(cursorUno, 90, 500);

			if (clientSelDos.equals("Main"))
				app.image(cursorDos, 150, 500);

			if (clientSelUno.equals(clientSelDos) && clientSelUno.equals("Main")) {
				clientSelUno = "";
				clientSelDos = "";
				pan.setNivel("Main");
				com.getClientes().get(0).enviar("Nivel,Main");
				com.getClientes().get(1).enviar("Nivel,Main");
			}
		}

	}// cierra el metodo pintar

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void click() {

		switch (pan.getNivel()) {

		case "Espera":
			if (com.getClientes().size() < 2) {
				if (app.mouseX > 465 && app.mouseX < 724 && app.mouseY > 595 && app.mouseY < 615
						&& com.getClientes().size() == 0 || readyOne) {
					pan.setNivel("Main");
					readyOne = false;
					readyTwo = false;
					readyCon = 3;
					if (com.getClientes().size() > 0) {
						com.getClientes().get(0).enviar("Nivel,Main");
					}

				}
			}
			break;

		case "Main":

			// pasar del menu principal al juego

			if (app.mouseX > 303 && app.mouseX < 489 && app.mouseY > 513 && app.mouseY < 585) {

				creacion();
			}

			// pasar del menu a instrucciones

			if (app.mouseX > 700 && app.mouseX < 890 && app.mouseY > 511 && app.mouseY < 585) {

				pan.setNivel("Help");
				clientSelUno = "";
				clientSelDos = "";

				for (int i = 0; i < com.getClientes().size(); i++) {
					com.getClientes().get(i).enviar("Nivel,Help");
				}

			}

			break;// cierra el case main
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
		case "Help":

			// de las instrucciones al menu
			if (PApplet.dist(app.mouseX, app.mouseY, 100, 600) < 85) {
				pan.setNivel("Main");
				clientSelUno = "";
				clientSelDos = "";

				for (int i = 0; i < com.getClientes().size(); i++) {
					com.getClientes().get(i).enviar("Nivel,Main");
				}

			}

			break;// cierra el case help
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
		case "Juego":

			// coger un objeto

			for (int i = 0; i < objetos.size(); i++) {

				Pick obj = objetos.get(i);

				if (app.mouseX > obj.getX() - obj.getHover().width / 2
						&& app.mouseX < obj.getX() + obj.getHover().width / 2
						&& app.mouseY > obj.getY() - obj.getHover().height / 2
						&& app.mouseY < obj.getY() + obj.getHover().height / 2) {

					if (turnoCoger == 1) {
						elementoUno = obj;
						objetos.remove(i);
						turnoCoger = 2;
						break;
					}

					if (turnoCoger == 2) {
						elementoDos = obj;
						objetos.clear();
						coger = false;
						poner = true;
						break;
					}
				}
			}

			// pasa de ronda

			if (pan.getCrecimiento() >= 100) {

				if (app.mouseX > 525 && app.mouseY > 588 && app.mouseX < 660 && app.mouseY < 615) {

					// crea los objetos

					objetos.add(new Pick(this.app, 430, this.app.height / 2 + 25));
					objetos.add(new Pick(this.app, 560, this.app.height / 2 + 25));
					objetos.add(new Pick(this.app, 690, this.app.height / 2 + 25));
					contadorCorrector = 0;
					ronda += 1;
					turnoCoger = 1;
					turnoPoner = 1;
					pan.setAumento(0);
					pan.setCrecimiento(0);
					elementoUno = null;
					elementoDos = null;
					finalCountdown = false;
					cuenta = 4;
					coger = true;

					for (int i = 0; i < elementos.size(); i++) {

						Elemento ele = elementos.get(i);

						if (ele.getTipo() == "Mina")
							elementos.get(i).setActivada(false);

					}

					if (jugadorUno.isGano()) {
						jugadorUno.setGano(false);
					}

					if (jugadorDos.isGano()) {

						jugadorDos.setGano(false);
					}

					// crea los jugadores
					jugadorUno = new Jugador(app, "Uno", 150, -200, com.getClientes());
					jugadorDos = new Jugador(app, "Dos", 250, -200, com.getClientes());
				}
			}

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

			// posicion relativa del mouse dentro de la camara

			for (int i = 0; i < cuadricula.size(); i++) {
				Cuadricula cuadro = cuadricula.get(i);

				if (mouseCam.x >= cuadro.getPos().x && mouseCam.x <= cuadro.getPos().x + cuadro.getAncho()
						&& mouseCam.y >= cuadro.getPos().y && mouseCam.y <= cuadro.getPos().y + cuadro.getAlto()
						&& cuadro.isOcupado() == false) {

					// crear arma, trampa o plataforma

					if (poner && finalCountdown == false && contadorCorrector > 10) {

						if (turnoPoner == 1) {
							if (elementoUno.getTipo() != "Suelo")
								elementos.add(new Elemento(app, cuadro.getPos().x + cuadro.getAncho() / 2,
										cuadro.getPos().y + cuadro.getAlto() / 2, elementoUno.getTipo()));
							if (elementoUno.getTipo() == "Suelo")
								suelo.add(new Suelo(app, cuadro.getPos().x, cuadro.getPos().y));
							elementoUno = null;
						}

						if (turnoPoner == 2) {
							if (elementoDos.getTipo() != "Suelo")
								elementos.add(new Elemento(app, cuadro.getPos().x + cuadro.getAncho() / 2,
										cuadro.getPos().y + cuadro.getAlto() / 2, elementoDos.getTipo()));
							if (elementoDos.getTipo() == "Suelo")
								suelo.add(new Suelo(app, cuadro.getPos().x, cuadro.getPos().y));
							elementoDos = null;
						}

						cuadricula.get(i).setOcupado(true);
						if (turnoPoner == 1) {
							turnoPoner = 2;
							break;
						}

						if (turnoPoner == 2) {
							finalCountdown = true;
							contadorCorrector = 0;
							turnoPoner = 1;
							break;
						}
					}
				}
			}

			break;// cierra el case juego

///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

		case "Game Over":
			// reset
			if (app.mouseX > 488 && app.mouseX < 711 && app.mouseY > 575 && app.mouseY < 625) {
				reset();
			}

			break; // cierraa el case Game Ovel
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
		}// cierra el switch de nivel
	}// cierra el metodo click

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	private void camara() {

		// la camara se mueve respecto el punto medio entre los jugadores

		// si ambos siguen en tierra y vivos

		if (jugadorUno.isCaido() == false && jugadorUno.isNoqueado() == false && jugadorDos.isCaido() == false
				&& jugadorDos.isNoqueado() == false) {

			origenCam = new PVector(app.width / 2 - (jugadorUno.getPos().x / 2 + jugadorDos.getPos().x / 2), 0);
			origenCam.x = origenCam.x * zoom + zoom * 150;
		}

		// si uno de los jugadores se cae o muere

		// se cae o muere el jugador uno

		if ((jugadorUno.getPos().y - jugadorUno.getAlto()) * zoom - origenCam.y > 1000 / zoom - origenCam.y) {

			jugadorUno.setPosY(-2000);
			jugadorUno.setGravedad(false);
			jugadorUno.setCaido(true);
		}

		if (jugadorUno.isNoqueado() || jugadorUno.isCaido()) {
			origenCam = new PVector(app.width / 2 - (jugadorDos.getPos().x / 2 + bandera.getPos().x / 2), 0);
			origenCam.x = (float) (origenCam.x * zoom + zoom * 300);
		}

		// se cae o muere el jugador dos

		if ((jugadorDos.getPos().y - jugadorDos.getAlto()) * zoom - origenCam.y > 1000 / zoom - origenCam.y) {

			jugadorDos.setPosY(-2000);
			jugadorDos.setGravedad(false);
			jugadorDos.setCaido(true);

		}
		if (jugadorDos.isNoqueado() || jugadorDos.isCaido()) {
			origenCam = new PVector(app.width / 2 - (jugadorUno.getPos().x / 2 + bandera.getPos().x / 2), 0);
			origenCam.x = (float) (origenCam.x * zoom + zoom * 300);
		}

		// ambos se cayeron o murieron

		if ((jugadorUno.isCaido() || jugadorUno.isNoqueado()) && (jugadorDos.isCaido() || jugadorDos.isNoqueado())) {
			origenCam = new PVector(app.width / 2, 0);
			origenCam.x = (float) (origenCam.x * zoom + zoom * 300);
		}

		// distancia de los jugadores
		distancia = PApplet.abs(jugadorDos.getPos().x - jugadorUno.getPos().x);
		if (distancia >= 1650)
			distancia = 1650;

		// distancia de los jugadores a la bandera

		distanciaMetaUno = PApplet.abs(jugadorUno.getPos().x - bandera.getPos().x);
		if (distanciaMetaUno >= 1600)
			distanciaMetaUno = 1600;

		distanciaMetaDos = PApplet.abs(jugadorDos.getPos().x - bandera.getPos().x);
		if (distanciaMetaDos >= 1600)
			distanciaMetaDos = 1600;

		// la distancia entre los jugadores determina el zoom si ambos siguen en el mapa

		if (poner == false && coger == false) {

			if (jugadorUno.isCaido() == false && jugadorUno.isNoqueado() == false && jugadorDos.isCaido() == false
					&& jugadorDos.isCaido() == false) {

				zoom = PApplet.map(distancia, 0, 1650, 1, 0.60f);

			}

			// el zoom varia dependiendo de cuantos jugadores se hayan caido

			if (jugadorUno.isCaido() == false && jugadorDos.isCaido()) {
				zoom = PApplet.map(distanciaMetaUno, 0, 1600, 0.9f, 0.70f);
			}

			if ((jugadorUno.isCaido()) && jugadorDos.isCaido() == false) {
				zoom = PApplet.map(distanciaMetaDos, 0, 1600, 0.9f, 0.70f);
			}

			if (jugadorUno.isCaido() && jugadorDos.isCaido()) {
				zoom = 0.70f;
			}
		} else if ((poner || coger) && finalCountdown == false)
			zoom = 0.70f;

		// se va acercando mientras termina la cuenta atras

		if (finalCountdown) {
			zoom += 0.0015;
		}

		// correciones y limites de la camara

		if (this.origenCam.x >= 50 * zoom)
			this.origenCam.x = 50 * zoom;

		// aplica el zoom

		app.scale((float) zoom);

		// aplica la posicion de la camara

		app.translate(origenCam.x, origenCam.y);

		// posicion del mouse al respecto

		mouseCam = new PVector((float) (app.mouseX / zoom - origenCam.x), (float) (app.mouseY / zoom - origenCam.y));

	}// cierra el metodo camara
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	class conteoRegresivo implements Runnable {

		public void run() {
			while (true) {
				try {

					Thread.sleep(1000);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (finalCountdown) {
					cuenta -= 1;
					if (cuenta == 0) {
						cuenta = 4;
						finalCountdown = false;
						poner = false;
					}
				}
			}
		}// cierra el run
	}// cierra la clase conteoRegresivo

	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	private void reset() {

		// rondas a jugar
		ronda = 1;

		// turno de poner
		coger = false;
		poner = false;
		turnoCoger = 1;
		turnoPoner = 1;
		elementoUno = null;
		elementoDos = null;
		contadorCorrector = 0;

		jugadorUno = null;
		jugadorDos = null;

		// contador
		finalCountdown = false;
		cuenta = 4;

		// suelo del spawn
		elementos.clear();
		suelo.clear();
		cuadricula.clear();
		objetos.clear();

		// armas. trampas y plataformas

		pan.setAumento(0);
		pan.setCrecimiento(0);
		pan.setUnoPuntaje(0);
		pan.setDosPuntaje(0);

		pan.setNivel("Main");
	}// cierra el metodo reset

	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	public void creacion() {

/////////////////// Generacion del suelo////////////////////////

		for (int i = 0; i < (int) (app.random(3, 5)); i++) {
			suelo.add(new Suelo(app, i * 100, 600));
		}

		for (int i = 0; i < (int) (app.random(2, 5)); i++) {

			suelo.add(new Suelo(app, i * 100, 500));
		}

		for (int i = 1; i < (int) (app.random(2, 4)); i++) {
			suelo.add(new Suelo(app, i * 100, 400));
		}

		for (int i = 7; i < 11; i++) {
			suelo.add(new Suelo(app, 0, i * 100));
			suelo.add(new Suelo(app, 100, i * 100));
		}

		// suelo de la meta

		for (int j = 10; j > (int) (app.random(1, 6)); j--) {
			for (int i = 15; i > (int) (app.random(13, 15)); i--) {

				suelo.add(new Suelo(app, i * 100, j * 100));
			}
		}

/////////////////// Crea la cuadricula////////////////////////

		for (int i = 5; i < 13; i++) {
			for (int j = 1; j < 8; j++) {

				cuadricula.add(new Cuadricula(app, i * 100, j * 100));
			}
		}

// hilos

		coger = true;

		// crea los objetos
		objetos.add(new Pick(app, 430, app.height / 2 + 25));
		objetos.add(new Pick(app, 560, app.height / 2 + 25));
		objetos.add(new Pick(app, 690, app.height / 2 + 25));

		// crea la bandera
		bandera = new Bandera(app, 1551, -100);
		letrero = new Bandera(app, 40, -300);

		// crea los jugadores
		jugadorUno = new Jugador(app, "Uno", 150, -200, com.getClientes());
		jugadorDos = new Jugador(app, "Dos", 250, -200, com.getClientes());

		conteoRegresivoHilo = new Thread(new conteoRegresivo());
		conteoRegresivoHilo.start();
		pan.setNivel("Juego");

		// clientes
		clientSelUno = "";
		clientSelDos = "";
		for (int i = 0; i < com.getClientes().size(); i++) {
			com.getClientes().get(i).enviar("Nivel,Jugar");
		}

	}// cierra el metodo creacion

	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	public void presionarTeclas() {

		if (poner == false && coger == false) {
			jugadorUno.mover();
			jugadorDos.mover();
		}

	}// presionar teclas

	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	public void soltarTeclas() {
		jugadorUno.parar();
		jugadorDos.parar();
	}// soltar teclas

	@Override
	public void update(Observable o, Object arg) {
		String mensaje = (String) arg;

		if (mensaje.contains("Listo")) {

			String[] valores = mensaje.split(",");
			String jugador = valores[0];
			boolean listo = Boolean.parseBoolean(valores[2]);

			if (jugador.equals("JugadorUno")) {
				readyOne = listo;
			} else if (jugador.equals("JugadorDos")) {
				readyTwo = listo;
			}

		} else if (mensaje.contains("Pantalla")) {
			String[] valores = mensaje.split(",");
			String jugador = valores[0];
			String pantalla = valores[2];

			if (jugador.equals("JugadorUno")) {
				clientSelUno = pantalla;

			} else if (jugador.equals("JugadorDos")) {
				clientSelDos = pantalla;

			}

		}

	}// cierra el metodo update

	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

}// cierra la clase Logica
