package tallerUno.Payan;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Comunicacion extends Observable implements Runnable, Observer {

	private static Comunicacion ref;
	private ServerSocket ss;
	private ArrayList<ListaClientes> clientes;

	private Comunicacion() {

		try {
			ss = new ServerSocket(5000);
			clientes = new ArrayList<>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// constructor

	public static Comunicacion getSingleton() {
		if (ref == null) {
			ref = new Comunicacion();
		}
		return ref;
	}// instancia

	@Override
	public void run() {

		while (true) {
			if (clientes.size() < 2) {
				try {
					recibirClientes();
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} // cierra el while
	}// cierra el run

	private void recibirClientes() throws IOException {

		Socket socketNuevoCliente = ss.accept();

		ListaClientes nuevoCliente = new ListaClientes(socketNuevoCliente);

		clientes.add(nuevoCliente);

		Thread t = new Thread(nuevoCliente);

		t.start();

		// asigna el numero de jugador

		if (clientes.size() == 1) {

			clientes.get(0).enviar("JugadorUno");
		} else if (clientes.size() == 2) {

			clientes.get(1).enviar("JugadorDos");

		}

		nuevoCliente.addObserver(this);

		System.out.println("Ahora tengo " + clientes.size() + " Clientes");

	}

	@Override
	public void update(Observable o, Object arg) {

		System.out.println((String) arg);

		setChanged();
		notifyObservers((String) arg);
		clearChanged();
	}

	public ArrayList<ListaClientes> getClientes() {
		return clientes;
	}

}// ciera la clase comunicacion
