package com.nsg.dtlibrary;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;


//import com.nsg.nsgdtlibrary.Classes.OldCode.NSGIMainFragment;
import com.nsg.nsgdtlibrary.Classes.util.NSGTiledLayerOnMap;

import java.io.File;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by sailaja.ch NSGI on 27/09/2019
 */
public class NSGApiActivity extends FragmentActivity implements NSGTiledLayerOnMap.FragmentToActivity {
    //implements HomeFragment.FragmentToActivity{
    private double srcLatitude;
    private double srcLongitude;
    private double destLatitude;
    private double desLongitude;
    private int enteredMode;
    private int bufferSize=30;
    private String charlsisNumber;
    private String jobId="1",routeId;
    String SourcePosition="";
    String DestinationPosition="";
    private TextView tv;
    String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "DubaiBasemap"+".mbtiles";
    String CSVFile_Path= Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "RouteSample"+".txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);

        Bundle NSGIBundle = getIntent().getExtras();
        charlsisNumber = NSGIBundle.getString("charlsisNumber");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         if(charlsisNumber.equals("RD1")) {
             double srcLatitude=55.067291;
             double srcLongitude=24.978782;
             double destLatitude=55.067205;
             double desLongitude=24.979878;
            routeId="RD1";
            enteredMode = NSGIBundle.getInt("enteredMode");
            bufferSize = NSGIBundle.getInt("bufferSize");
            // fragmentTransaction.add(R.id.map_container, new MainMapFragment(srcLatitude,srcLongitude,destLatitude,desLongitude,1,bufferSize));//getRoutes Direction
            fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT,CSVFile_Path,jobId,routeId,2,bufferSize));//getRoutes Direction
        }else if(charlsisNumber.equals("RD2")) {
            routeId="RD2";
            enteredMode = NSGIBundle.getInt("enteredMode");
            bufferSize = NSGIBundle.getInt("bufferSize");
          //  Log.e("Route Details------", " Route Details------ " +" srcLatitude : "+ srcLatitude +"\n"+" srcLongitude : "+ srcLongitude +"\n"+" destLatitude : "+destLatitude+"\n"+" desLongitude : "+desLongitude+"\n");
            fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT,CSVFile_Path,jobId,routeId,3,bufferSize));//getRoutes Direction
        }
        fragmentTransaction.commit();
    }

    @Override
    public String communicate(String comm) {
        Log.d("received", "Recieved From ETA Listener---"+ comm);
         tv=(TextView)findViewById(R.id.tv);
         tv.setText(comm);

        //tv1=(TextView)findViewById(R.id.text1);
      //  tv1.setText(comm);
        return comm;
    }
    public void onResume() {
        super.onResume();
    }

}