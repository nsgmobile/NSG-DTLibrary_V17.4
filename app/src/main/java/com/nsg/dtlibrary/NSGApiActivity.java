package com.nsg.dtlibrary;

import android.os.Bundle;
import android.os.Environment;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


//import com.nsg.nsgdtlibrary.Classes.OldCode.NSGIMainFragment;
import com.nsg.nsgdtlibrary.Classes.util.NSGIMapFragmentActivity;
import com.nsg.nsgdtlibrary.Classes.util.NSGINavigationFragment;
import com.nsg.nsgdtlibrary.Classes.util.NavigationProperties;

import java.io.File;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by sailaja.ch NSGI on 27/09/2019
 */
public class NSGApiActivity extends FragmentActivity implements NSGIMapFragmentActivity.FragmentToActivity, View.OnClickListener {
    private int bufferSize=10;
    private String charlsisNumber;
    private  Button Start,Stop;
    private String jobId="1",routeId;
    private String SourcePosition = "55.067291 24.978782";
    private String DestinationPosition = "55.067205 24.979878";
    private String GeoFenceCordinates="24.979977 55.067160,\n" +
            "24.979958 55.067257,\n" +
            "24.979864 55.067257,\n" +
            "24.979831 55.067185,\n" +
            "24.979895 55.067124,\n" +
            "24.979979 55.067136";
    String routeData="\n" +
            "\n" +
            "{\"$id\":\"1\",\"Message\":\"Sucess\",\"Status\":\"Success\",\"TotalDistance\":0.00884315523,\"Route\":[{\"$id\":\"2\",\"EdgeNo\":\"102\",\"GeometryText\":\"Take Right \n" +
            "at Shell Trading Middle East Private Limited\",\"Geometry\":{\"$id\":\"3\",\"type\":\"LineString\",\"coordinates\":[[55.06727997182,24.9787947412557],\n" +
            "[55.067020892000073,24.978570495000042],[55.066790925000078,24.978370131000077],[55.066620030000081,24.978221328000075],\n" +
            "[55.06650374700007,24.97812037500006],[55.066452143000049,24.978075252000053],[55.066388841000048,24.978020054000069],\n" +
            "[55.066216137000083,24.977870199000051],[55.06598632500004,24.97767018400009],[55.065755946000081,24.977470103000087],\n" +
            "[55.065526233000071,24.977270178000083],[55.065312867000046,24.977084458000036]]}},{\"$id\":\"4\",\"EdgeNo\":\"1334\",\"GeometryText\":\"Take Right \n" +
            "at\",\"Geometry\":{\"$id\":\"5\",\"type\":\"LineString\",\"coordinates\":[[55.065312867000046,24.977084458000036],[55.065287629000068,24.977076221000061],\n" +
            "[55.065261227000065,24.97707199000007],[55.065234420000081,24.97707188600009],[55.065207979000036,24.977075912000089],\n" +
            "[55.065182665000066,24.97708395300009],[55.065159206000033,24.977095778000091],[55.065138276000084,24.977111045000072],\n" +
            "[55.065138276000084,24.977111045000072],[55.065120166000042,24.977128114000038],[55.064756250000073,24.977475793000053],\n" +
            "[55.064379641000073,24.977835331000051],[55.064249201000052,24.977960644000063],[55.064249201000052,24.977960644000063],\n" +
            "[55.064238539000087,24.977972603000069],[55.064230288000033,24.977986052000062],[55.064224693000085,24.978000592000058],\n" +
            "[55.064221918000044,24.978015793000054],[55.064222048000033,24.978031201000078],[55.064222048000033,24.978031201000078],\n" +
            "[55.064387059000069,24.978174369000044],[55.064439134000054,24.978219639000088],[55.064439134000054,24.978219639000088],\n" +
            "[55.064525820000085,24.978294996000045],[55.064525820000085,24.978294996000045],[55.064649532000033,24.978402540000047],\n" +
            "[55.06498055600008,24.978690915000072],[55.06498055600008,24.978690915000072],[55.065164137000068,24.978850842000043],\n" +
            "[55.065338824000037,24.979002188000038],[55.065338824000037,24.979002188000038],[55.065422408000074,24.979074604000061],\n" +
            "[55.065573362000066,24.979205705000084],[55.065573362000066,24.979205705000084],[55.065666012000065,24.979286171000069],\n" +
            "[55.065666012000065,24.979286171000069],[55.065681098000084,24.979299272000048],[55.065938324000058,24.979522600000053],\n" +
            "[55.066002768000033,24.979578645000061],[55.066002768000033,24.979578645000061],[55.066081442000041,24.979647065000051],\n" +
            "[55.066081442000041,24.979647065000051],[55.066110416000072,24.979672262000065],[55.066245676000051,24.979789959000072],\n" +
            "[55.066245676000051,24.979789959000072],[55.06634370900008,24.979875263000054],[55.06634370900008,24.979875263000054],\n" +
            "[55.066752725000072,24.980231166000067],[55.066752725000072,24.980231166000067],[55.066772902000082,24.980240215000038],\n" +
            "[55.066794299000037,24.98024651500009],[55.066816470000049,24.980249936000064],[55.066838951000079,24.980250405000049],\n" +
            "[55.066861270000061,24.980247913000085]]}},{\"$id\":\"6\",\"EdgeNo\":\"443\",\"GeometryText\":\"-\",\"Geometry\":\n" +
            "{\"$id\":\"7\",\"type\":\"LineString\",\"coordinates\":[[55.066861270000061,24.980247913000085],[55.0672260238388,24.9799000715094]]}}]}";

