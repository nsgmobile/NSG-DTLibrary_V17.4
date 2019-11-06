package com.nsg.nsgdtlibrary.Classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.nsg.nsgdtlibrary.R;

import java.io.File;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class NSGTiledLayerOnMap extends Fragment {
    GoogleMap mMap;
    private String BASE_MAP_URL_FORMAT;
    public NSGTiledLayerOnMap() { }
    @SuppressLint("ValidFragment")
    public NSGTiledLayerOnMap(String BASE_MAP_URL_FORMAT) {
        this.BASE_MAP_URL_FORMAT = BASE_MAP_URL_FORMAT;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.maplite, container, false);
         SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                public void onMapReady(GoogleMap googlemap) {
                    NSGTiledLayerOnMap.this.mMap = googlemap;
                    TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(BASE_MAP_URL_FORMAT.toString()), 256, 256);
                    TileOverlay tileOverlay = NSGTiledLayerOnMap.this.mMap.addTileOverlay((new TileOverlayOptions()).tileProvider(tileProvider));
                    tileOverlay.setTransparency(0.5F - tileOverlay.getTransparency());
                    tileOverlay.setVisible(true);
                    CameraPosition googlePlex = CameraPosition.builder().target(new LatLng(24.984836D, 55.071661D)).zoom(15.0F).tilt(45.0F).build();
                    NSGTiledLayerOnMap.this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
                    NSGTiledLayerOnMap.this.mMap.addMarker((new MarkerOptions()).position(new LatLng(24.984836D, 55.071661D)).title("").snippet("DP World Operations Training Center").icon(NSGTiledLayerOnMap.this.bitmapDescriptorFromVector(NSGTiledLayerOnMap.this.getActivity(), R.drawable.red_marker)));
                }
            });

        return rootView;
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
