package com.example.quanlyamnhac.thongke;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import com.example.quanlyamnhac.Database;


import com.example.quanlyamnhac.R;

import java.util.ArrayList;
import java.util.List;


public class ThongkeFragment extends Fragment {

    List<DataEntry> data;
    List<String> tenBaiHats;
    List<Integer> luotTrinhDiens;

    public ThongkeFragment() {
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
        View root = inflater.inflate(R.layout.thongke_fragment, container, false);

        AnyChartView anyChartView = root.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar));
//
        this.data = getData();
        Cartesian cartesian = AnyChart.column();
        Column column = cartesian.column(data);
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("Lượt: {%Value}");
        cartesian.animation(true);
        cartesian.title("Top 10 bài hát phổ biến nhất");

        cartesian.yScale().ticks().allowFractional(false);
        cartesian.yScale().minimum(0d);
//        cartesian.yAxis(0).labels().format("{%Value}");
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title("Bài hát");
        cartesian.yAxis(0).title("Lượt trình diễn");

        anyChartView.setChart(cartesian);
        return root;
    }

    private List<DataEntry> getData() {
        List<DataEntry> data = new ArrayList<>();

        List<String> maBaiHats = getListMaBaiHat();
//        tenBaiHats = getListTenBaiHat();
        luotTrinhDiens = getLuotTrinhdien(maBaiHats);
        customSortList(tenBaiHats, luotTrinhDiens);
        for (int i = 0; i < luotTrinhDiens.toArray().length; i++) {
            data.add( new ValueDataEntry( tenBaiHats.get(i), luotTrinhDiens.get(i) ) );
        }

        return data;
    }

    //tang dan
    private void customSortList(List<String> listString2sort, List<Integer> list2sort) {

        for (int i = 0; i < list2sort.size() - 1; i++) {
            for (int j = i + 1; j < list2sort.size(); j++) {
                if (list2sort.get(i) < list2sort.get(j)) {
                    System.out.println("list2sort i: " + list2sort.get(i));
                    System.out.println("list2sort j: " + list2sort.get(j));
                    int temp = list2sort.get(i);
                    list2sort.set(i, list2sort.get(j));
                    list2sort.set(j, temp);

                    System.out.println("list2sort i 2: " + list2sort.get(i));
                    System.out.println("list2sort j 2: " + list2sort.get(j));
                    System.out.println("liststring2sort i: " + listString2sort.get(i));
                    System.out.println("liststring2sort j: " + listString2sort.get(j));

                    String s_temp = listString2sort.get(i);
                    listString2sort.set(i, listString2sort.get(j));
                    listString2sort.set(j, s_temp);

                    System.out.println("liststring2sort i 2: " + listString2sort.get(i));
                    System.out.println("liststring2sort j 2: " + listString2sort.get(j));
                }
            }
        }

    }

    private List<String> getListMaBaiHat() {
        Database database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        List<String> list = new ArrayList<>();
        tenBaiHats = new ArrayList<>();
        Cursor dataBaiHat = database.GetData("SELECT * FROM BaiHat ");
        list.clear();

        int i = 0;
        while (dataBaiHat.moveToNext()){
            System.out.println("mabh: " + dataBaiHat.getString(0));
            System.out.println("tenbh: " + dataBaiHat.getString(0));
            if (i < 10) {
                list.add(dataBaiHat.getString(0));
                tenBaiHats.add(dataBaiHat.getString(1));
            }

        }


        return list;
    }

    private List<String> getListTenBaiHat() {
        Database database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        ArrayList<String> list = new ArrayList<>();
        Cursor dataBaiHat = database.GetData("SELECT TenBaiHat FROM BaiHat LIMIT 10");
        list.clear();

        while (dataBaiHat.moveToNext())
            list.add(dataBaiHat.getString(0));

        return list;
    }

    private List<Integer> getLuotTrinhdien(List<String> listMaBaiHat) {
        Database database = new Database(getContext(), "QuanLyAmNhac.sqlite", null, 1);
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < listMaBaiHat.size(); i++){
            String maBaihat = listMaBaiHat.get(i);
            String sql = "Select COUNT(MaBaihat) FROM BieuDien where MaBaihat = '" + maBaihat + "'";
            System.out.println("sql: " + sql);
            Cursor dataTrinhDien = database.GetData(sql);
            while (dataTrinhDien.moveToNext()) {
                list.add(dataTrinhDien.getInt(0));
            }
        }

        return list;
    }


}