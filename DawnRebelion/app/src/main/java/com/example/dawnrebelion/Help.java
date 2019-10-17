package com.example.dawnrebelion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

public class Help extends AppCompatActivity implements Observer {

    private Comunicacion com;
    private String jugador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        com = Comunicacion.instancia();
        com.addObserver(this);

        Intent extrasEnviados = getIntent();
        jugador = extrasEnviados.getStringExtra("NumeroJugador");
    }

    public void goBack(View view) {

        com.enviar(jugador + ",Pantalla,Main");
    }

    @Override
    public void update(Observable o, Object arg) {
        String mensaje = (String) arg;
        if (mensaje.contains("Nivel")) {
            String[] valores = mensaje.split(",");
            String nivel = valores[1];
            if (nivel.equals("Main")) {

                finish();
            }

        }

    }
}
