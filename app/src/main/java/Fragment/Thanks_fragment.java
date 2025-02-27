package Fragment;

import android.content.Intent;
import android.app.Fragment;

import android.app.FragmentManager;

import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.belanjan.MainActivity;
import com.belanjan.My_Order_activity;
import com.belanjan.R;

/**
 * Created by Rajesh Dabhi on 29/6/2017.
 */

public class Thanks_fragment extends Fragment implements View.OnClickListener {

    TextView tv_info;
    RelativeLayout btn_home, btn_order;

    public Thanks_fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_thanks, container, false);

        ((MainActivity) getActivity()).setTitle(getResources().getString(R.string.thank_you));
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fm = new Home_fragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });

        String data = getArguments().getString("msg");

        tv_info = (TextView) view.findViewById(R.id.tv_thank_info);
        btn_home = (RelativeLayout) view.findViewById(R.id.btn_thank_home);
        btn_order = (RelativeLayout) view.findViewById(R.id.btn_track_order);
        tv_info.setText(Html.fromHtml(data));

        btn_home.setOnClickListener(this);
        btn_order.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_thank_home) {
            Fragment fm = new Home_fragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();
        }
        if (id == R.id.btn_track_order) {
            Intent myIntent = new Intent(getActivity(), My_Order_activity.class);
            getActivity().startActivity(myIntent);
        }


    }

}