    /*

    String SourcePosition="78.570799 17.473538";
    String DestinationPosition="78.555714 17.456068";
    private String routeData="{\n" +
            "    \"$id\": \"1\",\n" +
            "    \"Message\": \"Sucess\",\n" +
            "    \"Status\": \"Success\",\n" +
            "    \"TotalDistance\": 0.0,\n" +
            "    \"Route\": [\n" +
            "        {\n" +
            "            \"$id\": \"2\",\n" +
            "            \"EdgeNo\": \"1310\",\n" +
            "            \"GeometryText\": \"Take Left at Ecil Police Station\",\n" +
            "            \"Geometry\": {\n" +
            "                \"$id\": \"3\",\n" +
            "                \"type\": \"LineString\",\n" +
            "                \"coordinates\": [\n" +
            "                    [\n" +
            "                        78.5709058464531,\n" +
            "                        17.4733904501362\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.570427904479345,\n" +
            "                        17.473044354224118\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"$id\": \"4\",\n" +
            "            \"EdgeNo\": \"1320\",\n" +
            "            \"GeometryText\": \"-\",\n" +
            "            \"Geometry\": {\n" +
            "                \"$id\": \"5\",\n" +
            "                \"type\": \"LineString\",\n" +
            "                \"coordinates\": [\n" +
            "                    [\n" +
            "                        78.570427904479345,\n" +
            "                        17.473044354224118\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.57042107957173,\n" +
            "                        17.473039412049637\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.5700382441982,\n" +
            "                        17.472762186434323\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.569268087486776,\n" +
            "                        17.472259017382854\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.568672499629926,\n" +
            "                        17.47185853589291\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.568066643016934,\n" +
            "                        17.471540204452182\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.56765589277083,\n" +
            "                        17.471262948036067\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.5669062735717,\n" +
            "                        17.47068789769153\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.566557135862524,\n" +
            "                        17.470400372519261\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.566279879446412,\n" +
            "                        17.470102578590843\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.565489185222674,\n" +
            "                        17.469157853024818\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.564934672390436,\n" +
            "                        17.468408233825691\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.564544459656645,\n" +
            "                        17.467925602286527\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.564318547021287,\n" +
            "                        17.467586733333498\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.564041290605175,\n" +
            "                        17.467217058112009\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.563825646725974,\n" +
            "                        17.467052758013569\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.563199252600683,\n" +
            "                        17.466580395230558\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.562819308623034,\n" +
            "                        17.466200451252917\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.562521514694623,\n" +
            "                        17.46578970100682\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.5623264083277,\n" +
            "                        17.465440563297634\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.562213452010027,\n" +
            "                        17.465153038125365\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.562182645741572,\n" +
            "                        17.464916856733861\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.562131301960818,\n" +
            "                        17.4646704065862\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.5620696894239,\n" +
            "                        17.464311000120865\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.562018345643139,\n" +
            "                        17.464156968778578\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.561956733106229,\n" +
            "                        17.464013206192444\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.561700014202415,\n" +
            "                        17.463684605995567\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.561289263956311,\n" +
            "                        17.463263586993317\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.561063351320968,\n" +
            "                        17.46302740560181\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.560806632417155,\n" +
            "                        17.462770686698\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.56050883848873,\n" +
            "                        17.462626924111866\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.560190507048,\n" +
            "                        17.462401011476512\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.5599132506319,\n" +
            "                        17.462154561328852\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.559420350336552,\n" +
            "                        17.461918379937355\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.558927450041239,\n" +
            "                        17.46164112352124\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.5585885810882,\n" +
            "                        17.461507629691258\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        78.558383205965157,\n" +
            "                        17.46146655466665\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";

     */
/**

     private String SourcePosition = "55.33279 25.26886";
     private String DestinationPosition = "55.3327 25.27078";
   // 25.26886,55.33279   25.27078,55.3327
    String routeData="{\"$id\": \"1\",\"Message\": \"Sucess\",\"Status\": \"Success\",\"TotalDistance\": 0.04324998409,\"Route\": [{\"$id\": \"114\",\"EdgeNo\": \"1817\",\"GeometryText\": \"-\",\"Geometry\": {\n"+
          "\"$id\": \"115\",\"type\": \"LineString\",\"coordinates\": [[55.33279,25.26886],[55.33314,25.26797],[55.33249,25.26771 ],[55.33196,25.26755],[55.33147,25.26733 ],[55.33089,25.26717],[55.33063,25.2672],[55.33064,25.26776],[55.33072,25.26849],[55.33071,25.26907],[55.33064,25.26961],[55.33066,25.27013 ],[55.33098,25.2704],[55.33156,25.27057],[55.33225,25.27069],[55.3327,25.27078]]}}]}";

    private String  routeData="{\"$id\":\"1\",\"Message\":\"Sucess\",\"Status\":\"Success\",\"TotalDistance\":0.00884315523,\"Route\":[{\"$id\":\"2\",\"EdgeNo\":\"102\",\"GeometryText\":\"-\",\"Geometry\":{\"$id\":\"3\",\"type\":\"LineString\",\"coordinates\":[[78.571275,17.473804],[78.571132,17.473587],[78.570936,17.473375],[78.570724,17.473250],[78.570370,17.473004],[78.569989,17.472763],[78.569373,17.472311],[78.568690,17.471816],[78.568026,17.471415],[78.566716,17.470434],[78.565718,17.469347],[78.564651,17.468051]]}}]}";
    String SourcePosition="78.571275 17.473804";
    String DestinationPosition="78.564651 17.468051";
*/
    private TextView tv;
    private String routeDeviatedDT_URL="http://202.53.11.74/dtnavigation/api/routing/routenavigate";
    String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "DubaiBasemap"+".mbtiles";
    private String AuthorisationKey="\n" +
            "b3TIz98wORn6daqmthiEu48TAW1ZEQjPuRLapxJPV6HJQiJtO9LsOErPexmDhbZtD76U2AbJ+jXarYr3gAqkkcTQdIZD2yB0yS0HxRBNZ0ZlzbqtIT8INzSTINlwuSvCMGHdvxLqGdNOjixagtRXuQ==";
    String CSVFile_Path= Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "RouteSample"+".txt";
  //  com.nsg.dtlibrary.NavigationProperties properties=new com.nsg.dtlibrary.NavigationProperties();

