package com.customview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.customview.view.CircleView;

public class MainCircleActivity extends Activity {
    CircleView cv1;
    CircleView cv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȥ����Ϣ��

        setContentView(R.layout.main);
        cv1 = (CircleView) findViewById(R.id.circle_view1);
        cv2 = (CircleView) findViewById(R.id.circle_view2);

        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cv1.setPercent(98);
            }

        });

        Button btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cv2.setPercent(18);
                cv2.setTimeText("10 h 45 min");
                cv2.setDistText("2200 km");
            }

        });

    }

}
