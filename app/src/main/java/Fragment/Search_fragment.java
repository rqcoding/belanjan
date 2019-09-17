package Fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.belanjan.AppController;
import com.belanjan.MainActivity;
import com.belanjan.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.Adapter;
import Adapter.Search_adapter;
import Config.BaseURL;
import Model.DataModel;
import Model.Product_model;
import util.CustomVolleyJsonRequest;

import static android.content.Context.SEARCH_SERVICE;

/**
 * Created by Rajesh Dabhi on 14/7/2017.
 */

public class Search_fragment extends Fragment implements SearchView.OnQueryTextListener{

    private static String TAG = Search_fragment.class.getSimpleName();
    //    String[] fruits = {"MIlk butter & cream", "Bread Buns & Pals", "Dals Mix Pack", "buns-pavs", "cakes", "Channa Dal", "Toor Dal", "Wheat Atta"
//            , "Beson", "Almonds", "Packaged Drinking", "Cola drinks", "Other soft drinks", "Instant Noodles", "Cup Noodles", "Salty Biscuits", "cookie", "Sanitary pads", "sanitary Aids"
//            , "Toothpaste", "Mouthwash", "Hair oil", "Shampoo", "Pure & pomace olive", "ICE cream", "Theme Egg", "Amul Milk", "AMul Milk Pack power", "kaju pista dd"};
//    private AutoCompleteTextView acTextView;
//    private RelativeLayout btn_search;
    private RecyclerView rv_search;

    private List<Product_model> product_modelList = new ArrayList<>();
    private Search_adapter adapter_product;
    SearchView searchView;
    ListView list_view;
    ArrayList<DataModel> listData = new ArrayList<DataModel>();
    Adapter adapter;
    ProgressBar progressBar;

    public Search_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.search));

//        btn_search = (RelativeLayout) view.findViewById(R.id.btn_search);
        rv_search = (RecyclerView) view.findViewById(R.id.rv_search);
        rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

//        acTextView = (AutoCompleteTextView) view.findViewById(R.id.et_search);
//        acTextView.setAdapter(new SuggestionAdapter(getActivity(), acTextView.getText().toString()));
//        acTextView.setTextColor(getResources().getColor(R.color.green));
//
//        btn_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String get_search_txt = acTextView.getText().toString();
//                if (TextUtils.isEmpty(get_search_txt)) {
//                    Toast.makeText(getActivity(), getResources().getString(R.string.enter_keyword), Toast.LENGTH_SHORT).show();
//                } else {
//                    if (ConnectivityReceiver.isConnected()) {
//                        makeGetProductRequest(get_search_txt);
//                    } else {
//                        ((MainActivity) getActivity()).onNetworkConnectionChanged(false);
//                    }
//                }
//
//            }
//        });
        list_view = (ListView) view.findViewById(R.id.list_view);

        adapter = new Adapter(getActivity(), listData);
        list_view.setAdapter(adapter);
        searchView = (SearchView) view.findViewById(R.id.searchview);
        searchView.setQueryHint("Search...");
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setOnQueryTextListener(this);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(SearchViewLayout.this, ""+position, Toast.LENGTH_SHORT).show();
                handelListItemClick((DataModel) adapter.getItem(position));
                if(position>0 && position <= listData.size()) {

                }
            }
        });
//        makeGetProductRequest("");

        return view;
    }

    /**
     * Method to make json object request where json response starts wtih {
     */
    private void makeGetProductRequest(String search_text) {
        progressBar.setVisibility(View.VISIBLE);

        // Tag used to cancel the request
        String tag_json_obj = "json_product_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("search", search_text);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_PRODUCT_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar.setVisibility(View.GONE);

                try {
                    Boolean status = response.getBoolean("responce");
                    if (status) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Product_model>>() {
                        }.getType();

                        product_modelList = gson.fromJson(response.getString("data"), listType);

                        adapter_product = new Search_adapter(product_modelList, getActivity());
                        rv_search.setAdapter(adapter_product);
                        //adapter_product.notifyDataSetChanged();

                        if (getActivity() != null) {
                            if (product_modelList.isEmpty()) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_rcord_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
//                    Boolean status = response.getBoolean("response");
//                    if (status) {
//
//                        listData.clear();
//                        adapter.notifyDataSetChanged();
//                        JSONArray response2 = response.getJSONArray("data");
//                        for (int i = 0; i < response2.length(); i++) {
//                            try {
//
//                                JSONObject obj = response2.getJSONObject(i);
//
//                                DataModel item = new DataModel();
//
//                                item.setId(obj.getString("product_id"));
//                                item.setNama(obj.getString("product_name"));
//                                listData.add(item);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//
//                    }
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


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Toast.makeText(getActivity(), newText, Toast.LENGTH_SHORT).show();
        makeGetProductRequest(newText);
        return false;
    }
    private void handelListItemClick(DataModel user) {
        // close search view if its visible
        if (searchView.isShown()) {
            searchView.setQuery("", false);
        }
        Toast.makeText(getActivity(), user.getNama(), Toast.LENGTH_SHORT).show();
//        Intent intent=new Intent();
//        intent.putExtra("name",user.getNama());
//        intent.putExtra("code",user.getId());
//        setResult(1,intent);
//        finish();

    }
    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }
}
