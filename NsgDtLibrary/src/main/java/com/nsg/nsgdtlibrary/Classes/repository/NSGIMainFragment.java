package com.nsg.nsgdtlibrary.Classes.repository;

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

public class NSGIMainFragment extends Fragment implements View.OnClickListener, SensorEventListener {
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
    private List distancesList;
    private List distanceValuesList;
    HashMap<String, String> hash_map;
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
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
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
    public NSGIMainFragment(){ }
    @SuppressLint("ValidFragment")
    public NSGIMainFragment(String BASE_MAP_URL_FORMAT, String DBCSV_PATH, String jobId, String routeId, int mode, int radius ) {
        enteredMode = mode;
        routeDeviationDistance=radius;
        NSGIMainFragment.this.BASE_MAP_URL_FORMAT = BASE_MAP_URL_FORMAT;
        NSGIMainFragment.this.DBCSV_PATH = DBCSV_PATH;
        NSGIMainFragment.this.routeIDName=routeId;
        NSGIMainFragment.this.jobId=jobId;
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
            Callback = (FragmentToActivity) context;
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
        mSensorManager = (SensorManager)getContext().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        checkPermission();
        requestPermission();
        String delQuery = "DELETE  FROM " + RouteT.TABLE_NAME;
        sqlHandler.executeQuery(delQuery);
        InsertAllRouteData(DBCSV_PATH);
        getRouteAccordingToRouteID(routeIDName);
        change_map_options = (ImageButton)rootView.findViewById(R.id.change_map_options);
        change_map_options.setOnClickListener(this);
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
                NSGIMainFragment.this.mMap = googlemap;
                NSGIMainFragment.this.mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.stle_map_json));
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
                sendData(routeAlert.toString());
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
                       if(RouteDataList!=null && RouteDataList.size()>0) {
                           dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                           dialog.setMessage("Fetching Route");
                           dialog.setMax(100);
                           dialog.show();
                           new Handler().postDelayed(new Runnable() {
                               //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                               @Override
                               public void run() {
                                   // dialog.dismiss();
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
                                   if(enteredMode==1 &&edgeDataList!=null && edgeDataList.size()>0){
                                       ETACalclator etaCalculator1=new ETACalclator();
                                       double resultTotalETA=etaCalculator1.cal_time(TotalDistanceInMTS, maxSpeed);
                                       double resultTotalTimeConverted = DecimalUtils.round(resultTotalETA,0);
                                       tv.setText("Total Time: "+ resultTotalTimeConverted +" SEC" );
                                       tv2.setText("Time ETA  : "+ resultTotalTimeConverted +" SEC ");

                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {                                   // MoveWithGPSMARKER();

                                           mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                               @Override
                                               public void onMyLocationChange(Location location) {
                                                   if (mPositionMarker != null) {
                                                       mPositionMarker.remove();
                                                   }
                                                   vehicleSpeed=location.getSpeed();
                                                   if( currentGpsPosition!=null && locationFakeGpsListener > 0) {
                                                       lastGPSPosition=new ArrayList<>();
                                                       lastGPSPosition.add(currentGpsPosition);
                                                       OldGPSPosition=lastGPSPosition.get(0);
                                                   }
                                                   getLatLngPoints();
                                                   currentGpsPosition = LatLngDataArray.get(locationFakeGpsListener);


                                                   if(isRouteDeviated==false) {
                                                       MoveWithGpsPointInBetWeenAllPoints(OldGPSPosition, currentGpsPosition);
                                                   }else{
                                                       MoveWithGpsPointInRouteDeviatedPoints( currentGpsPosition);
                                                   }

                                                   new Handler().postDelayed(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           locationFakeGpsListener = locationFakeGpsListener + 1;
                                                       }
                                                   }, 0);


                                               }
                                           });
                                       }
                                   }else if(enteredMode==2){
                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                               mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                   @Override
                                                   public void onMyLocationChange(Location location) {
                                                       if (mPositionMarker != null) {
                                                           mPositionMarker.remove();
                                                       }
                                                       LatLng currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                                       Log.e("currentGpsPosition","currentGpsPosition"+currentGpsPosition);
                                                        /*
                                                       mPositionMarker = mMap.addMarker(new MarkerOptions()
                                                               .position(currentGpsPosition)
                                                               .title("currentLocation")
                                                               .anchor(0.5f, 0.5f)
                                                               .rotation(location.bearingTo(location))
                                                               .flat(true)
                                                               .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));
                                                       //changing direction to NORTH as Shown in vedio by DT Team 65.5f

                                                       CameraPosition currentPlace = new CameraPosition.Builder()
                                                               .target(new LatLng(currentGpsPosition.latitude, currentGpsPosition.longitude))
                                                               .bearing(location.bearingTo(location)).tilt(65.5f).zoom(20)
                                                               .build();
                                                       mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 5000, null);
                                                       */

                                                       if(isRouteDeviated==false) {
                                                           MoveWithGpsPointInBetWeenAllPoints(OldGPSPosition, currentGpsPosition);
                                                       }else{
                                                           MoveWithGpsPointInRouteDeviatedPoints( currentGpsPosition);
                                                       }

                                                   }
                                               });
                                       }
                                   }else if(enteredMode==3){
                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                           if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                                Log.e("SourceNode","SourceNode -- "+ SourceNode);
                                                     mPositionMarker = mMap.addMarker(new MarkerOptions()
                                                               .position(SourceNode)
                                                               .title("currentLocation")
                                                               .anchor(0.5f, 0.5f)
                                                               .flat(true)
                                                               .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));

                                       }
                                   }
                                   dialog.dismiss();
                               }
                           }, 0);
                       }
                   }
               });

               location_tracking_stop.setOnClickListener(new View.OnClickListener() {
                   LatLng currentGpsPosition;
                   @Override
                   public void onClick(View v) {

                           mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                               @Override
                               public void onMyLocationChange(final Location location) {
                                   if (mPositionMarker != null) {
                                       mPositionMarker.remove();
                                   }
                                   currentGpsPosition = new LatLng(location.getLatitude(),location.getLongitude());

                               }
                           });
                           if(currentGpsPosition!=null){
                               mMap.setMyLocationEnabled(false);
                               sendData(currentGpsPosition.toString());
                           }
                           getActivity().onBackPressed();


                   }
               });

            }
        });
        return rootView;
    }
    @Override
    public void onDetach() {
        Callback = null;
        super.onDetach();
    }
    private void sendData(String comm)
    {
        //comm=time.toString();
        Log.e("SendData","SendData ------- "+ comm);
        Callback.communicate(comm);

    }
    private  List<RouteT> getRouteAccordingToRouteID(String routeIDName) {
        String query = "SELECT * FROM " + RouteT.TABLE_NAME +" WHERE routeID = "+"'"+routeIDName+"'";
        Cursor c1 = sqlHandler.selectQuery(query);
        RouteDataList = (List<RouteT>) SqlHandler.getDataRows(RouteT.MAPPING, RouteT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return RouteDataList;
    }

    private  List<EdgeDataT> getAllEdgesData() {
        String query = "SELECT * FROM " + EdgeDataT.TABLE_NAME;
        Cursor c1 = sqlHandler.selectQuery(query);
        edgeDataList = (List<EdgeDataT>) SqlHandler.getDataRows(EdgeDataT.MAPPING, EdgeDataT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return edgeDataList;
    }
    private String getAddress(Double latitude, Double longitude) {
        String strAdd="";

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current  address", strReturnedAddress.toString());
            } else {
                Log.w("My Current  address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current  address", "Canont get Address!");
        }
        return strAdd;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(10, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void MoveWithGpsPointInBetWeenAllPoints(final LatLng PrevousGpsPosition ,final LatLng currentGpsPosition){
        LatLng OldGps,nayaGps;
        List<LatLng> EdgeWithoutDuplicates = removeDuplicates(edgeDataPointsList);
        nearestValuesMap=new HashMap<>();
        if (EdgeWithoutDuplicates != null && EdgeWithoutDuplicates.size() > 0) {
            String FirstCordinate="",SecondCordinate="";
            distancesList = new ArrayList();
            distanceValuesList = new ArrayList();
            hash_map = new HashMap<String, String>();
            for (int epList = 0; epList < EdgeWithoutDuplicates.size(); epList++) {
                LatLng PositionMarkingPoint = EdgeWithoutDuplicates.get(epList);
               // Log.e("Distances List","Distances List PositionMarkingPoint"+PositionMarkingPoint);
               // Log.e("Distances List","Distances List currentGpsPosition "+currentGpsPosition);
                double distance = distFrom(PositionMarkingPoint.latitude,PositionMarkingPoint.longitude,currentGpsPosition.longitude,currentGpsPosition.latitude);
                hash_map.put(String.valueOf(distance), String.valueOf(EdgeWithoutDuplicates.get(epList)));
                distancesList.add(distance);
                Collections.sort(distancesList);
            }
            for(int i=0;i<distancesList.size();i++) {
               // Log.e("Distances List","Distances List"+distancesList.get(i));
            }

            String FirstShortestDistance = String.valueOf(distancesList.get(0));
            String SecondShortestDistance = String.valueOf(distancesList.get(1));
            boolean answerFirst= hash_map.containsKey(FirstShortestDistance);
            if (answerFirst) {
                System.out.println("The list contains " + FirstShortestDistance);
                FirstCordinate = (String)hash_map.get(FirstShortestDistance);
                key= String.valueOf(getKeysFromValue(AllPointEdgeNo,FirstCordinate));
                distanceKey= String.valueOf(getKeysFromValue(AllPointEdgeDistaces,FirstCordinate));
            } else {
                System.out.println("The list does not contains "+ "FALSE");
            }
            boolean answerSecond= hash_map.containsKey(SecondShortestDistance);
            if (answerSecond) {
                System.out.println("The list contains " + SecondShortestDistance);
                SecondCordinate = (String)hash_map.get(SecondShortestDistance);

            } else {
                System.out.println("The list does not contains "+ "FALSE");
            }
            String First= FirstCordinate.replace("lat/lng: (","");
            First= First.replace(")","");
            String[] FirstLatLngsData=First.split(",");
            double FirstLatitude= Double.valueOf(FirstLatLngsData[0]);
            double FirstLongitude= Double.valueOf(FirstLatLngsData[1]);

            geometryDirectionText=key;
            geometryDirectionDistance=distanceKey;

            String Second= SecondCordinate.replace("lat/lng: (","");
            Second= Second.replace(")","");
            String[] SecondLatLngsData=Second.split(",");
            double SecondLatitude= Double.valueOf(SecondLatLngsData[0]);
            double SecondLongitude= Double.valueOf(SecondLatLngsData[1]);

            double x= currentGpsPosition.longitude;
            double y= currentGpsPosition.longitude;
            int value = (int)x;
            int value1 = (int)y;
            LatLng source=new LatLng(FirstLongitude,FirstLatitude);
            LatLng destination=new LatLng(SecondLongitude,SecondLatitude);

            nearestPositionPoint= findNearestPoint(currentGpsPosition,source,destination);
            OldNearestGpsList.add(nearestPositionPoint);

        }
      //  Log.e("EdgeSt Point", "End point" + OldNearestGpsList.size());
        if(OldNearestGpsList.isEmpty() && OldNearestGpsList.size()==0){
            OldGps=OldNearestGpsList.get(0);
            int indexVal=OldNearestGpsList.indexOf(nearestPositionPoint);
            nayaGps=OldNearestGpsList.get(indexVal);
        }else{
            int indexVal=OldNearestGpsList.indexOf(nearestPositionPoint);
            OldGps=OldNearestGpsList.get(indexVal-1);
            nayaGps=OldNearestGpsList.get(indexVal);
        }
        nearestValuesMap.put(String.valueOf(nearestPositionPoint),geometryDirectionText);
        nearestPointValuesList.add(nearestPositionPoint);
      //  if(currentGpsPosition.equals(LatLngDataArray.get(LatLngDataArray.size()-1))){
       //     nearestPointValuesList.add(DestinationPosition);
       // }

        float bearing = (float) bearingBetweenLocations(OldGps,nayaGps); //correct method to change orientation of map
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(SourceNode)
                .title("currentLocation")
                .anchor(0.5f, 0.5f)
                .rotation(bearing)
                .flat(true));
              //  .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));
        if( OldGps .equals(nearestPositionPoint)){

        }else{
            animateCarMove(mPositionMarker, OldGps, nearestPositionPoint, 5000,currentGpsPosition);
        }
       // verifyRouteDeviation(currentGpsPosition,50);
        caclulateETA(TotalDistanceInMTS,SourceNode,currentGpsPosition,DestinationNode);
        NavigationDirection(currentGpsPosition,DestinationNode);
        verifyRouteDeviation(PrevousGpsPosition,currentGpsPosition,DestinationNode,40,EdgeWithoutDuplicates);
        AlertDestination(currentGpsPosition);

        int width =getView().getMeasuredWidth();
        Log.e("width","width"+width);
        int height =getView().getMeasuredHeight();
        Log.e("Height","Height"+height);


/*
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nearestPositionPoint, 18));


       // Point offset = new Point(center.x, (center.y + 320));
        Point bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
        bottomRightPoint.y=center.y;
        LatLng centerLoc = p.fromScreenLocation(center);
        LatLng offsetNewLoc = p.fromScreenLocation(bottomRightPoint);
        double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);



        Projection p = mMap.getProjection();
        Point somePoint=p.toScreenLocation(nearestPositionPoint);
        // Point centerScreenPoint = p.toScreenLocation(p.getVisibleRegion().latLngBounds.getCenter());

        Point somePoint1 = new Point(somePoint.x + height/2,somePoint.y + height);
        LatLng somePoint1ll = p.fromScreenLocation(somePoint1);
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(somePoint1ll)
                .bearing(bearing).tilt(65.5f).zoom(20)
                .build();
                */
        Projection p = mMap.getProjection();
       Point bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
        Point center = new Point(bottomRightPoint.x/2,bottomRightPoint.y/2);
        Point offset = new Point(center.x, (center.y+(height/4)));
        LatLng centerLoc = p.fromScreenLocation(center);
        LatLng offsetNewLoc = p.fromScreenLocation(offset);
        double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);
        LatLng shadowTgt = SphericalUtil.computeOffset(nearestPositionPoint,offsetDistance,bearing);

        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(shadowTgt)
                .bearing(bearing).tilt(65.5f).zoom(20)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 5000, null);



       // LatLng shadowTgt = SphericalUtil.computeOffset(nearestPositionPoint,offsetDistance,bearing);
        /*
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(aboveMarkerLatLng)
                .bearing(bearing).tilt(65.5f).zoom(18)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 5000, null);
        */
        //mMap.setPadding(0,0,0,330);


/*

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override            public void onCameraChange(CameraPosition cameraPosition) {
                Log.e("Destination points","Destination points "+destLat+destLng);
                getTextImplementation(currentGpsPosition,DestinationNode);
               // verifyRouteDeviation(currentGpsPosition,10);
            }
        });
        */
    }
    public void animateCamera(LatLng nearestPositionPoint,float bearing ){
        Projection p = mMap.getProjection();
        Point  bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
        Point center = new Point(bottomRightPoint.x/2,bottomRightPoint.y/2);
        Point offset = new Point(center.x, (center.y + 300));
        LatLng centerLoc = p.fromScreenLocation(center);
        LatLng offsetNewLoc = p.fromScreenLocation(offset);
        double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);
        LatLng shadowTgt = SphericalUtil.computeOffset(nearestPositionPoint,offsetDistance,bearing);

        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(shadowTgt)
                .bearing(bearing).tilt(65.5f).zoom(20)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 10000, null);

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


    private LatLng animateLatLngZoom(LatLng latlng, int reqZoom, int offsetX, int offsetY) {
        // Save current zoom
        float originalZoom = mMap.getCameraPosition().zoom;
        // Move temporarily camera zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(reqZoom));
        Point pointInScreen = mMap.getProjection().toScreenLocation(latlng);
        Point newPoint = new Point();
        newPoint.x = pointInScreen.x - offsetX;
        newPoint.y = pointInScreen.y + offsetY;
        newCenterLatLng = mMap.getProjection().fromScreenLocation(newPoint);
        // Restore original zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoom));
        // Animate a camera with new latlng center and required zoom.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng, reqZoom));
        return newCenterLatLng;
    }

    public void AlertDestination(LatLng currentGpsPosition){
        int GpsIndex=OldNearestGpsList.indexOf(nearestPositionPoint);
        LatLng cameraPosition=OldNearestGpsList.get(GpsIndex);
        if (currentGpsPosition.equals(DestinationNode)) {
            lastDistance= showDistance(cameraPosition,DestinationNode);
            if (lastDistance <5) {
                if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                mMap.setMyLocationEnabled(false);
                //Speech implementation
                String data1=" Your Destination Reached ";
                int speechStatus1 = textToSpeech.speak(data1, TextToSpeech.QUEUE_FLUSH, null);
                if (speechStatus1 == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.yourDialog);
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.car_icon_32);
                builder.setMessage("Destination Reached")
                        .setCancelable(false)
                        .setPositiveButton(" Finish ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i=new Intent(getActivity(), NSGIMainFragment.class);
                                startActivity(i);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }else{
        }
    }
    double mphTOkmph(double mph)
    {
        return mph * 1.60934;
    }
    public Bitmap fromDrawable(final Drawable drawable) {
        final Bitmap bitmap = Bitmap.createBitmap(25, 25, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
    public Set<Object> getKeysFromValueinLatLng(Map<LatLng, String> map, String key) {
        Set<Object> keys = new HashSet<Object>();
        for (Map.Entry<LatLng, String> entry : map.entrySet()) {
            //if value != null
            if (entry.getKey().equals(key)){
                keys.add(entry.getValue());
            }
        }
        return keys;
    }

    private void GetRouteDetails(final String deviationPoint, final String newDestinationPoint){
        try{
           // Log.e("Deviation Point","Deviation Point" + deviationPoint);
           // Log.e("newDestinationPoint","newDestinationPoint" + newDestinationPoint);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy =
                                new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            String httprequest = "http://202.53.11.74/dtnavigation/api/routing/routenavigate";
                          //  Log.e("HTTP REQUEST","HTTP REQUEST"+httprequest);
                            String FeatureResponse = HttpPost(httprequest,deviationPoint,newDestinationPoint);
                          //  Log.e("HTTP REQUEST","HTTP REQUEST"+ FeatureResponse);
                            JSONObject jsonObject = null;
                            try {
                                if(FeatureResponse!=null){
                                    String delQuery = "DELETE  FROM " + GeometryT.TABLE_NAME;
                                    sqlHandler.executeQuery(delQuery.toString());
                                 //   Log.e("DelQuery","DelQuery"+delQuery);
                                    jsonObject = new JSONObject(FeatureResponse);
                                    String ID = String.valueOf(jsonObject.get("$id"));
                                   // MESSAGE = jsonObject.getString("Message");
                                    String Status = jsonObject.getString("Status");
                                    double TotalDistance = jsonObject.getDouble("TotalDistance");
                                    TotalRouteDeviatedDistanceInMTS = jsonObject.getDouble("TotalDistance");
                                    JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                                    PolylineOptions polylineOptions = new PolylineOptions();

                                    polylineOptions.add(OldGPSPosition);
                                    PointBeforeRouteDeviation=new LatLng(OldGPSPosition.latitude,OldGPSPosition.longitude);
                                    Polyline polyline = null;
                                     RouteDeviationConvertedPoints=new ArrayList<LatLng>();
                                    geometryRouteDeviatedEdgesData=new ArrayList<GeometryT>();
                                    for (int i = 0; i < jSonRoutes.length(); i++) {
                                        List deviationPoints=new ArrayList();
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
                                             deviationPoints.add(jSonLegs.get(j));

                                        }
                                     //   Log.e("DEVIATION POINTS","DEVIATION POINTS"+deviationPoints.size());
                                        String  stPoint=String.valueOf(jSonLegs.get(0));
                                        stPoint=stPoint.replace("[","");
                                        stPoint=stPoint.replace("]","");
                                        String [] firstPoint=stPoint.split(",");
                                        Double stPointLat= Double.valueOf(firstPoint[0]);
                                        Double stPointLongi= Double.valueOf(firstPoint[1]);
                                        LatLng stVertex=new LatLng(stPointLongi,stPointLat);

                                        StringBuilder query = new StringBuilder("INSERT INTO ");
                                        query.append(GeometryT.TABLE_NAME).append("(edgeNo,distanceInVertex,startPoint,allPoints,geometryText,endPoint) values (")
                                                .append("'").append(EdgeNo).append("',")
                                                .append("'").append("distanceInKM").append("',")
                                                .append("'").append(jSonLegs.get(0)).append("',")
                                                .append("'").append(deviationPoints).append("',")
                                                .append("'").append(GeometryText).append("',")
                                                .append("'").append(jSonLegs.get(jSonLegs.length()-1)).append("')");
                                        sqlHandler.executeQuery(query.toString());
                                       // Log.e("INSERTION QUERY","INSERTION QUERY ----- "+ query);
                                        sqlHandler.closeDataBaseConnection();
                                        for (int p = 0; p < deviationPoints.size(); p++) {

                                            String listItem = deviationPoints.get(p).toString();
                                            listItem = listItem.replace("[", "");
                                            listItem = listItem.replace("]", "");
                                            String[] subListItem = listItem.split(",");
                                            Double y = Double.valueOf(subListItem[0]);
                                            Double x = Double.valueOf(subListItem[1]);
                                            StringBuilder sb=new StringBuilder();
                                            LatLng latLng = new LatLng(x, y);
                                            RouteDeviationConvertedPoints.add(latLng);
                                            GeometryT edgeRouteDeviatedPointData = new GeometryT(stPoint,jSonLegs.get(jSonLegs.length()-1).toString(),String.valueOf(latLng),GeometryText,"");
                                            geometryRouteDeviatedEdgesData.add(edgeRouteDeviatedPointData);
                                        }
                                       // Log.e("INSERTION QUERY","RouteDeviationConvertedPoints----- "+ RouteDeviationConvertedPoints);
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        for (int k = 0; k < RouteDeviationConvertedPoints.size(); k++) {
                                            if(polylineOptions!=null && mMap!=null) {
                                               // markerOptions.position(RouteDeviationConvertedPoints.get(k));
                                               // Log.e("INSERTION QUERY","RouteDeviationConvertedPoints----- "+ RouteDeviationConvertedPoints.get(k));
                                                markerOptions.title("Position");
                                            }
                                        }
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }catch (Exception ex){

                        }
                        dialog.dismiss();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        dialog.dismiss();
    }
    private String HttpPost(String myUrl,String latLng1,String latLng2) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String LoginResponse = "";
        String result = "";
        URL url = new URL(myUrl);
        Log.v("URL ", " URL: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/plain");
        JSONObject jsonObject = buidJsonObject(latLng1,latLng2);
       // Log.e("JSON OBJECT","JSON OBJECT ------- "+jsonObject);
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        result = conn.getResponseMessage();
        if (conn.getResponseCode() != 200) {

        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = null;
            //   System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                LoginResponse = sb.append(output).append(" ").toString();

            }
        }
        conn.disconnect();
        return LoginResponse;
    }

    private JSONObject buidJsonObject(String latLng1,String latLng2) throws JSONException {
        JSONObject buidJsonObject = new JSONObject();
        buidJsonObject.accumulate("UserData", buidJsonObject1());
        buidJsonObject.accumulate("StartNode", latLng1);
        buidJsonObject.accumulate("EndNode", latLng2);
        return buidJsonObject;
    }

    private JSONObject buidJsonObject1() throws JSONException {
        JSONObject buidJsonObject1 = new JSONObject();
        buidJsonObject1.accumulate("username", "admin");
        buidJsonObject1.accumulate("password", "admin");
        buidJsonObject1.accumulate("License", "b3TIz98wORn6daqmthiEu48TAW1ZEQjPuRLapxJPV6HJQiJtO9LsOErPexmDhbZtD76U2AbJ+jXarYr3gAqkkRiofLSN+321AlzVW1K3L8feXLYTRSP8NSgJ7v7460H1hxKIp+mbak6+Q6tNoWgKpA==");

        return buidJsonObject1;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        // Log.i(LoginActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
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
    private String GenerateLinePoint(double startPointX, double startPointY, double endPointX, double endPointY, double pointX, double pointY)
    {
        double k = ((endPointY - startPointY) * (pointX - startPointX) - (endPointX - startPointX) * (pointY - startPointY)) / (Math.pow(endPointY - startPointY, 2)
                + Math.pow(endPointX - startPointX, 2));
        double resultX = pointX - k * (endPointY - startPointY);
        double resultY = pointY + k * (endPointX - startPointX);
        StringBuilder sb=new StringBuilder();
        sb.append(resultX).append(",").append(resultY);

        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void verifyRouteDeviation(final LatLng PrevousGpsPosition, final LatLng currentGpsPosition, final LatLng DestinationPosition, int markDistance, final List<LatLng>EdgeWithoutDuplicates) {

        Log.e("Route Deviation", "CURRENT GPS ----" + currentGpsPosition);
        Log.e("Route Deviation", " OLD GPS POSITION  ----" + PrevousGpsPosition);
        if (PrevousGpsPosition != null){
        double returnedDistance = showDistance(currentGpsPosition, PrevousGpsPosition);
        Log.e("Route Deviation","ROUTE DEVIATION DISTANCE ----"+returnedDistance);
        float rotateBearing= (float) bearingBetweenLocations(PrevousGpsPosition,currentGpsPosition);
         //   Log.e("Route Deviation","ROUTE DEVIATION ANGLE ----"+ rotateBearing);
            if(returnedDistance > markDistance) {
                      //  mMap.stopAnimation();
                        String cgpsLat = String.valueOf(currentGpsPosition.latitude);
                        String cgpsLongi = String.valueOf(currentGpsPosition.longitude);
                        final String routeDiationPosition = cgpsLongi.concat(" ").concat(cgpsLat);

                        String destLatPos = String.valueOf(DestinationPosition.latitude);
                        String destLongiPos = String.valueOf(DestinationPosition.longitude);
                        final String destPoint = destLongiPos.concat(" ").concat(destLatPos);
                        RouteDeviatedSourcePosition=new LatLng(Double.parseDouble(cgpsLat),Double.parseDouble(cgpsLat));

                      //  Log.e("returnedDistance", "RouteDiationPosition --------- " + routeDiationPosition);
                     //   Log.e("returnedDistance", "Destination Position --------- " + destPoint);
                        //  DestinationPosition = new LatLng(destLat, destLng);
                     //   dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                     //   dialog.setMessage("Fetching new Route");
                     //   dialog.setMax(100);
                     //   dialog.show();
                        //new Handler().postDelayed(new Runnable() {
                    new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                String MESSAGE = "";
                                GetRouteDetails(routeDiationPosition, destPoint);
                                //checkPointsOfRoue1withNewRoute(EdgeWithoutDuplicates,PointBeforeRouteDeviation);
                                if(RouteDeviationConvertedPoints!=null &&RouteDeviationConvertedPoints.size()>0 ) {
                                    isRouteDeviated = true;

                                    LayoutInflater inflater1 = getActivity().getLayoutInflater();
                                    @SuppressLint("WrongViewCast") View layout = inflater1.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.textView_toast));
                                    TextView text = (TextView) layout.findViewById(R.id.textView_toast);

                                    text.setText("Route Deviated");

                                    Toast toast = new Toast(getActivity().getApplicationContext());
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.setGravity(Gravity.TOP, 0, 150);
                                    toast.setView(layout);
                                    toast.show();
                                    if(mPositionMarker!=null){
                                      mPositionMarker.remove();
                                      Log.e("REMOVING MARKER","REMOVING MARKER");
                                    }
                                    mPositionMarker = mMap.addMarker(new MarkerOptions()
                                            .position(currentGpsPosition)
                                            .title("currentLocation")
                                            .anchor(0.5f, 0.5f)
                                            .flat(true)
                                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));

                                    CameraUpdate center =
                                            CameraUpdateFactory.newLatLng(currentGpsPosition);
                                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(22);
                                    mMap.moveCamera(center);
                                    mMap.animateCamera(zoom);
                                    if(mPositionMarker!=null && mPositionMarker.isVisible()==true) {
                                        PolylineOptions polylineOptions = new PolylineOptions();
                                        polylineOptions.add(OldGPSPosition);
                                        polylineOptions.addAll(RouteDeviationConvertedPoints);
                                        Polyline polyline = mMap.addPolyline(polylineOptions);
                                        polylineOptions.color(Color.RED).width(30);
                                        mMap.addPolyline(polylineOptions);
                                        polyline.setJointType(JointType.ROUND);
                                    }
                                }


                            }
                        };
            }

        }else{

        }
    }
    public void checkPointsOfRoue1withNewRoute(List<LatLng> edgeWithoutDuplicates,LatLng PointBeforeRouteDeviation){
       // Log.e("CGPS","CGPS POINR PRESENT--------"+PointBeforeRouteDeviation);
        String deviatedRoute1Point="";
        List distancesList1=new ArrayList<>();
        List<LatLng> sublistBefore;
        List<LatLng> sublistAfter;
        Map<String,String> hash_map1 =new HashMap<String,String>();
       // Log.e("Route Deviated", "Route Deviated EdgesList ------- " + RouteDeviationConvertedPoints.size());
        List<LatLng> EdgeWithoutDuplicatesInRouteDeviationPoints = removeDuplicatesRouteDeviated(RouteDeviationConvertedPoints);
        for(int k=0;k< EdgeWithoutDuplicatesInRouteDeviationPoints.size();k++) {
          //  Log.e("Route Deviated----", "EdgeWithoutDuplicatesInRouteDeviationPoints ------- " + EdgeWithoutDuplicatesInRouteDeviationPoints.get(k));
        }
        for(int k=0;k< edgeWithoutDuplicates.size();k++) {
           // Log.e("Route Deviated----", "edgeWithoutDuplicates ------- " + edgeWithoutDuplicates.get(k));
        }
        for (int epList = 0; epList < edgeWithoutDuplicates.size(); epList++) {

            LatLng PositionMarkingPoint = edgeWithoutDuplicates.get(epList);
            double distance = distFrom(PositionMarkingPoint.latitude,PositionMarkingPoint.longitude,PointBeforeRouteDeviation.latitude,PointBeforeRouteDeviation.longitude);
            hash_map1.put(String.valueOf(distance),String.valueOf(edgeWithoutDuplicates.get(epList)));
            distancesList1.add(distance);
            Collections.sort(distancesList1);
        }
        String shortestDistance = String.valueOf(distancesList1.get(0));
      //  Log.e("Route Deviated----", "shortestDistance  Route Deviated ------- " + shortestDistance);
        boolean value= hash_map1.containsKey(shortestDistance);
     //   Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT ------- " + value);
        if(value){
            deviatedRoute1Point= hash_map1.get(shortestDistance);
         //   Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT ------- " + deviatedRoute1Point);
        } else{
            System.out.println("Key not matched with ID");
        }
        String deviationRoutePoint1=deviatedRoute1Point.replace("lat/lng: (","");
        String deviatedPosition=deviationRoutePoint1.replace(")","");
        String[] deviatedPositionPts=deviatedPosition.split(",");
        double longi= Double.parseDouble(deviatedPositionPts[0]);
        double lat= Double.parseDouble(deviatedPositionPts[1]);
        LatLng deviatedLatlng=new LatLng(longi,lat);

        boolean listVerifyCon = edgeWithoutDuplicates.contains(deviatedLatlng);
       // Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT INDEX BOOL ------- " + listVerifyCon);
        if(listVerifyCon) {
            int index = edgeWithoutDuplicates.indexOf(deviatedLatlng);
          //  Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT INDEX ------- " + index);
            sublistBefore = edgeWithoutDuplicates.subList(0, index);
          //  Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT SUBLIST BEFORE------- " + sublistBefore.size());
            sublistAfter = edgeWithoutDuplicates.subList(index, edgeWithoutDuplicates.size());
         //   Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT SUBLIST AFTER ------- " + sublistAfter.size());

            for (int p = 0; p < sublistBefore.size(); p++) {
           //     Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT SUBLIST BEFORE ------- " + sublistBefore.get(p));
            }
            for (int p = 0; p < sublistAfter.size(); p++) {
           //     Log.e("Route Deviated----", "shortestDistance  Route Deviated POINT SUBLIST AFTER ------- " + sublistAfter.get(p));
            }
        }
    }
    public void MoveWithGpsPointInRouteDeviatedPoints(LatLng currentGpsPosition){
      LatLng FirstCordinate = null,SecondCordinate=null;
       String st_vertex="",end_vertex="", directionTextRouteDeviation="";
        if(RouteDeviationConvertedPoints!=null) {
          //  Log.e("Route Deviated", "Route Deviated EdgesList ------- " + RouteDeviationConvertedPoints.size());
         //   Log.e("Route Deviated", "Current GPS position ------- " + currentGpsPosition);
            List<LatLng> EdgeWithoutDuplicatesInRouteDeviationPoints = removeDuplicatesRouteDeviated(RouteDeviationConvertedPoints);
            for (int k = 0; k < EdgeWithoutDuplicatesInRouteDeviationPoints.size(); k++) {
              //  Log.e("Route Deviated----", "EdgeWithoutDuplicatesInRouteDeviationPoints ------- " + EdgeWithoutDuplicatesInRouteDeviationPoints.get(k));
            }
            if (EdgeWithoutDuplicatesInRouteDeviationPoints != null && EdgeWithoutDuplicatesInRouteDeviationPoints.size() > 0) {

                List distancesInDeviation = new ArrayList();
                HashMap<String, LatLng> distancesMapInRouteDeviation = new HashMap<String, LatLng>();
                for (int epList = 0; epList < EdgeWithoutDuplicatesInRouteDeviationPoints.size(); epList++) {
                    LatLng PositionMarkingPoint = EdgeWithoutDuplicatesInRouteDeviationPoints.get(epList);
                  //  Log.e("Route Deviation", " Route Deviation PositionMarking Point" + PositionMarkingPoint);
                    double distance = distFrom(PositionMarkingPoint.longitude, PositionMarkingPoint.latitude, currentGpsPosition.longitude, currentGpsPosition.latitude);
                    distancesMapInRouteDeviation.put(String.valueOf(distance), PositionMarkingPoint);
                    distancesInDeviation.add(distance);
                    Collections.sort(distancesInDeviation);
                }
                for (int k = 0; k < distancesInDeviation.size(); k++) {
                   // Log.e("Route Deviation", " distancesInDeviation" + distancesInDeviation.get(k));
                }
               // Log.e("Route Deviation", " distancesInDeviation" + distancesInDeviation);
               // Log.e("Route Deviation", " distancesMapInRouteDeviation" + distancesMapInRouteDeviation);
                String firstShortestDistance = String.valueOf(distancesInDeviation.get(0));
                String secondShortestDistance = String.valueOf(distancesInDeviation.get(1));
                boolean answerFirst = distancesMapInRouteDeviation.containsKey(firstShortestDistance);
                if (answerFirst) {
                    System.out.println("The list contains " + firstShortestDistance);
                    FirstCordinate = distancesMapInRouteDeviation.get(firstShortestDistance);
                   // Log.e("Route Deviation", " FIRST Cordinate  From Route deviation" + FirstCordinate);
                    for(int i=0;i<geometryRouteDeviatedEdgesData.size();i++){
                        GeometryT geometry=geometryRouteDeviatedEdgesData.get(i);
                       String routeDeviationTextPoint= geometry.getPositionMarkingPoint();
                       // Log.e("Route Deviation", " ST_VERTEX From Route deviation" + routeDeviationTextPoint);

                       if(routeDeviationTextPoint.equals(FirstCordinate.toString())){
                          int index = geometryRouteDeviatedEdgesData.indexOf(routeDeviationTextPoint);
                           st_vertex=geometry.getStartPoint();
                           end_vertex=geometry.getEndPoint();
                           directionTextRouteDeviation=geometry.getGeometryText();

                       }
                    }
                  //  Log.e("Route Deviation", " ST_VERTEX From Route deviation" + st_vertex);
                  //  Log.e("Route Deviation", " END_VERTEX  From Route deviation" + end_vertex);
                  //  Log.e("Route Deviation", " DIRECTION TEXT Cordinate  From Route deviation" + directionTextRouteDeviation);

                    // key= getKeysFromValueinLatLng(EdgeWithoutDuplicatesInRouteDeviationPoints,FirstCordinate);
                    // distanceKey= String.valueOf(getKeysFromValue(AllPointEdgeDistaces,FirstCordinate));
                } else {
                    System.out.println("The list does not contains " + "FALSE");
                }
                boolean answerSecond = distancesMapInRouteDeviation.containsKey(secondShortestDistance);
                if (answerSecond) {
                    System.out.println("The list contains " + secondShortestDistance);
                    SecondCordinate = distancesMapInRouteDeviation.get(secondShortestDistance);
                  //  Log.e("Route Deviation", " SECOND Cordinate  From Route deviation" + SecondCordinate);
                    // key= String.valueOf(getKeysFromValue(EdgeWithoutDuplicatesInRouteDeviationPoints,FirstCordinate));
                    // distanceKey= String.valueOf(getKeysFromValue(AllPointEdgeDistaces,FirstCordinate));
                } else {
                    System.out.println("The list does not contains " + "FALSE");
                }
               // Log.e("Route Deviation", " currentGpsPosition From Route deviation " + currentGpsPosition);
               // Log.e("Route Deviation", " FirstCordinate From Route deviation " + FirstCordinate);
               // Log.e("Route Deviation", " Second Cordinate From Route deviation " + SecondCordinate);


                nearestPositionPoint = findNearestPoint(currentGpsPosition, FirstCordinate, SecondCordinate);
               // Log.e("Route Deviation", " NEAREST POSITION From Route deviation " + nearestPositionPoint);
                OldNearestGpsList.add(nearestPositionPoint);
            }
            if (OldNearestGpsList.isEmpty() && OldNearestGpsList.size() == 0) {
                OldGps = OldNearestGpsList.get(0);
                int indexVal = OldNearestGpsList.indexOf(nearestPositionPoint);
                nayaGps = OldNearestGpsList.get(indexVal);
            } else {
                int indexVal = OldNearestGpsList.indexOf(nearestPositionPoint);
                OldGps = OldNearestGpsList.get(indexVal - 1);
                nayaGps = OldNearestGpsList.get(indexVal);
            }
            nearestValuesMap.put(String.valueOf(nearestPositionPoint), geometryDirectionText);
            nearestPointValuesList.add(nearestPositionPoint);
            if (currentGpsPosition.equals(LatLngDataArray.get(LatLngDataArray.size() - 1))) {
                nearestPointValuesList.add(DestinationPosition);
            }
           // Log.e("Route Deviation", " OldGps POSITION From Route deviation " + OldGps);
            float bearing = (float) bearingBetweenLocations(OldGps, nayaGps); //correct method to change orientation of map
            mPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(SourceNode)
                    .title("currentLocation")
                    .anchor(0.5f, 0.5f)
                    .rotation(bearing)
                    .flat(true));

            //  .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent)));
            if (OldGps.equals(nearestPositionPoint)) {

            } else {
                animateCarMove(mPositionMarker, OldGps, nearestPositionPoint, 5000, currentGpsPosition);
            }
          //  animateCamera(nearestPositionPoint, bearing);
            Projection p = mMap.getProjection();
            Point bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
            Point center = new Point(bottomRightPoint.x/2,bottomRightPoint.y/2);
          //  Point offset = new Point(center.x, (center.y + 320));
            Point offset = new Point((center.x + 320), (center.y + 320));
            LatLng centerLoc = p.fromScreenLocation(center);
            LatLng offsetNewLoc = p.fromScreenLocation(offset);
            double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);
            LatLng shadowTgt = SphericalUtil.computeOffset(nearestPositionPoint,offsetDistance,bearing);

            CameraPosition currentPlace = new CameraPosition.Builder()
                    .target(shadowTgt)
                    .bearing(bearing).tilt(65.5f).zoom(20)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 5000, null);
            TextImplementationRouteDeviationDirectionText(directionTextRouteDeviation,st_vertex,end_vertex);
            CaluculateETAInRouteDeviationDirection(TotalRouteDeviatedDistanceInMTS,RouteDeviatedSourcePosition,currentGpsPosition,DestinationNode);
            AlertDestination(currentGpsPosition);

        }



    }
    public void CaluculateETAInRouteDeviationDirection( final double TotalDistance, final LatLng sourcePosition, final LatLng currentGpsPosition, LatLng DestinationPosition){
       // Log.e("Total Distance"," Route Deviation  ETA sourcePosition "+ sourcePosition);
       // Log.e("Total Distance"," Route Deviation  ETA  DestinationPosition "+ DestinationPosition);
       // Log.e("Total Distance"," Route Deviation  ETA  currentGpsPosition "+ currentGpsPosition);


       // Log.e("Total Distance","Total Distance"+ TotalRouteDeviatedDistanceInMTS);
        double  TotalDistanceDeviated= TotalRouteDeviatedDistanceInMTS*100000;

       // Log.e("Total Distance","Total Distance"+ TotalDistanceDeviated);

        ETACalclator etaCalculator1=new ETACalclator();
        double resultTotalETA=etaCalculator1.cal_time(TotalDistanceDeviated, maxSpeed);
        final double resultTotalTimeConverted = DecimalUtils.round(resultTotalETA,0);
      //  Log.e("resultTotalTime ","resultTotalTimeConverted ------- "+ resultTotalTimeConverted);

        double resultTravelledTimeConverted=0.0;
        // double resultNeedToTeavelTimeConverted=0.0;
        double resultNeedToTeavelTime=0.0;
        double EtaCrossedTime = 0.0;
        double EtaElapsed = 0.0;
        String etaCrossedFlag = "NO";

        double travelledDistance = showDistance(sourcePosition, currentGpsPosition);
        String travelledDistanceInMTS = String.format("%.0f", travelledDistance);
        ETACalclator etaCalculator = new ETACalclator();
        double resultTravelledTime = etaCalculator.cal_time(travelledDistance, 10);
        resultTravelledTimeConverted = DecimalUtils.round(resultTravelledTime, 0);


        double needToTravelDistance = TotalDistanceDeviated - travelledDistance;
        String needToTravelDistanceInMTS = String.format("%.0f", needToTravelDistance);
        ETACalclator etaCalculator2 = new ETACalclator();
        resultNeedToTeavelTime = etaCalculator2.cal_time(needToTravelDistance, 10);
        resultNeedToTeavelTimeConverted = DecimalUtils.round(resultNeedToTeavelTime, 0);

       // Log.e("TAG", " currentGpsPosition @@@@ " + currentGpsPosition);
       // Log.e("TAG", " travelledDistanceInMTS " + travelledDistanceInMTS);
       // Log.e("TAG", " travelled Time  " + resultTravelledTime);
       // Log.e("TAG", "  Need To travel DistanceInMTS " + needToTravelDistanceInMTS);
       // Log.e("TAG", "  Need To travel  Time " + resultNeedToTeavelTime);
        // double presentETATime = resultTravelledTime+resultNeedToTeavelTime;
        tv2.setText("Time ETA : "+ resultNeedToTeavelTimeConverted +" SEC ");

        if (resultTravelledTimeConverted > resultTotalTimeConverted) {
            etaCrossedFlag = "YES";
            EtaCrossedTime = resultTravelledTime - resultTotalTimeConverted;
            EtaElapsed = DecimalUtils.round(EtaCrossedTime, 0);
        } else {
            etaCrossedFlag = "NO";
        }


        time.append("Distance").append(TotalDistance +" Meters ").append("\n").append("Total ETA ").append(resultTotalETA +" SEC ").append("\n").append(" Distance To Travel").append(resultNeedToTeavelTime +"Sec").append("Elapsed Time").append(EtaElapsed).append("\n");
        sendData(time.toString());

        tv.setText("Total Time: "+ resultTotalTimeConverted +" SEC" );
        tv1.setText("Time  Traveled: "+ resultTravelledTimeConverted +" SEC ");

        tv3.setText(" ETA Crossed Alert : "+ etaCrossedFlag + "  ");
    }
    public void TextImplementationRouteDeviationDirectionText(String directionTextInDeviation,String stPoint,String endPoint){
       // Log.e("TAG", "  START POSITION " + stPoint);
      //  Log.e("TAG", " END POSITION " + endPoint);
      //  Log.e("TAG", " END POSITION " + directionTextInDeviation);
        String stPoint_data=stPoint.replace("[","");
        String stPoint_data1=stPoint_data.replace("]","");
        String[] st_point=stPoint_data1.split(",");
        double st_point_lat= Double.parseDouble(st_point[1]);
        double st_point_lnag= Double.parseDouble(st_point[0]);
        LatLng st_Point_vertex=new LatLng(st_point_lat,st_point_lnag);

        String endPoint_data=endPoint.replace("[","");
        String endPoint_data1=endPoint_data.replace("]","");
        String[] end_point=endPoint_data1.split(",");
        double end_point_lat= Double.parseDouble(end_point[1]);
        double end_point_lnag= Double.parseDouble(end_point[0]);
        LatLng end_Point_vertex=new LatLng(end_point_lat,end_point_lnag);
        double Distance_To_travelIn_Vertex=showDistance(currentGpsPosition,end_Point_vertex);
        String Distance_To_travelIn_Vertex_Convetred=String.format("%.0f", Distance_To_travelIn_Vertex);

        if(directionTextInDeviation.equals("-")){

        }else {
            String data = directionTextInDeviation + " " + Distance_To_travelIn_Vertex_Convetred + "Meters";
            //String data=" in "+ DitrectionDistance +" Meters "+ directionTextFinal;
            int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
            if (speechStatus == TextToSpeech.ERROR) {
             //   Log.e("TTS", "Error in converting Text to Speech!");
            }
            // Toast.makeText(getActivity(), "" + directionTextInDeviation + " " + Distance_To_travelIn_Vertex_Convetred + "Meters", Toast.LENGTH_SHORT).show();
            LayoutInflater inflater1 = getActivity().getLayoutInflater();
            @SuppressLint("WrongViewCast") View layout = inflater1.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.textView_toast));
            TextView text = (TextView) layout.findViewById(R.id.textView_toast);

            text.setText("" + directionTextInDeviation + " " + Distance_To_travelIn_Vertex_Convetred + "Meters");
            ImageView image = (ImageView) layout.findViewById(R.id.image_toast);
            if (directionTextInDeviation.contains("Take Right")) {
                image.setImageResource(R.drawable.direction_right);
            } else if (directionTextInDeviation.contains("Take Left")) {
                image.setImageResource(R.drawable.direction_left);
            }

            Toast toast = new Toast(getActivity().getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.setGravity(Gravity.TOP, 0, 150);
            toast.setView(layout);
            toast.show();

        }

    }
    private List<LatLng> removeDuplicatesRouteDeviated(List<LatLng> EdgeWithoutDuplicatesInRouteDeviationPoints)
    {
        int count = RouteDeviationConvertedPoints.size();

        for (int i = 0; i < count; i++)
        {
            for (int j = i + 1; j < count; j++)
            {
                if (RouteDeviationConvertedPoints.get(i).equals(RouteDeviationConvertedPoints.get(j)))
                {
                    RouteDeviationConvertedPoints.remove(j--);
                    count--;
                }
            }
        }
        return EdgeWithoutDuplicatesInRouteDeviationPoints;
    }

    private void drawMarkerWithCircle(LatLng gpsPosition, double radius){
        // double radiusInMeters = 400.0;
        CircleOptions circleOptions = new CircleOptions().center(gpsPosition).radius(radius).fillColor(Color.parseColor("#2271cce7")).strokeColor(Color.parseColor("#2271cce7")).strokeWidth(3);
        mCircle = mMap.addCircle(circleOptions);

    }
    private double showDistance(LatLng latlng1, LatLng latLng2) {
        double distance = SphericalUtil.computeDistanceBetween(latlng1,latLng2);
        return distance;
    }
    public int getLatLngPoints(){

        LatLngDataArray.add(new LatLng(24.978782,55.067291));
        LatLngDataArray.add(new LatLng(24.978792,55.067279));
        LatLngDataArray.add(new LatLng(24.978762,55.067241));
        LatLngDataArray.add(new LatLng(24.978765,55.067237));
        LatLngDataArray.add(new LatLng(24.978755,55.067218));
        LatLngDataArray.add(new LatLng(24.978449,55.067310));
        LatLngDataArray.add(new LatLng(24.978656,55.066997));
        LatLngDataArray.add(new LatLng(24.978408,55.066897));
        LatLngDataArray.add(new LatLng(24.978349, 55.066801));
        LatLngDataArray.add(new LatLng(24.978025,55.066462));
        LatLngDataArray.add(new LatLng(24.977993,55.066226));

        LatLngDataArray.add(new LatLng( 24.977771, 55.066040));
        LatLngDataArray.add(new LatLng( 24.977636, 55.065907));


        LatLngDataArray.add(new LatLng(24.977358,55.065692));


        LatLngDataArray.add(new LatLng(24.977132,55.065436));
        LatLngDataArray.add(new LatLng(24.977126,55.065249));
        LatLngDataArray.add(new LatLng(24.977164,55.065171));
        LatLngDataArray.add(new LatLng(24.977257,55.064874));
        LatLngDataArray.add(new LatLng(24.977631,55.06466));
        LatLngDataArray.add(new LatLng(24.977690, 55.064490));
        LatLngDataArray.add(new LatLng(24.977881, 55.064262));
        LatLngDataArray.add(new LatLng( 24.977960, 55.064183));
        LatLngDataArray.add(new LatLng(  24.978012, 55.064151));
        LatLngDataArray.add(new LatLng(24.978098, 55.064253));
        LatLngDataArray.add(new LatLng( 24.978167, 55.064331));

        //Route Deviation points starts from here ----

        LatLngDataArray.add(new LatLng( 24.978179,55.064389)); //Route deviation point
        LatLngDataArray.add(new LatLng(24.978547,55.064227));
        LatLngDataArray.add(new LatLng( 24.978617,55.064152));
        LatLngDataArray.add(new LatLng( 24.978720,55.064048));
        LatLngDataArray.add(new LatLng(24.978816,55.063959 ));
        LatLngDataArray.add(new LatLng(24.978879,55.063874 ));
        LatLngDataArray.add(new LatLng(24.978968,55.063839 ));
        LatLngDataArray.add(new LatLng( 24.979060,55.063933));//
        LatLngDataArray.add(new LatLng( 24.979202,55.064101));
        LatLngDataArray.add(new LatLng(24.979288,55.064200 ));
        LatLngDataArray.add(new LatLng( 24.979387,55.064313));
        LatLngDataArray.add(new LatLng( 24.979527,55.064469));
        LatLngDataArray.add(new LatLng( 24.979651,55.064618));
        LatLngDataArray.add(new LatLng( 24.979770,55.064747));
        LatLngDataArray.add(new LatLng( 24.979840,55.064828));
        LatLngDataArray.add(new LatLng(24.979861,55.064973));
        LatLngDataArray.add(new LatLng( 24.979760,55.065077));
        LatLngDataArray.add(new LatLng( 24.979559,55.065288));
        LatLngDataArray.add(new LatLng( 24.979448,55.065401));
        LatLngDataArray.add(new LatLng( 24.979342,55.065516));

        //Route Deviation points are uoto here---

        LatLngDataArray.add(new LatLng(24.979304, 55.065536));
        LatLngDataArray.add(new LatLng(24.979261, 55.065570));
        LatLngDataArray.add(new LatLng(24.979722, 55.066073));
        LatLngDataArray.add(new LatLng(24.979961, 55.066314));
        LatLngDataArray.add(new LatLng(24.980189, 55.066572));
        LatLngDataArray.add(new LatLng(24.980335, 55.066770));
        LatLngDataArray.add(new LatLng(24.980174, 55.066937));
        LatLngDataArray.add(new LatLng(24.979878,55.067205));  //Destinationm point


        return LatLngDataArray.size();

    }
    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return atan2(sin(dl) * cos(f2) , cos(f1) * sin(f2) - sin(f1) * cos(f2) * cos(dl));
    }
    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if (!isNecessaryToKeepOrig) {
            bm.recycle();
        }
        return resizedBitmap;
    }
    public Bitmap addPaddingLeftForBitmap(Bitmap bitmap, int paddingLeft) {

        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingLeft, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, paddingLeft, 0, null);
        return outputBitmap;
    }

    public Bitmap addPaddingRightForBitmap(Bitmap bitmap, int paddingRight) {

        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingRight, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawColor(Color.RED);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
    public Bitmap setBounds(Bitmap bitmap,int paddingRight,int paddingLeft){
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingLeft - paddingRight, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        // canvas.drawColor(Color.RED);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap ;
    }

    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration, final LatLng currentGpsPosition) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        // Bitmap opBitMap= addPaddingLeftForBitmap(mMarkerIcon,60);
        Bitmap opBitMap= setBounds(mMarkerIcon,15,15);
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
                      computeRotation(10,beginAngle,endAngle);
                }
            }
        });
    }


    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
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


    public void addFakeGPSMarkers(){
        getLatLngPoints();
        for(int p=0;p<LatLngDataArray.size();p++){
            fakeGpsMarker =mMap.addMarker(new MarkerOptions()
                    .position(LatLngDataArray.get(p))
                    .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.symbol_shackel_point)));
            markerlist= new ArrayList<Marker>();
            markerlist.add(fakeGpsMarker);
        }
    }
    public void removeFakeGPSMarkers(){
        getLatLngPoints();
        for(int p=0;p<LatLngDataArray.size();p++) {
            if (markerlist != null && !markerlist.isEmpty()) {
                //  markerlist.get(p).remove(); // Add this line
                markerlist.remove(p);
                if(  fakeGpsMarker.getPosition().equals(LatLngDataArray.get(p))){
                    fakeGpsMarker.remove();
                }
            }
        }
    }

  //  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        if(v==location_tracking){
            Toast.makeText(getContext(), "Location Tracking button clicked.", Toast.LENGTH_SHORT).show();
            mMap.setBuildingsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabled(false);

            new Handler().postDelayed(new Runnable() {
                //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {

                }
            },5);


        } else if(v==change_map_options){

            PopupMenu popup = new PopupMenu(getContext(), change_map_options);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.popup_menu, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.slot1) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            Toast.makeText(getContext(), "NORMAL MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (itemId == R.id.slot2) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            Toast.makeText(getContext(), "SATELLITE MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (itemId == R.id.slot3) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            Toast.makeText(getContext(), "TERRAIN MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }else if (itemId == R.id.slot4) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            Toast.makeText(getContext(), "HYBRID MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                    return true;
                }
            });
            popup.show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
    public void sendTokenRequest(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String url1 = "http://86.96.196.245/ROROAPI/Login/GetToken";
                    tokenResponse = HttpPost1(url1);
                    JSONObject obj = new JSONObject(tokenResponse);
                    tokenNumber = obj.getString("tokenNumber");

                    if(tokenNumber!=null && !tokenNumber.isEmpty()){
                        String url = "http://86.96.196.245/ROROAPI/NSGMap/AlertDataProcess";
                        updaterServiceResponse = HttpPost(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 10);//just mention the time when you want to launch your action

    }
    private String HttpPost(String myUrl) throws IOException, JSONException {
        StringBuilder sbResponse=new StringBuilder();
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String basicAuth = "Bearer "+tokenNumber;
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization",basicAuth);
        //; charset=utf-8
        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();
        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        result = conn.getResponseMessage();
        // 5. return response message
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output=null;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            sbResponse.append(output).append(" ");
        }
        return sbResponse.toString();
    }
    private JSONObject buidJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("DriverID", "15022");
        jsonObject.accumulate("AlertType", "ETA");
        jsonObject.accumulate("AlertCode",  "ETACroess");
        jsonObject.accumulate("AlertName", "ETA Croessed");
        jsonObject.accumulate("AlertValue", "true");
        jsonObject.accumulate("OptionalString1",  "mobile---Application Testing From NSGI");
        jsonObject.accumulate("OptionalString1", "24.978782,55.067291");
        jsonObject.accumulate("OptionalInt1",  "24.979745, 55.067548");
        jsonObject.accumulate("OptionalInt2", "");
        jsonObject.accumulate("UserID", "nsgadmin");
        jsonObject.accumulate("ApplicationID",  "10");
        jsonObject.accumulate("CompanyUno", "2");
        jsonObject.accumulate("LanguageUno",  "1033");
        jsonObject.accumulate("Condition",  1);
        return jsonObject;
    }
    private String HttpPost1(String myUrl) throws IOException, JSONException {
        StringBuilder sbResponse=new StringBuilder();
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        JSONObject jsonObject = buidJsonObjectTokenService();
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        result = conn.getResponseMessage();
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output=null;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            sbResponse.append(output).append(" ");
        }
        return sbResponse.toString();
    }
    private JSONObject buidJsonObjectTokenService() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Username","nsgadmin");
        jsonObject.accumulate("Password","nsgadmin");
        return jsonObject;
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
    /*
    public void InsertAllRouteData(String DBCSV_PATH){
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(RouteT.TABLE_NAME).append("(routeID,startNode,endNode,routeData) values (")
                .append("'").append("RD1").append("',")
                .append("'").append("55.067291 24.978782").append("',")
                .append("'").append("55.067205 24.979878").append("',")
                .append("'").append("{\"$id\":\"1\",\"Message\":\"Sucess\",\"Status\":\"Success\",\"TotalDistance\":0.00884315523,\"Route\":[{\"$id\":\"2\",\"EdgeNo\":\"102\",\"GeometryText\":\"Take Left at Shell Trading Middle East Private Limited\",\"Geometry\":{\"$id\":\"3\",\"type\":\"LineString\",\"coordinates\":[[55.06727997182,24.9787947412557],[55.067020892000073,24.978570495000042],[55.066790925000078,24.978370131000077],[55.066620030000081,24.978221328000075],[55.06650374700007,24.97812037500006],[55.066452143000049,24.978075252000053],[55.066388841000048,24.978020054000069],[55.066216137000083,24.977870199000051],[55.06598632500004,24.97767018400009],[55.065755946000081,24.977470103000087],[55.065526233000071,24.977270178000083],[55.065312867000046,24.977084458000036]]}},{\"$id\":\"4\",\"EdgeNo\":\"1334\",\"GeometryText\":\"Take Right at\",\"Geometry\":{\"$id\":\"5\",\"type\":\"LineString\",\"coordinates\":[[55.065312867000046,24.977084458000036],[55.065287629000068,24.977076221000061],[55.065261227000065,24.97707199000007],[55.065234420000081,24.97707188600009],[55.065207979000036,24.977075912000089],[55.065182665000066,24.97708395300009],[55.065159206000033,24.977095778000091],[55.065138276000084,24.977111045000072],[55.065138276000084,24.977111045000072],[55.065120166000042,24.977128114000038],[55.064756250000073,24.977475793000053],[55.064379641000073,24.977835331000051],[55.064249201000052,24.977960644000063]]}},{\"$id\":\"6\",\"EdgeNo\":\"383\",\"GeometryText\":\"Take Right at\",\"Geometry\":{\"$id\":\"7\",\"type\":\"LineString\",\"coordinates\":[[55.064249201000052,24.977960644000063],[55.064238539000087,24.977972603000069],[55.064230288000033,24.977986052000062],[55.064224693000085,24.978000592000058],[55.064221918000044,24.978015793000054],[55.064222048000033,24.978031201000078],[55.064222048000033,24.978031201000078],[55.064387059000069,24.978174369000044],[55.064439134000054,24.978219639000088]]}},{\"$id\":\"8\",\"EdgeNo\":\"405\",\"GeometryText\":\"Take Right at\",\"Geometry\":{\"$id\":\"9\",\"type\":\"LineString\",\"coordinates\":[[55.064439134000054,24.978219639000088],[55.064525820000085,24.978294996000045],[55.064525820000085,24.978294996000045],[55.064649532000033,24.978402540000047],[55.06498055600008,24.978690915000072]]}},{\"$id\":\"10\",\"EdgeNo\":\"413\",\"GeometryText\":\"Take Right at\",\"Geometry\":{\"$id\":\"11\",\"type\":\"LineString\",\"coordinates\":[[55.06498055600008,24.978690915000072],[55.065164137000068,24.978850842000043],[55.065338824000037,24.979002188000038],[55.065338824000037,24.979002188000038],[55.065422408000074,24.979074604000061],[55.065573362000066,24.979205705000084]]}},{\"$id\":\"12\",\"EdgeNo\":\"396\",\"GeometryText\":\"Take Right at\",\"Geometry\":{\"$id\":\"13\",\"type\":\"LineString\",\"coordinates\":[[55.065573362000066,24.979205705000084],[55.065666012000065,24.979286171000069],[55.065666012000065,24.979286171000069],[55.065681098000084,24.979299272000048],[55.065938324000058,24.979522600000053],[55.066002768000033,24.979578645000061]]}},{\"$id\":\"14\",\"EdgeNo\":\"423\",\"GeometryText\":\"Take Right at\",\"Geometry\":{\"$id\":\"15\",\"type\":\"LineString\",\"coordinates\":[[55.066002768000033,24.979578645000061],[55.066081442000041,24.979647065000051],[55.066081442000041,24.979647065000051],[55.066110416000072,24.979672262000065],[55.066245676000051,24.979789959000072]]}},{\"$id\":\"16\",\"EdgeNo\":\"440\",\"GeometryText\":\"Take Right at\",\"Geometry\":{\"$id\":\"17\",\"type\":\"LineString\",\"coordinates\":[[55.066245676000051,24.979789959000072],[55.06634370900008,24.979875263000054],[55.06634370900008,24.979875263000054],[55.066752725000072,24.980231166000067]]}},{\"$id\":\"18\",\"EdgeNo\":\"454\",\"GeometryText\":\"Take Left at\",\"Geometry\":{\"$id\":\"19\",\"type\":\"LineString\",\"coordinates\":[[55.066752725000072,24.980231166000067],[55.066772902000082,24.980240215000038],[55.066794299000037,24.98024651500009],[55.066816470000049,24.980249936000064],[55.066838951000079,24.980250405000049],[55.066861270000061,24.980247913000085]]}},{\"$id\":\"20\",\"EdgeNo\":\"443\",\"GeometryText\":\"-\",\"Geometry\":{\"$id\":\"21\",\"type\":\"LineString\",\"coordinates\":[[55.066861270000061,24.980247913000085],[55.0672260238388,24.9799000715094]]}}]}").append("'");
        sqlHandler.executeQuery(query.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void InsertAllRouteData(String DBCSV_PATH){
        File file = new File(DBCSV_PATH);
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        Log.e("OUTPUT FILE","OUTPUT FILE"+file);
        if(file.exists()) {
            try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
                CsvRow row;
                while ((row = csvParser.nextRow()) != null) {
                    System.out.println("Read line: " + row);
                    String ID = row.getField("ID");
                    String RouteID = row.getField("RouteID");
                    String startNode = row.getField("StartPoint");
                    String endNode = row.getField("EndPoint");
                    String routeData = row.getField("Route");

                    StringBuilder query = new StringBuilder("INSERT INTO ");
                    query.append(RouteT.TABLE_NAME).append("(routeID,startNode,endNode,routeData) values (")
                            .append("'").append(RouteID).append("',")
                            .append("'").append(startNode).append("',")
                            .append("'").append(endNode).append("',")
                            .append("'").append(routeData).append("')");

                    sqlHandler.executeQuery(query.toString());
                    sqlHandler.closeDataBaseConnection();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
       }else{

       }

    }
*/

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
    @Override
    public void onSensorChanged(final SensorEvent event) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                degree = Math.round(event.values[0]);

            }
        }, 100);



    }

    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

    }

    public static Bitmap RotateBitmap(Bitmap source, float azimuth) {
        Bitmap outputBitmap;
        Matrix matrix=new Matrix();
        matrix.postRotate(azimuth);
        outputBitmap=Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return outputBitmap;
    }
    public void RotateBitmap(Marker marker, float mCurrentDegree, float azimuthInDegress){
        Matrix matrix = new Matrix();
        matrix.postRotate(azimuthInDegress);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onResume() {
        super.onResume();
        if (mSensorManager != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }
    public String NavigationDirection(final LatLng currentGpsPosition, LatLng DestinationPosition) {
        final String shortestDistancePoint = "";

                ArrayList<Double> EdgeDistancesList=new ArrayList<Double>();
                HashMap EdgeDistancesMap=new HashMap<String,String>();
                String stPoint = "", endPoint = "", geometryTextimpValue = "", distanceInEdge = "";
                String position="";
                for(int k=0;k<EdgeContainsDataList.size();k++){
                    EdgeDataT edgeK=EdgeContainsDataList.get(k);
                    StringBuilder sb=new StringBuilder();
                    sb.append("STPOINT :"+edgeK.getStartPoint()+"EndPt:"+edgeK.getEndPoint()+"Points:"+edgeK.getPositionMarkingPoint()+"Geometry TEXT:"+ edgeK.getGeometryText());

                    String pointDataText=edgeK.getPositionMarkingPoint();
                    String stPoint_data=pointDataText.replace("lat/lng: (","");
                    String stPoint_data1=stPoint_data.replace(")","");
                    String[] st_point=stPoint_data1.split(",");
                    double st_point_lat= Double.parseDouble(st_point[1]);
                    double st_point_lnag= Double.parseDouble(st_point[0]);
                    LatLng st_Point_vertex_main=new LatLng(st_point_lnag,st_point_lat);
                    double distanceOfCurrentPosition=showDistance(st_Point_vertex_main,currentGpsPosition);
                    EdgeDistancesList.add(distanceOfCurrentPosition);
                    EdgeDistancesMap.put(String.valueOf(distanceOfCurrentPosition).trim(),String.valueOf(pointDataText));

                    Collections.sort(EdgeDistancesList);
                }

                     GetSortetPoint(EdgeDistancesMap,EdgeDistancesList, currentGpsPosition );

        return shortestDistancePoint;

    }
    public String  GetSortetPoint(HashMap EdgeDistancesMap, ArrayList<Double>  EdgeDistancesList, LatLng currentGpsPosition ){
        String vfinalValue = "";
        String FirstShortestDistance = String.valueOf(EdgeDistancesList.get(0));
        boolean verify=EdgeDistancesMap.containsKey(FirstShortestDistance.trim());
        if(verify){

            Object Value =String.valueOf( EdgeDistancesMap.get(FirstShortestDistance));
            vfinalValue= String.valueOf(EdgeDistancesMap.get(FirstShortestDistance));

        } else{
            System.out.println("Key not matched with ID");
        }
        EdgesEndContaingData(currentGpsPosition,vfinalValue);

        return vfinalValue;
    }
    public void EdgesEndContaingData(LatLng currentGpsPosition, String shortestDistancePoint){
        String stPoint = "", endPoint = "", geometryTextimpValue = "", distanceInEdge = "";
        String position="";
        int indexPosition=0;
        EdgeDataT edgeCurrentPoint= null;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<EdgeContainsDataList.size();i++){
             edgeCurrentPoint=EdgeContainsDataList.get(i);
             position=edgeCurrentPoint.getPositionMarkingPoint();
            if(position.equals(shortestDistancePoint)){
                distanceInEdge=EdgeContainsDataList.get(i).getGeometryText();
                stPoint=  EdgeContainsDataList.get(i).getStartPoint();
                endPoint= EdgeContainsDataList.get(i).getEndPoint();
                geometryTextimpValue=EdgeContainsDataList.get(i).getGeometryText();
            }else{
            }
        }

        String stPoint_data=stPoint.replace("[","");
        String stPoint_data1=stPoint_data.replace("]","");
        String[] st_point=stPoint_data1.split(",");
        double st_point_lat= Double.parseDouble(st_point[1]);
        double st_point_lnag= Double.parseDouble(st_point[0]);
        LatLng st_Point_vertex=new LatLng(st_point_lat,st_point_lnag);

        String endPoint_data=endPoint.replace("[","");
        String endPoint_data1=endPoint_data.replace("]","");
        String[] end_point=endPoint_data1.split(",");
        double end_point_lat= Double.parseDouble(end_point[1]);
        double end_point_lnag= Double.parseDouble(end_point[0]);
        LatLng end_Point_vertex=new LatLng(end_point_lat,end_point_lnag);
        double Distance_To_travelIn_Vertex=showDistance(currentGpsPosition,end_Point_vertex);
        String Distance_To_travelIn_Vertex_Convetred=String.format("%.0f", Distance_To_travelIn_Vertex);
        if(geometryTextimpValue.equals("-")){

        }else {
            String data = geometryTextimpValue + " " + Distance_To_travelIn_Vertex_Convetred + "Meters";
            //String data=" in "+ DitrectionDistance +" Meters "+ directionTextFinal;
            int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
            if (speechStatus == TextToSpeech.ERROR) {
                Log.e("TTS", "Error in converting Text to Speech!");
            }
           // Toast.makeText(getActivity(), "" + geometryTextimpValue + " " + Distance_To_travelIn_Vertex_Convetred + "Meters", Toast.LENGTH_SHORT).show();
            LayoutInflater inflater1 = getActivity().getLayoutInflater();
            @SuppressLint("WrongViewCast") View layout = inflater1.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.textView_toast));
            TextView text = (TextView) layout.findViewById(R.id.textView_toast);

            text.setText("" + geometryTextimpValue + " " + Distance_To_travelIn_Vertex_Convetred + "Meters");
            ImageView image = (ImageView) layout.findViewById(R.id.image_toast);
            if (geometryTextimpValue.contains("Take Right")) {
                image.setImageResource(R.drawable.direction_right);
            } else if (geometryTextimpValue.contains("Take Left")) {
                image.setImageResource(R.drawable.direction_left);
            }

            Toast toast = new Toast(getActivity().getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.setGravity(Gravity.TOP, 0, 180);
            toast.setView(layout);
            toast.show();

        }

    }
    public void caclulateETA(final double TotalDistance, final LatLng sourcePosition, final LatLng currentGpsPosition, LatLng DestinationPosition){

       // Log.e("Total Distance","Total Distance"+ TotalDistanceInMTS);
        ETACalclator etaCalculator1=new ETACalclator();
        double resultTotalETA=etaCalculator1.cal_time(TotalDistanceInMTS, maxSpeed);
        final double resultTotalTimeConverted = DecimalUtils.round(resultTotalETA,0);
        //Log.e("resultTotalTime ","resultTotalTimeConverted ------- "+ resultTotalTimeConverted);

        double resultTravelledTimeConverted=0.0;
       // double resultNeedToTeavelTimeConverted=0.0;
        double resultNeedToTeavelTime=0.0;
        double EtaCrossedTime = 0.0;
        double EtaElapsed = 0.0;
        String etaCrossedFlag = "NO";

        double travelledDistance = showDistance(sourcePosition, currentGpsPosition);
        String travelledDistanceInMTS = String.format("%.0f", travelledDistance);
        ETACalclator etaCalculator = new ETACalclator();
        double resultTravelledTime = etaCalculator.cal_time(travelledDistance, 10);
        resultTravelledTimeConverted = DecimalUtils.round(resultTravelledTime, 0);


        double needToTravelDistance = TotalDistance - travelledDistance;
        String needToTravelDistanceInMTS = String.format("%.0f", needToTravelDistance);
        ETACalclator etaCalculator2 = new ETACalclator();
        resultNeedToTeavelTime = etaCalculator2.cal_time(needToTravelDistance, 10);
        resultNeedToTeavelTimeConverted = DecimalUtils.round(resultNeedToTeavelTime, 0);

        // double presentETATime = resultTravelledTime+resultNeedToTeavelTime;
        tv2.setText("Time ETA : "+ resultNeedToTeavelTimeConverted +" SEC ");

        if (resultTravelledTimeConverted > resultTotalTimeConverted) {
            etaCrossedFlag = "YES";
            EtaCrossedTime = resultTravelledTime - resultTotalTimeConverted;
            EtaElapsed = DecimalUtils.round(EtaCrossedTime, 0);
        } else {
            etaCrossedFlag = "NO";
        }


        time.append("Distance").append(TotalDistance +" Meters ").append("\n").append("Total ETA ").append(resultTotalETA +" SEC ").append("\n").append(" Distance To Travel").append(resultNeedToTeavelTime +"Sec").append("Elapsed Time").append(EtaElapsed).append("\n");
        sendData(time.toString());

        tv.setText("Total Time: "+ resultTotalTimeConverted +" SEC" );
        tv1.setText("Time  Traveled: "+ resultTravelledTimeConverted +" SEC ");

        tv3.setText(" ETA Crossed Alert : "+ etaCrossedFlag + "  ");
    }
    public int getLatLngPointsForRoute_1(){

        LatLngDataArray.add(new LatLng(24.978782,55.067291));
        LatLngDataArray.add(new LatLng(24.978792,55.067279));
        LatLngDataArray.add(new LatLng(24.978762,55.067241));
        LatLngDataArray.add(new LatLng(24.978765,55.067237));
        LatLngDataArray.add(new LatLng(24.978755,55.067218));
        LatLngDataArray.add(new LatLng(24.978449,55.067310));
        LatLngDataArray.add(new LatLng(24.978656,55.066997));
        LatLngDataArray.add(new LatLng(24.978408,55.066897));
        LatLngDataArray.add(new LatLng(24.978025,55.066462));
        LatLngDataArray.add(new LatLng(24.977993,55.066226));
        LatLngDataArray.add(new LatLng(24.97761,55.065815));
        LatLngDataArray.add(new LatLng(24.977358,55.065692));
        LatLngDataArray.add(new LatLng(24.977132,55.065436));
        LatLngDataArray.add(new LatLng(24.977126,55.065249));
        LatLngDataArray.add(new LatLng(24.977164,55.065171));
        LatLngDataArray.add(new LatLng(24.977257,55.064874));
        LatLngDataArray.add(new LatLng(24.977631,55.06466));
        LatLngDataArray.add(new LatLng(24.977690, 55.064490));
        LatLngDataArray.add(new LatLng(24.977881, 55.064262));
        LatLngDataArray.add(new LatLng( 24.977960, 55.064183));

        LatLngDataArray.add(new LatLng(  24.978012, 55.064151));



        LatLngDataArray.add(new LatLng(24.978098, 55.064253));
        LatLngDataArray.add(new LatLng( 24.978167, 55.064331));
        //Route Deviation points starts from here ----
        LatLngDataArray.add(new LatLng( 24.978179,55.064389)); //Route deviation point
        LatLngDataArray.add(new LatLng(24.978547,55.064227));
        LatLngDataArray.add(new LatLng( 24.978617,55.064152));
        LatLngDataArray.add(new LatLng( 24.978720,55.064048));
        LatLngDataArray.add(new LatLng(24.978816,55.063959 ));
        LatLngDataArray.add(new LatLng(24.978879,55.063874 ));
        LatLngDataArray.add(new LatLng(24.978968,55.063839 ));
        LatLngDataArray.add(new LatLng( 24.979060,55.063933));//
        LatLngDataArray.add(new LatLng( 24.979202,55.064101));
        LatLngDataArray.add(new LatLng(24.979288,55.064200 ));
        LatLngDataArray.add(new LatLng( 24.979387,55.064313));
        LatLngDataArray.add(new LatLng( 24.979527,55.064469));
        LatLngDataArray.add(new LatLng( 24.979651,55.064618));
        LatLngDataArray.add(new LatLng( 24.979770,55.064747));
        LatLngDataArray.add(new LatLng( 24.979840,55.064828));
        LatLngDataArray.add(new LatLng(24.979861,55.064973));
        LatLngDataArray.add(new LatLng( 24.979760,55.065077));
        LatLngDataArray.add(new LatLng( 24.979559,55.065288));
        LatLngDataArray.add(new LatLng( 24.979448,55.065401));
        LatLngDataArray.add(new LatLng( 24.979342,55.065516));

        //Route Deviation points are upto here---

        LatLngDataArray.add(new LatLng(24.979722, 55.066073));
        LatLngDataArray.add(new LatLng(24.979961, 55.066314));
        LatLngDataArray.add(new LatLng(24.980189, 55.066572));
        LatLngDataArray.add(new LatLng(24.980335, 55.066770));
        LatLngDataArray.add(new LatLng(24.980174, 55.066937));
        LatLngDataArray.add(new LatLng(24.979878,55.067205));  //Destinationm point
        return LatLngDataArray.size();

    }
    public int getLatLngPointsForRoute_2(){
        LatLngDataArray.add(new LatLng(24.981071,55.067708));
        LatLngDataArray.add(new LatLng(24.981227,55.067913));
        LatLngDataArray.add(new LatLng(24.981518,55.068258));
        LatLngDataArray.add(new LatLng(24.981922,55.068711));
        LatLngDataArray.add(new LatLng(24.982424,55.069294));
        LatLngDataArray.add(new LatLng(24.982787,55.069716));
        LatLngDataArray.add(new LatLng(24.983160,55.070151));
        LatLngDataArray.add(new LatLng(24.983335,55.070321));
        return LatLngDataArray.size();
    }

}

