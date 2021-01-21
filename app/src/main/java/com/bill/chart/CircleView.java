package com.bill.chart;

import java.util.ArrayList;
import java.util.List;

import com.bill.R;
import com.bill.value.Constant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * canvas.drawText 的 xy 是從字的左下角開始
 */

public class CircleView extends View {
    private static final int flag_add = 0;
    private static final int flag_reduce = 1;

    private static final int flag_stop = -1;
    private static final int flag_run = 2;
    private static boolean flag_loading = false;

    public static final int flag_position_top = 1;    //上面的文字
    public static final int flag_position_buttom = 2;    //下面的文字

    static final int textArraySize = 3;        //文字組別數量

    private setValueAnimation_Thread mSetValueAnimation_Thread = null;    //增減動畫
    private showLoading_Thread mShowLoading_Thread = null;    //持續旋轉

    Rect bounds;
    RectF arcRF0;
    RectF smallArcRF0;
    Paint PaintArc;
    Paint PaintBackground;
    Paint PaintLabel;
    ChartCalc xcalc;
    int ScrWidth, ScrHeight;

    //演示用的百分比例,實際使用中，即為外部傳入的比例參數      
    List<Float> arrPer = new ArrayList<Float>();        //圓圈百分比，共兩圈
    List<Float> arrOrgPer = new ArrayList<Float>();
    List<Float> arrNewPer = new ArrayList<Float>();

    //RGB顏色數組  
    private final int arrColorRgb[][] = {{253, 180, 90},
            {52, 194, 188}};

    String[] topTextArray = new String[textArraySize];
    String topText = "";    //上面文字
    int topTextPx = 27;    //上面文向素
    int topTextSize;            //上面文字大小

    String[] bottomTextArray = new String[textArraySize];
    String bottomText = "";    //下面文字
    int bottomTextPx = 27;    //下面文字大小
    int bottomTextSize;            //下面文字大小

