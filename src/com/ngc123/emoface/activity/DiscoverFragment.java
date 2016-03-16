/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ngc123.emoface.activity;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.easemob.chat.EMChatManager;
import com.hymusic.hymusicsdk.InitListener;
import com.hymusic.hymusicsdk.heartrate.EmoRateListener;
import com.hymusic.hymusicsdk.heartrate.Ratedetect;
import com.hymusic.hymusicsdk.record.EmotionVoiceListener;
import com.hymusic.hymusicsdk.record.Emotiondetect;
import com.hymusic.hymusicsdk.record.RecognizerResult;
import com.hymusic.hymusicsdk.util.SDKUtil;
import com.hymusic.hymusicsdk.util.sdkAppInit;
import com.ngc123.emoface.Constant;
import com.ngc123.emoface.R;
import com.ngc123.emoface.tools.BlurLayout;
import com.ngc123.emoface.utils.HttpUtils;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class DiscoverFragment extends Fragment implements OnClickListener {
	
    
    private InputMethodManager inputMethodManager;
    
    /**
     * 摇一摇按钮
     */
    private Button rockBtn;
    private Button rockBtn2;
    
    private Context mContext;
    private BlurLayout mSampleLayout, mSampleLayout2, mSampleLayout3,
            mSampleLayout4;
    

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_discover, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;		
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		mContext = getActivity();
		
		sdkAppInit.CreateInstance(mContext);
        emotiondetect = Emotiondetect.createRecognizer(mContext, mInitListener);
        rt = Ratedetect.createRecognizer(mContext, mInitListener);
		
        BlurLayout.setGlobalDefaultDuration(450);
        mSampleLayout = (BlurLayout) getView().findViewById(R.id.blur_layout);
        View hover = LayoutInflater.from(mContext).inflate(
                R.layout.hover_sample, null);
        hover.findViewById(R.id.heart).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Tada).duration(550).playOn(v);
                    }
                });
        hover.findViewById(R.id.share).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.Swing).duration(550).playOn(v);
                    }
                });
        mSampleLayout.setHoverView(hover);
        mSampleLayout.setBlurDuration(550);
        mSampleLayout.addChildAppearAnimator(hover, R.id.heart,
                Techniques.FlipInX);
        mSampleLayout.addChildAppearAnimator(hover, R.id.share,
                Techniques.FlipInX);
        mSampleLayout.addChildAppearAnimator(hover, R.id.more,
                Techniques.FlipInX);

        mSampleLayout.addChildDisappearAnimator(hover, R.id.heart,
                Techniques.FlipOutX);
        mSampleLayout.addChildDisappearAnimator(hover, R.id.share,
                Techniques.FlipOutX);
        mSampleLayout.addChildDisappearAnimator(hover, R.id.more,
                Techniques.FlipOutX);

        mSampleLayout.addChildAppearAnimator(hover, R.id.description,
                Techniques.FadeInUp);
        mSampleLayout.addChildDisappearAnimator(hover, R.id.description,
                Techniques.FadeOutDown);

        // sample 2

        mSampleLayout2 = (BlurLayout) getView().findViewById(R.id.blur_layout2);
        View hover2 = LayoutInflater.from(mContext).inflate(
                R.layout.hover_sample2, null);
        hover2.findViewById(R.id.avatar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "Pretty Cool, Right?",
                                Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent();
                        intent.putExtra("flog", "p");
                        intent.setClass(getActivity(), RockActivity.class);
                        startActivity(intent);
                        
                    }
                });
        mSampleLayout2.setHoverView(hover2);

        mSampleLayout2.addChildAppearAnimator(hover2, R.id.description,
                Techniques.FadeInUp);
        mSampleLayout2.addChildDisappearAnimator(hover2, R.id.description,
                Techniques.FadeOutDown);
        mSampleLayout2.addChildAppearAnimator(hover2, R.id.avatar,
                Techniques.DropOut, 1200);
        mSampleLayout2.addChildDisappearAnimator(hover2, R.id.avatar,
                Techniques.FadeOutUp);
        mSampleLayout2.setBlurDuration(1000);

        // sample3
        mSampleLayout3 = (BlurLayout) getView().findViewById(R.id.blur_layout3);
        View hover3 = LayoutInflater.from(mContext).inflate(
                R.layout.hover_sample3, null);
        hover3.findViewById(R.id.eye).setOnClickListener(
                new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        emotiondetect.startListening(mRecognizerListener);
                        
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                }
                                emotiondetect.stopListening();
                            }
                        }).start();
                        
                    }
                });
        
        
        mSampleLayout3.setHoverView(hover3);
        mSampleLayout3.addChildAppearAnimator(hover3, R.id.eye,
                Techniques.Landing);
        mSampleLayout3.addChildDisappearAnimator(hover3, R.id.eye,
                Techniques.TakingOff);
        mSampleLayout3.enableZoomBackground(true);
        mSampleLayout3.setBlurDuration(1200);

        // sample 4

        mSampleLayout4 = (BlurLayout) getView().findViewById(R.id.blur_layout4);
        View hover4 = LayoutInflater.from(mContext).inflate(
                R.layout.hover_sample4, null);
        mSampleLayout4.setHoverView(hover4);
        mSampleLayout4.addChildAppearAnimator(hover4, R.id.cat,
                Techniques.SlideInLeft);
        mSampleLayout4.addChildAppearAnimator(hover4, R.id.mail,
                Techniques.SlideInRight);

        mSampleLayout4.addChildDisappearAnimator(hover4, R.id.cat,
                Techniques.SlideOutLeft);
        mSampleLayout4.addChildDisappearAnimator(hover4, R.id.mail,
                Techniques.SlideOutRight);

        mSampleLayout4.addChildAppearAnimator(hover4, R.id.content,
                Techniques.BounceIn);
        mSampleLayout4.addChildDisappearAnimator(hover4, R.id.content,
                Techniques.FadeOutUp);

        hover4.findViewById(R.id.cat).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent getWebPage = new Intent(Intent.ACTION_VIEW, Uri
                                .parse("http://www.baidu.com"));
                        startActivity(getWebPage);
                    }
                });

        hover4.findViewById(R.id.mail).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent emailIntent = new Intent(
                                android.content.Intent.ACTION_SEND);

                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(
                                android.content.Intent.EXTRA_EMAIL,
                                new String[] { "153516796@qq.com" });
                        emailIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT,
                                "About AndroidViewHover");
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                "我想提出的意见..");

                        startActivity(Intent.createChooser(emailIntent,
                                "Send mail..."));
                    }
                });

		
		
