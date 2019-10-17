package com.example.dawnrebelion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    private Comunicacion com;
    private boolean listo;
    private String jugador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com = Comunicacion.instancia();
        com.addObserver(this);
        Thread t = new Thread(com);
        t.start();
        listo = false;
        jugador = "";
    }


    public void Listo(View view) {
        listo = !listo;
        com.enviar(jugador + ",Listo," + listo);
    }

    @Override
    public void update(Observable o, Object arg) {
        String mensaje = (String) arg;

        if (mensaje.contains("Jugador")) {
            jugador = (String) arg;
        }

        if (mensaje.contains("Nivel")) {
            String[] valores = mensaje.split(",");
            String nivel = valores[1];
            if (nivel.equals("Main")) {

                Intent goMain = new Intent(getApplicationContext(), MainMenu.class);
                goMain.putExtra("NumeroJugador", jugador);
                startActivity(goMain);
            }

        }

    }
}
