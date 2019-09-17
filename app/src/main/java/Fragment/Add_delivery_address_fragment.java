package Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.belanjan.MapsActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Config.BaseURL;
import com.belanjan.AppController;
import com.belanjan.MainActivity;
import com.belanjan.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.Session_management;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Rajesh Dabhi on 6/7/2017.
 */

public class Add_delivery_address_fragment extends Fragment implements View.OnClickListener {

    private static String TAG = Add_delivery_address_fragment.class.getSimpleName();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    private EditText et_phone, et_name, et_pin, et_house;
    private RelativeLayout btn_update;
    private TextView tv_phone, tv_name, tv_pin, tv_house, tv_socity, btn_socity;
    private String getsocity = "";

    private Session_management sessionManagement;

    private boolean isEdit = false;
    List<Address>addresses=null;

    private String getlocation_id;
    double lat,lng;

//    private MapView mapView;
//    private GoogleMap map;
//    GoogleApiClient mGoogleApiClient;
//    Marker markerAdd;
//    private Marker mCurrLocationMarker;
//    private Location mLastLocation;
    EditText et_alamat;
    String kota;

    public Add_delivery_address_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_delivery_address, container, false);

        ((MainActivity) getActivity()).setTitle("Alamat");

        sessionManagement = new Session_management(getActivity());

        et_phone = (EditText) view.findViewById(R.id.et_add_adres_phone);
        et_name = (EditText) view.findViewById(R.id.et_add_adres_name);
        tv_phone = (TextView) view.findViewById(R.id.tv_add_adres_phone);
        tv_name = (TextView) view.findViewById(R.id.tv_add_adres_name);
        tv_pin = (TextView) view.findViewById(R.id.tv_add_adres_pin);
        et_pin = (EditText) view.findViewById(R.id.et_add_adres_pin);
        et_house = (EditText) view.findViewById(R.id.et_add_adres_home);
        tv_house = (TextView) view.findViewById(R.id.tv_add_adres_home);
        tv_socity = (TextView) view.findViewById(R.id.tv_add_adres_socity);
        btn_update = (RelativeLayout) view.findViewById(R.id.btn_add_adres_edit);
        btn_socity = (TextView) view.findViewById(R.id.btn_add_adres_socity);
        et_alamat=(EditText)view.findViewById(R.id.et_alamat);

        et_alamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

            }
        });

