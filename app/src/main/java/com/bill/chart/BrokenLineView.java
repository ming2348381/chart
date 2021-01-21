package com.bill.chart;

import java.util.ArrayList;
import java.util.List;

import com.bill.R;
import com.bill.value.Constant;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;


public class BrokenLineView extends View {
	private int ScrHeight;
	private int ScrWidth;

	//筆
	private Paint PaintCircle;
	private Paint PaintText = null;
	private Paint PaintLine;

	private boolean firstDraw = true;
	
	/** 動畫總耗時 毫秒 */
	private static final int AnimationTotalTime = 500;
	/** 最大顯示數值個數 */
	private static final int MaxValueCapacity = 12;	

	float topY;
	int initY;
	
	// 圖表值
	private List<Float> arrNum = new ArrayList<Float>();
	private List<String> arrText = new ArrayList<String>();	
	private List<Float> arrOrgNum = new ArrayList<Float>();
	private List<Float> arrNewTop = new ArrayList<Float>();
	
	/** 動畫執行緒 */
	private setValueAnimation_Thread setValueAnimation_Thread = null;
	
	public BrokenLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		doAdditionalConstructorWork(context);
	}

	public BrokenLineView(Context context) {
		super(context);
		doAdditionalConstructorWork(context);
	}

	private void doAdditionalConstructorWork(Context context) {
		// 解決4.1版本 以下canvas.drawTextOnPath()不顯示問題
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		//設定預設值
		setDefaultValue();
		
		PaintCircle = new Paint();
		PaintCircle.setColor(Color.parseColor("#FF484891"));	//深靛色
		PaintCircle.setStyle(Paint.Style.FILL);
		PaintCircle.setAntiAlias(true);

		PaintText = new Paint();
		PaintText.setColor(getResources().getColor(R.color.gray));
		PaintText.setStrokeWidth(3);
		PaintText.setAntiAlias(true);
		PaintText.setTextSize(40);
		PaintText.setTypeface(Typeface.DEFAULT_BOLD);
		
		PaintLine = new Paint();
		PaintLine.setColor(Color.parseColor("#FF9999CC"));		//淡靛色
		PaintLine.setStyle(Paint.Style.FILL);
		PaintLine.setAntiAlias(true);
	}

	public void onDraw(Canvas canvas) {
		// view長寬
		ScrHeight = getHeight();
		ScrWidth = getWidth();

		// 畫布背景
		//canvas.drawColor(Color.WHITE);
		
		float maxArrNumValue = getMaxValue(arrNum);	//最大值
		
		int lineCount = 5; // 標識線數量
		int lnSpace = (int)((ScrHeight * 0.85) / (lineCount)); // 標識間距

		int initX = 120;
		initY = (int)(ScrHeight * 0.9);

		int topX = initX;
		//topY;

		// Y 軸 標識線和值
		for (int i = 0; i <= lineCount; i++) {
			float y = initY - i * lnSpace;
			canvas.drawLine(topX, y, ScrWidth, y, PaintText);
			int text = (int)(maxArrNumValue * i / lineCount);
			canvas.drawText(Integer.toString(text), (float)(ScrWidth * 0.02), y, PaintText);
		}
	
		// 折線
		//float circleRadius = 20;	//小圓點半徑
		float circleRadius = (int)((ScrHeight * ScrWidth) * 0.000012);	//小圓點半徑	1640 * 1080 = 約20		
		PaintLine.setStrokeWidth(circleRadius * 0.8f);
		topY = initY - (lineCount) * lnSpace; // 標識線頂點
		float top;
		int xSpace = ScrWidth / 15;
		float previousTopX = 0;
		float previousTopY = 0;
		for (int i = 0; i < arrNum.size(); i++) {
			topX = initX + (i + 1) * xSpace;			
			
			if(firstDraw){				
				top = (float) (topY + (1 - (arrNum.get(i) / maxArrNumValue)) * (initY - topY));
			}else{
				top = arrNewTop.get(i);
			}
			
			if(i > 0){			
				canvas.drawLine(previousTopX, previousTopY, topX, top, PaintLine);
				canvas.drawCircle(previousTopX, previousTopY, circleRadius, PaintCircle);
			} 
			
			if(i == arrNum.size() - 1){
		        canvas.drawCircle(topX, top, circleRadius, PaintCircle);
			}
			
			previousTopX = topX;
			previousTopY = top;
			
			// 標識文字
			canvas.drawText(arrText.get(i), topX, (float)(ScrHeight * 0.95), PaintText);
		}
	}
	