    public CircleView(Context context) {
        super(context);
        doAdditionalConstructorWork(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        doAdditionalConstructorWork(context);
    }

    private void doAdditionalConstructorWork(Context context) {
        bounds = new Rect();
        arcRF0 = new RectF();            //圓圈的矩形範圍
        smallArcRF0 = new RectF();
        xcalc = new ChartCalc();

        topTextSize = dip2px(getContext(), topTextPx);// 上面文字大小

        //畫筆初始化
        PaintBackground = new Paint();
        PaintBackground.setAntiAlias(true);
        PaintBackground.setARGB(255, 148, 159, 181);

        PaintArc = new Paint();
        PaintArc.setAntiAlias(true);

        PaintLabel = new Paint();
        PaintLabel.setAntiAlias(true);
        PaintLabel.setStyle(Style.FILL_AND_STROKE);
        PaintLabel.setTextAlign(Align.LEFT);
        PaintLabel.setTextSize(topTextSize);

        defaultValue();
    }

    public void onDraw(Canvas canvas) {
        // view長寬
        ScrHeight = getHeight();
        ScrWidth = getWidth();

        //畫布背景  
        //canvas.drawColor(Color.WHITE);                     

        float radius;
        //半徑長度
        if (ScrHeight < ScrWidth) {
            radius = ScrHeight / 2;
        } else {
            radius = ScrWidth / 2;
        }

        //float radius = ScrHeight / 2 ;
        //中心點
        float cirX = ScrWidth / 2;
        float cirY = ScrHeight / 2;

        //上下左右 的點
        float arcLeft = cirX - radius;
        float arcTop = cirY - radius;
        float arcRight = cirX + radius;
        float arcBottom = cirY + radius;
        arcRF0.set(arcLeft, arcTop, arcRight, arcBottom);

        //內圈大小
        float insideArcRadius = radius * (float) 0.9;

        float Percentage = 0.0f;
        float CurrPer = 0.0f + 270f;

        //底圈
        canvas.drawArc(arcRF0, CurrPer, 360, true, PaintBackground);

        //將百分比轉換為餅圖顯示角度     
        if (flag_loading) {
            Percentage = 360 * (arrNewPer.get(0) / 100);
            PaintArc.setARGB(255, arrColorRgb[0][0], arrColorRgb[0][1], arrColorRgb[0][2]);
            canvas.drawArc(arcRF0, Percentage, 108, true, PaintArc);
            showSmallArc(insideArcRadius, radius, cirX, cirY, Percentage, 108, canvas);
        } else {
            int index;
            if (arrNewPer.get(0) < arrNewPer.get(1)) {
                index = 1;
            } else {
                index = 0;
            }
            Percentage = 360 * (arrNewPer.get(index) / 100);
            PaintArc.setARGB(255, arrColorRgb[index][0], arrColorRgb[index][1], arrColorRgb[index][2]);
            canvas.drawArc(arcRF0, CurrPer, Percentage, true, PaintArc);
            showSmallArc(insideArcRadius, radius, cirX, cirY, CurrPer, Percentage, canvas);

            if (index == 0) {
                index = 1;
            } else {
                index = 0;
            }
            Percentage = 360 * (arrNewPer.get(index) / 100);
            PaintArc.setARGB(255, arrColorRgb[index][0], arrColorRgb[index][1], arrColorRgb[index][2]);
            canvas.drawArc(arcRF0, CurrPer, Percentage, true, PaintArc);
            showSmallArc(insideArcRadius, radius, cirX, cirY, CurrPer, Percentage, canvas);
        }
        //0xFFEDEDED
        //畫圓心  
        PaintArc.setColor(getResources().getColor(R.color.bg_default));
        canvas.drawCircle(cirX, cirY, insideArcRadius, PaintArc);

//      //計算百分比標籤  
//      xcalc.CalcArcEndPointXY(cirX, cirY, radius - radius/2/2, CurrPer + Percentage/2);     
//      //標識  
        //canvas.drawText(Float.toString(arrPer.ResourceUtils(0))+"%",xcalc.getPosX(), xcalc.getPosY() , PaintLabel);
        //標識  
        //canvas.drawText(Float.toString(arrPer.ResourceUtils(1))+"%",xcalc.getPosX(), xcalc.getPosY() , PaintLabel);

        //float topTextHeight	= bounds.height() / 2;						//文字一半的高度
        //canvas.drawText(topText, arcRF0.centerX() - topTextWidth , arcRF0.centerY() + topTextHeight , PaintLabel);	//正中間

        //圓圈中上面的文字
        PaintLabel.getTextBounds(topText, 0, topText.length(), bounds);            //取得文字的邊界長度至bounds
        float topTextWidth = PaintLabel.measureText(topText) / 2;                //文字一半的寬度
        float topTextX = arcRF0.centerX() - topTextWidth;                    //文字X軸
        float topTextY = (float) ((arcRF0.bottom - arcRF0.top) * 0.45);    //文字Y軸
        canvas.drawText(topText, topTextX, topTextY, PaintLabel);

        //圓圈中下面的文字
        PaintLabel.getTextBounds(bottomText, 0, bottomText.length(), bounds);        //取得文字的邊界長度至bounds
        float bottomTextWidth = PaintLabel.measureText(bottomText) / 2;            //文字一半的寬度
        float bottomTextX = arcRF0.centerX() - bottomTextWidth;                //文字X軸
        float bottomTextY = (float) ((arcRF0.bottom - arcRF0.top) * 0.75);    //文字Y軸
        canvas.drawText(bottomText, bottomTextX, bottomTextY, PaintLabel);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 改變文字
     *
     * @param value    預計改變的文字
     * @param position 文字的位置
     * @param index    文字放在第index組
     **/
    public void setText(String value, int position, int index) {
        if (index > textArraySize || index < 0) return;

        if (position == flag_position_top) {
            topText = value;
            topTextArray[index] = value;
        } else if (position == flag_position_buttom) {
            bottomText = value;
            bottomTextArray[index] = value;
        }

        //invalidate();
    }

//	/** 改變值 整段一起跑*/
//	public void setValue(float value, String textValue){
//		arrPer.set(0, value);
//		this.textValue = textValue;
//		invalidate();
//	}

    /**
     * 改變百分比值
     */
    public void setValue(List<Float> value) {
        arrPer.clear();
        arrPer.addAll(value);
        invalidate();
    }

    /**
     * 改變值，動畫方式
     */
    public synchronized void setValueAnimation(final List<Float> value, final String textValue) {
        stopAllThread();

        synchronized (CircleView.class) {
            mSetValueAnimation_Thread = new setValueAnimation_Thread(value, textValue);
            mSetValueAnimation_Thread.start();
        }
    }

    /**
     * 停止持續旋轉
     */
    public void stopLoading() {
        stopAllThread();
    }

    /**
     * 持續旋轉
     */
    public synchronized void showLoading() {
        if (flag_loading == true) {
            return;
        }

        stopAllThread();

        mShowLoading_Thread = new showLoading_Thread();
        mShowLoading_Thread.start();
    }

    /**
     * 停止所有動畫
     */
    private void stopAllThread() {
        if (mSetValueAnimation_Thread != null) {
            mSetValueAnimation_Thread.stopRun();
        }

        if (mShowLoading_Thread != null) {
            mShowLoading_Thread.stopRun();
        }
    }

    /**
     * 改變值，動畫方式 實作
     */
    private class setValueAnimation_Thread extends Thread {
        String textValue;
        int flag_state;

        setValueAnimation_Thread(final List<Float> value, final String textValue) {
            arrOrgPer.clear();
            arrOrgPer.addAll(arrPer);

            arrPer.clear();
            arrPer.addAll(value);

            this.textValue = textValue;
            flag_state = flag_run;
        }

        private void stopRun() {
            flag_state = flag_stop;
            mSetValueAnimation_Thread = null;
        }

        private void syncPer(){
            if(arrOrgPer.size() > arrPer.size()){

            }
        }

        public void run() {
            synchronized (CircleView.class) {
                List<Integer> arrOperationNum = new ArrayList<Integer>();



                //設定增加或減少
                for (int i = 0; i < arrPer.size(); i++) {
                    if (arrOrgPer.get(i) > arrPer.get(i)) {
                        arrOperationNum.add(flag_reduce);
                    } else {
                        arrOperationNum.add(flag_add);
                    }
                }

                boolean arr1 = false;
                boolean arr2 = false;

                do {
                    for (int i = 0; i < arrPer.size(); i++) {
                        if (arrOperationNum.get(i) == flag_add) {
                            if (arrNewPer.get(i) < arrPer.get(i) || arrNewPer.size() == 1) {
                                arrNewPer.set(i, arrNewPer.get(i) + 1);
                            } else {
                                arr1 = true;
                            }
                        } else {
                            if (arrNewPer.get(i) > arrPer.get(i) || arrNewPer.size() == 1) {
                                arrNewPer.set(i, arrNewPer.get(i) - 1);
                            } else {
                                arr2 = true;
                            }
                        }
                    }

                    if (flag_state == flag_stop) {
                        arrPer.clear();
                        arrPer.addAll(arrNewPer);
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
                } while (arr1 == false || arr2 == false);
            }//synchronized end
        }
    }

    /**
     * 持續旋轉 實作
     */
    private class showLoading_Thread extends Thread {
        int flag_state;

        showLoading_Thread() {
            flag_loading = true;
            flag_state = flag_run;
        }

        private void stopRun() {
            flag_loading = false;
            flag_state = flag_stop;
        }

        public void run() {
            arrNewPer.set(0, 0f);
            while (flag_state == flag_run) {
                float temp = arrNewPer.get(0);

                try {
                    Thread.sleep(Constant.AnimationInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (flag_state == flag_stop) {
                    return;
                }

                post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });

                if (temp >= 100) {
                    arrNewPer.set(0, 0f);
                } else {
                    arrNewPer.set(0, temp + 1);
                }
            }
        }
    }

    /**
     * 小圓球
     */
    private void showSmallArc(float insideArcRadius, float outsideArcRadius, float cirX, float cirY, float starPercentage, float endPercentage, Canvas canvas) {
        //起點不畫
        if (endPercentage == 0) {
            return;
        }

        float smallArcLeft;
        float smallArcTop;
        float smallArcRight;
        float smallArcBottom;

        float smallRadius = ((outsideArcRadius - insideArcRadius) / 2);
        float smallArc = outsideArcRadius - smallRadius;

        //取得小圓球中心點  
        xcalc.CalcArcEndPointXY(cirX, cirY, smallArc, starPercentage);
        smallArcLeft = xcalc.getPosX() - smallRadius;
        smallArcTop = xcalc.getPosY() - smallRadius;
        smallArcRight = xcalc.getPosX() + smallRadius;
        smallArcBottom = xcalc.getPosY() + smallRadius;
        smallArcRF0.set(smallArcLeft, smallArcTop, smallArcRight, smallArcBottom);
        canvas.drawArc(smallArcRF0, 0, 360, true, PaintArc);

        //取得小圓球中心點  
        xcalc.CalcArcEndPointXY(cirX, cirY, smallArc, starPercentage + endPercentage);
        smallArcLeft = xcalc.getPosX() - smallRadius;
        smallArcTop = xcalc.getPosY() - smallRadius;
        smallArcRight = xcalc.getPosX() + smallRadius;
        smallArcBottom = xcalc.getPosY() + smallRadius;
        smallArcRF0.set(smallArcLeft, smallArcTop, smallArcRight, smallArcBottom);
        canvas.drawArc(smallArcRF0, 0, 360, true, PaintArc);
    }

    /**
     * 初始化預設圖
     */
    private void defaultValue() {
        //arrPer.add(100f);
        arrPer.add(30f);
        arrPer.add(80f);

        arrNewPer.addAll(arrPer);

        //文字組別
        for (int i = 0; i < textArraySize; i++) {
            topTextArray[i] = "";
            bottomTextArray[i] = "";
        }
    }

    /**
     * 初始化值
     */
    public void initValue() {
        arrPer.clear();
        arrPer.add(0f);
        arrPer.add(0f);

        arrNewPer.clear();
        arrNewPer.addAll(arrPer);

        //文字組別
        for (int i = 0; i < textArraySize; i++) {
            topTextArray[i] = "";
            bottomTextArray[i] = "";
        }
    }
}  
