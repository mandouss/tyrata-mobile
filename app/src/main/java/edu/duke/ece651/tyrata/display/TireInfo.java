package edu.duke.ece651.tyrata.display;

/**
 * This class has Tireinfo display page
 * @author De Lan
 * Created by Alan on 2/27/2018.
 */
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.duke.ece651.tyrata.R;
import edu.duke.ece651.tyrata.calibration.TireInfoInput;
import edu.duke.ece651.tyrata.datamanagement.Database;
import edu.duke.ece651.tyrata.vehicle.Tire;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class TireInfo extends AppCompatActivity {
    int axis_row;
    int axis_index;
    char axis_side;
    String vin;
    String message_manufacturer;
    String message_sensorID;
    String message_model;
    String message_SKU;
    String message_Thickness;

    private LineChartView lineChart;

    String[] date = {"10-22","11-22","12-22","1-22","6-22","5-23","5-22","6-22","5-23","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22","5-22"};//X轴的标注
    int[] score= {50,42,90,33,10,74,22,18,79,20,50,42,90,33,10,74,22,18,79,20,50,42,90,33,10,74,22,18,79,20};//图表的数据点
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tire_info);
        Database.myDatabase = openOrCreateDatabase("TyrataData", MODE_PRIVATE, null);
        Tire curr_tire = Database.getTire("sensor1");
        Database.myDatabase.close();
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        axis_row = intent.getIntExtra("AXIS_ROW",0);
        axis_index = intent.getIntExtra("AXIS_INDEX",0);
        axis_side = intent.getCharExtra("AXIS_SIDE",'a');
        vin = intent.getStringExtra("VIN");

        if(curr_tire != null) {
            message_manufacturer = curr_tire.getManufacturer();
            message_sensorID = curr_tire.getSensor();
            message_model = curr_tire.getModel();
            message_SKU = curr_tire.getSku();
            message_Thickness = String.valueOf(curr_tire.get_INIT_THICK());
        }

        if(message_manufacturer == null)
            message_manufacturer = "Default manufacturer";
        TextView textView_manufacturer = findViewById(R.id.textView_manufacturer);
        textView_manufacturer.setText(message_manufacturer);

        if(message_sensorID == null)
            message_sensorID = "Default sensorID";
        TextView textView_sensorID = findViewById(R.id.textView_sensorID);
        textView_sensorID.setText(message_sensorID);

        if(message_model == null)
            message_model = "Default MODEL";
        TextView textView_model = findViewById(R.id.textView_model);
        textView_model.setText(message_model);

        if(message_SKU == null)
            message_SKU = "Default SKU";
        TextView textView_SKU = findViewById(R.id.textView_SKU);
        textView_SKU.setText(message_SKU);

        //TODO: calculate thickness

        if(message_Thickness == null)
            message_Thickness = "Default THICKNESS";
        TextView textView_Thickness = findViewById(R.id.textView_thickness);
        textView_Thickness.setText(message_Thickness);


        /* @TODO: read S11 and odometer from database, sync with BT*/
        String message_Odometer = "odometer from BT";
        TextView textView_Odometer = findViewById(R.id.textView_odometer);
        textView_Odometer.setText(message_Odometer);

        String message_S11 = "S11 from BT";
        TextView textView_S11 = findViewById(R.id.textView_S11);
        textView_S11.setText(message_S11);


        String message_EOL = "EOL from calculation";
        TextView textView_EOL = findViewById(R.id.textView_EOL);
        textView_EOL.setText(message_EOL);

        String message_rep = "time to rep from calculation";
        TextView textView_rep = findViewById(R.id.textView_replace);
        textView_rep.setText(message_rep);

        lineChart = (LineChartView)findViewById(R.id.line_chart);
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化

    }
    public void switchToEdit(View view) {
        Intent intent = new Intent(TireInfo.this, TireInfoInput.class);

        intent.putExtra("axis_IDX", axis_index);
        intent.putExtra("axis_ROW", axis_row);
        intent.putExtra("axis_SIDE", axis_side);
        intent.putExtra("VIN",vin);

        Log.i("NOTIFICATION","Tireinfo");
        Log.i("axis_ROW",String.valueOf(axis_row));
        Log.i("axis_IDX", String.valueOf(axis_index));
        Log.i("axis_SIDE", String.valueOf(axis_side));
        Log.i("VIN", vin);
        startActivity(intent);
    }
    //TODO: call BT to for S11 and ODM ref
    public void switchToS11ODM(View view) {
        Intent intent = new Intent(TireInfo.this, TireInfoInput.class);

        startActivity(intent);
    }

    private void getAxisXLables(){
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }

    private void getAxisPoints() {
        for (int i = 0; i < score.length; i++) {
            mPointValues.add(new PointValue(i, score[i]));
        }
    }

    private void initLineChart(){
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //color of the line（orange）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//shape of the dot on line  (circle) （three types ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//curve or broken line
        line.setFilled(false);
        line.setHasLabels(true);
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//whether have line
        line.setHasPoints(true);
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //X axis
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(true);  //whether the x axis text is italic
        axisX.setTextColor(Color.BLACK);  //text color
        //axisX.setName("date");  //axis name
        axisX.setTextSize(14);//text size
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x axis is at bottom
        axisX.setHasLines(true); //x axis dividing rules


        // Y axis
        Axis axisY = new Axis();
        axisY.setTextColor(Color.BLACK);
        axisY.setName("Tire Thickness");
        axisY.setTextSize(14);
        data.setAxisYLeft(axisY);  //Y axis is on the left
        axisY.setHasLines(true);

        /*axisY.setMaxLabelChars(6);//max label length, for example 60
        List<AxisValue> values = new ArrayList<>();
        for(int i = 0; i < 100; i+= 10){
            AxisValue value = new AxisValue(i);
            String label = String.valueOf(i);
            value.setLabel(label);
            values.add(value);
        }
        axisY.setValues(values);*/




        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right= 7;
        lineChart.setCurrentViewport(v);
    }
}
