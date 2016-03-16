package com.ngc123.emoface.activity;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chat.EMContactManager;
import com.ngc123.emoface.R;
import com.ngc123.emoface.R.id;
import com.ngc123.emoface.R.layout;
import com.ngc123.emoface.R.string;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EmoActivity extends Activity {
    
    private String[] userList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emo);
        mContext = this;
        TextView textView = (TextView)findViewById(R.id.result);
        Intent intent = getIntent();
        Boolean flag = intent.getBooleanExtra("flag",false);
        String resultText = intent.getStringExtra("resultText");
        textView.setText(resultText);
        if(flag){
            ListView listView = (ListView)this.findViewById(R.id.matchlist);
            Bundle bundle = intent.getBundleExtra("userList");
            userList = bundle.getStringArray("userList");
            List<EmoUser> emoUsers = new ArrayList<EmoUser>();
            for(String user : userList){
                emoUsers.add(new EmoUser(user)); 
            }
            EmoUserAdapter emoUserAdapter = new EmoUserAdapter(this,R.layout.emotional_list,R.id.name,emoUsers);
            listView.setAdapter(emoUserAdapter);
        }
    }
    
    
    
    class EmoUserAdapter extends ArrayAdapter<EmoUser>{
        private int mResourceId;
        
        public EmoUserAdapter(Context context, int resource,int textViewResourceId, List<EmoUser> emoUsers) {
            super(context, resource,textViewResourceId, emoUsers);
            // TODO Auto-generated constructor stub
            this.mResourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;  
            EmoListener emoListener=null; 
            emoListener = new EmoListener(position);
            if(convertView == null){
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(mResourceId, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.name);
                holder.button = (Button)convertView.findViewById(R.id.add_emo);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder)convertView.getTag();
            }
            
            holder.textView.setText(getItem(position).toString());
            holder.button.setTag(position);
            holder.button.setOnClickListener(emoListener);
            return convertView;
        }
    }
    
    class EmoUser{
        private String name;
        
        public EmoUser(String name){
            this.name = name;
        }
        
        public String toString(){
            return name;
        }
    }
    
    private static class ViewHolder{
        public Button button;
        public TextView textView;
    }
    
    private class EmoListener implements OnClickListener{
        int mPosition;
        private ProgressDialog progressDialog; 
        
        public EmoListener(int position){
            mPosition = position;
        }
        
        @Override
        public void onClick(View v) {
            progressDialog = new ProgressDialog(mContext);
            String stri = getResources().getString(R.string.Is_sending_a_request);
            progressDialog.setMessage(stri);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            
            new Thread(new Runnable() {
                public void run() {
                    
                    try {
                        //demo写死了个reason，实际应该让用户手动填入
                        EMContactManager.getInstance().addContact(userList[mPosition], "情绪分析匹配好友");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "匹配成功，等待对方验证", 1).show();
                            }
                        });
                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "添加失败" + e.getMessage(), 1).show();
                            }
                        });
                    }
                }
            }).start();
        }
        
    }
}
