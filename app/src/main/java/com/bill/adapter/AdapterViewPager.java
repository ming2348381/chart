package com.bill.adapter;

import java.util.List;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class AdapterViewPager extends PagerAdapter {
	public List<View> mListView;
	
	public AdapterViewPager(List<View> listView) {
		mListView = listView;
	}
	
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mListView.get(arg1));
	}
	
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(mListView.get(arg1), 0);
		return mListView.get(arg1);
	}
	
	@Override
	public int getCount() {
		return mListView.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}
	
	@Override
	public Parcelable saveState() {
		return null;
	}
	
}
