package com.nsg.dtlibrary;

import android.os.Bundle;
import android.os.Environment;


import com.nsg.nsgdtlibrary.Classes.activities.NSGIMainFragment;

import java.io.File;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by sailaja.ch NSGI on 27/09/2019 *
 * Modified on 14/11/2019
 *
 */
public class MainActivity extends FragmentActivity implements NSGIMainFragment.FragmentToActivity{
    private String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "DubaiBasemap" + ".mbtiles";
    private String CSVFile_Path= Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "RouteSample"+".csv";
    private double srcLatitude=55.067291;
    private double srcLongitude=24.978782;
    private double destLatitude=55.067205;
    private double desLongitude=24.979878;
    private String JOBID="";
    private String routeIDName="RD1";
    private int enteredMode=1;
    private int bufferSize=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map_container, new NSGIMainFragment(BASE_MAP_URL_FORMAT,CSVFile_Path,JOBID,routeIDName,enteredMode,bufferSize));
        fragmentTransaction.commit();
    }
    @Override
    public String communicate(String comm) {
        return null;
    }
}

