package com.customview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.customview.view.CircleView;
import com.customview.view.DataChartView;

public class MainChartActivity extends Activity {
    DataChartView dcv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȥ����Ϣ��

        setContentView(R.layout.chart_main);
        dcv1 = (DataChartView) findViewById(R.id.chart_view1);

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dcv1.setXAxisString(new String[] { "500 h", "400 h", "300 h", "200 h", "100 h", "Now" });
            }

        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dcv1.setXAxisValue(new int[] {188, 0, -89, 99, 121, -56});
            }

        });

    }

}
