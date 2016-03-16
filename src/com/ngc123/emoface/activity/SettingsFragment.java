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

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.ngc123.applib.controller.HXSDKHelper;
import com.ngc123.emoface.Constant;
import com.ngc123.emoface.DemoApplication;
import com.ngc123.emoface.DemoHXSDKModel;
import com.ngc123.emoface.R;
import com.ngc123.emoface.utils.HttpUtils;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class SettingsFragment extends Fragment implements OnClickListener {

    
    /**********************************新增用户信息*****************************************/
    /**
     * 设置用户头像
     */
    
    private String username = EMChatManager.getInstance().getCurrentUser(); 
    
    private RelativeLayout rl_user_pic;
    /**
     * 设置用户昵称
     */
    private RelativeLayout rl_user_nicheng;
    /**
     * 设置用户性别
     */
    private RelativeLayout rl_user_xingbie;
    /**
     * 设置用户年龄
     */
    private RelativeLayout rl_user_nianling;
    /**
     * 设置用户心情
     */
    private RelativeLayout rl_user_emo;
    /**
     * 设置用户职业
     */
    private RelativeLayout rl_user_zhiye;
    /**
     * 设置用户签名
     */
    private RelativeLayout rl_user_qianming;
    /**
     * 设置用户心情的动态
     */
    private RelativeLayout rl_user_emodetail;
    
    /**
     * 用户头像imageView
     */
    private ImageView iv_user_photo;
    
    /**
     * 用户昵称
     */
    private TextView tv_user_nicheng;
    /**
     * 用户性别
     */
    private TextView tv_user_xingbie;
    /**
     * 用户年龄
     */
    private TextView tv_user_nianling;
    /**
     * 用户情绪
     */
    private TextView tv_user_emo;
    /**
     * 用户职业
     */
    private TextView tv_user_zhiye;
    /**
     * 用户签名
     */
    private TextView tv_user_qianming;
    /**
     * 用户在哪动态
     */
    public TextView tv_user_emodetail;
    
    private String UserPic = null;
    private String UserNickName = null;
    private String UserSex = null;
    private String UserAge = null;
    private String UserArea = null;
    private String UserZaina = null;
    private String UserZhiye = null;
    private String UserQianming = null;
    
    private Context mcontext;
    
    private String emoCode;
    
    
	/**
	 * 设置新消息通知布局
	 */
	private RelativeLayout rl_switch_notification;
	/**
	 * 设置声音布局
	 */
	private RelativeLayout rl_switch_sound;
	/**
	 * 设置震动布局
	 */
	private RelativeLayout rl_switch_vibrate;
	/**
	 * 设置扬声器布局
	 */
	private RelativeLayout rl_switch_speaker;

	/**
	 * 打开新消息通知imageView
	 */
	private ImageView iv_switch_open_notification;
	/**
	 * 关闭新消息通知imageview
	 */
	private ImageView iv_switch_close_notification;
	/**
	 * 打开声音提示imageview
	 */
	private ImageView iv_switch_open_sound;
	/**
	 * 关闭声音提示imageview
	 */
	private ImageView iv_switch_close_sound;
	/**
	 * 打开消息震动提示
	 */
	private ImageView iv_switch_open_vibrate;
	/**
	 * 关闭消息震动提示
	 */
	private ImageView iv_switch_close_vibrate;
	/**
	 * 打开扬声器播放语音
	 */
	private ImageView iv_switch_open_speaker;
	/**
	 * 关闭扬声器播放语音
	 */
	private ImageView iv_switch_close_speaker;

	/**
	 * 声音和震动中间的那条线
	 */
	private TextView textview1, textview2;

	private LinearLayout blacklistContainer;
	
	
	private HashMap<String,String> emoMap = new HashMap<String,String>();
	private HashMap<String, String> emoMapDetail = new HashMap<String,String>();
	private EmoHandler handler;

	/**
	 * 退出按钮
	 */
	private Button logoutBtn;
	private ProgressDialog pd;

	private RelativeLayout rl_switch_chatroom_leave;
	private ImageView iv_switch_room_owner_leave_allow;
	private ImageView iv_switch_room_owner_leave_disallow;
	
	private EMChatOptions chatOptions;
 
	/**
	 * 诊断
	 */
	private LinearLayout llDiagnose;
	/**
	 * iOS离线推送昵称
	 */
	private LinearLayout pushNick;
	
	DemoHXSDKModel model;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    mcontext = getActivity();
	    handler = new EmoHandler();
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
		return inflater.inflate(R.layout.fragment_conversation_settings, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
		pd = new ProgressDialog(getActivity());
        pd.setMessage("正在提交请求...");
        
        

		
		/***************用户信息设置**************/
        rl_user_pic  = (RelativeLayout) getView().findViewById(R.id.rl_user_pic);
        rl_user_nicheng = (RelativeLayout) getView().findViewById(R.id.rl_user_nicheng);
        rl_user_xingbie = (RelativeLayout) getView().findViewById(R.id.rl_user_xingbie);
        rl_user_nianling = (RelativeLayout) getView().findViewById(R.id.rl_user_nianling);
        rl_user_emo = (RelativeLayout) getView().findViewById(R.id.rl_user_emo);
        rl_user_emodetail = (RelativeLayout) getView().findViewById(R.id.rl_user_emodetail);
        //---------
        rl_user_zhiye = (RelativeLayout) getView().findViewById(R.id.rl_user_zhiye);
        rl_user_qianming = (RelativeLayout) getView().findViewById(R.id.rl_user_qianming);
        
        //实例化用户信息组件
        iv_user_photo = (ImageView) getView().findViewById(R.id.iv_user_photo);
        tv_user_nicheng = (TextView) getView().findViewById(R.id.tv_user_nicheng);
        tv_user_xingbie = (TextView) getView().findViewById(R.id.tv_user_xingbie);
        tv_user_nianling = (TextView) getView().findViewById(R.id.tv_user_nianling);
        tv_user_emo = (TextView) getView().findViewById(R.id.tv_user_emo);
        tv_user_emodetail = (TextView) getView().findViewById(R.id.tv_user_emodetail);
        //--------
        tv_user_zhiye = (TextView) getView().findViewById(R.id.tv_user_zhiye);
        tv_user_qianming = (TextView) getView().findViewById(R.id.tv_user_qianming);
        
        /***********初始化值*********/
        tv_user_nicheng.setText(username);
        getEmoCode();
        
		
		
		rl_switch_notification = (RelativeLayout) getView().findViewById(R.id.rl_switch_notification);
		rl_switch_sound = (RelativeLayout) getView().findViewById(R.id.rl_switch_sound);
		rl_switch_vibrate = (RelativeLayout) getView().findViewById(R.id.rl_switch_vibrate);
		rl_switch_speaker = (RelativeLayout) getView().findViewById(R.id.rl_switch_speaker);
		rl_switch_chatroom_leave = (RelativeLayout) getView().findViewById(R.id.rl_switch_chatroom_owner_leave);

		iv_switch_open_notification = (ImageView) getView().findViewById(R.id.iv_switch_open_notification);
		iv_switch_close_notification = (ImageView) getView().findViewById(R.id.iv_switch_close_notification);
		iv_switch_open_sound = (ImageView) getView().findViewById(R.id.iv_switch_open_sound);
		iv_switch_close_sound = (ImageView) getView().findViewById(R.id.iv_switch_close_sound);
		iv_switch_open_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_open_vibrate);
		iv_switch_close_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_close_vibrate);
		iv_switch_open_speaker = (ImageView) getView().findViewById(R.id.iv_switch_open_speaker);
		iv_switch_close_speaker = (ImageView) getView().findViewById(R.id.iv_switch_close_speaker);
		
		iv_switch_room_owner_leave_allow = (ImageView) getView().findViewById(R.id.iv_switch_chatroom_owner_leave_allow);
		iv_switch_room_owner_leave_disallow = (ImageView) getView().findViewById(R.id.iv_switch_chatroom_owner_leave_not_allow);
		
		logoutBtn = (Button) getView().findViewById(R.id.btn_logout);
		if(!TextUtils.isEmpty(EMChatManager.getInstance().getCurrentUser())){
			logoutBtn.setText(getString(R.string.button_logout) + "(" + EMChatManager.getInstance().getCurrentUser() + ")");
		}

		textview1 = (TextView) getView().findViewById(R.id.textview1);
		textview2 = (TextView) getView().findViewById(R.id.textview2);
		
		blacklistContainer = (LinearLayout) getView().findViewById(R.id.ll_black_list);
		llDiagnose=(LinearLayout) getView().findViewById(R.id.ll_diagnose);
		pushNick=(LinearLayout) getView().findViewById(R.id.ll_set_push_nick);
		
		/**********设置***********/
        rl_user_pic.setOnClickListener(this);   
        rl_user_nicheng.setOnClickListener(this);
        rl_user_xingbie.setOnClickListener(this);  
        rl_user_nianling.setOnClickListener(this); 
        rl_user_emo.setOnClickListener(this); 
        rl_user_emodetail.setOnClickListener(this);
        rl_user_zhiye.setOnClickListener(this);
        rl_user_qianming.setOnClickListener(this);
		
		blacklistContainer.setOnClickListener(this);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_sound.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);
		rl_switch_speaker.setOnClickListener(this);
		logoutBtn.setOnClickListener(this);
		llDiagnose.setOnClickListener(this);
		pushNick.setOnClickListener(this);
		rl_switch_chatroom_leave.setOnClickListener(this);
		
		chatOptions = EMChatManager.getInstance().getChatOptions();
		
		model = (DemoHXSDKModel) HXSDKHelper.getInstance().getModel();
		
		// 震动和声音总开关，来消息时，是否允许此开关打开
		// the vibrate and sound notification are allowed or not?
		if (model.getSettingMsgNotification()) {
			iv_switch_open_notification.setVisibility(View.VISIBLE);
			iv_switch_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_notification.setVisibility(View.INVISIBLE);
			iv_switch_close_notification.setVisibility(View.VISIBLE);
		}
		
		// 是否打开声音
		// sound notification is switched on or not?
		if (model.getSettingMsgSound()) {
			iv_switch_open_sound.setVisibility(View.VISIBLE);
			iv_switch_close_sound.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_sound.setVisibility(View.INVISIBLE);
			iv_switch_close_sound.setVisibility(View.VISIBLE);
		}
		
		// 是否打开震动
		// vibrate notification is switched on or not?
		if (model.getSettingMsgVibrate()) {
			iv_switch_open_vibrate.setVisibility(View.VISIBLE);
			iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
			iv_switch_close_vibrate.setVisibility(View.VISIBLE);
		}

		// 是否打开扬声器
		// the speaker is switched on or not?
		if (model.getSettingMsgSpeaker()) {
			iv_switch_open_speaker.setVisibility(View.VISIBLE);
			iv_switch_close_speaker.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_speaker.setVisibility(View.INVISIBLE);
			iv_switch_close_speaker.setVisibility(View.VISIBLE);
		}

		// 是否允许聊天室owner leave
		if(model.isChatroomOwnerLeaveAllowed()){
		    iv_switch_room_owner_leave_allow.setVisibility(View.VISIBLE);
		    iv_switch_room_owner_leave_disallow.setVisibility(View.INVISIBLE);
		}else{
		    iv_switch_room_owner_leave_allow.setVisibility(View.INVISIBLE);
            iv_switch_room_owner_leave_disallow.setVisibility(View.VISIBLE);
		}
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_user_pic:
            //startActivity(new Intent(getActivity(), BlacklistActivity.class));
            //更改头像图片
            break;
        case R.id.rl_user_nicheng:
            //startActivity(new Intent(getActivity(), BlacklistActivity.class));
            //修改昵称
            break;
        case R.id.rl_user_xingbie:
            //修改性别
            break;
        case R.id.rl_user_nianling:
            //修改年龄
            changeAge(""+tv_user_nianling.getText().toString().trim());
            break;
        case R.id.rl_user_emo:
            //修改城市
            break;
        case R.id.rl_user_zhiye:
            //修改职业
            break;
        case R.id.rl_user_qianming:
            //修改签名
            break;
        case R.id.rl_user_emodetail:
            //修改在哪动态
            break;
		case R.id.rl_switch_notification:
			if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
				iv_switch_open_notification.setVisibility(View.INVISIBLE);
				iv_switch_close_notification.setVisibility(View.VISIBLE);
				rl_switch_sound.setVisibility(View.GONE);
				rl_switch_vibrate.setVisibility(View.GONE);
				textview1.setVisibility(View.GONE);
				textview2.setVisibility(View.GONE);
				chatOptions.setNotificationEnable(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);

				HXSDKHelper.getInstance().getModel().setSettingMsgNotification(false);
			} else {
				iv_switch_open_notification.setVisibility(View.VISIBLE);
				iv_switch_close_notification.setVisibility(View.INVISIBLE);
				rl_switch_sound.setVisibility(View.VISIBLE);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				textview1.setVisibility(View.VISIBLE);
				textview2.setVisibility(View.VISIBLE);
				chatOptions.setNotificationEnable(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgNotification(true);
			}
			break;
		case R.id.rl_switch_sound:
			if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
				iv_switch_open_sound.setVisibility(View.INVISIBLE);
				iv_switch_close_sound.setVisibility(View.VISIBLE);
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
			} else {
				iv_switch_open_sound.setVisibility(View.VISIBLE);
				iv_switch_close_sound.setVisibility(View.INVISIBLE);
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
				iv_switch_close_vibrate.setVisibility(View.VISIBLE);
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
			} else {
				iv_switch_open_vibrate.setVisibility(View.VISIBLE);
				iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
			}
			break;
		case R.id.rl_switch_speaker:
			if (iv_switch_open_speaker.getVisibility() == View.VISIBLE) {
				iv_switch_open_speaker.setVisibility(View.INVISIBLE);
				iv_switch_close_speaker.setVisibility(View.VISIBLE);
				chatOptions.setUseSpeaker(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSpeaker(false);
			} else {
				iv_switch_open_speaker.setVisibility(View.VISIBLE);
				iv_switch_close_speaker.setVisibility(View.INVISIBLE);
				chatOptions.setUseSpeaker(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
			}
			break;
		case R.id.rl_switch_chatroom_owner_leave:
		    if(this.iv_switch_room_owner_leave_allow.getVisibility() == View.VISIBLE){
		        iv_switch_room_owner_leave_allow.setVisibility(View.INVISIBLE);
                iv_switch_room_owner_leave_disallow.setVisibility(View.VISIBLE);
                chatOptions.allowChatroomOwnerLeave(false);
                EMChatManager.getInstance().setChatOptions(chatOptions);
                model.allowChatroomOwnerLeave(false);

		    }else{
		        iv_switch_room_owner_leave_allow.setVisibility(View.VISIBLE);
                iv_switch_room_owner_leave_disallow.setVisibility(View.INVISIBLE);
                chatOptions.allowChatroomOwnerLeave(true);
                EMChatManager.getInstance().setChatOptions(chatOptions);
                model.allowChatroomOwnerLeave(true);
		    }
		    break;
		case R.id.btn_logout: //退出登陆
			logout();
			break;
		case R.id.ll_black_list:
			startActivity(new Intent(getActivity(), BlacklistActivity.class));
			break;
		case R.id.ll_diagnose:
			startActivity(new Intent(getActivity(), DiagnoseActivity.class));
			break;
		case R.id.ll_set_push_nick:
			startActivity(new Intent(getActivity(), OfflinePushNickActivity.class));
			break;
		default:
			break;
		}
		
	}

	void logout() {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st = getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		DemoApplication.getInstance().logout(new EMCallBack() {
			
			@Override
			public void onSuccess() {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						// 重新显示登陆页面
						((MainActivity) getActivity()).finish();
						startActivity(new Intent(getActivity(), LoginActivity.class));
						
					}
				});
			}
			
			@Override
			public void onProgress(int progress, String status) {
				
			}
			
			@Override
			public void onError(int code, String message) {
				
			}
		});
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
    
    protected void changeAge(String age){
        final EditText texta = new EditText(getActivity());
        texta.setText(age);
        //设置EditText输入类型
        texta.setKeyListener(new NumberKeyListener() {  
            public int getInputType() {  
                return InputType.TYPE_CLASS_PHONE;  
            } 
            @Override
            protected char[] getAcceptedChars() {
                // TODO Auto-generated method stub
                char[] numbers = new char[] {'0', '1', '2', '3', '4', '5','6','7','8','9'};  
                return numbers;  
            } 
        });
        new AlertDialog.Builder(getActivity())  
        .setTitle("请输入您的年龄")  
        .setIcon(android.R.drawable.ic_dialog_info)  
        .setView(texta)  
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {
                String age = texta.getEditableText().toString();
                HashMap<String, Object> data = new HashMap<String,Object>();
                data.put("username", username);
                data.put("age", age);
                HttpUtils.postHttp(data, "/index.php/Info/addInfo.html");
                //设置你的操作事项  
                pd.show();
                dialog.dismiss();
            }  
        })  
        .setNegativeButton("取消", null)  
        .show();
    }
    
    protected void getEmoCode(){
        new Thread(){

            @Override
            public void run() {
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("username", username);
                emoCode = HttpUtils.postHttp(data, "/index.php/Emo/getEmo.html");
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
            tv_user_emo.setText(emoStr);
            tv_user_emodetail.setText(emoDetailStr);
            super.handleMessage(msg);
        }
        
        
    }
    
    
    
    
    
}
