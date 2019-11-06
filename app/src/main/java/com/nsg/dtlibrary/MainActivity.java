package com.nsg.dtlibrary;

import android.os.Bundle;

import com.nsg.nsgdtlibrary.Classes.NSGTiledLayerOnMap;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap());
        fragmentTransaction.commit();
    }
}

