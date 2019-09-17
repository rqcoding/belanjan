package com.belanjan;

import com.belanjan.R;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import Config.BaseURL;
import util.Session_management;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    Marker markerAdd;
    List<Address> addresses=null;
    String kota;
    String title,lat,lng;
    EditText et_alamat;
    Button bt_simpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
//                map.clear();
//                map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
//                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                markerAdd.setPosition(place.getLatLng());

            }

            @Override
            public void onError(Status status) {

            }
        });
        et_alamat=(EditText)findViewById(R.id.et_alamat);
        bt_simpan=(Button)findViewById(R.id.bt_simpan);
        bt_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session_management sessionManagement = new Session_management(getApplicationContext());
                String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);
                String getsocity_id = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
                lat=Double.toString(markerAdd.getPosition().latitude);
                lng=Double.toString(markerAdd.getPosition().longitude);

//                sessionManagement.updateSocity(getsocity_name,getsocity_id,lat,lng);

//                ((MainActivity) getApplicationContext()).onBackPressed();
                Intent intent = new Intent();
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("kota", kota);
                intent.putExtra("alamat", et_alamat.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }



//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//
//                //menentukan titik awal peta
//
//                LatLng lok = new LatLng(-7.0247246,110.3470239);
//
//                //menentukan posisi kamera
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(lok).zoom(10).build();
//                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                map.addMarker(new MarkerOptions()
//                        .position(lok)
//                        .draggable(false)
//                        .title("Pilih Lokasi"));
//    }
//    @Override
//    public void onResume() {
//        mapView.onResume();
//        super.onResume();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        mapView.onLowMemory();
//        super.onLowMemory();
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
//
        //menentukan titik awal peta

        LatLng lok = new LatLng(-7.0247246,110.3470239);

        //menentukan posisi kamera
        CameraPosition cameraPosition = new CameraPosition.Builder().target(lok).zoom(14).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        markerAdd=map.addMarker(new MarkerOptions()
                .position(lok)
                .draggable(false)
                .title("Pilih Lokasi"));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
               // buildGoogleApiClient();
                map.setMyLocationEnabled(true);
            }
        }
        else {
//            buildGoogleApiClient();
//            map.setMyLocationEnabled(true);
        }
        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                markerAdd.setPosition(map.getCameraPosition().target);
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(markerAdd.getPosition().latitude, markerAdd.getPosition().longitude, 1); //1 num of possible location returned
                    String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    kota=addresses.get(0).getSubAdminArea();
                    Log.e("kota",kota);
                    title = address;
                    et_alamat.setText(title);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
//        String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);
//        String getsocity_id = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
//        String getlat = sessionManagement.getUserDetails().get(BaseURL.KEY_LAT);
//        String getlng = sessionManagement.getUserDetails().get(BaseURL.KEY_LNG);
//        if (!TextUtils.isEmpty(getsocity_name)) {
//
//            btn_socity.setText(getsocity_name);
//            sessionManagement.updateSocity(getsocity_name, getsocity_id,getlat,getlng);
//            //Toast.makeText(getActivity(), getlat+","+getlng, Toast.LENGTH_SHORT).show();
//            LatLng loks = new LatLng(Double.parseDouble(getlat),Double.parseDouble(getlng));
//
//            //menentukan posisi kamera
//            CameraPosition cameraPositions = new CameraPosition.Builder().target(loks).zoom(14).build();
//            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositions));
//        }
    }
}