//        mapView = (MapView) view.findViewById(R.id.mapview);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);

        String getsocity_name = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_NAME);
        String getsocity_id = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
        String getlat = sessionManagement.getUserDetails().get(BaseURL.KEY_LAT);
        String getlng = sessionManagement.getUserDetails().get(BaseURL.KEY_LNG);

        Bundle args = getArguments();

        if (args != null) {
            getlocation_id = getArguments().getString("location_id");
            String get_name = getArguments().getString("name");
            String get_phone = getArguments().getString("mobile");
            String get_pine = getArguments().getString("pincode");
            String get_socity_id = getArguments().getString("socity_id");
            String get_socity_name = getArguments().getString("socity_name");
            String get_house = getArguments().getString("house");
            String get_lat = getArguments().getString("lat");
            String get_lng = getArguments().getString("lng");

            if (TextUtils.isEmpty(get_name) && get_name == null) {
                isEdit = false;
            } else {
                isEdit = true;

                Toast.makeText(getActivity(), "edit", Toast.LENGTH_SHORT).show();

                et_name.setText(get_name);
                et_phone.setText(get_phone);
                et_pin.setText(get_pine);
                et_house.setText(get_house);
                btn_socity.setText(get_socity_name);

                sessionManagement.updateSocity(get_socity_name, get_socity_id,get_lat,get_lng);
                //Toast.makeText(getActivity(), get_lat+","+get_lng, Toast.LENGTH_SHORT).show();
            }
        }

        if (!TextUtils.isEmpty(getsocity_name)) {

            btn_socity.setText(getsocity_name);
            sessionManagement.updateSocity(getsocity_name, getsocity_id,getlat,getlng);
            //Toast.makeText(getActivity(), getlat+","+getlng, Toast.LENGTH_SHORT).show();
//            LatLng lok = new LatLng(Double.parseDouble(getlat),Double.parseDouble(getlng));
//
//            //menentukan posisi kamera
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(lok).zoom(14).build();
//            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        btn_update.setOnClickListener(this);
        btn_socity.setOnClickListener(this);

//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                map = googleMap;
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
////                try {
////                    // Customise the styling of the base map using a JSON object defined
////                    // in a raw resource file.
////                    boolean success = googleMap.setMapStyle(
////                            MapStyleOptions.loadRawResourceStyle(
////                                    getActivity(), R.raw.map_style2));
////
////                    if (!success) {
////                        Log.e("Map", "Style parsing failed.");
////                    }
////                } catch (Resources.NotFoundException e) {
////                    Log.e("Map", "Can't find style. Error: ", e);
////                }
//
//
////
////                gps = new GPSTracker(getActivity());
//
//                // check if GPS enabled
////                if(gps.canGetLocation()){
////
////                    double latitude = gps.getLatitude();
////                    double longitude = gps.getLongitude();
////                    if ((latitude!=0)&&(longitude!=0)){
////                        session.createLokasi(Double.toString(latitude), Double.toString(longitude));
////
////                        LatLng lokasiku = new LatLng(latitude, longitude);
////                        map.addMarker(new MarkerOptions()
////                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markeruser))
////                                .position(lokasiku)
////                                .title("Saya"));
////                        String url = Fungsi.getDirectionsUrl(lokasiku,lok);
////
////                        DownloadTask downloadTask = new DownloadTask();
////                        downloadTask.execute(url);
////                    }
////
////                }else{
////                    // can't get location
////                    // GPS or Network is not enabled
////                    // Ask user to enable GPS/network in settings
////                    gps.showSettingsAlert();
////                }
//            }
//        });



        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_add_adres_edit) {
            attemptEditProfile();
        } else if (id == R.id.btn_add_adres_socity) {

            /*String getpincode = et_pin.getText().toString();

            if (!TextUtils.isEmpty(getpincode)) {*/

                Bundle args = new Bundle();
                Fragment fm = new Socity_fragment();
                //args.putString("pincode", getpincode);
                fm.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            /*} else {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_pincode), Toast.LENGTH_SHORT).show();
            }*/

        }
    }

    private void attemptEditProfile() {

        tv_phone.setText(getResources().getString(R.string.receiver_mobile_number));
        tv_pin.setText(getResources().getString(R.string.tv_reg_pincode));
        tv_name.setText(getResources().getString(R.string.receiver_name_req));
        tv_house.setText(getResources().getString(R.string.tv_reg_house));
        tv_socity.setText(getResources().getString(R.string.tv_reg_socity));

        tv_name.setTextColor(getResources().getColor(R.color.dark_gray));
        tv_phone.setTextColor(getResources().getColor(R.color.dark_gray));
        tv_pin.setTextColor(getResources().getColor(R.color.dark_gray));
        tv_house.setTextColor(getResources().getColor(R.color.dark_gray));
        tv_socity.setTextColor(getResources().getColor(R.color.dark_gray));

        String getphone = et_phone.getText().toString();
        String getname = et_name.getText().toString();
        String getpin = et_pin.getText().toString();
        String gethouse = et_house.getText().toString();
        String getalamat=et_alamat.getText().toString();
        String getsocity = sessionManagement.getUserDetails().get(BaseURL.KEY_SOCITY_ID);
        String getlat=Double.toString(lat);
        String getlng=Double.toString(lng);
        //Toast.makeText(getActivity(), getalamat, Toast.LENGTH_SHORT).show();



        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(getphone)) {
            tv_phone.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_phone;
            cancel = true;
        } else if (!isPhoneValid(getphone)) {
            tv_phone.setText(getResources().getString(R.string.phone_too_short));
            tv_phone.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_phone;
            cancel = true;
        }

        if (TextUtils.isEmpty(getname)) {
            tv_name.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = et_name;
            cancel = true;
        }
        if (TextUtils.isEmpty(getalamat)) {
            focusView = et_alamat;
            cancel = true;
        }

