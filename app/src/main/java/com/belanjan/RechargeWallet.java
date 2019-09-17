package com.belanjan;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Config.BaseURL;
import Config.SharedPref;
import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import com.belanjan.NetworkConnectivity.NetworkConnection;
import com.belanjan.NetworkConnectivity.NetworkError;
import util.Session_management;

public class RechargeWallet extends AppCompatActivity {
    private Session_management sessionManagement;
    EditText Wallet_Ammount;
    RelativeLayout Recharge_Button;
    String ammount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Recharge Wallet");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RechargeWallet.this, MainActivity.class);
                startActivity(intent);
            }
        });
        sessionManagement = new Session_management(RechargeWallet.this);
        final String email = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
        final String mobile = sessionManagement.getUserDetails().get(BaseURL.KEY_MOBILE);
        final String name = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
        Wallet_Ammount = (EditText) findViewById(R.id.et_wallet_ammount);
        Recharge_Button = (RelativeLayout) findViewById(R.id.recharge_button);

        Recharge_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ammount = Wallet_Ammount.getText().toString();
                //Recharge_wallet();
                  callInstamojoPay(email, mobile, ammount, "official", name);

            }
        });
    }
    private void callInstamojoPay(String email, String phone, String ammount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", ammount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    InstapayListener listener;


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Recharge_wallet();
                Intent intent = new Intent(RechargeWallet.this, ThanksOrder.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int code, String reason) {
                Intent intent = new Intent(RechargeWallet.this, OrderFail.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                //Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG).show();
            }
        };
    }

    private void Recharge_wallet() {
        final String user_id = sessionManagement.getUserDetails().get(BaseURL.KEY_ID);
        if (NetworkConnection.connectionChecking(this)) {
            RequestQueue rq = Volley.newRequestQueue(this);
            StringRequest postReq = new StringRequest(Request.Method.POST, "http://neerajbisht.com/grocery_test/index.php/api/recharge_wallet",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("eclipse", "Response=" + response);
                            try {
                                JSONObject object = new JSONObject(response);
                                if (object.optString("success").equalsIgnoreCase("success")) {
                                    String wallet_amount = object.getString("wallet_amount");
                                    SharedPref.putString(RechargeWallet.this, BaseURL.KEY_WALLET_Ammount, wallet_amount);
                                } else {
                                    // Toast.makeText(DashboardPage.this, "" + jObj.optString("msg"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Error [" + error + "]");
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", user_id);
                    params.put("wallet_amount", ammount);
                    return params;
                }
            };
            rq.add(postReq);
        } else {
            Intent intent = new Intent(RechargeWallet.this, NetworkError.class);
            startActivity(intent);
        }

    }

}
