package com.bill.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import androidx.viewpager.widget.ViewPager;

import com.bill.R;
import com.bill.adapter.AdapterViewPager;
import com.bill.chart.BrokenLineView;
import com.bill.chart.CircleView;
import com.bill.chart.PieView;
import com.bill.chart.PillarView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    ViewPager mInfoViewPager;
    CircleView mCircleView;
    BrokenLineView mBrokenLineView;
    PieView mPieView;
    PillarView mPillarView;

    View mRunButton;
    View mDemoButtonView;
    View mDemo2ButtonView;
    View mDataFloatingActionButton;
    EditText mInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.man);
        findView();
        initView();
    }

    private void findView() {
        mInfoViewPager = (ViewPager) findViewById(R.id.info_ViewPager);
        mInputEditText = (EditText) findViewById(R.id.input_edit_text);
        mDemoButtonView = findViewById(R.id.demo_button);
        mDemo2ButtonView = findViewById(R.id.demo2_button);
        mDataFloatingActionButton = findViewById(R.id.data_floating_action_button);
    }

    private void initView() {
        mDataFloatingActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < 12; i++) {
                    result.append((int) (Math.random() * 100 + 1)).append(",");
                }
                mInputEditText.setText(result.substring(0, result.length() - 1));
                runAnimation();
            }
        });

        mDemoButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputEditText.setText("0,52,45,22,93,61,34,77,8,28,72,44");
                runAnimation();
            }
        });
        mDemo2ButtonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputEditText.setText("31,23,54,11,36,14,43,39,2,11,3,89");
                runAnimation();
            }
        });

        List<View> chartViews = new ArrayList<>();
        chartViews.add(mCircleView = new CircleView(this));
        chartViews.add(mBrokenLineView = new BrokenLineView(this));
        chartViews.add(mPieView = new PieView(this));
        chartViews.add(mPillarView = new PillarView(this));
        mInfoViewPager.setAdapter(new AdapterViewPager(chartViews));

        mRunButton = findViewById(R.id.test_button);
        mRunButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                runAnimation();
            }
        });
    }

    private void runAnimation() {
        switch (mInfoViewPager.getCurrentItem()) {
            case 0:
                mCircleView.setValueAnimation(getValue(2), null);
                break;
            case 1:
                mBrokenLineView.setValue(getValue(12));
                break;
            case 2:
                mPieView.setValue(getValue(8), getDemoText(8));
                break;
            case 3:
                mPillarView.setValue(getValue(12), getDemoText(12));
                break;
        }
    }

    private List<Float> getValue(int max) {
        List<String> values = Arrays.asList(mInputEditText.getText().toString().split(","));
        List<Float> intValue = new ArrayList<>();
        for (String value : values) {
            if (intValue.size() >= max) {
                break;
            }
            Float result = Float.valueOf(value);
            result = result > 100 ? 100 : result;
            result = result < 0 ? 0 : result;
            intValue.add(result);
        }
        return intValue;
    }

    private List<String> getDemoText(int max) {
        List<String> values = Arrays.asList("花", "魚", "狗", "貓", "蛇", "虎", "兔", "龍", "鼠", "豬");
        List<String> intValue = new ArrayList<>();
        for (String value : values) {
            if (intValue.size() >= max) {
                break;
            }

            intValue.add(value);
        }
        return intValue;
    }
}