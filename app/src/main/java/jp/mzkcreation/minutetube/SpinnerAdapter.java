package jp.mzkcreation.minutetube;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nakanomizuki on 2016/08/22.
 */

public class SpinnerAdapter extends BaseAdapter {

    private Activity mActivity;

    public SpinnerAdapter(Activity activity) {
        mActivity = activity;
    }

    private ArrayList<String> mItems = new ArrayList<>();

    public void addItem(String msg) {
        mItems.add(msg);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = mActivity.getLayoutInflater().inflate(R.layout.spinner_item_dropdown,
                    parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView)view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mActivity.getLayoutInflater().inflate(
                    R.layout.spinner_toolbar,
                    parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position) : "";
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}