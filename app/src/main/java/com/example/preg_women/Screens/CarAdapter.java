package com.example.preg_women.Screens;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.preg_women.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class CarAdapter extends BaseAdapter {
    private final ArrayList<Car> ambulanceInfo;
    private TextView driverpn2,driverpn1 ,driver, platenum;

    public CarAdapter( ArrayList<Car> ambulanceInfo) {
        this.ambulanceInfo = ambulanceInfo;

    }

    @Override
    public int getCount() {
        return ambulanceInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(viewGroup.getContext(), R.layout.activity_car_row,null);
     platenum=v.findViewById(R.id.platenumber);
        driver=v.findViewById(R.id.driver);
         driverpn1=v.findViewById(R.id.driverpn1);
     driverpn2=v.findViewById(R.id.driverpn2);
        platenum.setText(ambulanceInfo.get(i).getPlateNumber());
        driverpn2.setText(ambulanceInfo.get(i).getDriverPhoneNumber2());
        driver.setText(ambulanceInfo.get(i).getDriverName());
        driverpn1.setText(ambulanceInfo.get(i).getDriverPhoneNumber1());
        return v;
    }
}
