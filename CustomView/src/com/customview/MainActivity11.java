package com.customview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import com.customview.view.GoodProgressView;
import android.app.Activity;
import android.graphics.Color;

public class MainActivity11 extends Activity
{

	GoodProgressView good_progress_view1;
	GoodProgressView good_progress_view2;
	private int mRadio;
	
	int progressValue=0;	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȥ����Ϣ��
		
		setContentView(R.layout.activity_main);
		good_progress_view1 = (GoodProgressView)findViewById(R.id.good_progress_view1);
		good_progress_view2 = (GoodProgressView)findViewById(R.id.good_progress_view2);
		
		//��һ��������ʹ��Ĭ�Ͻ�����ɫ���ڶ���ָ����ɫ��������ɣ�
		good_progress_view2.setColors(randomColors());

        timer.schedule(task, 1000, 1000); // 1s��ִ��task,����1s�ٴ�ִ��          
	}

    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
            if (msg.what == 1) {  
            	Log.i("log","handler : progressValue="+progressValue);
            	
            	//֪ͨview������ֵ�б仯
            	good_progress_view1.setProgressValue(progressValue*2);
            	good_progress_view1.postInvalidate();
                
            	good_progress_view2.setProgressValue(progressValue);
            	good_progress_view2.postInvalidate();
            	
                progressValue+=1;
                if(progressValue>100){
                	timer.cancel();
                }
            }  
            super.handleMessage(msg);              
        };  
    };  
    
    private int[] randomColors() {
    	int[] colors=new int[2];

		Random random = new Random();
		int r,g,b;
		for(int i=0;i<2;i++){
			r=random.nextInt(256);
			g=random.nextInt(256);
			b=random.nextInt(256);
			colors[i]=Color.argb(255, r, g, b);
			Log.i("customView","log: colors["+i+"]="+Integer.toHexString(colors[i]));
		}
    	
		return colors;
	}
    
    Timer timer = new Timer();  
    TimerTask task = new TimerTask() {  
  
        @Override  
        public void run() {  
            // ��Ҫ������:������Ϣ  
        	Message message = new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
        }  
    }; 	

}
