package com.ngc123.emoface.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.ngc123.emoface.R;
import com.ngc123.emoface.tools.ColorAnimationView;
import com.ngc123.emoface.video.util.PrefUtils;

/**
 * 新手引导
 * 
 * @author yuan
 */
public class GuideActivity extends FragmentActivity {
    private static final int[] resource = new int[]{R.drawable.welcome1, R.drawable.welcome2,
        R.drawable.welcome3, R.drawable.welcome4};
    private static final String TAG = GuideActivity.class.getSimpleName();
    private Button btnStart;// 开始体验

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ColorAnimationView colorAnimationView = (ColorAnimationView) findViewById(R.id.ColorAnimationView);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        MyFragmentStatePager adpter = new MyFragmentStatePager(getSupportFragmentManager());
        viewPager.setAdapter(adpter);
        /**
         *  首先，你必须在 设置 Viewpager的 adapter 之后在调用这个方法
         *  第二点，setmViewPager(ViewPager mViewPager,Object obj, int count, int... colors)
         *         第一个参数 是 你需要传人的 viewpager
         *         第二个参数 是 一个实现了ColorAnimationView.OnPageChangeListener接口的Object,用来实现回调
         *         第三个参数 是 viewpager 的 孩子数量
         *         第四个参数 int... colors ，你需要设置的颜色变化值~~ 如何你传人 空，那么触发默认设置的颜色动画
         * */
        /**
         *  Frist: You need call this method after you set the Viewpager adpter;
         * Second: setmViewPager(ViewPager mViewPager,Object obj， int count, int... colors)
         *          so,you can set any length colors to make the animation more cool!
         * Third: If you call this method like below, make the colors no data, it will create
         *          a change color by default.
         * */
        colorAnimationView.setmViewPager(viewPager, resource.length);
        colorAnimationView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("TAG","onPageScrolled");
            }
    
            @Override
            public void onPageSelected(int position) {
                Log.e("TAG","onPageSelected");
                if (position == resource.length-1) {// 最后一个页面
                    btnStart.setVisibility(View.VISIBLE);// 显示开始体验的按钮
                } else {
                    btnStart.setVisibility(View.INVISIBLE);
                }
            }
    
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("TAG","onPageScrollStateChanged");
            }
        });
        // Four : Also ,you can call this method like this:
        // colorAnimationView.setmViewPager(viewPager,this,resource.length,0xffFF8080,0xff8080FF,0xffffffff,0xff80ff80);
        btnStart = (Button) findViewById(R.id.start_app);
        btnStart.setVisibility(View.INVISIBLE);
        btnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 更新sp, 表示已经展示了新手引导
                PrefUtils.setBoolean(GuideActivity.this,
                        "is_user_guide_showed", true);

                // 跳转主页面
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    
    
    
    public class MyFragmentStatePager
            extends FragmentStatePagerAdapter {
    
        public MyFragmentStatePager(FragmentManager fm) {
            super(fm);
        }
    
        @Override public Fragment getItem(int position) {
            return new MyFragment(position);
        }
    
        @Override public int getCount() {
            return resource.length;
        }
    }
    
    @SuppressLint("ValidFragment") public class MyFragment
            extends Fragment {
        private int position;
    
        public MyFragment(int position) {
            this.position = position;
        }
    
        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(resource[position]);
            return imageView;
        }
    }
}
