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
    private int bufferSize=30;
    private String charlsisNumber;
    private String jobId="1",routeId,routeData="{\"$id\":\"1\",\"Message\":\"Sucess\",\"Status\":\"Success\",\"TotalDistance\":0.00884315523,\"Route\":[{\"$id\":\"2\",\"EdgeNo\":\"102\",\"GeometryText\":\"-\",\"Geometry\":{\"$id\":\"3\",\"type\":\"LineString\",\"coordinates\":[[78.571275,17.473804],[78.571132,17.473587],[78.570936,17.473375],[78.570724,17.473250],[78.570370,17.473004],[78.569989,17.472763],[78.569373,17.472311],[78.568690,17.471816],[78.568026,17.471415],[78.566716,17.470434],[78.565718,17.469347],[78.564651,17.468051]]}}]}";
    String SourcePosition="78.571275 17.473804";
    String DestinationPosition="78.564651 17.468051";
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

            // fragmentTransaction.add(R.id.map_container, new MainMapFragment(srcLatitude,srcLongitude,destLatitude,desLongitude,1,bufferSize));//getRoutes Direction
            fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT,SourcePosition,DestinationPosition,routeData,2,bufferSize));//getRoutes Direction
        }else if(charlsisNumber.equals("RD2")) {
          //  Log.e("Route Details------", " Route Details------ " +" srcLatitude : "+ srcLatitude +"\n"+" srcLongitude : "+ srcLongitude +"\n"+" destLatitude : "+destLatitude+"\n"+" desLongitude : "+desLongitude+"\n");
         //   fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT,routeData,2,bufferSize));//getRoutes Direction
        }
        fragmentTransaction.commit();
    }

    @Override
    public String communicate(String comm) {
        return null;
    }

    @Override
    public String communicate(String comm, int alertType) {
        Log.d("received", " Recieved From ETA Listener---"+ comm + "alert type "+ alertType);

       //  tv=(TextView)findViewById(R.id.tv);
       //  tv.setText(comm);

        //tv1=(TextView)findViewById(R.id.text1);
      //  tv1.setText(comm);
        return comm;
    }
    public void onResume() {
        super.onResume();
    }

}