//		rockBtn = (Button) getView().findViewById(R.id.btn_rock_one);
//		rockBtn2 = (Button) getView().findViewById(R.id.btn_rock_two);
//		rockBtn.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
	    Intent intent = null;
//		switch (v.getId()) {
//		case R.id.btn_rock_one:
//            intent = new Intent();
//            intent.putExtra("flog", "p");
//            intent.setClass(getActivity(), RockActivity.class);
//            startActivity(intent);
//            break;
//		case R.id.btn_rock_two:
//            intent = new Intent();
//            intent.putExtra("flog", "c");
//            intent.setClass(getActivity(), RockActivity.class);
//            startActivity(intent);
//            break;
//		default:
//			break;
//		}
		
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
        	outState.putBoolean("isConflict", true);
        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
        	outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }
    
    /**************************************情绪分析***************************/
    private Emotiondetect emotiondetect;
    //情绪代码与自然语言转换
    private final HashMap<String,Object> emoMap = new HashMap<String,Object>();
    //与服务器通讯数据
    private final HashMap<String,Object> emoData = new HashMap<String,Object>();

    Ratedetect rt;
    
    /**
     * 语音识别监听器。
     */
    private EmotionVoiceListener mRecognizerListener = new EmotionVoiceListener() {

        @Override
        public void onVolumeChanged(int volume) {
            Log.d("test","当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            Log.i("EEE", "THE RESULT IS" + result.toString());
        }

        @Override
        public void onEndOfSpeech() {
            
        }

        @Override
        public void onBeginOfSpeech() {
            
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onVoiceResult(String result) {
            // TODO Auto-generated method stub
            emoJson(result);
            
        }

    };

    /**
     * 心率识别监听器。
     */
    EmoRateListener recoginze_listener = new EmoRateListener() {

        @Override
        public void Begindetect() {
            // TODO Auto-generated method stub
        }

        @Override
        public void Enddetect(String result) {
            // TODO Auto-generated method stub
            emoJson(result);
        }

        @Override
        public void monitor(double rgb) {
            // TODO Auto-generated method stub
            Log.i("recognizetag", "" + rgb);

        }

    };
    
    /**
     * 处理返回的json结果
     */
    protected void emoJson(String result){
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
        Log.d("json", "THE RESULT INFO IS" + result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            int resultcode = jsonObject.getInt("resultcode");
            Log.d("5v",resultcode+"");
            
            if(resultcode == 200){
                //发送结果到数据库
                
                String username = EMChatManager.getInstance().getCurrentUser();
                String rc_main = jsonObject.getString("rc_main");
                String rc_minor = jsonObject.getString("rc_minor");
                emoData.put("username", URLEncoder.encode(username, "UTF-8"));
                emoData.put("rc_main", URLEncoder.encode(rc_main, "UTF-8"));
                emoData.put("rc_minor", URLEncoder.encode(rc_minor, "UTF-8"));
                final String main = (String)emoMap.get(rc_main);
                final String minor = (String)emoMap.get(rc_minor);
                
                new Thread(){
                    public void run(){
                        String resStr = HttpUtils.postHttp(emoData, "/index.php/Emo/addEmo.html");
                        
                        try {
                            String flag = "false";
                            if(resStr.equals("null")){
                                Intent intent = new Intent(getActivity(),EmoActivity.class);
                                intent.putExtra("flag",false);
                                intent.putExtra("resultText","主要情绪"+main+"，次要情绪"+minor+" 暂时没有与您情绪匹配的用户");
                                startActivity(intent);
                                return;
                            }
                            Intent intent = new Intent(getActivity(),EmoActivity.class);
                            intent.putExtra("resultText","主要情绪"+main+"，次要情绪"+minor+" 以下是系统为您匹配的用户");
                            intent.putExtra("flag",true);
                            JSONArray jsonArray = new JSONArray(resStr);
                            int length = jsonArray.length();
                            JSONObject[] jsonObj = new JSONObject[length];
                            String[] userList = new String[length];
                            for(int i=0;i<length;i++){
                                jsonObj[i] = jsonArray.getJSONObject(i);
                                userList[i] = jsonObj[i].getString("username");
                                Log.d("5v",jsonObj[i].getString("username"));
                            }
                            Bundle bundle = new Bundle();
                            bundle.putStringArray("userList", userList);
                            intent.putExtra("userList", bundle);
                            startActivity(intent);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }.start();
            }else{
                final Map<String, Object> param = new HashMap<String, Object>();
                param.put("front_camera", "" + SDKUtil.Front_carmera);
                rt.startratelistening(param, recoginze_listener);
                Log.d("5v","打印错误提示");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            sdkAppInit.registerforuid("demo11", "dupf", "test");
        }
    };
}
