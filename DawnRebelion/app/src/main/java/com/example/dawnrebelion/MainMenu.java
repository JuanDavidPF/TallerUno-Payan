package com.example.dawnrebelion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

public class MainMenu extends AppCompatActivity implements Observer {

    private Comunicacion com;
    private String jugador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        com = Comunicacion.instancia();
        com.addObserver(this);

        Intent extrasEnviados = getIntent();
        jugador = extrasEnviados.getStringExtra("NumeroJugador");

    }

    public void Instrucciones(View view) {
        com.enviar(jugador + ",Pantalla,Instrucciones");
    }//cierra el metodo instrucciones

    public void Juego(View view) {
        com.enviar(jugador + ",Pantalla,Juego");
    }

    @Override
    public void update(Observable o, Object arg) {
        String mensaje = (String) arg;

        if (mensaje.contains("Nivel")) {
            String[] valores = mensaje.split(",");
            String nivel = valores[1];
            if (nivel.equals("Help")) {

                Intent goHelp = new Intent(getApplicationContext(), Help.class);
                goHelp.putExtra("NumeroJugador", jugador);
                startActivity(goHelp);
            }

            if (nivel.equals("Jugar")) {

                Intent goPlay = new Intent(getApplicationContext(), GamePad.class);
                goPlay.putExtra("NumeroJugador", jugador);
                startActivity(goPlay);
            }
        }
    }
}//cierra la clase onCreat
