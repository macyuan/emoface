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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.ngc123.emoface.DemoApplication;
import com.ngc123.emoface.R;
import com.ngc123.emoface.utils.FileUtil;
import com.ngc123.emoface.utils.NetUtil;
import com.ngc123.emoface.widget.photoview.CircleImg;
import com.ngc123.emoface.widget.photoview.SelectPicPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	private Context mContext;
    private CircleImg avatarImg;// 头像图片
    private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
    // 上传服务器的路径【一般不硬编码到程序中】
    private String imgUrl = "";
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private String urlpath;         // 图片本地路径
    private String resultStr = "";  // 服务端返回结果集
    private static ProgressDialog pd;// 等待进度圈
    private static final int REQUESTCODE_PICK = 0;      // 相册选图标记
    private static final int REQUESTCODE_TAKE = 1;      // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;   // 图片裁切标记

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mContext = RegisterActivity.this;
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
		
		avatarImg = (CircleImg) findViewById(R.id.avatarImg);
        avatarImg.setOnClickListener(this);
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		String st1 = getResources().getString(R.string.User_name_cannot_be_empty);
		String st2 = getResources().getString(R.string.Password_cannot_be_empty);
		String st3 = getResources().getString(R.string.Confirm_password_cannot_be_empty);
		String st4 = getResources().getString(R.string.Two_input_password);
		String st5 = getResources().getString(R.string.Is_the_registered);
		final String st6 = getResources().getString(R.string.Registered_successfully);
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, st1, Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, st2, Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, st3, Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, st4, Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage(st5);
			pd.show();
			final String st7 = getResources().getString(R.string.network_anomalies);
			final String st8 = getResources().getString(R.string.User_already_exists);
			final String st9 = getResources().getString(R.string.registration_failed_without_permission);
			final String st10 = getResources().getString(R.string.Registration_failed);
			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(username, pwd);
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								// 保存用户名
								DemoApplication.getInstance().setUserName(username);
								Toast.makeText(getApplicationContext(), st6, 0).show();
								finish();
							}
						});
					} catch (final EaseMobException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								int errorCode=e.getErrorCode();
								if(errorCode==EMError.NONETWORK_ERROR){
									Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
								}else if(errorCode==EMError.USER_ALREADY_EXISTS){
									Toast.makeText(getApplicationContext(), st8, Toast.LENGTH_SHORT).show();
								}else if(errorCode==EMError.UNAUTHORIZED){
									Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getApplicationContext(), st10 + e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				}
			}).start();

		}
	}

	public void back(View view) {
		finish();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.avatarImg:// 更换头像点击事件
            menuWindow = new SelectPicPopupWindow(mContext, itemsOnClick);  
            menuWindow.showAtLocation(findViewById(R.id.registerLayout), 
                    Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); 
            break;
        default:
            break;
        }
        
    }

    //为弹出窗口实现监听类  
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
            // 拍照
            case R.id.takePhotoBtn:
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                break;
            // 相册选择图片
            case R.id.pickPhotoBtn:
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                break;
            default:
                break;
            }
        }
    }; 
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        switch (requestCode) {
        case REQUESTCODE_PICK:// 直接从相册获取
            try {
                startPhotoZoom(data.getData());
            } catch (NullPointerException e) {
                e.printStackTrace();// 用户点击取消操作
            }
            break;
        case REQUESTCODE_TAKE:// 调用相机拍照
            File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
            startPhotoZoom(Uri.fromFile(temp));
            break;
        case REQUESTCODE_CUTTING:// 取得裁剪后的图片
            if (data != null) {
                setPicToView(data);
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
        
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }
        
    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            urlpath = FileUtil.saveFile(mContext, "temphead.jpg", photo);
            avatarImg.setImageDrawable(drawable);

        }
    }

    /**
     * 使用HttpUrlConnection模拟post表单进行文件
     * 上传平时很少使用，比较麻烦
     * 原理是： 分析文件上传的数据格式，然后根据格式构造相应的发送给服务器的字符串。
     */
    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {
            
            if(TextUtils.isEmpty(imgUrl)){
                Toast.makeText(mContext, "还没有设置上传服务器的路径！", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Map<String, String> textParams = new HashMap<String, String>();
            Map<String, File> fileparams = new HashMap<String, File>();
            
            try {
                // 创建一个URL对象
                URL url = new URL(imgUrl);
                textParams = new HashMap<String, String>();
                fileparams = new HashMap<String, File>();
                // 要上传的图片文件
                File file = new File(urlpath);
                fileparams.put("image", file);
                // 利用HttpURLConnection对象从网络中获取网页数据
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
                conn.setConnectTimeout(5000);
                // 设置允许输出（发送POST请求必须设置允许输出）
                conn.setDoOutput(true);
                // 设置使用POST的方式发送
                conn.setRequestMethod("POST");
                // 设置不使用缓存（容易出现问题）
                conn.setUseCaches(false);
                conn.setRequestProperty("Charset", "UTF-8");//设置编码   
                // 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
                conn.setRequestProperty("ser-Agent", "Fiddler");
                // 设置contentType
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + NetUtil.BOUNDARY);
                OutputStream os = conn.getOutputStream();
                DataOutputStream ds = new DataOutputStream(os);
                NetUtil.writeStringParams(textParams, ds);
                NetUtil.writeFileParams(fileparams, ds);
                NetUtil.paramsEnd(ds);
                // 对文件流操作完,要记得及时关闭
                os.close();
                // 服务器返回的响应吗
                int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
                // 对响应码进行判断
                if (code == 200) {// 返回的响应码200,是成功
                    // 得到网络返回的输入流
                    InputStream is = conn.getInputStream();
                    resultStr = NetUtil.readString(is);
                } else {
                    Toast.makeText(mContext, "请求URL失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);// 执行耗时的方法之后发送消给handler
        }
    };
    
    Handler handler = new Handler(new Handler.Callback() {
        
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                pd.dismiss();
                
                try {
                    // 返回数据示例，根据需求和后台数据灵活处理
                    // {"status":"1","statusMessage":"上传成功","imageUrl":"http://120.24.219.49/726287_temphead.jpg"}
                    JSONObject jsonObject = new JSONObject(resultStr);
                    
                    // 服务端以字符串“1”作为操作成功标记
                    if (jsonObject.optString("status").equals("1")) {
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
                        option.inSampleSize = 1;
                        
                        // 服务端返回的JsonObject对象中提取到图片的网络URL路径
                        String imageUrl = jsonObject.optString("imageUrl");
                        Toast.makeText(mContext, imageUrl, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(mContext, jsonObject.optString("statusMessage"), Toast.LENGTH_SHORT).show();
                    }
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                break;
                
            default:
                break;
            }
            return false;
        }
    });
}
