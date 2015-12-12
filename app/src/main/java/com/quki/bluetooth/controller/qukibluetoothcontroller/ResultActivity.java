package com.quki.bluetooth.controller.qukibluetoothcontroller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        ArrayList<Float> al = new ArrayList<>();

            al.add((float)62);
            al.add((float)82);
            al.add((float)52);
            al.add((float)55);
            al.add((float)67);
            al.add((float)68);
            al.add((float)72);
            al.add((float)88);
            al.add((float)90);
            al.add((float)66);

        // Set Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.chartContainer, new PlaceHolderFragment(al))
                .commit();
    }
/* Fragment
     * Chart, SeekBar 모두 PlaceholderFragment에서 작업*/
        public class PlaceHolderFragment extends Fragment {

            private ArrayList<Float> mList;

            private LineChartView chart;
            private LineChartData data;
            private LineChartData preData;
            private PreviewLineChartView previewChart;
            private LineChartData previewData;

            private ValueShape shape = ValueShape.CIRCLE;

            // Viewport는 쉽게 말해 화면 (View)라고 생각하면 된다. 주로 보여지는 범위를 지정할 때 주로 사용함.
            private Viewport maxViewport,currentViewport;
            private int maxValue=90, minValue=50;

            public PlaceHolderFragment() {
            }

            // Volley로 부터 받아온 ArrayList 초기화 작업
            public PlaceHolderFragment(ArrayList<Float> mList) {
                this.mList = mList;
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                setHasOptionsMenu(true);
                View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

                chart = (LineChartView) rootView.findViewById(R.id.chart);
                previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);
                chart.setOnValueTouchListener(new ValueTouchListener());

                maxViewport = new Viewport(chart.getMaximumViewport());
                currentViewport = new Viewport(chart.getCurrentViewport());


                    // chart draw !!
                    // 최초에에 chart에 뿌려 줄 data 생성
                    generateData();
                    // 자동으로 chart가 계산 되는 것 방지
                    chart.setViewportCalculationEnabled(false);


                return rootView;
            }

    // 최초에 chart에 뿌려 줄 data 생성
    private void generateData() {

        List<Line> lines = new ArrayList<>();            //보여질 데이터를 위한 List
        List<Line> linesForPreData = new ArrayList<>();  //미리보기 데이터를 위한 List

        List<PointValue> values = new ArrayList<>();
        for(int j=0 ; j<mList.size(); j++) {
            values.add(new PointValue(j,mList.get(j)) ); //adding point to the first line
        }

            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[0]);

            // 최초에 뿌려지는 chart의 line
                line.setShape(shape); // point -> circle
                line.setCubic(true); // line -> curve
                line.setFilled(true); // area 채우기
                line.setHasLabels(false);
                line.setPointRadius(1);
                line.setHasLabelsOnlyForSelected(false); //눌렀을 때, 라벨 표시
            lines.add(line);
                //미리보기 데이터에는 심장박동수 라인만 넣기!
                linesForPreData.add(line);

        data = new LineChartData(lines); // 최초에 뿌려진 data chart
        preData = new LineChartData(linesForPreData); // 미리보기 chart

        // X축
        Axis axisX = new Axis()
                .setHasLines(true)
                .setLineColor(ChartUtils.DEFAULT_COLOR).setTextColor(ChartUtils.DEFAULT_COLOR);
        data.setAxisXBottom(axisX);

        // Y 축
        Axis axisY = new Axis()
                    .setHasLines(true)
                    .setHasTiltedLabels(true)  // 글자 기울임
                    .setName("Hz");
            data.setAxisYLeft(axisY);

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);
        chart.setZoomEnabled(false);
        chart.setScrollEnabled(false);

        // 미리 보기 설정
        previewData = new LineChartData(preData);
        previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
        previewChart.setLineChartData(previewData);
        previewChart.setViewportChangeListener(new ViewportListener());
        previewChart.setZoomType(ZoomType.HORIZONTAL); // X축 방향으로만 움직임

        setMaxViewport();
        setCurrentViewport();

    }
    // 최대 Viewport 값 지정
    private void setMaxViewport(){
        maxViewport.top = maxValue+30;
        maxViewport.bottom = minValue-10;
        maxViewport.left=0;
        maxViewport.right = mList.size();
        chart.setMaximumViewport(maxViewport);
        previewChart.setMaximumViewport(maxViewport);

    }
    // 현재 보여질 Viewport 값 지정
    private void setCurrentViewport() {

        currentViewport.top=maxValue+30;
        currentViewport.bottom=minValue-10;
        currentViewport.left=0;
        currentViewport.right = maxViewport.width() / 3;
        previewChart.setCurrentViewportWithAnimation(currentViewport);
        chart.setCurrentViewportWithAnimation(currentViewport);
    }

    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {

            chart.setCurrentViewport(newViewport);
        }

    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }
    }



    }
}
