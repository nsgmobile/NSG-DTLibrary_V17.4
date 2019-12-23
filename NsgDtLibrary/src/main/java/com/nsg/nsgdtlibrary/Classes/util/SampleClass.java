package com.nsg.nsgdtlibrary.Classes.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.SphericalUtil;
import com.nsg.nsgdtlibrary.Classes.activities.ExpandedMBTilesTileProvider;
import com.nsg.nsgdtlibrary.Classes.database.db.SqlHandler;
import com.nsg.nsgdtlibrary.Classes.database.dto.EdgeDataT;
import com.nsg.nsgdtlibrary.Classes.database.dto.GeometryT;
import com.nsg.nsgdtlibrary.Classes.database.dto.RouteT;
import com.nsg.nsgdtlibrary.Classes.repository.NSGIMainFragment;
import com.nsg.nsgdtlibrary.Classes.util.DecimalUtils;
import com.nsg.nsgdtlibrary.Classes.util.ETACalclator;
import com.nsg.nsgdtlibrary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.Context.SENSOR_SERVICE;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class SampleClass extends Fragment  {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int SENSOR_DELAY_NORMAL =50;
    private ProgressDialog dialog;
    private TextToSpeech textToSpeech;
    LatLng SourcePosition, DestinationPosition,OldGPSPosition,PointBeforeRouteDeviation;
    //LatLng convertedSrcPosition,convertedDestinationPoisition;
    double sourceLat, sourceLng, destLat, destLng;
    LatLng dubai;
    String SourcePoint;
    String DestinationPoint,tokenResponse,etaResponse;
    Marker markerSource, markerDestination,mPositionMarker;
    Handler mHandler = new Handler();
    private Polyline mPolyline;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    GoogleMap.CancelableCallback callback;
    private double TotalRouteDeviatedDistanceInMTS;
    private List points;
    private List<LatLng> convertedPoints;
    private LatLng OldGps,nayaGps;
    // LatLng currentGpsPosition,lastKnownLocation;
    StringBuilder sb = new StringBuilder();
    private List LocationPerpedicularPoints=new ArrayList();
    private ArrayList<LatLng> currentLocationList=new ArrayList<LatLng>();
    private Marker sourceMarker,destinationMarker;
    private List<EdgeDataT> edgeDataList;
    private List<GeometryT> geometryRouteDeviatedEdgesData;
    List RouteDeviationConvertedPoints;
    private List<RouteT> RouteDataList;
    private List PreviousGpsList;
    private Handler handler = new Handler();
    // private int index=0;
    // private int next=0;
    private int enteredMode;
    private Marker carMarker;
    private int routeDeviationDistance;
    List<LatLng> LatLngDataArray=new ArrayList<LatLng>();
    private String currentGpsPoint;
    private Polyline line;
    private List polyLines;
    private Circle mCircle;
    private List<LatLng>lastGPSPosition;
    private LatLng nearestPositionPoint;
    // BitmapDescriptor mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon_32);
    Bitmap mMarkerIcon;
    int mIndexCurrentPoint=0;
    private List<LatLng> edgeDataPointsList ;
    Map<String, List> mapOfLists = new HashMap<String, List>();
    private List AllPointsList ;
    HashMap<String,String> AllPointEdgeNo;
    HashMap<String,String> AllPointEdgeDistaces;
    private LatLng newCenterLatLng,PointData;
    private List distanceValuesList;
    private List<LatLng> nearestPointValuesList;
    private Marker gpsMarker;
    private TextView tv,tv1,tv2,tv3,tv4,tv5;
    private String routeIDName;
    LatLng centerFromPoint;
    LatLng point;
    private ImageButton etaListener;
    private ImageButton location_tracking;
    Marker fakeGpsMarker;
    List<Marker> markerlist;
    ArrayList<String> etaList;
    private ArrayList lastDistancesList;
    private double lastDistance;
    // private String geometryText;
    private LocationManager locationManager;
    private Location lastLocation;
    Bitmap tileBitmap;
    // MultiMap multiMap = new MultiValueMap();
    private ImageButton change_map_options;
    String tokenNumber,updaterServiceResponse;
    private long startTime,presentTime,previousTime,TimeDelay;
    private  List<LatLng>listOfLatLng;
    HashMap<LatLng,String>edgeDataPointsListData;
    private String geometryDirectionText="",key="",distanceKey="",geometryDirectionDistance="";
    HashMap<String,String>nearestValuesMap;
    private List<LatLng> OldNearestGpsList;
    private int locationFakeGpsListener=0;
    String GeometryDirectionText="";
    ImageView water_ball;
    private double vehicleSpeed;
    private double maxSpeed=30;
    private boolean isMarkerRotating=false;
    private String BASE_MAP_URL_FORMAT,DBCSV_PATH,jobId;
    private LatLng SourceNode,DestinationNode;
    private SensorManager mSensorManager;
    LatLng currentGpsPosition,RouteDeviatedSourcePosition;
    float azimuthInRadians;
    float azimuthInDegress;
    float degree,lastUpdate;
    private String TotalDistance;
    double TotalDistanceInMTS;
    private List<EdgeDataT> EdgeContainsDataList;
    private double resultNeedToTeavelTimeConverted;
    RouteT route;
    boolean isRouteDeviated=false;
    private Button location_tracking_start,location_tracking_stop;
    StringBuilder time= new StringBuilder();
    public interface FragmentToActivity {
        String communicate(String comm);
    }
    private FragmentToActivity Callback;
    public SampleClass(){ }
    @SuppressLint("ValidFragment")
    public SampleClass(String BASE_MAP_URL_FORMAT, String DBCSV_PATH, String jobId, String routeId, int mode, int radius ) {
        enteredMode = mode;
        routeDeviationDistance=radius;
        SampleClass.this.BASE_MAP_URL_FORMAT = BASE_MAP_URL_FORMAT;
        SampleClass.this.DBCSV_PATH = DBCSV_PATH;
        SampleClass.this.routeIDName=routeId;
        SampleClass.this.jobId=jobId;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sqlHandler = new SqlHandler(getContext());// Sqlite handler
            Callback = (SampleClass.FragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.gps_transperent_98);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        tv = (TextView) rootView.findViewById(R.id.tv);
        tv1 = (TextView) rootView.findViewById(R.id.tv1);
        tv2 = (TextView) rootView.findViewById(R.id.tv2);
        tv3 = (TextView) rootView.findViewById(R.id.tv3);
        location_tracking_start=(Button)rootView.findViewById(R.id.location_tracking_start);
        location_tracking_stop=(Button)rootView.findViewById(R.id.location_tracking_stop);
        // location_tracking=(ImageButton)rootView.findViewById(R.id.location_tracking);
        // location_tracking.setOnClickListener(this);
       // mSensorManager = (SensorManager)getContext().getSystemService(SENSOR_SERVICE);
       // mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
       // mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        checkPermission();
        requestPermission();
        String delQuery = "DELETE  FROM " + RouteT.TABLE_NAME;
        sqlHandler.executeQuery(delQuery);
        InsertAllRouteData(DBCSV_PATH);
        getRouteAccordingToRouteID(routeIDName);
       // change_map_options = (ImageButton)rootView.findViewById(R.id.change_map_options);
      //  change_map_options.setOnClickListener(SampleClass.this);
        if(RouteDataList!=null) {
            route = RouteDataList.get(0);
        }
        final String routeData = route.getRouteData();
        String sourceText=route.getStartNode();
        String[]  text =sourceText.split(" ");
        sourceLat= Double.parseDouble(text[1]);
        sourceLng= Double.parseDouble(text[0]);
        String destinationText=route.getEndNode();
        String[]  text1 =destinationText.split(" ");
        destLat= Double.parseDouble(text1[1]);
        destLng= Double.parseDouble(text1[0]);
        SourceNode=new LatLng(sourceLat,sourceLng);
        DestinationNode=new LatLng(destLat,destLng);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment1 = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googlemap) {
                SampleClass.this.mMap = googlemap;
                SampleClass.this.mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.stle_map_json));
                TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(BASE_MAP_URL_FORMAT.toString()), 256, 256);
                TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
                tileOverlay.setTransparency(0.5f - tileOverlay.getTransparency());
                tileOverlay.setVisible(true);
                if(routeData!=null) {
                    GetRouteFromDBPlotOnMap(routeData);
                    // GetRouteDetails(SourcePosition.toString(),DestinationPosition.toString());
                }
                StringBuilder routeAlert=new StringBuilder();
                // routeAlert.append("src");
              //  sendData(routeAlert.toString());
                // sendTokenRequest();
                getAllEdgesData();
                addMarkers();
                getValidRouteData();
                if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    return;
                }

                getRouteAccordingToRouteID(routeIDName);
                location_tracking_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                                    nearestPointValuesList=new ArrayList<LatLng>();
                                    nearestPointValuesList.add(new LatLng(sourceLat,sourceLng));
                                    OldNearestGpsList=new ArrayList<>();
                                    OldNearestGpsList.add(new LatLng(sourceLat,sourceLng));
                                    mMap.setMyLocationEnabled(true);
                                    mMap.setBuildingsEnabled(true);
                                    mMap.getUiSettings().setZoomControlsEnabled(true);
                                    mMap.getUiSettings().setCompassEnabled(true);
                                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                    mMap.getUiSettings().setMapToolbarEnabled(true);
                                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                                    mMap.getUiSettings().setTiltGesturesEnabled(true);
                                    mMap.getUiSettings().setRotateGesturesEnabled(true);
                                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                     if(enteredMode==2){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                @Override
                                                public void onMyLocationChange(final Location location) {
                                                    if (mPositionMarker != null) {
                                                        mPositionMarker.remove();
                                                    }
                                                    if (currentGpsPosition!=null){
                                                       OldGPSPosition=currentGpsPosition;
                                                    }
                                                       Timer timer=new Timer();
                                                        TimerTask doAsynchronousTask = new TimerTask() {
                                                            @Override
                                                            public void run() {
                                                                handler.post(new Runnable() {
                                                                    public void run() {
                                                                        currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());

                                                                        mPositionMarker = mMap.addMarker(new MarkerOptions()
                                                                                .position(currentGpsPosition)
                                                                                .title("currentLocation")
                                                                                .anchor(0.5f, 0.5f)
                                                                                .rotation(location.bearingTo(location))
                                                                                .flat(true)
                                                                                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));
                                                                        if (OldGPSPosition !=null && currentGpsPosition!=null) {
                                                                            LatLng nPoint = GetNearestPointOnRoadFromGPS(currentGpsPosition);
                                                                            Log.e("Current","CURRENT"+currentGpsPosition +"||"+nPoint);
                                                                            animateMarker(mPositionMarker,OldGPSPosition,nPoint,false);
                                                                            // animateCarMove(mPositionMarker, OldGPSPosition, nPoint, 10);
                                                                        }

                                                                    }
                                                                });
                                                            }
                                                        };
                                                        timer.schedule(doAsynchronousTask, 20000,20000);  //



                                                }
                                            });
                                        }
                                    }
                    }

                });


            }
        });
        return rootView;
    }
    private  List<RouteT> getRouteAccordingToRouteID(String routeIDName) {
        String query = "SELECT * FROM " + RouteT.TABLE_NAME +" WHERE routeID = "+"'"+routeIDName+"'";
        Cursor c1 = sqlHandler.selectQuery(query);
        RouteDataList = (List<RouteT>) SqlHandler.getDataRows(RouteT.MAPPING, RouteT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return RouteDataList;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public LatLng GetNearestPointOnRoadFromGPS(final LatLng currentGpsPosition)    {
        List distancesList=new ArrayList();
        HashMap<String,String>  hash_map=new HashMap<>();
        String FirstCordinate=null,SecondCordinate=null;
        LatLng newGPS=null;
        List<LatLng> EdgeWithoutDuplicates = removeDuplicates(edgeDataPointsList);
        if (EdgeWithoutDuplicates != null && EdgeWithoutDuplicates.size() > 0) {
            for (int epList = 0; epList < EdgeWithoutDuplicates.size(); epList++) {
                LatLng poinOnROAD = EdgeWithoutDuplicates.get(epList);
                double distance = distFrom(poinOnROAD.latitude,poinOnROAD.longitude,currentGpsPosition.longitude,currentGpsPosition.latitude);
                distancesList.add(distance);
                hash_map.put(String.valueOf(distance),poinOnROAD.toString());
                Collections.sort(distancesList);
            }
            String FirstShortestDistance = String.valueOf(distancesList.get(0));
            String SecondShortestDistance = String.valueOf(distancesList.get(1));

            boolean answerFirst= hash_map.containsKey(FirstShortestDistance);
            if (answerFirst) {

                FirstCordinate = (String)hash_map.get(FirstShortestDistance);
                key= String.valueOf(getKeysFromValue(AllPointEdgeNo,FirstCordinate));
                distanceKey= String.valueOf(getKeysFromValue(AllPointEdgeDistaces,FirstCordinate));
            }
            boolean answerSecond= hash_map.containsKey(SecondShortestDistance);
            if (answerSecond) {
                SecondCordinate = (String)hash_map.get(SecondShortestDistance);

            }


            if(FirstCordinate != null && SecondCordinate != null)
            {
                String First= FirstCordinate.replace("lat/lng: (","");
                First = First.replace(")","");

                String[] FirstLatLngsData=First.split(",");

                double FirstLatitude= Double.valueOf(FirstLatLngsData[0]);
                double FirstLongitude= Double.valueOf(FirstLatLngsData[1]);



                String Second= SecondCordinate.replace("lat/lng: (","");
                Second= Second.replace(")","");

                String[] SecondLatLngsData=Second.split(",");

                double SecondLatitude= Double.valueOf(SecondLatLngsData[0]);
                double SecondLongitude= Double.valueOf(SecondLatLngsData[1]);

                LatLng source=new LatLng(FirstLongitude,FirstLatitude);
                LatLng destination=new LatLng(SecondLongitude,SecondLatitude);

                newGPS= findNearestPoint(currentGpsPosition,source,destination);


            }

        }
    return newGPS;
    }










    private  List<EdgeDataT> getAllEdgesData() {
        String query = "SELECT * FROM " + EdgeDataT.TABLE_NAME;
        Cursor c1 = sqlHandler.selectQuery(query);
        edgeDataList = (List<EdgeDataT>) SqlHandler.getDataRows(EdgeDataT.MAPPING, EdgeDataT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return edgeDataList;
    }
    private List<LatLng> removeDuplicates(List<LatLng> EdgeWithoutDuplicates)
    {
        int count = edgeDataPointsList.size();

        for (int i = 0; i < count; i++)
        {
            for (int j = i + 1; j < count; j++)
            {
                if (edgeDataPointsList.get(i).equals(edgeDataPointsList.get(j)))
                {
                    edgeDataPointsList.remove(j--);
                    count--;
                }
            }
        }
        return EdgeWithoutDuplicates;
    }
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = sin(dLat/2) * sin(dLat/2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                        sin(dLng/2) * sin(dLng/2);
        double c = 2 * atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);
        return dist;
    }
    public Set<Object> getKeysFromValue(Map<String, String> map, String key) {
        Set<Object> keys = new HashSet<Object>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //if value != null
            if (entry.getKey().equals(key)){
                keys.add(entry.getValue());
            }
        }
        return keys;
    }

    public void getValidRouteData(){
        if (edgeDataList != null && edgeDataList.size() > 0) {
            edgeDataPointsList = new ArrayList<LatLng>();
            AllPointsList=new ArrayList();
            AllPointEdgeNo=new HashMap<>();
            AllPointEdgeDistaces=new HashMap<>();
            EdgeContainsDataList=new ArrayList<EdgeDataT>();
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT(); //creating object for EDGETABLE
                edge = edgeDataList.get(i);
                int edgeNo = edge.getEdgeNo(); //Edge Number
                String stPoint = edge.getStartPoint(); //Start Point
                String endPoint = edge.getEndPoint();//End Point
                String points = edge.getAllPoints(); // All points in the edge
                String geometryText=edge.getGeometryText();
                String distanceInEdge = edge.getDistanceInVertex();
                TotalDistance =edge.getTotaldistance();
                if(points!=null){
                    String AllPoints = points.replace("[", "");
                    AllPoints = AllPoints.replace("]", "");
                    String[] AllPointsArray = AllPoints.split(", ");
                    for (int ap = 0; ap < AllPointsArray.length; ap++) {

                        String data = String.valueOf(AllPointsArray[ap]);
                        String dataStr = data.replace("[", "");
                        dataStr = dataStr.replace("]", "");
                        String ptData[] = dataStr.split(",");
                        double Lat = Double.parseDouble(ptData[0]);
                        double Lang = Double.parseDouble(ptData[1]);
                        PointData = new LatLng(Lat, Lang);
                        AllPointEdgeNo.put(String.valueOf(PointData),geometryText);
                        AllPointEdgeDistaces.put(String.valueOf(PointData),distanceInEdge);
                        AllPointsList.add(AllPointsArray[ap]);
                        PointData = new LatLng(Lang,Lat);
                        EdgeDataT edgePointData = new EdgeDataT(stPoint,endPoint,String.valueOf(PointData),geometryText,distanceInEdge);
                        EdgeContainsDataList.add(edgePointData);

                    }
                }


                for (int pntCount = 0; pntCount < AllPointsList.size(); pntCount++) {
                    String data = String.valueOf(AllPointsList.get(pntCount));
                    String dataStr = data.replace("[", "");
                    dataStr = dataStr.replace("]", "");
                    String ptData[] = dataStr.split(",");
                    double Lat = Double.parseDouble(ptData[0]);
                    double Lang = Double.parseDouble(ptData[1]);
                    PointData = new LatLng(Lat, Lang);
                    edgeDataPointsList.add(PointData);


                }
            }


            for(int k=0;k<EdgeContainsDataList.size();k++){
                EdgeDataT edgeK=EdgeContainsDataList.get(k);
                StringBuilder sb=new StringBuilder();
                sb.append("STPOINT :"+edgeK.getStartPoint()+"EndPt:"+edgeK.getEndPoint()+"Points:"+edgeK.getPositionMarkingPoint()+"Geometry TEXT:"+ edgeK.getGeometryText());
            }

        }

    }

    private LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end) {
        if (start.equals(end)) {
            return start;
        }
        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }

        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));
    }

    public void addMarkers(){
        sourceMarker = mMap.addMarker(new MarkerOptions()
                .position(SourceNode)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.source_red)));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(SourceNode)
                .zoom(18)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

        destinationMarker= mMap.addMarker(new MarkerOptions()
                .position(DestinationNode)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.destination_green)));
        CameraPosition googlePlex1 = CameraPosition.builder()
                .target(DestinationNode)
                .zoom(18)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex1), 1000, null);
    }
    public void GetRouteFromDBPlotOnMap(String FeatureResponse){
        JSONObject jsonObject = null;
        try {
            if(FeatureResponse!=null){
                jsonObject = new JSONObject(FeatureResponse);
                String ID = String.valueOf(jsonObject.get("$id"));
                String Status = jsonObject.getString("Status");
                double TotalDistance = jsonObject.getDouble("TotalDistance");
                TotalDistanceInMTS= TotalDistance*100000;
                JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                PolylineOptions polylineOptions = new PolylineOptions();
                Polyline polyline = null;
                convertedPoints=new ArrayList<LatLng>();
                for (int i = 0; i < jSonRoutes.length(); i++) {
                    points=new ArrayList();
                    JSONObject Routes = new JSONObject(jSonRoutes.get(i).toString());
                    String $id = Routes.getString("$id");
                    String EdgeNo = Routes.getString("EdgeNo");
                    String GeometryText = Routes.getString("GeometryText");
                    String Geometry = Routes.getString("Geometry");
                    JSONObject geometryObject = new JSONObject(Routes.getString("Geometry"));
                    String $id1 = geometryObject.getString("$id");
                    String type = geometryObject.getString("type");
                    String coordinates = geometryObject.getString("coordinates");
                    JSONArray jSonLegs = new JSONArray(geometryObject.getString("coordinates"));
                    for (int j = 0; j < jSonLegs.length(); j++) {
                        points.add(jSonLegs.get(j));
                    }
                    String  stPoint=String.valueOf(jSonLegs.get(0));
                    String  endPoint=String.valueOf(jSonLegs.get(jSonLegs.length()-1));

                    stPoint=stPoint.replace("[","");
                    stPoint=stPoint.replace("]","");
                    String [] firstPoint=stPoint.split(",");
                    Double stPointLat= Double.valueOf(firstPoint[0]);
                    Double stPointLongi= Double.valueOf(firstPoint[1]);
                    LatLng stVertex=new LatLng(stPointLongi,stPointLat);

                    endPoint=endPoint.replace("[","");
                    endPoint=endPoint.replace("]","");
                    String [] secondPoint=endPoint.split(",");
                    Double endPointLat= Double.valueOf(secondPoint[0]);
                    Double endPointLongi= Double.valueOf(secondPoint[1]);
                    LatLng endVertex=new LatLng(endPointLongi,endPointLat);

                    double distance=showDistance(stVertex,endVertex);
                    String distanceInKM = String.valueOf(distance/1000);
                    StringBuilder query = new StringBuilder("INSERT INTO ");
                    query.append(EdgeDataT.TABLE_NAME).append("(edgeNo,distanceInVertex,startPoint,allPoints,geometryText,endPoint) values (")
                            .append("'").append(EdgeNo).append("',")
                            .append("'").append(distanceInKM).append("',")
                            // .append("'").append(String.valueOf(TotalDistanceInMTS)).append("',")
                            .append("'").append(jSonLegs.get(0)).append("',")
                            .append("'").append(points).append("',")
                            .append("'").append(GeometryText).append("',")
                            .append("'").append(jSonLegs.get(jSonLegs.length()-1)).append("')");
                    sqlHandler.executeQuery(query.toString());
                    sqlHandler.closeDataBaseConnection();
                    for (int p = 0; p < points.size(); p++) {
                        String listItem = points.get(p).toString();
                        listItem = listItem.replace("[", "");
                        listItem = listItem.replace("]", "");
                        String[] subListItem = listItem.split(",");
                        Double y = Double.valueOf(subListItem[0]);
                        Double x = Double.valueOf(subListItem[1]);
                        StringBuilder sb=new StringBuilder();
                        LatLng latLng = new LatLng(x, y);
                        convertedPoints.add(latLng);
                    }
                    Log.e("convertedPoints", " convertedPoints------ " +  convertedPoints.size());
                    MarkerOptions markerOptions = new MarkerOptions();
                    for (int k = 0; k < convertedPoints.size(); k++) {
                        if(polylineOptions!=null && mMap!=null) {
                            markerOptions.position(convertedPoints.get(k));
                            markerOptions.title("Position");
                        }
                    }
                }
                polylineOptions.addAll(convertedPoints);
                polyline = mMap.addPolyline(polylineOptions);
                polylineOptions.color(Color.CYAN).width(30);
                mMap.addPolyline(polylineOptions);
                polyline.setJointType(JointType.ROUND);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(10, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    private double showDistance(LatLng latlng1, LatLng latLng2) {
        double distance = SphericalUtil.computeDistanceBetween(latlng1,latLng2);
        return distance;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    List resultList=new ArrayList();
    public void InsertAllRouteData(String DBCSV_PATH){
        File file = new File(DBCSV_PATH);
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        Log.e("OUTPUT FILE","OUTPUT FILE"+file);
        if (file.exists())
        {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String csvLine;
                while ((csvLine = br.readLine()) != null)
                {
                    String[] data=csvLine.split("@");
                    try
                    {
                        Toast.makeText(getContext(),data[0]+" "+data[1],Toast.LENGTH_SHORT).show();
                        Log.e("OUTPUT FILE","OUTPUT FILE --- DATA :  " + data[0] + " " + data[1] +" " +  data[2] +" " +  data[3]+ " " + data[4]);
                        String route=data[4].toString();
                        Log.e("OUTPUT FILE","OUTPUT FILE --- DATA :  " + data[4]);

                        Log.e("OUTPUT FILE"," QUERY "+route);
                        StringBuilder query = new StringBuilder("INSERT INTO ");
                        query.append(RouteT.TABLE_NAME).append("(routeID,startNode,endNode,routeData) values (")
                                .append("'").append(data[1] ).append("',")
                                .append("'").append(data[2]).append("',")
                                .append("'").append(data[3]).append("',")
                                .append("'").append(route).append("')");
                        Log.e("OUTPUT FILE"," QUERY "+query);

                        sqlHandler.executeQuery(query.toString());
                        sqlHandler.closeDataBaseConnection();


                    }
                    catch (Exception e)
                    {
                        Log.e("Problem",e.toString());
                    }
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
        {
            Toast.makeText(getContext(),"file not exists",Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE);
        //String resultAccepted == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && storageAccepted) {
                        // Toast.makeText(this, "Permission Granted,.", Toast.LENGTH_LONG).show();
                    }else {
                        // Toast.makeText(this, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Look at this dialog!")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    public void animateMarker(final Marker marker,final LatLng startLatLng, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
       // Point startPoint = proj.toScreenLocation(marker.getPosition());
       // final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 10000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));
        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, centerX,centerY, matrix, true)));
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                // calculate phase of animation
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                // calculate new position for marker
                double lat = (endLatLng.latitude - beginLatLng.latitude) * t + beginLatLng.latitude;
                double lngDelta = endLatLng.longitude - beginLatLng.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                Location location= new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(endLatLng.latitude);
                location.setLongitude(endLatLng.longitude);
                float bearingMap= location.getBearing();
                //  float bearingMap= mMap.getCameraPosition().bearing;
                float bearing = (float) bearingBetweenLocations(beginLatLng,endLatLng);
                float angle = -azimuthInDegress+bearing;
                float rotation = -azimuthInDegress * 360 / (2 * 3.14159f) ;
                double lng = lngDelta * t + beginLatLng.longitude;
                marker.setPosition(new LatLng(lat, lng));
                marker.setAnchor(0.5f, 0.5f);
                marker.setFlat(true);
                marker.setRotation(rotation);
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    float beginAngle = (float)(90 * getAngle(beginLatLng, endLatLng) / Math.PI);
                    float endAngle = (float)(90 * getAngle(currentGpsPosition, endLatLng) / Math.PI);
                }
            }
        });
    }


    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = sin(dLon) * cos(lat2);
        double x = cos(lat1) * sin(lat2) - sin(lat1)
                * cos(lat2) * cos(dLon);
        double brng = atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }

    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return atan2(sin(dl) * cos(f2) , cos(f1) * sin(f2) - sin(f1) * cos(f2) * cos(dl));
    }
}