//	/** 改變值 */
//	public void setValue(List<Integer> arrValue, List<String> arrText){
//		arrNum = arrValue;
//		if(arrText != null){
//			this.arrText = arrText;
//		}		
//		invalidate();
//	}
	
	
	/** 改變值 */
	public void setValue(List<Float> arrValue, List<String> arrText){
		if(setValueAnimation_Thread != null){
			setValueAnimation_Thread.stopRun();
		}
		
		synchronized(PillarView.class){
		firstDraw = false;
		
		arrOrgNum.clear();
		arrOrgNum.addAll(arrNum);
	
		arrNum.clear();
		arrNum.addAll(arrValue);
		
		if(arrText != null){
			//arrOrgText = arrText;
		}
		
		setValueAnimation_Thread = new setValueAnimation_Thread();
		setValueAnimation_Thread.start();
		}
	}
	
	/** 改變值，動畫方式 實作*/
	private class setValueAnimation_Thread extends Thread{
		private static final int flag_stop = -1;
		private static final int flag_run = 2;
		private static final int flag_add = 0;
		private static final int flag_reduce = 1;
		
		/** 畫面改變總次數 */
		private int AnimationInterval = (int)(AnimationTotalTime / Constant.AnimationInterval);
		private List<Integer> arrOperationNum = new ArrayList<Integer>();
		private List<Float> arrDistanceNum = new ArrayList<Float>();

		int flag_state = flag_run;
		
		public void stopRun(){
			flag_state = flag_stop;
			setValueAnimation_Thread = null;
		}
		
		public void run() {
			synchronized(PillarView.class){
			complementArr();
			
			for(int i = 0; i < arrNum.size(); i++){	
				//相對原始位置 增加或減少
				float orgTop = topY + (1 - (arrOrgNum.get(i) / getMaxValue(arrOrgNum))) * (initY - topY);
				float top = topY + (1 - (arrNum.get(i) / getMaxValue(arrNum))) * (initY - topY);
				
				if(orgTop > top){
					arrOperationNum.add(flag_reduce);
				}else{
					arrOperationNum.add(flag_add);
				}
				
				//與原始位置相差距離
				float distanceNum = Math.abs(orgTop - top);
				distanceNum = distanceNum / AnimationInterval;				
				arrDistanceNum.add(distanceNum);
			}		
			
			for(int i = 0; i < AnimationInterval; i++){
				for(int arrNumIndex = 0; arrNumIndex < arrNum.size(); arrNumIndex++){
					float distanceNum = arrDistanceNum.get(arrNumIndex) * (i + 1);					
					float orgTop = topY + (1 - (arrOrgNum.get(arrNumIndex) / getMaxValue(arrOrgNum))) * (initY - topY);
					
					if(arrOperationNum.get(arrNumIndex) == flag_add){						
						arrNewTop.set(arrNumIndex, orgTop + distanceNum);
					}else{
						arrNewTop.set(arrNumIndex, orgTop - distanceNum);
					}
				}
	
				if(flag_state == flag_stop){
					return;
				}
				
				post(new Runnable() {
					@Override
					public void run() {		
						invalidate();
					}
				});
				
				try {
					Thread.sleep(Constant.AnimationInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
			}
		}
		
		/** 數值補位至圖表上限 */
		private void complementArr(){
			for(int i = 1; i <= MaxValueCapacity; i++){
				if(arrNum.size() < i){					
					arrNum.add(0f);
				}
				
				if(arrOrgNum.size() < i){
					arrOrgNum.add(0f);
				}
			}
		}
	}
	
	/** 取得最大值 */
	private Float getMaxValue(List<Float> list){
		if(list.size() == 0){
			return 0f;
		}
		
		Float max = list.get(0);
		for(int i = 1; i < list.size(); i++){
			if(list.get(i) > max){
				max = list.get(i);
			}
		}
		
		if(max == 0){
			max = 100f;
		}
		
		return max;
	}
	
	private void setDefaultValue(){
		// 預設值
		arrNum.add(1f);
		arrNum.add(34f);
		arrNum.add(3f);
		arrNum.add(22f);
		arrNum.add(82f);
		arrNum.add(12f);
		arrNum.add(22f);
		arrNum.add(100f);
		arrNum.add(2f);
		arrNum.add(72f);
		arrNum.add(12f);
		arrNum.add(45f);
		
		//預設值
		arrText.add("1");
		arrText.add("2");
		arrText.add("3");
		arrText.add("4");
		arrText.add("5");
		arrText.add("6");
		arrText.add("7");
		arrText.add("8");
		arrText.add("9");
		arrText.add("10");
		arrText.add("11");
		arrText.add("12");
		
		arrNewTop.clear();
		arrNewTop.addAll(arrNum);
	}

}