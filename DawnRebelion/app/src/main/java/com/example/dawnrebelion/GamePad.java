package com.example.dawnrebelion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class GamePad extends AppCompatActivity implements Observer {

    private Comunicacion com;
    private String jugador;
    private boolean left, right, jump, crouch;

    private ImageView lbtn, rbtn, ubtn, dbtn;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pad);

        com = Comunicacion.instancia();
        com.addObserver(this);

        Intent extrasEnviados = getIntent();
        jugador = extrasEnviados.getStringExtra("NumeroJugador");

        lbtn = findViewById(R.id.left);
        rbtn = findViewById(R.id.right);
        ubtn = findViewById(R.id.up);
        dbtn = findViewById(R.id.down);


        lbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    left = true;
                }


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    left = false;

                }
                com.enviar(jugador + ",left," + left);

                return true;
            }
        }); //cierra el listener de lbtn

        rbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    right = true;
                }


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    right = false;

                }
                com.enviar(jugador + ",right," + right);

                return true;
            }
        }); //cierra el listener de lbtn

        ubtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    jump = true;
                }


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    jump = false;

                }
                com.enviar(jugador + ",jump," + jump);

                return true;
            }
        }); //cierra el listener de lbtn

        dbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    crouch = true;
                }


                if (event.getAction() == MotionEvent.ACTION_UP) {
                    crouch = false;

                }
                com.enviar(jugador + ",crouch," + crouch);

                return true;
            }
        }); //cierra el listener de lbtn


    }//cierra onCreate


    @Override
    public void update(Observable o, Object arg) {
        String mensaje = (String) arg;
    }//cierra el update
}//cierra la clase gamepad
