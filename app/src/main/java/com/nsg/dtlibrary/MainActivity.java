package com.nsg.dtlibrary;

import android.os.Bundle;
import android.os.Environment;

import com.nsg.nsgdtlibrary.Classes.NSGTiledLayerOnMap;

import java.io.File;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
    private String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator +"DubaiBasemap"+".mbtiles";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT));
        fragmentTransaction.commit();
    }
}

