package com.example.app_cliente;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.navigation.NavigationView;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private RequestQueue queue;         //Es una cola donde van los request que voy haciendo (libreria volley)

    private int[] arrayTension;      //Aca deberiamos usar number o float
    private int[] arrayCorriente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ConsumoFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_charge);
        }

        queue = Volley.newRequestQueue(this);   //inicializo la queue

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                obtenerDatosVolley();
            }
        }, 0, 300);//put here time 1000 milliseconds = 1 second
    }

    private void obtenerDatosVolley(){   //Nuevo metodo con logica para obtener json
        String url = "http://192.168.1.239";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                int i = 0;

                try {
                    JSONArray myJsonArray = response.getJSONArray("data");
                    int jsonLength = myJsonArray.length();
                    arrayCorriente = new int[jsonLength];
                    arrayTension = new int[jsonLength];

                    for( i = 0; i < myJsonArray.length(); i++ ) {
                        JSONObject myJsonObject = myJsonArray.getJSONObject(i);
                        int tension = myJsonObject.getInt("tension");
                        int corriente = myJsonObject.getInt("corriente");   // Aca deberiamos usar number o float

                        arrayTension[i] = tension;
                        arrayCorriente[i] = corriente;
                    }
                    //actualizarDisplay();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i=0;
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Mensaje", String.valueOf(error));
                    }
                });
        queue.add(request); // Esto es del volley
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_charge:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ConsumoFragment()).commit();
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}