//        if (TextUtils.isEmpty(getpin)) {
//            tv_pin.setTextColor(getResources().getColor(R.color.colorPrimary));
//            focusView = et_pin;
//            cancel = true;
//        }
//
//        if (TextUtils.isEmpty(gethouse)) {
//            tv_house.setTextColor(getResources().getColor(R.color.colorPrimary));
//            focusView = et_house;
//            cancel = true;
//        }
//
        if (TextUtils.isEmpty(getsocity) && getsocity == null) {
            tv_socity.setTextColor(getResources().getColor(R.color.colorPrimary));
            focusView = btn_socity;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null){
                focusView.requestFocus();
            }


        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            if (ConnectivityReceiver.isConnected()) {

                String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);

                // check internet connection
                if (ConnectivityReceiver.isConnected()) {
                    if (isEdit) {
                        makeEditAddressRequest(getlocation_id, getpin, getsocity, gethouse, getname, getphone,getalamat,getlat,getlng);
                    } else {
                        makeAddAddressRequest(user_id, getpin, getsocity, gethouse, getname, getphone,getalamat,getlat,getlng);
                    }
                }
            }
        }
    }

    private boolean isPhoneValid(String phoneno) {
        //TODO: Replace this with your own logic
        return phoneno.length() > 9;
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeAddAddressRequest(String user_id, String pincode, String socity_id,
                                       String house_no, String receiver_name, String receiver_mobile,String alamat,String lat,String lng) {

        // Tag used to cancel the request
        String tag_json_obj = "json_add_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("pincode", pincode);
        params.put("socity_id", socity_id);
        params.put("house_no", house_no);
        params.put("receiver_name", receiver_name);
        params.put("receiver_mobile", receiver_mobile);
        params.put("alamat", alamat);
        params.put("kota", kota);
        params.put("lat", lat);
        params.put("lng", lng);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.ADD_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        ((MainActivity) getActivity()).onBackPressed();

                    }else{
                       showAlert(response.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private void showAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setTitle("Respon")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Method to make json object request where json response starts wtih
     */
    private void makeEditAddressRequest(String location_id, String pincode, String socity_id,
                                        String house_no, String receiver_name, String receiver_mobile,String alamat,String lat,String lng) {

        // Tag used to cancel the request
        String tag_json_obj = "json_edit_address_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("location_id", location_id);
        params.put("pincode", pincode);
        params.put("socity_id", socity_id);
        params.put("house_no", house_no);
        params.put("receiver_name", receiver_name);
        params.put("receiver_mobile", receiver_mobile);
        params.put("alamat", alamat);
        params.put("lat", lat);
        params.put("lng", lng);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.EDIT_ADDRESS_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        String msg = response.getString("data");
                        Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();

                        ((MainActivity) getActivity()).onBackPressed();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
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
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onMapReady(final GoogleMap googleMap) {
//        map = googleMap;
//        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
////
//        //menentukan titik awal peta
//
//        LatLng lok = new LatLng(-7.0247246,110.3470239);
//
//        //menentukan posisi kamera
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(lok).zoom(14).build();
//        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        markerAdd=map.addMarker(new MarkerOptions()
//                .position(lok)
//                .draggable(false)
//                .title("Pilih Lokasi"));
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(getContext(),
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//               // buildGoogleApiClient();
//                map.setMyLocationEnabled(true);
//            }
//        }
//        else {
////            buildGoogleApiClient();
////            map.setMyLocationEnabled(true);
//        }
//        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
//            @Override
//            public void onCameraMove() {
//                markerAdd.setPosition(map.getCameraPosition().target);
//            }
//        });
//
//        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
//                try {
//                    addresses = geocoder.getFromLocation(markerAdd.getPosition().latitude, markerAdd.getPosition().longitude, 1); //1 num of possible location returned
//                    String address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
//                    String city = addresses.get(0).getLocality();
//                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    kota=addresses.get(0).getSubAdminArea();
//                    Log.e("kota",kota);
//                    String title = address;
//                    et_alamat.setText(title);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
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
//    }
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1) {
        if(resultCode == RESULT_OK) {
            lat=Double.parseDouble(data.getStringExtra("lat"));
            lng=Double.parseDouble(data.getStringExtra("lng"));
            kota=data.getStringExtra("kota");
            et_alamat.setText(data.getStringExtra("alamat"));


        }
    }
}



}
