package com.belanjan;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import Adapter.Socity_adapter;
import Config.BaseURL;
import Fragment.Socity_fragment;
import Model.Socity_model;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonArrayRequest;
import util.RecyclerTouchListener;
import util.Session_management;

public class Kecamatan extends AppCompatActivity {

    private static String TAG = Socity_fragment.class.getSimpleName();

    private EditText et_search;
    private RecyclerView rv_socity;

    private List<Socity_model> socity_modelList = new ArrayList<>();
    private Socity_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_socity);

        //String getpincode = getArguments().getString("pincode");

        et_search = (EditText) findViewById(R.id.et_socity_search);
        rv_socity = (RecyclerView) findViewById(R.id.rv_socity);
        rv_socity.setLayoutManager(new LinearLayoutManager(this));

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // check internet connection
        if (ConnectivityReceiver.isConnected()) {
            makeGetSocityRequest();
        } else {
            ((MainActivity) getApplicationContext()).onNetworkConnectionChanged(false);
        }

        rv_socity.addOnItemTouchListener(new RecyclerTouchListener(this, rv_socity, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String socity_id = socity_modelList.get(position).getSocity_id();
                String socity_name = socity_modelList.get(position).getSocity_name();
                String lat = socity_modelList.get(position).getLat();
                String lng = socity_modelList.get(position).getLng();

                Session_management sessionManagement = new Session_management(getApplicationContext());

                sessionManagement.updateSocity(socity_name,socity_id,lat,lng);

                Intent intent = new Intent();
                intent.putExtra("socity_id",socity_id);
                intent.putExtra("socity_name",socity_name);
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                setResult(RESULT_OK, intent);
                finish();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }
    private void makeGetSocityRequest() {

        // Tag used to cancel the request
        String tag_json_obj = "json_socity_req";

        /*Map<String, String> params = new HashMap<String, String>();
        params.put("pincode", pincode);*/

        CustomVolleyJsonArrayRequest jsonObjReq = new CustomVolleyJsonArrayRequest(Request.Method.GET,
                BaseURL.GET_SOCITY_URL, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Socity_model>>() {
                }.getType();

                socity_modelList = gson.fromJson(response.toString(), listType);

                adapter = new Socity_adapter(socity_modelList);
                rv_socity.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if(socity_modelList.isEmpty()){
                    if(getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    if(getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

}
