package com.edusoho.kuozhi.clean.module.main.mine.examine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidkun.xtablayout.XTabLayout;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.adapter.XTabAdapter;
import com.edusoho.kuozhi.clean.module.main.mine.examine.fra.InnerSpFragment;

import java.util.ArrayList;
import java.util.List;

import myutils.FastClickUtils;
import myutils.StatusBarUtil;

public class MySpActivity extends AppCompatActivity {

    private ImageView img_close;
    private XTabLayout tab_data;
    private ViewPager vp_my_pre;
//    private String titles[] = {"待审核", "已审核"};
    private String titles[] = {"待审核"};
    private List<Fragment> fragments = new ArrayList<>();
    private XTabAdapter adapter;
    private int index = 0;//1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_my_sp);
        tab_data = findViewById(R.id.tab);
        img_close = findViewById(R.id.img_close);
        vp_my_pre = findViewById(R.id.vp_my_pre);
        initTab();
        initClick();
    }

    //----------------------------------工具类----------------------------------
    private void initClick() {
        //返回
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FastClickUtils.isFastClick()) {
                    return;
                }
                finish();
            }
        });
    }

    //入口
    public static void launch(Context context) {
        Intent intent = new Intent(context, MySpActivity.class);
        context.startActivity(intent);
    }

    private void initTab() {
        //"待审核", "已审核", "被驳回"
        fragments.add(InnerSpFragment.newInstance(1));
//        fragments.add(InnerSpFragment.newInstance(2));
        adapter = new XTabAdapter(getSupportFragmentManager(), fragments);
        vp_my_pre.setAdapter(adapter);
        tab_data.setupWithViewPager(vp_my_pre);
        tab_data.setVisibility(View.GONE);
        vp_my_pre.setOffscreenPageLimit(fragments.size());
//        int dp15 = DensityUtil.dip2px(this, 15f);
        for (int i = 0; i < fragments.size(); i++) {
            XTabLayout.Tab tab = tab_data.getTabAt(i);
            tab.setCustomView(R.layout.layout_order_tab);
            if (i == 0) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_text)).setTextColor(this.getResources().getColor(R.color.color_161616));
//                ((TextView) tab.getCustomView().findViewById(R.id.tv_text)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            TextView tv_text = tab.getCustomView().findViewById(R.id.tv_text);
            tv_text.setText(titles[i]);
            /*TextView tv_tab_num = tab.getCustomView().findViewById(R.id.tv_tab_num);
            tv_tab_num.setVisibility(View.GONE);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(dp15, dp15);
            if (rlp instanceof RelativeLayout.MarginLayoutParams) {
                rlp.leftMargin = DensityUtil.dip2px(this, (i == 0) ? 20f : 36f);
                rlp.topMargin = DensityUtil.dip2px(this, 6f);
            }
            tv_tab_num.setLayoutParams(rlp);*/
        }
        tab_data.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_text)).setTextColor(MySpActivity.this.getResources().getColor(R.color.color_0292fd));
                ((TextView) tab.getCustomView().findViewById(R.id.tv_text)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                vp_my_pre.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tv_text)).setTextColor(MySpActivity.this.getResources().getColor(R.color.color_161616));
                ((TextView) tab.getCustomView().findViewById(R.id.tv_text)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            @Override
            public void onTabReselected(XTabLayout.Tab tab) {

            }
        });
        vp_my_pre.setCurrentItem(index);
    }
}
