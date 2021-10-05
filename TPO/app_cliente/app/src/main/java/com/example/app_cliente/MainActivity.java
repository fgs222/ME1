package com.example.app_cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;         //Es una cola donde van los request que voy haciendo (libreria volley)

    private Operations operations = new Operations();

    private Buffer bufferNames = new Buffer();       // Almacena el employee_name
    private Buffer bufferTension = new Buffer();     // Almacena el employee_salary
    private Buffer bufferCorriente = new Buffer();   // Almacena el employee_age

    private TextView textVef;
    private TextView textIef;
    private TextView textPot;

    //-----------------Interfaz-----------------//
    Button showPlotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textVef = (TextView) findViewById(R.id.displayVef);
        textIef = (TextView) findViewById(R.id.displayIef);
        //textPot = (TextView) findViewById(R.id.displayPot);

        queue = Volley.newRequestQueue(this);   //inicializo la queue

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                obtenerDatosVolley();
                actualizarDisplay();
            }
        }, 0, 500);//put here time 500 milliseconds = 0.5 second


        showPlotButton = (Button)findViewById(R.id.button_plot);
        showPlotButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Intents are objects of the android.content.Intent type. Your code can send them
                // to the Android system defining the components you are targeting.
                // Intent to start an activity called SecondActivity with the following code:

                Intent intent = new Intent(MainActivity.this, PlotActivity.class);

                // start the activity connect to the specified class
                startActivity(intent);
            }
        });
    }

    private void obtenerDatosVolley(){   //Nuevo metodo con logica para obtener json

        String url = "https://fakejsonapi.com/fake-api/employee/api/v1/employees";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray myJsonArray = response.getJSONArray("data");

                    initializeBuffers( myJsonArray.length() );

                    for( int i = 0; i < myJsonArray.length(); i++ ) {
                        //JSONObject myJsonObject = myJsonArray.getJSONObject(i);
                        //String name = myJsonObject.getString("employee_name");
                        //String age = myJsonObject.getString("employee_age");
                        //String salary = myJsonObject.getString("employee_salary");
                        bufferCorriente.SetBufferValue(i, 1);
                        bufferTension.SetBufferValue(i, 1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //mathematicalOperations();
            }
        },
            new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Mensaje", String.valueOf(error));
            }
        });
        queue.add(request);
    }

    private void actualizarDisplay() {
        mathematicalOperations();

        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                TextView textPot= (TextView) findViewById(R.id.displayPot);
                textPot.setText( Double.toString( operations.activePowerValue() ) + " W" );
            }
        });

        //textVef.setText( Double.toString( operations.getVoltageRmsValue() ) );
        //textIef.setText( Double.toString( operations.getCurrentRmsValue() ) );
        //textPot.setText( Double.toString( operations.activePowerValue() ) );
    }

    private void initializeBuffers( int bufferSize ){
        bufferNames.SetBuffer( bufferSize );
        bufferTension.SetBuffer( bufferSize );
        bufferCorriente.SetBuffer( bufferSize );
    }

    private void mathematicalOperations(){
        operations.setCurrentMeanValue(operations.meanValue(bufferCorriente.asInt()));
        operations.setVoltageMeanValue(operations.meanValue(bufferTension.asInt()));
        operations.setCurrentRmsValue(operations.rmsValue(bufferCorriente.asInt()));
        operations.setVoltageRmsValue(operations.rmsValue(bufferTension.asInt()));

        Log.d("Potencia activa", operations.activePowerValue() +"W");

        Log.d("Potencia aparente", operations.apparentPower(bufferTension.asInt(), bufferCorriente.asInt()) +"VA");
    }
}