package com.ngc123.emoface.activity;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngc123.emoface.R;
import com.ngc123.emoface.activity.SettingsFragment.EmoHandler;
import com.ngc123.emoface.utils.HttpUtils;

public class PersonalInfoActivity extends BaseActivity implements OnClickListener {
    
    /**********************************新增用户信息*****************************************/  
    /**
     * 用户头像imageView
     */
    private ImageView iv_userinfo_photo;
    
    /**
     * 用户昵称
     */
    private TextView tv_userinfo_nicheng;
    /**
     * 用户性别
     */
    private TextView tv_userinfo_xingbie;
    /**
     * 用户年龄
     */
    private TextView tv_userinfo_nianling;
    /**
     * 用户地区
     */
    private TextView tv_userinfo_emo;
    /**
     * 用户职业
     */
    private TextView tv_userinfo_zhiye;
    /**
     * 用户签名
     */
    private TextView tv_userinfo_qianming;
    /**
     * 用户在哪动态
     */
    private TextView tv_userinfo_emodetail;
    
    private String UserPic = null;
    private String UserNickName = null;
    private String UserSex = null;
    private String UserAge = null;
    private String UserArea = null;
    private String UserZaina = null;
    private String UserZhiye = null;
    private String UserQianming = null;
    
    private Button btn_huihua;
    private Button btn_add_f;
    private Button btn_lahei;
    private Button btn_jubao;
    
    private String userId = null;//用户唯一id
    
    public static final int REQUEST_CODE_ADD_TO_CONTACT = 226;
    public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 225;
    private ProgressDialog progressDialog;
    
    private LinearLayout about_gushi;
    private TextView ta_tv;
    private Context mcontext;
    private Handler handler;
    private HashMap<String,String> emoMap = new HashMap<String,String>();
    private HashMap<String,String> emoMapDetail = new HashMap<String,String>(); 
    
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        mcontext = this;
        handler = new EmoHandler();
        username = getIntent().getStringExtra("userId");
        tv_userinfo_nicheng = (TextView)findViewById(R.id.tv_userinfo_nicheng);
        tv_userinfo_nicheng.setText(username);
        tv_userinfo_emo = (TextView)findViewById(R.id.tv_userinfo_emo);
        tv_userinfo_emodetail = (TextView)findViewById(R.id.tv_userinfo_emodetail);
        
        emoMap.put("KQ+", "宽心,接纳，包容");
        emoMap.put("KI+", "专注，注意力集中");
        emoMap.put("CR+", "痛快，爽快，宣泄");
        emoMap.put("CA+", "豪放，从容，开朗");
        emoMap.put("YE+", "决断，果断，坚定");
        emoMap.put("YA+", "平和，平静，理智");
        emoMap.put("ME+", "舒适，轻松，自然");
        emoMap.put("MV+", "欢快，爽畅，舒畅");
        emoMap.put("ML+", "怜爱，同情，关心");
        emoMap.put("WC+", "喜欢，开心，高兴");
        emoMap.put("WS+", "积极，阳光，高涨");
        emoMap.put("WA+", "无谓，泰然，面对");
        
        emoMap.put("KQ-", "急躁，着急，躁动");
        emoMap.put("KI-", "不能专注，分心，异想天开");
        emoMap.put("CR-", "烦躁;暴躁;无名火");
        emoMap.put("CA-", "悲伤;伤心;哭泣");
        emoMap.put("YE-", "怯弱，犹豫不决，摇摆不定");
        emoMap.put("YA-", "生气，指责，攻击");
        emoMap.put("ME-", "紧张，失调，不知所措");
        emoMap.put("MV-", "压抑，窝心，别扭");
        emoMap.put("ML-", "哀伤，失落，幽怨");
        emoMap.put("WC-", "记恨，怨恨，仇恨");
        emoMap.put("WS-", "消极;灰暗;低迷;颓废");
        emoMap.put("WA-", "恐惧;害怕;惊恐");
        
        emoMapDetail.put("KQ+", "宽心的你");
        emoMapDetail.put("KI+", "专注的你");
        emoMapDetail.put("CR+", "爽快的你");
        emoMapDetail.put("CA+", "开朗爱笑的人运气不会太差～");
        emoMapDetail.put("YE+", "相信果敢的自己！！");
        emoMapDetail.put("YA+", "呼～吸～平和的世界如此美好～");
        emoMapDetail.put("ME+", "红酒配电影的生活～舒适～");
        emoMapDetail.put("MV+", "如此欢唱～来杯橘子汽水");
        emoMapDetail.put("ML+", "满满的同情心");
        emoMapDetail.put("WC+", "得意的笑");
        emoMapDetail.put("WS+", "阳光生活每一天！");
        emoMapDetail.put("WA+", "无畏的面对疾风吧！");
        emoMapDetail.put("KQ-", "急迫的心情！情史已经不容缓");
        emoMapDetail.put("KI-", "幻想着～什么时候能征服人类！");
        emoMapDetail.put("CR-", "烦烦烦！最近比较烦！");
        emoMapDetail.put("CA-", "给你一杯忘情水～");
        emoMapDetail.put("YE-", "一点污渍也不能有，处女座～");
        emoMapDetail.put("YA-", "我就BB！哼");
        emoMapDetail.put("ME-", "紧张的要哭出来了！");
        emoMapDetail.put("MV-", "干嘛！别理我！");
        emoMapDetail.put("ML-", "仰望天空这样眼泪就不会落下来！");
        emoMapDetail.put("WC-", "我要去报告老师！呜呜～");
        emoMapDetail.put("WS-", "闭上眼就能看到我的前途");
        emoMapDetail.put("WA-", "别别别！我叫辣！");
        getEmoCode();
    }
    //发送消息，打开聊天界面
    public void sendMessage(View v){
        startActivity(new Intent(PersonalInfoActivity.this, ChatActivity.class).putExtra("userId", getIntent().getStringExtra("userId")));
        this.finish();
    } 
    //返回
    public void back(View view) {
        this.finish();
    }
    //打电话发短信
    public void call_message(View view){
        Intent intent = new Intent(this, CallAndMessageActivity.class);
        startActivity(intent);
    }
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        
        
    }
    
    protected void getEmoCode(){
        new Thread(){

            @Override
            public void run() {
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("username", username);
                String emoCode = HttpUtils.postHttp(data, "/index.php/Emo/getEmo.html");
                Message msg = new Message();
                Bundle b = new Bundle();
                if(emoCode.equals("null")){
                    b.putString("emo", "未测试过情绪");
                    b.putString("emoDetail","未测试过情绪");
                }else{
                    b.putString("emo", emoMap.get(emoCode));
                    b.putString("emoDetail",emoMapDetail.get(emoCode));
                }
                msg.setData(b);
                handler.sendMessage(msg);
            }
            
        }.start();
        
    }
    
    class EmoHandler extends Handler{
        public EmoHandler(){
            
        }
        
        public EmoHandler(Looper L){
            super(L);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Bundle b = msg.getData();
            String emoStr = b.getString("emo");
            String emoDetailStr = b.getString("emoDetail");
            tv_userinfo_emo.setText(emoStr);
            tv_userinfo_emodetail.setText(emoDetailStr);
            super.handleMessage(msg);
        }
        
        
    }
}
