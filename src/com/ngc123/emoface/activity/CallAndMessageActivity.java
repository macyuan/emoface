package com.ngc123.emoface.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ngc123.emoface.R;

public class CallAndMessageActivity extends Activity {

    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_and_message);
        layout=(LinearLayout)findViewById(R.id.exit_layout2);
        layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),"我也不知道这是什么",
                        Toast.LENGTH_SHORT).show(); 
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }
    //打电话
    public void call(View v){
        //直接拨打
//        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:10010"));
        //调用面板
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:10010"));
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        startActivity(intent);
        this.finish();
    }
    //发短信
    public void sendMessage(View v){
        Uri smsToUri = Uri.parse("smsto:10000");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        //intent.putExtra("sms_body", "测试发送短信");
        startActivity(intent);
        this.finish();
    }
    //取消
    public void cancel(View v) {  
        this.finish();      
      }  

}