    NSGIMapFragmentActivity test = new NSGIMapFragmentActivity(BASE_MAP_URL_FORMAT,SourcePosition,DestinationPosition,routeData,bufferSize,routeDeviatedDT_URL,AuthorisationKey);
  // NSGTiledLayerOnMap test = new NSGTiledLayerOnMap(BASE_MAP_URL_FORMAT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_map);
        Start =(Button)findViewById(R.id.start);
        Start.setOnClickListener(this);
        Stop=(Button)findViewById(R.id.stop);
        Stop.setOnClickListener(this);


        Bundle NSGIBundle = getIntent().getExtras();
        charlsisNumber = NSGIBundle.getString("charlsisNumber");

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         if(charlsisNumber.equals("RD1")) {
              fragmentTransaction.add(R.id.map_container, test);
         }else if(charlsisNumber.equals("RD2")) {

        }
        fragmentTransaction.commit();

    }

   // @Override
  //  public String communicate(String comm) {
   //     return null;
  //  }

    @Override
    public String communicate(String comm, int alertType) {
        Log.d("received", " Recieved From ETA Listener---"+ comm + "alert type "+ alertType);
        if(alertType==MapEvents.ALERTTYPE_6){
            // if alert recieved you can start navigation here
           // test.startNavigation();
           // Log.e("Started","Started "+test.startNavigation());
        }else if(alertType==MapEvents.ALERTTYPE_1){

        }else if(alertType==MapEvents.ALERTTYPE_2){

        }else if(alertType==MapEvents.ALERTTYPE_3){

        }else if(alertType==MapEvents.ALERTTYPE_4){

        }else if(alertType==MapEvents.ALERTTYPE_5){

        }
        return comm;
    }
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
       if(v==Start){
           test.startNavigation();
           Log.e("Started","Started "+test.startNavigation());
       }else if(v==Stop){
           test.stopNavigation();
           Log.e("Stopped","Stopped "+test.stopNavigation());
       }
    }
}