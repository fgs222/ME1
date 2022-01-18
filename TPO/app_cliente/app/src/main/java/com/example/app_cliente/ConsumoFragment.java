package com.example.app_cliente;

import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ConsumoFragment extends Fragment {

    private TextView textVef;
    private TextView textIef;
    private TextView textPot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handleObservers()
        return inflater.inflate(R.layout.fragment_consumo, container, false);
    }


}
