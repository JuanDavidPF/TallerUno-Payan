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
public class Jugador implements Observer {

	private Main app;
	private Jugador adversario;
	private int puntaje, aguante, campanazoKO, frames, ancho, alto, rebotePared;
	private PVector pos, vel, acel;
	private String idPlayer, perspectiva, estado;
	private boolean izq, der, cayendo, gano, agachado, puedeSaltar, arrastra, parado, aplastado, caido, noqueado,
			gravedad, isDrawRunning;
	private Thread fisicasHilo, recoverHilo, frameRateHilo;
	private PImage[] quietoD, quietoI, corriendoI, corriendoD, estrellitas, deslizandoI, deslizandoD;
	private PImage agachadoI;
	private PImage agachadoD;
	private PImage aplastadoI;
	private PImage aplastadoD;
	private PImage arrastrandoseI;
	private PImage arrastrandoseD;
	private PImage frenandoI;
	private PImage frenandoD;
	private PImage elevandoI;
	private PImage elevandoD;
	private PImage fallingD;
	private PImage fallingI;
	private PImage idUno;
	private PImage idDos;
	private ArrayList<Suelo> suelo;
	private ArrayList<ListaClientes> clientes;
	private ArrayList<Elemento> elementos;
	private Bandera bandera;
	private Comunicacion com;
	private boolean clientLeft, clientRight, clientJump, clientCrouch;

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	public Jugador(Main app, String idPlayer, int x, int y, ArrayList<ListaClientes> clientes) {

		this.app = app;

		// Cliente socket
		this.clientes = clientes;

		// numero de jugador

		this.idPlayer = idPlayer;

		// puntaje

		puntaje = 0;

		// posicion y movimiento

		pos = new PVector(x, y);
		vel = new PVector(0, 0);
		acel = new PVector(0, 0);

		izq = false;
		der = false;
		cayendo = true;
		gano = false;
		agachado = false;
		puedeSaltar = false;
		arrastra = false;
		parado = false;
		aplastado = false;
		caido = false;
		noqueado = false;
		gravedad = true;
		isDrawRunning = false;

		aguante = 1;
		campanazoKO = 0;

		// dimensiones

		ancho = 40;
		alto = 70;

		// hilos

		fisicasHilo = new Thread(new fisicas());
		fisicasHilo.start();
		recoverHilo = new Thread(new recover());
		recoverHilo.start();
		frameRateHilo = new Thread(new frameRate());
		frameRateHilo.start();

		// animaciones
		perspectiva = "der";
		estado = "";
		frames = 0;

		// imagenes
		quietoD = new PImage[2];
		quietoI = new PImage[2];
		corriendoD = new PImage[10];
		corriendoI = new PImage[10];
		estrellitas = new PImage[3];
		deslizandoI = new PImage[4];
		deslizandoD = new PImage[4];

		for (int i = 0; i < 10; i++) {
			if (i < 2)
				quietoD[i] = app.loadImage("./data/Jugadores/Oveja/quietoD" + (i + 1) + ".png");
			if (i < 2)
				quietoI[i] = app.loadImage("./data/Jugadores/Oveja/quietoI" + (i + 1) + ".png");
			if (i < 3)
				estrellitas[i] = app.loadImage("./data/Jugadores/Oveja/efectomuerto" + (i + 1) + ".png");
			if (i < 4)
				deslizandoD[i] = app.loadImage("./data/Jugadores/Oveja/deslizandoD" + (i + 1) + ".png");
			if (i < 4)
				deslizandoI[i] = app.loadImage("./data/Jugadores/Oveja/deslizandoI" + (i + 1) + ".png");
			if (i < 10)
				corriendoD[i] = app.loadImage("./data/Jugadores/Oveja/corriendoD" + (i + 1) + ".png");
			if (i < 10)
				corriendoI[i] = app.loadImage("./data/Jugadores/Oveja/corriendoI" + (i + 1) + ".png");
		}

		agachadoI = app.loadImage("./data/Jugadores/Oveja/agachadoI.png");
		agachadoD = app.loadImage("./data/Jugadores/Oveja/agachadoD.png");

		aplastadoI = app.loadImage("./data/Jugadores/Oveja/aplastadoI.png");
		aplastadoD = app.loadImage("./data/Jugadores/Oveja/aplastadoD.png");

		arrastrandoseI = app.loadImage("./data/Jugadores/Oveja/arrastrandoseI.png");
		arrastrandoseD = app.loadImage("./data/Jugadores/Oveja/arrastrandoseD.png");

		frenandoI = app.loadImage("./data/Jugadores/Oveja/frenandoI.png");
		frenandoD = app.loadImage("./data/Jugadores/Oveja/frenandoD.png");

		elevandoI = app.loadImage("./data/Jugadores/Oveja/elevandoI.png");
		elevandoD = app.loadImage("./data/Jugadores/Oveja/elevandoD.png");

		fallingI = app.loadImage("./data/Jugadores/Oveja/cayendoI.png");
		fallingD = app.loadImage("./data/Jugadores/Oveja/cayendoD.png");

		idUno = app.loadImage("./data/Escenario/Iconos/idUno.png");
		idDos = app.loadImage("./data/Escenario/Iconos/idDos.png");

		// comunicacion
		com = Comunicacion.getSingleton();
		com.addObserver(this);
	}// cierra el metodo jugar

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void pintar(ArrayList<Suelo> suelo, Jugador adversario, ArrayList<Elemento> elementos, Bandera bandera) {
		isDrawRunning = true;
		this.suelo = suelo;
		this.adversario = adversario;
		this.elementos = elementos;
		this.bandera = bandera;

		sprites();

		if (idPlayer == "Uno") {
			app.image(idUno, pos.x, pos.y - 90, 70, 50);
		}

		if (idPlayer == "Dos") {
			app.image(idDos, pos.x, pos.y - 90, 70, 50);
		}

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

		if ((clientes.size() == 1 && idPlayer == "Uno") || clientes.size() == 2) {
			if (clientRight) {
				moverDerecha();
			} else if (clientRight == false) {
				NomoverDerecha();
			}

			if (clientLeft) {
				moverIzquierda();
			} else if (clientLeft == false) {
				NomoverIzquierda();
			}

			if (clientJump) {
				saltar();
			} else if (clientJump == false) {
				Nosaltar();
			}

			if (clientCrouch) {
				agachar();
			} else if (clientCrouch == false) {
				Noagachar();
			}
		}
	}// cierra el metodo pintar
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void mover() {

		if (clientes.size() == 0 || (clientes.size() == 1 && idPlayer == "Dos")) {

			if (isDrawRunning) {

				switch (idPlayer) {

				case "Uno":

					if (app.key == 'd' || app.key == 'D' && aplastado == false) {
						moverDerecha();
					}
					if (app.key == 'a' || app.key == 'A' && aplastado == false) {
						moverIzquierda();
					}

					if (app.key == 's' || app.key == 'S' && arrastra == false) {

						agachar();
					}

					if (app.key == 'w' || app.key == 'W') {

						saltar();
					}

					break;

				case "Dos":

					if (app.keyCode == PConstants.RIGHT && aplastado == false) {
						moverDerecha();
					}
					if (app.keyCode == PConstants.LEFT && aplastado == false) {
						moverIzquierda();
					}

					if (app.keyCode == PConstants.DOWN && arrastra == false) {
						agachar();
					}

					if (app.keyCode == PConstants.UP) {

						saltar();
					}

					break;
				} // cierra el switch de player

			} // cierra el if
		}

	}// cierra el metodo mover

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void parar() {
		if (clientes.size() == 0 || (clientes.size() == 1 && idPlayer == "Dos")) {
			switch (idPlayer) {

			case "Uno":

				if (app.key == 'd' || app.key == 'D' && aplastado == false) {
					NomoverDerecha();
				}

				if (app.key == 'a' || app.key == 'A' && aplastado == false) {
					NomoverIzquierda();
				}

				if (app.key == 's' || app.key == 'S' && arrastra == false) {
					Noagachar();
				}

				if (app.key == 'w' || app.key == 'W' && gano == false) {
					Nosaltar();

				}

				break;

			case "Dos":

				if (app.keyCode == PConstants.RIGHT && aplastado == false) {
					NomoverDerecha();
				}

				if (app.keyCode == PConstants.LEFT && aplastado == false) {
					NomoverIzquierda();
				}

				if (app.keyCode == PConstants.DOWN && arrastra == false) {
					Noagachar();
				}

				if (app.keyCode == PConstants.UP && gano == false) {

					Nosaltar();
				}
				break;
			} // cierra el switch de player
		}
	}// cierra el metodo parar
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	class fisicas implements Runnable {

		@Override
		public void run() {

			while (true) {

				System.out.print("");

				if (isDrawRunning) {

					try {
						Thread.sleep(12);

						// el personaje reacciona a la fisica de su mundo

						// capa la posicion si se cae del mapa
						if (caido || aplastado || gano) {

							izq = false;
							der = false;
						}

						// aplica las aceleraciones a las velocidades
						vel.x += acel.x;
						vel.y += acel.y;

						// aplica las fisicas a la posicion
						pos.x += (int) vel.x;
						pos.y += (int) vel.y;

						// gravedad
						if (gravedad) {
							acel.y = 0.38f;
						} else if (gravedad == false) {
							acel.y = 0;
							vel.y = 0;
						}

						// limite de gravedad para evitar bugs :V
						if (vel.y >= 10)
							vel.y = 10;

						// super noqueado
						if (noqueado) {

							aplastado = true;
						}

						// noqueado

						if (aplastado) {
							izq = false;
							der = false;
							agachado = true;
							acel.x = 0;
						}

						// aceleracion al correr

						if (agachado == false) {

							if (izq) {

								if (vel.x > -4) {
									acel.x = -0.3f;
								} else
									acel.x = 0;
							}
							if (der) {
								if (vel.x < 4) {
									acel.x = 0.3f;

								} else
									acel.x = 0;
							}
						}

						// desaceleracion, inercia y frenado.

						if (izq == false && der == false) {

							// frenado si está caminando

							if (agachado == false) {
								if (vel.x < 0) {
									acel.x = 0.08f;
									if ((int) vel.x == 0) {
										vel.x = 0;
										acel.x = 0;
									}
								}

								if (vel.x > 0) {
									acel.x = -0.08f;
									if ((int) vel.x == 0) {
										vel.x = 0;
										acel.x = 0;
									}
								}
							} else if (agachado) {

								// desliza si hace planchazo

								if (vel.x < 0) {
									acel.x = 0.04f;
									if ((int) vel.x == 0) {
										vel.x = 0;
										acel.x = 0;
									}
								}

								if (vel.x > 0) {
									acel.x = -0.04f;
									if ((int) vel.x == 0) {
										vel.x = 0;
										acel.x = 0;
									}
								}
							}
						}

						// se agacha

						if (agachado) {
							ancho = 80;
							alto = 30;
						} else if (agachado == false) {
							ancho = 40;
							alto = 70;

						}

						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////

						// el jugador interactua con las paredes y el suelo

						for (int i = 0; i < suelo.size(); i++) {
							Suelo sue = suelo.get(i);

							// el jugador está pisando suelo
							if (vel.y >= 0 && (int) pos.y + alto / 2 > sue.getPos().y
									&& (int) (pos.y + alto / 2) < sue.getPos().y + 30
									&& pos.x - ancho / 2 <= sue.getPos().x + sue.getAncho()
									&& pos.x + ancho / 2 >= sue.getPos().x) {
								vel.y = 0;
								pos.y = sue.getPos().y - alto / 2;
								puedeSaltar = true;
								arrastra = false;
								parado = true;
								if (gano)
									vel.y = -10;
							}

							// el jugador se da con el techo

							if (vel.y < 0 && (int) pos.y - alto / 2 < sue.getPos().y + sue.getAlto()
									&& (int) pos.y - alto / 2 > sue.getPos().y
									&& pos.x - ancho / 2 <= sue.getPos().x + sue.getAncho()
									&& pos.x + ancho / 2 >= sue.getPos().x) {
								vel.y *= -1;
							}

							// el jugador se da con las paredes y se aferra a ellas

							if (pos.y + alto / 2 > sue.getPos().y + 10
									&& pos.y - alto / 2 < sue.getPos().y + sue.getAlto()) {

								if (vel.x > 0 && pos.x + ancho / 2 + vel.x >= sue.getPos().x - 5
										&& pos.x - ancho / 2 < sue.getPos().x) {
									puedeSaltar = false;
									arrastra = true;
									parado = false;
									vel.x = 0;
									acel.y = 0.1f;
									rebotePared = -4;
									break;
								}

								if (vel.x < 0 && pos.x - ancho / 2 + vel.x <= sue.getPos().x + sue.getAncho() + 5
										&& pos.x + ancho / 2 > sue.getPos().x + sue.getAncho()) {
									puedeSaltar = false;
									arrastra = true;
									parado = false;
									acel.y = 0.1f;
									vel.x = 0;
									rebotePared = 4;
									break;
								} else
									arrastra = false;
							}
						}

						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////

						// logica del 1 vs 1

						// empuja enemigo

						if (pos.y + alto / 2 > adversario.getPos().y - adversario.getAlto() / 2 + 10
								&& pos.y - alto / 2 < adversario.getPos().y + adversario.getAlto() / 2) {

							// rebote de fueezas contrarias

							if (vel.x > 0 && adversario.getVel().x != 0
									&& pos.x + ancho / 2 + vel.x >= adversario.getPos().x - adversario.getAncho() / 2
									&& pos.x - ancho / 2 < adversario.getPos().x - adversario.getAncho() / 2) {
								vel.x = -3;
							}

							if (vel.x < 0 && adversario.getVel().x != 0
									&& pos.x - ancho / 2 + vel.x <= adversario.getPos().x + adversario.getAncho() / 2
									&& pos.x + ancho / 2 > adversario.getPos().x + adversario.getAncho() / 2) {
								vel.x = 3;
							}

							// empuja si el adversario se quedó quieto

							if (vel.x > 0 && adversario.getVel().x == 0
									&& pos.x + ancho / 2 + vel.x >= adversario.getPos().x - adversario.getAncho() / 2
									&& pos.x - ancho / 2 < adversario.getPos().x - adversario.getAncho() / 2) {
								adversario.setVelX(4);
								adversario.setPerspectiva("der");
								vel.x = 0;
							}

							if (vel.x < 0 && adversario.getVel().x == 0
									&& pos.x - ancho / 2 + vel.x <= adversario.getPos().x + adversario.getAncho() / 2
									&& pos.x + ancho / 2 > adversario.getPos().x + adversario.getAncho() / 2) {
								adversario.setPerspectiva("izq");
								adversario.setVelX(-4);
								vel.x = 0;

							}
						}

						// aplastó enemigo y sale disparado

						if (vel.y > 0 && agachado == false
								&& ((pos.x - ancho / 2 > adversario.getPos().x - adversario.getAncho() / 2
										&& pos.x - ancho / 2 < adversario.getPos().x + adversario.getAncho() / 2)
										|| (pos.x + ancho / 2 > adversario.getPos().x - adversario.getAncho() / 2
												&& pos.x + ancho / 2 < adversario.getPos().x
														+ adversario.getAncho() / 2))
								&& pos.y + alto / 2 > adversario.getPos().y - adversario.getAlto() / 2
								&& pos.y + alto / 2 < adversario.getPos().y) {
							adversario.setVelY(adversario.getVel().y + vel.y);
							vel.y = -8;
							parado = false;
							puedeSaltar = true;
							if (adversario.getParado() && adversario.getAgachado() == false)
								adversario.setAplastado(true);
						}

						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////
						// logica de la interaccion con armas, plataformas o trampas

						// el jugador está pisando una plataforma arma o trampa

						for (int i = 0; i < elementos.size(); i++) {

							Elemento ele = elementos.get(i);

							if (vel.y >= 0 && (int) pos.y + alto / 2 > ele.getPos().y - ele.getAlto() / 2
									&& (int) pos.y + alto / 2 < ele.getPos().y + ele.getAlto() / 2
									&& pos.x - ancho / 2 <= ele.getPos().x + ele.getAncho() / 2
									&& pos.x + ancho / 2 >= ele.getPos().x - ele.getAncho() / 2) {

								vel.y = 0;
								pos.y = (ele.getPos().y - ele.getAlto() / 2) - alto / 2;
								puedeSaltar = true;
								arrastra = false;
								parado = true;

								// activa la mina
								if (ele.getTipo() == "Mina" && ele.isActivada() == false && noqueado == false
										&& pos.x > ele.getPos().x - 30 && pos.x < ele.getPos().x + 30) {

									ele.setExplota(true);
									ele.setActivada(true);
									vel.x *= -1.5;
									vel.y = -10;
									noqueado = true;
									if (perspectiva == "izq") {
										perspectiva = "der";
										break;
									}
									if (perspectiva == "der") {
										perspectiva = "izq";
										break;
									}
								}
							}

							// el jugador golpea algunos elemnetos por debajo

							if (vel.y < 0 && ele.getTipo() != "Andamio"
									&& (int) (pos.y - alto / 2) < ele.getPos().y + ele.getAlto() / 2
									&& (int) (pos.y - alto / 2) > ele.getPos().y - ele.getAlto() / 2
									&& pos.x - ancho / 2 <= ele.getPos().x + ele.getAncho() / 2
									&& pos.x + ancho / 2 >= ele.getPos().x - ele.getAncho() / 2) {
								vel.y *= -1;
							}

							// el jugador puede escalar algunos elementos

							if (ele.getTipo() != "Andamio" && pos.y + alto / 2 > ele.getPos().y - ele.getAlto() / 2 + 10
									&& pos.y - alto / 2 < ele.getPos().y + ele.getAlto() / 2) {

								if (vel.x > 0 && pos.x + ancho / 2 + vel.x >= ele.getPos().x - ele.getAncho() / 2 - 5
										&& pos.x - ancho / 2 < ele.getPos().x - ele.getAncho() / 2) {
									puedeSaltar = false;
									arrastra = true;
									parado = false;
									vel.x = 0;
									acel.y = 0.1f;
									rebotePared = -4;
									break;
								}

								if (vel.x < 0 && pos.x - ancho / 2 + vel.x <= ele.getPos().x + ele.getAncho() / 2 + 5
										&& pos.x + ancho / 2 > ele.getPos().x + ele.getAncho() / 2) {
									puedeSaltar = false;
									arrastra = true;
									parado = false;
									acel.y = 0.1f;
									vel.x = 0;
									rebotePared = 4;
									break;
								}
							}
						}

						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////

						// evalua si el jugador ya llego a la meta

						if (PApplet.dist(pos.x, pos.y, bandera.getPos().x, bandera.getPos().y) < 50)
							gano = true;

						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////
						/////////////////////////////////////////////////////////////////////////////

						// todos los estados del jugador

						if (gravedad) {

							if (agachado && aplastado == false)
								estado = "Agachado";
							if (vel.y == 0 && izq == false && der == false && vel.x == 0 && agachado == false)
								estado = "Quieto";
							if (vel.y == 0 && (izq || der) && arrastra == false && agachado == false
									&& (vel.x > 2 || vel.x < -2))
								estado = "Caminando";
							if (arrastra)
								estado = "Arrastra";
							if (vel.y >= 1 && arrastra == false && agachado == false)
								estado = "Cayendo";
							if (vel.y < 0 && arrastra == false && agachado == false)
								estado = "Saltando";
							if (agachado && vel.x != 0 && aplastado == false && vel.y == 0)
								estado = "Deslizando";
							if (aplastado)
								estado = "Noqueado";
							if (der == false && vel.y == 0 && izq == false && vel.x != 0 && agachado == false
									&& (vel.x > 3 || vel.x < -3))
								estado = "Frenando";
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // cierra el if
			} // cierra el while
		}// cierra el run
	}// cierra la clase fisicas

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

// indica el tiempo que el jugador pasa noqueado

	class recover implements Runnable {
		@Override
		public void run() {

			while (true) {

				System.out.print("");

				if (isDrawRunning) {

					try {
						Thread.sleep(1000);

						if (noqueado == false) {

							if (aplastado)
								campanazoKO += 1;

							if (campanazoKO >= aguante) {
								aplastado = false;
								agachado = false;
								vel.y = -7;
								campanazoKO = 0;
								aguante += 0.5;

								if (aguante >= 7)
									aguante = 7;
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	} // cierra el hilo recover

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	class frameRate implements Runnable {

		@Override
		public void run() {

			while (true) {

				System.out.print("");

				if (isDrawRunning) {

					try {
						Thread.sleep(50);
						// determina la velocidad de frame para cada sprite

						switch (estado) {
						case "Quieto":

							if (frames < quietoD.length) {
								frames += 0.2;
							}
							break;

						case "Caminando":
							if (frames < corriendoD.length) {
								frames += 1;
							}
							break;

						case "Noqueado":
							if (frames < estrellitas.length) {
								frames += 1;
							}
							break;

						case "Deslizando":
							if (frames < deslizandoD.length) {
								frames += 1;
							}
							break;

						} // cierra el switch de estado

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // cierra el if
			} // cierra el while
		}// cierra el run

	} // cierra el hiloframeRate
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void sprites() {

		// carga el sprite correspondiente para cada estado

		app.imageMode(PConstants.CENTER);
		switch (estado) {
		case "Quieto":

			if (frames >= quietoD.length)
				frames = 0;
			if (perspectiva == "der")
				app.image(quietoD[(int) frames], pos.x + 6, pos.y - 5, ancho + 15, alto + 10);
			if (perspectiva == "izq")
				app.image(quietoI[(int) frames], pos.x - 6, pos.y - 5, ancho + 15, alto + 10);

			break;

		case "Caminando":

			if (frames >= corriendoD.length)
				frames = 0;
			if (perspectiva == "der")
				app.image(corriendoD[(int) frames], pos.x, pos.y - 5, ancho + 50, alto + 10);
			if (perspectiva == "izq")
				app.image(corriendoI[(int) frames], pos.x, pos.y - 5, ancho + 50, alto + 10);
			break;

		case "Agachado":
			if (perspectiva == "der")
				app.image(agachadoD, pos.x, pos.y, ancho + 50, alto + 40);
			if (perspectiva == "izq")
				app.image(agachadoI, pos.x, pos.y, ancho + 50, alto + 40);

			break;

		case "Noqueado":

			if (frames >= estrellitas.length)
				frames = 0;

			if (perspectiva == "der") {
				app.image(estrellitas[(int) frames], pos.x + ancho / 2, pos.y - 35, 50, 50);
				app.image(aplastadoD, pos.x, pos.y - 5, ancho + 50, alto + 40);
			}

			if (perspectiva == "izq") {
				app.image(estrellitas[(int) frames], pos.x - ancho / 2, pos.y - 35, 50, 50);
				app.image(aplastadoI, pos.x, pos.y - 5, ancho + 50, alto + 40);
			}

			break;

		case "Deslizando":
			if (frames >= deslizandoD.length)
				frames = 0;

			if (perspectiva == "der") {
				app.image(deslizandoD[(int) frames], pos.x, pos.y - 8, ancho + 50, alto + 40);
			}

			if (perspectiva == "izq") {
				app.image(deslizandoI[(int) frames], pos.x, pos.y - 8, ancho + 50, alto + 40);
			}

			break;

		case "Arrastra":

			if (perspectiva == "der") {
				app.image(arrastrandoseD, pos.x - 10, pos.y - 5, ancho + 30, alto + 5);
			}
			if (perspectiva == "izq") {
				app.image(arrastrandoseI, pos.x + 10, pos.y - 5, ancho + 30, alto + 5);
			}
			break;

		case "Frenando":
			if (perspectiva == "der") {
				app.image(frenandoD, pos.x + 10, pos.y - 5, ancho + 50, alto + 50);
			}
			if (perspectiva == "izq") {
				app.image(frenandoI, pos.x - 10, pos.y - 5, ancho + 50, alto + 50);
			}
			break;

		case "Cayendo":

			if (perspectiva == "der") {
				app.image(fallingD, pos.x + 10, pos.y - 5, ancho + 20, alto + 20);
			}
			if (perspectiva == "izq") {
				app.image(fallingI, pos.x - 10, pos.y - 5, ancho + 20, alto + 20);
			}
			break;

		case "Saltando":

			if (perspectiva == "der") {
				app.image(elevandoD, pos.x + 10, pos.y - 5, ancho + 20, alto + 20);
			}
			if (perspectiva == "izq") {
				app.image(elevandoI, pos.x - 10, pos.y - 5, ancho + 20, alto + 20);
			}
			break;
		} // cierra el switch de estado
	}// cierra el metodo sprites

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	public void reiniciar() {
		gravedad = false;
		aplastado = false;
		noqueado = false;
		agachado = false;

		if (idPlayer == "Uno") {
			perspectiva = "der";
			pos.x = 40;
			pos.y = app.height / 2;
		} else {
			perspectiva = "izq";
			pos.x = 275;
			pos.y = app.height / 2;
		}

	}// cierra el metodo reiniciar
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	// getters and setters

	public boolean getParado() {
		return parado;
	}

	public boolean getAgachado() {
		return agachado;
	}

	public void setAplastado(boolean aplastado) {
		this.aplastado = aplastado;
	}

	public void setVelX(int velX) {
		this.vel.x = velX;
	}

	public void setPerspectiva(String perspectiva) {
		this.perspectiva = perspectiva;
	}

	public void setVelY(float velY) {
		this.vel.y = velY;
	}

	public void setPosX(float x) {
		this.pos.x = x;
	}

	public void setPosY(int posY) {
		this.pos.y = posY;
	}

	public Jugador getAdversario() {
		return adversario;
	}

	public String getEstado() {
		return estado;
	}

	public int getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(int puntaje) {
		this.puntaje = puntaje;
	}

	public boolean isGano() {
		return gano;
	}

	public void setGano(boolean gano) {
		this.gano = gano;
	}

	public boolean isCaido() {
		return caido;
	}

	public PVector getPos() {
		return pos;
	}

	public PVector getVel() {
		return vel;
	}

	public int getAlto() {
		return alto;
	}

	public int getAncho() {
		return ancho;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public void setGravedad(boolean gravedad) {
		this.gravedad = gravedad;
	}

	public void setNoqueado(boolean noqueado) {
		this.noqueado = noqueado;
	}

	public boolean isNoqueado() {
		return noqueado;
	}

	public void setCaido(boolean caido) {
		this.caido = caido;
	}

	// mover

	public void moverIzquierda() {
		perspectiva = "izq";
		izq = true;
		der = false;
	}

	public void moverDerecha() {
		perspectiva = "der";
		der = true;
		izq = false;
	}

	public void saltar() {

		if (puedeSaltar && cayendo == false && aplastado == false) {
			if (agachado == false)
				vel.y = -10;
			if (agachado)
				vel.y = -8;
			puedeSaltar = false;
			cayendo = true;
			parado = false;
			arrastra = false;
		}

		if (arrastra && cayendo == false) {
			vel.y = -10;
			vel.x = rebotePared;
			arrastra = false;
			cayendo = true;
			parado = false;
		}

	}

	public void agachar() {
		agachado = true;
		izq = false;
		der = false;

	}
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
	// parar

	public void NomoverIzquierda() {
		izq = false;
	}

	public void NomoverDerecha() {
		der = false;
	}

	public void Nosaltar() {
		cayendo = false;
	}

	public void Noagachar() {
		agachado = false;
	}
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

	@Override
	public void update(Observable o, Object arg) {

		String mensaje = (String) arg;

		switch (idPlayer) {

		case "Uno":

			if (mensaje.contains("JugadorUno")) {
				String[] valores = mensaje.split(",");

				switch (valores[1]) {

				case "jump":
					clientJump = Boolean.parseBoolean(valores[2]);
					break;

				case "crouch":
					clientCrouch = Boolean.parseBoolean(valores[2]);
					break;

				case "left":
					clientLeft = Boolean.parseBoolean(valores[2]);
					break;

				case "right":
					clientRight = Boolean.parseBoolean(valores[2]);
					break;

				}
			}

			break;

		case "Dos":

			if (mensaje.contains("JugadorDos")) {
				String[] valores = mensaje.split(",");

				switch (valores[1]) {

				case "jump":
					clientJump = Boolean.parseBoolean(valores[2]);
					break;

				case "crouch":
					clientCrouch = Boolean.parseBoolean(valores[2]);
					break;

				case "left":
					clientLeft = Boolean.parseBoolean(valores[2]);
					break;

				case "right":
					clientRight = Boolean.parseBoolean(valores[2]);
					break;

				}

			}

			break;
		}// cierra el switch de Update

	}// cierra el metodo update

/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////

}// cierra la clase jugador
