package com.example.app_cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue; //Es una cola donde van los request que voy haciendo (libreria volley)
    private String[] BufferNames ;      // Almacena el employee_name
    private String[] BufferTension ;    // Almacena el employee_salary
    private String[] BufferCorriente ;  // Almacena el employee_age






    // Defino los buffers, potencia de 2 para mas placer y por la FFT
    private int POW_FREC_SHOW = 10;
    private int POW_TIME_SHOW = 8;
    private int POW_FFT_BUFFER = 16;

    private int BUFFER_SIZE_SHOW_FREQ = (int) Math.pow(2,POW_FREC_SHOW);
    private int BUFFER_SIZE_SHOW_TIME = (int) Math.pow(2,POW_TIME_SHOW);
    private int BUFFER_SIZE = (int) Math.pow(2,POW_FFT_BUFFER);

    // Buffer donde sale el valor crudo
   short[] buffer = new short[BUFFER_SIZE];
//
double[] buffer_double = new double[BUFFER_SIZE];



    // Contador de tiempo de ejecucion
    private double time_exe = 0.0;
    //---------------------------------------------------------------------------------------
    //-------------------------- NATIVE Cpp -------------------------------------------------
    //---------------------------------------------------------------------------------------
    // Cargo la libreria en C nativo "signal_proces-lib.so" (la que hicimos acá)
    /*static
    {
        System.loadLibrary("signal_proces_lib");
    }*/

    // Delcaro las funciones que voy a utilizar de la libreria
    private native String getNativeString(); // Esta funcion esta contenida en la libreria
    private native double[] calcularFFT(short[] input, int elementos);



    Spinner SpinnerNames;               // Spinner con nombres del BufferName
    TextView ViewTension;               // TextView con valores de BufferTension
    TextView ViewCorriente;             // TextView con valores de BufferCorriente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpinnerNames = findViewById( R.id.spinner_name );         // linkea con el id del xml
        ViewTension = findViewById( R.id.tension_datos );
        ViewCorriente = findViewById( R.id.corriente_datos );

        queue = Volley.newRequestQueue(this);   //inicializo la queue

        obtenerDatosVolley();
    }

    private void obtenerDatosVolley(){   //Nuevo metodo con logica para obtener json

        String url = "https://fakejsonapi.com/fake-api/employee/api/v1/employees";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray myJsonArray = response.getJSONArray("data");
                    BufferNames = new String[myJsonArray.length()];                 // Inicializa el array
                    BufferTension = new String[myJsonArray.length()];
                    BufferCorriente = new String[myJsonArray.length()];

                    ArrayAdapter<String> adapter = new ArrayAdapter<>( getApplicationContext(), android.R.layout.simple_spinner_item, BufferNames );    // hace magia
                    adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );       // mas magia por aca
                    SpinnerNames.setAdapter( adapter );     // linkea la magia al spinner
                    SpinnerNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ViewTension.setText( BufferTension[position] );         // Setea el texro al argumento
                            ViewCorriente.setText( BufferCorriente[position] );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // ver si hay algun caso donde no haya nada seleccionado
                        }
                    });

                    for(int i=0; i < myJsonArray.length();i++) {
                        JSONObject myJsonObject = myJsonArray.getJSONObject(i);
                        String name = myJsonObject.getString("employee_name");
                        String age = myJsonObject.getString("employee_age");
                        String salary = myJsonObject.getString("employee_salary");

                        BufferNames[i] = name;
                        BufferTension[i] = salary;
                        BufferCorriente[i] = age;

                        Log.d("Mensaje", name);

                        Toast.makeText(MainActivity.this,"Nombre:"+name,Toast.LENGTH_SHORT).show();

                    }



                } catch (JSONException e) {
                    e.printStackTrace();

                }

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
    private void fft() {   //Nuevo metodo con logica para obtener json

        long startTime = System.currentTimeMillis();

        // Calculo FFT en la libreria de C
        buffer_double = calcularFFT(buffer, BUFFER_SIZE);

        time_exe = System.currentTimeMillis() - startTime;
        // Actualizo el tiempo de calculo

        // obtenemos el modulo y mostramos en el grafico de FFT
        int buffer_mod_count = 0;
        for (int i = 0; i < BUFFER_SIZE_SHOW_FREQ; i++)
        {
            // calculamos el modulo
            double aux_mod = sqrt(buffer_double[buffer_mod_count]*buffer_double[buffer_mod_count] + buffer_double[buffer_mod_count+1]*buffer_double[buffer_mod_count+1]);

            // Adelantamos el index del buffer con un paso grande, submuestreando la salida real
            // asi no colgamos el grafico con muchos puntos.
            buffer_mod_count += 2^(POW_FFT_BUFFER-POW_FREC_SHOW);

        }
    }
}