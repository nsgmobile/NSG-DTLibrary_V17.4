package com.nsg.nsgdtlibrary.Classes.util;

import android.view.View;

import androidx.fragment.app.Fragment;

public class NSGTILEDLAYERS  extends Fragment implements View.OnClickListener{
    @Override
    public void onClick(View v) {

    }
    /*
      /*
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {


                          mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                              @Override
                              public void onMyLocationChange(final Location location) {
                                  if (currentGpsPosition != null) {
                                      OldGPSPosition = currentGpsPosition;
                                  }
                                  Runnable runnable = new Runnable() {
                                      public void run() {
                                          currentGpsPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                          Log.e("CurrentGpsPoint", " currentGpsPosition GpsPoint " + currentGpsPosition);
                                          vehicleSpeed = location.getSpeed();
                                          LatLng OldNearestPosition = null;
                                          if (isRouteDeviated == false) {
                                              if (OldGPSPosition != null) {
                                                  double distance = distFrom(OldGPSPosition.latitude, OldGPSPosition.longitude, currentGpsPosition.latitude, currentGpsPosition.longitude);
                                                  Log.e("distance", "distance" + distance);
                                                  if (distance > 10) {

                                                  } else {
                                                      OldNearestPosition = nPosition;
                                                      Log.e("CurrentGpsPoint", " OLD Nearest GpsPoint " + OldNearestPosition);
                                                      nPosition = GetNearestPointOnRoadFromGPS(OldGPSPosition, currentGpsPosition);
                                                      Log.e("CurrentGpsPoint", " Nearest GpsPoint" + nPosition);
                                                      if (mPositionMarker == null) {
                                                          mPositionMarker = mMap.addMarker(new MarkerOptions()
                                                                  .position(SourceNode)
                                                                  .title("Nearest GpsPoint")
                                                                  .anchor(0.5f, 0.5f)
                                                                  .flat(true)
                                                                  .icon(bitmapDescriptorFromVector(getContext(), R.drawable.gps_transperent_98)));
                                                      } else {
                                                          Log.e("CurrentGpsPoint", " currentGpsPosition ------ " + currentGpsPosition);
                                                          if (OldNearestPosition != null) {
                                                              animateCarMove(mPositionMarker, OldNearestPosition, nPosition, 1500);
                                                              float bearing = (float) bearingBetweenLocations(OldNearestPosition, nPosition);
                                                              Log.e("BEARING", "BEARING @@@@@@@ " + bearing);
                                                              int height = getView().getMeasuredHeight();
                                                              Projection p = mMap.getProjection();
                                                              Point bottomRightPoint = p.toScreenLocation(p.getVisibleRegion().nearRight);
                                                              Point center = new Point(bottomRightPoint.x / 2, bottomRightPoint.y / 2);
                                                              Point offset = new Point(center.x, (center.y + (height / 4)));
                                                              LatLng centerLoc = p.fromScreenLocation(center);
                                                              LatLng offsetNewLoc = p.fromScreenLocation(offset);
                                                              double offsetDistance = SphericalUtil.computeDistanceBetween(centerLoc, offsetNewLoc);
                                                              LatLng shadowTgt = SphericalUtil.computeOffset(nPosition, offsetDistance, bearing);
                                                              caclulateETA(TotalDistanceInMTS, SourceNode, currentGpsPosition, DestinationNode);
                                                              verifyRouteDeviation(OldGPSPosition, currentGpsPosition, DestinationNode, 40, null);

                                                              AlertDestination(currentGpsPosition);
                                                              if (bearing > 0.0) {
                                                                  CameraPosition currentPlace = new CameraPosition.Builder()
                                                                          .target(shadowTgt)
                                                                          .bearing(bearing).tilt(65.5f).zoom(18)
                                                                          .build();
                                                                  mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace), 1000, null);
                                                              } else {

                                                              }

                                                          }
                                                      }

                                                  }
                                              }

                                          } else {
                                              MoveWithGpsPointInRouteDeviatedPoints(currentGpsPosition);
                                          }
                                      }

                                  };

                                  Handler handler1 = new Handler();
                                  handler1.postDelayed(runnable, 0);
                              }

                          });

                      }
                       */

/*
  private void turnGpsOn(Context context) {
        String beforeEnable = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String newSet = String.format("%s,%s",
                beforeEnable,
                LocationManager.GPS_PROVIDER);
        try {
            Settings.Secure.putString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
                    newSet);
        } catch (Exception e) {
        }
    }

    public void turnGPSOn() {

        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.getContext().sendBroadcast(intent);

        String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.getContext().sendBroadcast(poke);
        }
    }

    // automatic turn off the gps
    public void turnGPSOff() {
        String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps")) { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.getContext().sendBroadcast(poke);
        }
    }
 */
}
