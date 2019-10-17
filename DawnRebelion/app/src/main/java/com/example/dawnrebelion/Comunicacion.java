package com.example.dawnrebelion;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

public class Comunicacion extends Observable implements Runnable {

    private static Comunicacion ref;
    private Socket s;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private InetAddress address;
    private boolean conectado;

    private Comunicacion() {

        conectado = false;

    }// constructor

    public static Comunicacion instancia() {
        if (ref == null) {
            ref = new Comunicacion();
        }
        return ref;
    }// instancia

    @Override
    public void run() {


        if (!conectado) {

            try {
                address = InetAddress.getByName("172.30.168.188");
                int port = 5000;
                s = new Socket(address, port);
                salida = new DataOutputStream(s.getOutputStream());
                entrada = new DataInputStream(s.getInputStream());
                conectado = true;
                salida.writeUTF("Hola!!");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            try {
                recibir();
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }// cierra el run

    private void recibir() throws IOException {
        if (conectado) {
            String mensajeRecibido = entrada.readUTF();
            setChanged();
            notifyObservers(mensajeRecibido);
            clearChanged();

        }

    }//cierra el metodo recibir

    public void enviar(final String mensaje) {
        if (conectado) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        salida.writeUTF(mensaje);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }//cierra el metodo enviar

}// ciera la clase comunicacion


