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

    private String jobId="1",routeId,routeData="{\n" +
            "    \"$id\": \"1\",\n" +
            "    \"Message\": \"Sucess\",\n" +
            "    \"Status\": \"Success\",\n" +
            "    \"TotalDistance\": 0.00884315523,\n" +
            "    \"Route\": [\n" +
            "        {\n" +
            "            \"$id\": \"2\",\n" +
            "            \"EdgeNo\": \"102\",\n" +
            "            \"GeometryText\": \"Take Left at Shell Trading Middle East Private Limited\",\n" +
            "            \"Geometry\": {\n" +
            "                \"$id\": \"3\",\n" +
            "                \"type\": \"LineString\",\n" +
            "                \"coordinates\": [\n" +
            "                    [\n" +
            "                        55.06727997182,\n" +
            "                        24.9787947412557\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.067020892000073,\n" +
            "                        24.978570495000042\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066790925000078,\n" +
            "                        24.978370131000077\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066620030000081,\n" +
            "                        24.978221328000075\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.06650374700007,\n" +
            "                        24.97812037500006\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066452143000049,\n" +
            "                        24.978075252000053\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066388841000048,\n" +
            "                        24.978020054000069\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066216137000083,\n" +
            "                        24.977870199000051\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.06598632500004,\n" +
            "                        24.97767018400009\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065755946000081,\n" +
            "                        24.977470103000087\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065526233000071,\n" +
            "                        24.977270178000083\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065312867000046,\n" +
            "                        24.977084458000036\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"$id\": \"4\",\n" +
            "            \"EdgeNo\": \"1334\",\n" +
            "            \"GeometryText\": \"Take Right at\",\n" +
            "            \"Geometry\": {\n" +
            "                \"$id\": \"5\",\n" +
            "                \"type\": \"LineString\",\n" +
            "                \"coordinates\": [\n" +
            "                    [\n" +
            "                        55.065312867000046,\n" +
            "                        24.977084458000036\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065287629000068,\n" +
            "                        24.977076221000061\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065261227000065,\n" +
            "                        24.97707199000007\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065234420000081,\n" +
            "                        24.97707188600009\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065207979000036,\n" +
            "                        24.977075912000089\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065182665000066,\n" +
            "                        24.97708395300009\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065159206000033,\n" +
            "                        24.977095778000091\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065138276000084,\n" +
            "                        24.977111045000072\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"$id\": \"6\",\n" +
            "            \"EdgeNo\": \"369\",\n" +
            "            \"GeometryText\": \"Take Right at\",\n" +
            "            \"Geometry\": {\n" +
            "                \"$id\": \"7\",\n" +
            "                \"type\": \"LineString\",\n" +
            "                \"coordinates\": [\n" +
            "                    [\n" +
            "                        55.065138276000084,\n" +
            "                        24.977111045000072\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065120166000042,\n" +
            "                        24.977128114000038\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064756250000073,\n" +
            "                        24.977475793000053\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064379641000073,\n" +
            "                        24.977835331000051\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064249201000052,\n" +
            "                        24.977960644000063\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"$id\": \"8\",\n" +
            "            \"EdgeNo\": \"383\",\n" +
            "            \"GeometryText\": \"Take Right at\",\n" +
            "            \"Geometry\": {\n" +
            "                \"$id\": \"9\",\n" +
            "                \"type\": \"LineString\",\n" +
            "                \"coordinates\": [\n" +
            "                    [\n" +
            "                        55.064249201000052,\n" +
            "                        24.977960644000063\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064238539000087,\n" +
            "                        24.977972603000069\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064230288000033,\n" +
            "                        24.977986052000062\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064224693000085,\n" +
            "                        24.978000592000058\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064221918000044,\n" +
            "                        24.978015793000054\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064222048000033,\n" +
            "                        24.978031201000078\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064222048000033,\n" +
            "                        24.978031201000078\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064387059000069,\n" +
            "                        24.978174369000044\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064439134000054,\n" +
            "                        24.978219639000088\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064439134000054,\n" +
            "                        24.978219639000088\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064525820000085,\n" +
            "                        24.978294996000045\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064525820000085,\n" +
            "                        24.978294996000045\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.064649532000033,\n" +
            "                        24.978402540000047\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.06498055600008,\n" +
            "                        24.978690915000072\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.06498055600008,\n" +
            "                        24.978690915000072\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065164137000068,\n" +
            "                        24.978850842000043\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065338824000037,\n" +
            "                        24.979002188000038\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065338824000037,\n" +
            "                        24.979002188000038\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065422408000074,\n" +
            "                        24.979074604000061\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065573362000066,\n" +
            "                        24.979205705000084\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065573362000066,\n" +
            "                        24.979205705000084\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065666012000065,\n" +
            "                        24.979286171000069\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065666012000065,\n" +
            "                        24.979286171000069\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065681098000084,\n" +
            "                        24.979299272000048\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.065938324000058,\n" +
            "                        24.979522600000053\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066002768000033,\n" +
            "                        24.979578645000061\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066002768000033,\n" +
            "                        24.979578645000061\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066081442000041,\n" +
            "                        24.979647065000051\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066081442000041,\n" +
            "                        24.979647065000051\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066110416000072,\n" +
            "                        24.979672262000065\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066245676000051,\n" +
            "                        24.979789959000072\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066245676000051,\n" +
            "                        24.979789959000072\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.06634370900008,\n" +
            "                        24.979875263000054\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.06634370900008,\n" +
            "                        24.979875263000054\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066752725000072,\n" +
            "                        24.980231166000067\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066752725000072,\n" +
            "                        24.980231166000067\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066772902000082,\n" +
            "                        24.980240215000038\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066794299000037,\n" +
            "                        24.98024651500009\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066816470000049,\n" +
            "                        24.980249936000064\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066838951000079,\n" +
            "                        24.980250405000049\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.066861270000061,\n" +
            "                        24.980247913000085\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"$id\": \"10\",\n" +
            "            \"EdgeNo\": \"443\",\n" +
            "            \"GeometryText\": \"-\",\n" +
            "            \"Geometry\": {\n" +
            "                \"$id\": \"11\",\n" +
            "                \"type\": \"LineString\",\n" +
            "                \"coordinates\": [\n" +
            "                    [\n" +
            "                        55.066861270000061,\n" +
            "                        24.980247913000085\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        55.0672260238388,\n" +
            "                        24.9799000715094\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";
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

            // fragmentTransaction.add(R.id.map_container, new MainMapFragment(srcLatitude,srcLongitude,destLatitude,desLongitude,1,bufferSize));//getRoutes Direction
            fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT,routeData,2,bufferSize));//getRoutes Direction
        }else if(charlsisNumber.equals("RD2")) {
          //  Log.e("Route Details------", " Route Details------ " +" srcLatitude : "+ srcLatitude +"\n"+" srcLongitude : "+ srcLongitude +"\n"+" destLatitude : "+destLatitude+"\n"+" desLongitude : "+desLongitude+"\n");
            fragmentTransaction.add(R.id.map_container, new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT,routeData,2,bufferSize));//getRoutes Direction
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