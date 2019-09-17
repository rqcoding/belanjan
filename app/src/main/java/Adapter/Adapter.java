package Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.belanjan.R;

import java.util.ArrayList;

import Model.DataModel;


/**
 * Created by KUNCORO on 09/08/2017.
 */

public class Adapter extends BaseAdapter implements Filterable {
    private FriendFilter friendFilter;
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<DataModel> friendList;
    private ArrayList<DataModel> item;

    public Adapter(Activity activity, ArrayList<DataModel> item) {
        this.activity = activity;
        this.item = item;
        this.friendList= (ArrayList<DataModel>) item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataModel user = (DataModel) getItem(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item, null);

        TextView txt_nama = (TextView) convertView.findViewById(R.id.txt_nama);

        txt_nama.setText(item.get(position).getNama());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilter();
        }

        return friendFilter;
    }

    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<DataModel> tempList = new ArrayList<DataModel>();

                // search content in friend list
                for (DataModel user : friendList) {
                    if (user.getNama().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = friendList.size();
                filterResults.values = friendList;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            item = (ArrayList<DataModel>) results.values;
            notifyDataSetChanged();
        }
    }
}