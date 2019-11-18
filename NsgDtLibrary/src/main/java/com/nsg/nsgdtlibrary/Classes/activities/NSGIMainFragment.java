package com.nsg.nsgdtlibrary.Classes.activities;

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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.nsg.nsgdtlibrary.Classes.database.db.SqlHandler;
import com.nsg.nsgdtlibrary.Classes.database.dto.EdgeDataT;
import com.nsg.nsgdtlibrary.Classes.util.DecimalUtils;
import com.nsg.nsgdtlibrary.Classes.util.ETACalclator;
import com.nsg.nsgdtlibrary.Classes.util.Util;
import com.nsg.nsgdtlibrary.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

/**
 * Created by sailaja.ch NSGI on 27/09/2019 *
 * Modified on 14/11/2019
 *
 */
public class NSGIMainFragment extends Fragment implements View.OnClickListener {
    private String BASE_MAP_URL_FORMAT;
    private ProgressDialog dialog;
    private TextToSpeech textToSpeech;
    private LatLng SourcePosition, DestinationPosition;
    private double sourceLat, sourceLng, destLat, destLng;
    private String SourcePoint,DestinationPoint,tokenResponse,etaResponse;
    private Marker markerSource, markerDestination,mPositionMarker;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    private GoogleMap.CancelableCallback callback;
    private List points;
    private List<LatLng> convertedPoints;
    private LatLng currentGpsPosition;
    private  StringBuilder sb = new StringBuilder();
    private Marker sourceMarker,destinationMarker;
    private List<EdgeDataT> edgeDataList;
    private Handler handler = new Handler();
    private int enteredMode,routeDeviationDistance;
    private List<LatLng> LatLngDataArray=new ArrayList<LatLng>();
    private String currentGpsPoint;
    private Circle mCircle;
    private LatLng nearestPositionPoint;
    private Bitmap mMarkerIcon;
    private int mIndexCurrentPoint=0;
    private List<LatLng> edgeDataPointsList ;
    private Map<String, List> mapOfLists = new HashMap<String, List>();
    private List AllPointsList ;
    private HashMap<String,String> AllPointEdgeNo;
    private LatLng newCenterLatLng,PointData;
    private List distancesList;
    private List distanceValuesList;
    private HashMap<String, String> hash_map;
    private List<LatLng> nearestPointValuesList;
    private Marker gpsMarker;
    private TextView tv,tv1,tv2,tv3,tv4;
    private String MESSAGE;
    private ToggleButton fakeGpsListener;
    Marker fakeGpsMarker;
    List<Marker> markerlist;
    ArrayList<String> etaList;
    private ArrayList lastDistancesList;
    private double lastDistance;
    private ImageButton change_map_options;
    String tokenNumber,updaterServiceResponse;
    private String geometryDirectionText="",key="";
    HashMap<String,String>nearestValuesMap;
    private List<LatLng> OldNearestGpsList;
    private int locationFakeGpsListener=0;
    private double vehicleSpeed;
    private double maxSpeed=30;
    private boolean isMarkerRotating=false;
    StringBuilder time= new StringBuilder();
    public interface FragmentToActivity { String communicate(String comm);}
    private NSGIMainFragment.FragmentToActivity Callback;
    public NSGIMainFragment() { }
    @SuppressLint("ValidFragment")
    public NSGIMainFragment(String BASE_MAP_URL_FORMAT,double v1, double v2, double v3, double v4, int mode, int radius ) {
        NSGIMainFragment.this.BASE_MAP_URL_FORMAT=BASE_MAP_URL_FORMAT;
        NSGIMainFragment.this.SourcePosition = new LatLng(v1, v2);
        NSGIMainFragment.this.DestinationPosition = new LatLng(v4, v3);
        NSGIMainFragment.this.sourceLat = v2;
        NSGIMainFragment.this.sourceLng = v1;
        NSGIMainFragment.this.destLat = v4;
        NSGIMainFragment.this.destLng =v3;
        NSGIMainFragment.this.enteredMode = mode;
        NSGIMainFragment.this.routeDeviationDistance=radius;
        NSGIMainFragment.this.SourcePoint=String.valueOf(v1).concat(" ").concat(String.valueOf(v2));
        NSGIMainFragment.this.DestinationPoint=String.valueOf(v3).concat(" ").concat(String.valueOf(v4));
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
                    } else {
                    }
                } else {
                }
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sqlHandler = new SqlHandler(getContext());
            Callback = (FragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.gps_transperent_98);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        tv=(TextView)rootView.findViewById(R.id.tv);
        tv1=(TextView)rootView.findViewById(R.id.tv1);
        tv2=(TextView)rootView.findViewById(R.id.tv2);
        tv3=(TextView)rootView.findViewById(R.id.tv3);
        tv4=(TextView)rootView.findViewById(R.id.tv4);
        fakeGpsListener=(ToggleButton)rootView.findViewById(R.id.fakeGps);
        fakeGpsListener.setOnClickListener(this);
        change_map_options = (ImageButton)rootView.findViewById(R.id.change_map_options);
        change_map_options.setOnClickListener(this);
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
                NSGIMainFragment.this.mMap.setBuildingsEnabled(true);
                if (Util.isInternetAvailable(getActivity()) == true && mMap != null ) {
                    dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                    dialog.setMessage("Fetching Route");
                    dialog.setMax(100);
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            GetRouteDetails();
                            if(MESSAGE!=null && MESSAGE.equals("Sucess")){
                                sendTokenRequest();
                                getAllEdgesData();
                                addMarkers();
                                getValidRouteData();
                                dialog.dismiss();
                                nearestPointValuesList=new ArrayList<LatLng>();
                                nearestPointValuesList.add(new LatLng(sourceLat,sourceLng));
                                OldNearestGpsList=new ArrayList<>();
                                OldNearestGpsList.add(new LatLng(sourceLat,sourceLng));
                                if(enteredMode==1 &&edgeDataList!=null && edgeDataList.size()>0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                     return;
                                            }
                                            mMap.setMyLocationEnabled(true);
                                            mMap.getUiSettings().setZoomControlsEnabled(false);
                                            mMap.getUiSettings().setCompassEnabled(true);
                                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                            mMap.getUiSettings().setMapToolbarEnabled(true);
                                            mMap.getUiSettings().setZoomGesturesEnabled(true);
                                            mMap.getUiSettings().setScrollGesturesEnabled(true);
                                            mMap.getUiSettings().setTiltGesturesEnabled(true);
                                            mMap.getUiSettings().setRotateGesturesEnabled(true);
                                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                @Override
                                                public void onMyLocationChange(Location location) {
                                                    if (mPositionMarker != null) {
                                                        mPositionMarker.remove();
                                                    }
                                                    // currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                                    vehicleSpeed =location.getSpeed();
                                                    getLatLngPoints();
                                                    currentGpsPosition = LatLngDataArray.get(locationFakeGpsListener);
                                                    MoveWithGpsPointInBetWeenAllPoints(currentGpsPosition);
                                                    locationFakeGpsListener = locationFakeGpsListener + 1;
                                                }
                                            });
                                        }

                                }else if(enteredMode==2){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            return;
                                        }
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
                                        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                            @Override
                                            public void onMyLocationChange(Location location) {
                                                if (mPositionMarker != null) {
                                                    mPositionMarker.remove();
                                                }
                                                currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                                mPositionMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(currentGpsPosition)
                                                        .title("currentLocation"));

                                                moveVechile(mPositionMarker,location);
                                                //animateMarkerToFinalDestination(mPositionMarker, currentGpsPosition, new LatLngInterpolator.LinearFixed());
                                                CameraPosition googlePlex = CameraPosition.builder()
                                                        .target(currentGpsPosition)
                                                        .zoom(18)
                                                        .tilt(45)
                                                        .build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);


                                            }
                                        });
                                    }

                                }
                            }else{
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Not Able to get Route from Service", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 30);
                } else {
                    Toast.makeText(getActivity(), "please turn on wifi/mobiledata", Toast.LENGTH_LONG).show();
                }
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
        Log.e("","SendData "+ comm);
        Callback.communicate(comm);

    }
    private  List<EdgeDataT> getAllEdgesData() {
        String query = "SELECT * FROM " + EdgeDataT.TABLE_NAME;
        Cursor c1 = sqlHandler.selectQuery(query);
        edgeDataList = (List<EdgeDataT>) SqlHandler.getDataRows(EdgeDataT.MAPPING, EdgeDataT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return edgeDataList;
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
        LatLng position1= new LatLng(sourceLat,sourceLng);
        sourceMarker = mMap.addMarker(new MarkerOptions()
                .position(position1)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.source_red)));
        CameraPosition googlePlex = CameraPosition.builder()
                .target(position1)
                .zoom(18)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
        LatLng position2= new LatLng(destLat,destLng);
        destinationMarker= mMap.addMarker(new MarkerOptions()
                .position(position2)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.destination_green)));
        CameraPosition googlePlex1 = CameraPosition.builder()
                .target(position2)
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
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT();
                edge = edgeDataList.get(i);
                int edgeNo = edge.getEdgeNo();
                String stPoint = edge.getStartPoint();
                String endPoint = edge.getEndPoint();
                String points = edge.getAllPoints();
                String geometryText=edge.getGeometryText();
                if(points!=null){
                    String AllPoints = points.replace("[", "");
                    AllPoints = AllPoints.replace("]", "");
                    String[] AllPointsArray = AllPoints.split(", ");
                    Log.e("ALL POINTS", "ALL POINTS" + AllPointsArray.length);
                    for (int ap = 0; ap < AllPointsArray.length; ap++) {

                        String data = String.valueOf(AllPointsArray[ap]);
                        String dataStr = data.replace("[", "");
                        dataStr = dataStr.replace("]", "");
                        String ptData[] = dataStr.split(",");
                        double Lat = Double.parseDouble(ptData[0]);
                        double Lang = Double.parseDouble(ptData[1]);
                        PointData = new LatLng(Lat, Lang);
                        AllPointEdgeNo.put(String.valueOf(PointData),geometryText);
                        AllPointsList.add(AllPointsArray[ap]);
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
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void MoveWithGpsPointInBetWeenAllPoints(LatLng currentGpsPosition){
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
                double distance = distFrom(PositionMarkingPoint.latitude,PositionMarkingPoint.longitude,currentGpsPosition.longitude,currentGpsPosition.latitude);
                 hash_map.put(String.valueOf(distance), String.valueOf(EdgeWithoutDuplicates.get(epList)));
                 distancesList.add(distance);
                 Collections.sort(distancesList);
            }
            for(int i=0;i<distancesList.size();i++) {
            }

            String FirstShortestDistance = String.valueOf(distancesList.get(0));
            String SecondShortestDistance = String.valueOf(distancesList.get(1));
            boolean answerFirst= hash_map.containsKey(FirstShortestDistance);
            if (answerFirst) {
                System.out.println("The list contains " + FirstShortestDistance);
                FirstCordinate = (String)hash_map.get(FirstShortestDistance);
                key= String.valueOf(getKeysFromValue(AllPointEdgeNo,FirstCordinate));
                Log.e("KEY ", "KEY " + key);
            } else {
                System.out.println(""+ "FALSE");
            }
            boolean answerSecond= hash_map.containsKey(SecondShortestDistance);
            if (answerSecond) {
                System.out.println("" + SecondShortestDistance);
                SecondCordinate = (String)hash_map.get(SecondShortestDistance);

            } else {
                System.out.println(" "+ "FALSE");
            }
            String First= FirstCordinate.replace("lat/lng: (","");
            First= First.replace(")","");
            String[] FirstLatLngsData=First.split(",");
            double FirstLatitude= Double.valueOf(FirstLatLngsData[0]);
            double FirstLongitude= Double.valueOf(FirstLatLngsData[1]);
            geometryDirectionText=key;
            String Second= SecondCordinate.replace("lat/lng: (","");
            Second= Second.replace(")","");
            String[] SecondLatLngsData=Second.split(",");
            double SecondLatitude= Double.valueOf(SecondLatLngsData[0]);
            double SecondLongitude= Double.valueOf(SecondLatLngsData[1]);

            double x= currentGpsPosition.longitude;
            double y= currentGpsPosition.longitude;

            LatLng source=new LatLng(FirstLongitude,FirstLatitude);
            LatLng destination=new LatLng(SecondLongitude,SecondLatitude);

            nearestPositionPoint= findNearestPoint(currentGpsPosition,source,destination);
            OldNearestGpsList.add(nearestPositionPoint);

        }
        for(int i=0;i<OldNearestGpsList.size();i++){

        }
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

        if(currentGpsPosition.equals(LatLngDataArray.get(LatLngDataArray.size()-1))){
            nearestPointValuesList.add(DestinationPosition);
        }
        CameraPosition googlePlex = CameraPosition.builder()
                .target(nearestPositionPoint)
                .zoom(20)
                .tilt(45)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(OldGps)
                .title("currentLocation")
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.red_marker_24)));

        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(nayaGps)
                .title("currentLocation")
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.green_marker_24)));


        float bearing = (float) bearingBetweenLocations(OldGps,nayaGps);
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(nearestPositionPoint)
                .title("currentLocation")
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent_98)).flat(true).rotation(bearing).anchor(0.5f, 0.5f));

        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(new LatLng(nearestPositionPoint.latitude, nearestPositionPoint.longitude))
                .bearing(bearing).tilt(65.5f).zoom(20)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
        if(nearestPointValuesList.size()>1) {
            getTextImplementation(currentGpsPosition,new LatLng(24.979878,55.067205));
            verifyRouteDeviation(50);
            LatLng centeredLatLng= animateLatLngZoom(nearestPositionPoint,20,10,10);
            animateCarMove(mPositionMarker, OldGps,centeredLatLng, 5000);
        }
    }
    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }
    public void moveVechile(final Marker myMarker, final Location finalPosition) {

        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                myMarker.setRotation(finalPosition.getBearing());
                myMarker.setAnchor(0.5f,0.5f);


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });

    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing =  -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private LatLng animateLatLngZoom(LatLng latlng, int reqZoom, int offsetX, int offsetY) {
        float originalZoom = mMap.getCameraPosition().zoom;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(reqZoom));
        Point pointInScreen = mMap.getProjection().toScreenLocation(latlng);
        Point newPoint = new Point();
        newPoint.x = pointInScreen.x - offsetX;
        newPoint.y = pointInScreen.y + offsetY;
        newCenterLatLng = mMap.getProjection().fromScreenLocation(newPoint);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng, reqZoom));
        return newCenterLatLng;
    }


    public void getTextImplementation(LatLng currentGpsPosition,LatLng DestinationPosition){
        double resultTotalDistance=showDistance(new LatLng(sourceLat,sourceLng),new LatLng(destLat,destLng));
        int GpsIndex=OldNearestGpsList.indexOf(nearestPositionPoint);
        LatLng cameraPosition=OldNearestGpsList.get(GpsIndex);
        String finalResultMts=String.format("%.0f", resultTotalDistance);
        ETACalclator calculator=new ETACalclator();
        double resultTotalTime=calculator.cal_time(resultTotalDistance, maxSpeed);
        resultTotalTime=DecimalUtils.round(resultTotalTime,0);
        int seconds = (int) ((resultTotalTime / 1000) % 60);
        int minutes = (int) ((resultTotalTime / 1000) / 60);

        String directionText= String.valueOf(getKeysFromValue(nearestValuesMap,cameraPosition.toString()));
        Log.e("Direction Text","Direction Text"+directionText);
        double resultRaminingDistance=showDistance(currentGpsPosition,new LatLng(destLat,destLng));
        ETACalclator etaCalculator=new ETACalclator();
        double speedKMPH= mphTOkmph(vehicleSpeed);
        String finalSpeed=String.format("%.0f", speedKMPH);

        double resultTime=etaCalculator.cal_time(resultRaminingDistance, speedKMPH);
        // resultTime=DecimalUtils.round(resultTime,1);
        String resultDistanceMts=String.format("%.0f", resultRaminingDistance);
        double elapsedTime = resultTotalTime-resultTime;



        time.append("Distance").append(finalResultMts+"Meters").append("\n").append("Speed").append(maxSpeed +"KMPH").append("\n").append("Estimated Time").append(resultTime+"Sec").append("Elapsed Time").append(elapsedTime).append("\n");
        sendData(time.toString());

        tv.setText("Estimated Time : "+ resultTotalTime +"Sec" );
        tv1.setText("Distance : "+ resultDistanceMts +" Meters ");
        tv2.setText("Speed : "+ finalSpeed +"KM ");
        tv3.setText("Direction : "+  directionText);


        //Speech implementation
        String data=" in "+ resultDistanceMts +" Meters "+ directionText;
        int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
        if(time!=null){
            sendTokenRequest();
        }
        // Log.e("Direction Text","Direction Text"+currentGpsPoint);
        // LatLng lastPoint=OldNearestGpsList.get(OldNearestGpsList.size()-1);
        // Log.e("last Point","last Point--------- "+ lastPoint);
        Log.e("currentGpsPositiont","currentGpsPosition--------- "+ currentGpsPosition);
        Log.e("currentGpsPositiont","currentGpsPosition--------- "+ DestinationPosition);
        if (currentGpsPosition.equals(DestinationPosition)) {
            Log.e("last Point","last Point--------- "+ "TRUE");
            lastDistance= showDistance(cameraPosition,DestinationPosition);
            Log.e("lastDistance","lastDistance--------- "+ lastDistance );
            if (lastDistance <5) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                Intent i=new Intent(getActivity(),NSGIMainFragment.class);
                                startActivity(i);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }else{
            Log.e("last Point","last Point--------- "+ "FALSE");
        }
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
    double mphTOkmph(double mph)
    {
        return mph * 1.60934;
    }
    private void GetRouteDetails(){
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy =
                                new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            String httprequest = "http://202.53.11.74/dtnavigation/api/routing/routenavigate";
                            String FeatureResponse = HttpPost(httprequest,SourcePoint,DestinationPoint);
                            JSONObject jsonObject = null;
                            try {
                                if(FeatureResponse!=null){
                                    String delQuery = "DELETE  FROM " + EdgeDataT.TABLE_NAME;
                                    sqlHandler.executeQuery(delQuery.toString());
                                    jsonObject = new JSONObject(FeatureResponse);
                                    String ID = String.valueOf(jsonObject.get("$id"));
                                    MESSAGE = jsonObject.getString("Message");
                                    String Status = jsonObject.getString("Status");
                                    String TotalDistance = jsonObject.getString("TotalDistance");
                                    JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                                    for (int i = 0; i < jSonRoutes.length(); i++) {
                                        points=new ArrayList();
                                        convertedPoints=new ArrayList<LatLng>();
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
                                        stPoint=stPoint.replace("[","");
                                        stPoint=stPoint.replace("]","");
                                        String [] firstPoint=stPoint.split(",");
                                        Double stPointLat= Double.valueOf(firstPoint[0]);
                                        Double stPointLongi= Double.valueOf(firstPoint[1]);
                                        LatLng stVertex=new LatLng(stPointLongi,stPointLat);

                                        StringBuilder query = new StringBuilder("INSERT INTO ");
                                        query.append(EdgeDataT.TABLE_NAME).append("(edgeNo,distanceInVertex,startPoint,allPoints,geometryText,endPoint) values (")
                                                .append("'").append(EdgeNo).append("',")
                                                .append("'").append("distanceInKM").append("',")
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
                                            for (int k = 0; k < convertedPoints.size(); k++) {
                                                MarkerOptions markerOptions = new MarkerOptions();
                                                PolylineOptions polylineOptions = new PolylineOptions();
                                                if(polylineOptions!=null && mMap!=null) {
                                                    markerOptions.position(convertedPoints.get(k));
                                                    markerOptions.title("Position");
                                                    polylineOptions.addAll(convertedPoints);
                                                    mMap.addPolyline(polylineOptions);
                                                    polylineOptions.color(Color.CYAN).width(30);
                                                    mMap.addPolyline(polylineOptions);

                                                }
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
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/plain");
        JSONObject jsonObject = buidJsonObject(latLng1,latLng2);
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        result = conn.getResponseMessage();
        if (conn.getResponseCode() != 200) {

        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = null;
            System.out.println("Output from Server .... \n");
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
        return buidJsonObject1;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
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
    public void verifyRouteDeviation(int markDistance){
        PolylineOptions polylineOptions = new PolylineOptions();
        String nearestPoint = GenerateLinePoint( sourceLng,sourceLat,destLng,destLat,currentGpsPosition.longitude,currentGpsPosition.latitude);
        Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
        String[] nearestDataStr = nearestPoint.split(",");
        double latitude = Double.parseDouble(nearestDataStr[0]);
        double longitude = Double.parseDouble(nearestDataStr[1]);
        LatLng nearestPosition=new LatLng(longitude,latitude);
        double returnedDistance= showDistance(currentGpsPosition,nearestPosition);
        Log.e("returnedDistance", "returnedDistance --------- "+ returnedDistance);
        drawMarkerWithCircle(nearestPosition,markDistance);
        if(returnedDistance > markDistance){
            Toast toast = Toast.makeText(getContext(), " ROUTE DEVIATED ", Toast.LENGTH_LONG);
            toast.setMargin(100, 100);
            toast.show();
            //drawDeviatedRoute(currentGpsPosition, DestinationPosition);
            String cgpsLat= String.valueOf(currentGpsPosition.latitude);
            String cgpsLongi= String.valueOf(currentGpsPosition.longitude);
            currentGpsPoint=cgpsLongi.concat(" ").concat(cgpsLat);
            Log.e("returnedDistance", "nearest Position--------- "+ nearestPosition);
            Log.e("returnedDistance", "Destination Position --------- "+ DestinationPosition);
            DestinationPosition=new LatLng(destLat,destLng);
            Log.e("returnedDistance", "DestinationPosition --------- "+ DestinationPosition);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentGpsPosition);
            markerOptions.position(DestinationPosition);
            markerOptions.title("Position");
             /*
             ReRouteFeaturesFromServer download=new ReRouteFeaturesFromServer();
              download.execute();

            polylineOptions.color(Color.RED);
            polylineOptions.width(6);
            points.add(nearestPosition);
            points.add(new LatLng(24.987665, 55.060701));
            points.add(new LatLng(24.988843, 55.062091));
            points.add(new LatLng(24.989472, 55.061488));
            points.add(DestinationPosition);
            if(points.size()>0) {
                polylineOptions.addAll(points);
                line = mMap.addPolyline(polylineOptions);
                if (polylineOptions != null) {
                    if (line != null) {
                        line.remove();
                    }
                    line = mMap.addPolyline(polylineOptions);
                } else
                    Toast.makeText(getContext(), "No route is found", Toast.LENGTH_LONG).show();
            }
            */

        }else{

        }
    }
    private void drawMarkerWithCircle(LatLng gpsPosition, double radius){
        CircleOptions circleOptions = new CircleOptions().center(gpsPosition).radius(radius).fillColor(Color.parseColor("#2271cce7")).strokeColor(Color.parseColor("#2271cce7")).strokeWidth(3);
        mCircle = mMap.addCircle(circleOptions);

    }
    private double showDistance(LatLng latlng1, LatLng latLng2) {
        double distance = SphericalUtil.computeDistanceBetween(latlng1,latLng2);
        return distance;
    }
    public int getLatLngPoints(){

        LatLngDataArray.add(new LatLng(24.978782,55.067291));
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
        LatLngDataArray.add(new LatLng(24.977631,55.06466)); //ok
        LatLngDataArray.add(new LatLng(24.977819,55.064294));//ok
        LatLngDataArray.add(new LatLng(24.978292,55.064001));//ok
        LatLngDataArray.add(new LatLng(24.97839,55.063665));//ok
        LatLngDataArray.add(new LatLng(24.978536,55.063522));//ok
        LatLngDataArray.add(new LatLng(24.978702,55.063579));//ok

        LatLngDataArray.add(new LatLng(24.978885,55.063587));//ok
        LatLngDataArray.add(new LatLng(24.979201,55.063928));//ok
        LatLngDataArray.add(new LatLng(24.979201,55.063928));//ok
        LatLngDataArray.add(new LatLng(24.979542,55.064338));//ok
        LatLngDataArray.add(new LatLng(24.979542,55.064338));//ok

        LatLngDataArray.add(new LatLng(24.979851,55.064687));//ok
        LatLngDataArray.add(new LatLng(24.980139,55.065028));//ok
        LatLngDataArray.add(new LatLng(24.980285,55.065195));//ok
        LatLngDataArray.add(new LatLng(24.980427,55.065333));//ok
        LatLngDataArray.add(new LatLng(24.980586,55.065491));//ok
        LatLngDataArray.add(new LatLng(24.980833,55.0658));//ok
        LatLngDataArray.add(new LatLng(24.981081,55.066064));//ok
        LatLngDataArray.add(new LatLng(24.980886,55.066323));//ok
        LatLngDataArray.add(new LatLng(24.980614,55.066624));//ok
        LatLngDataArray.add(new LatLng(24.980146,55.066946)); //ok
        LatLngDataArray.add(new LatLng(24.980072,55.067073)); //ok
        LatLngDataArray.add(new LatLng(24.979965,55.067191));// ok
        LatLngDataArray.add(new LatLng(24.979878,55.067205));// ok

        return LatLngDataArray.size();

    }
    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }
    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
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

    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lat = (endLatLng.latitude - beginLatLng.latitude) * t + beginLatLng.latitude;
                double lngDelta = endLatLng.longitude - beginLatLng.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * t + beginLatLng.longitude;

                 marker.setPosition(new LatLng(lat, lng));
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setFlat(true);
                        marker.setRotation(0);

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
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
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




    @Override
    public void onClick(View v) {
       if(v==change_map_options){
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
                    Log.e("SAVING DATA ", "SAVING DATA " + url1);
                    tokenResponse = HttpPost1(url1);

                    JSONObject obj = new JSONObject(tokenResponse);
                    tokenNumber = obj.getString("tokenNumber");
                    if(tokenNumber!=null && !tokenNumber.isEmpty()){
                        String url = "http://86.96.196.245/ROROAPI/NSGMap/AlertDataProcess";
                        Log.e("SAVING DATA ", "SAVING DATA " + url);
                        updaterServiceResponse = HttpPost(url);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 10);

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
        JSONObject jsonObject = buidJsonObject();
        Log.e("jsonObject", "jsonObject: " + jsonObject);
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

}