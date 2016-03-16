package com.ngc123.emoface.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.ngc123.emoface.DemoHXSDKHelper;
import com.ngc123.emoface.R;
import com.ngc123.emoface.video.util.PrefUtils;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 3000;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		startAnim();
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//进入主页面
//					startActivity(new Intent(SplashActivity.this, MainActivity.class));
//					finish();
					  // 判断之前有没有显示过新手引导
			        boolean userGuide = PrefUtils.getBoolean(SplashActivity.this, "is_user_guide_showed",
			                false);

			        if (!userGuide) {
			            // 跳转到新手引导页
			            startActivity(new Intent(SplashActivity.this, MainActivity.class));     // GuideActivity.class
			        } else {
			            startActivity(new Intent(SplashActivity.this, MainActivity.class));
			        }

			        finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					jumpNextPage();
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}
	
	/**
     * 开启动画
     */
    private void startAnim() {

        // 动画集合
        AnimationSet set = new AnimationSet(false);

        // 旋转动画
        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(2000);// 动画时间
        rotate.setFillAfter(true);// 保持动画状态

        // 缩放动画
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(2000);// 动画时间
        scale.setFillAfter(true);// 保持动画状态

        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(3000);// 动画时间
        alpha.setFillAfter(true);// 保持动画状态

        set.addAnimation(rotate);
        set.addAnimation(scale);
        set.addAnimation(alpha);

        // 设置动画监听
        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            // 动画执行结束
            @Override
            public void onAnimationEnd(Animation animation) {
                jumpNextPage();
            }
        });

        rootLayout.startAnimation(set);
    }
    

    /**
     * 跳转下一个页面
     */
    private void jumpNextPage() {
//        // 判断之前有没有显示过新手引导
//        boolean userGuide = PrefUtils.getBoolean(this, "is_user_guide_showed",
//                false);
//
//        if (!userGuide) {
//            // 跳转到新手引导页
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));     // GuideActivity.class
//        } else {
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
//        }
//
//        finish();
    }

	
	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}
