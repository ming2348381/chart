package com.bill.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PieView extends View {
    private int ScrHeight;
    private int ScrWidth;

    private Paint[] arrPaintArc;
    private Paint PaintText = null;

    private Path pathArc;
    RectF arcRF0;
    /*
     * final int[] colors = new int[]{ R.color.red, R.color.white,
     * R.color.green, R.color.yellow, R.color.blue, };
     */

    // RGB顏色數組
    private final int arrColorRgb[][] = {{77, 83, 97},
            {148, 159, 181},
            {253, 180, 90},
            {52, 194, 188},
            {39, 51, 72},
            {255, 135, 195},
            {215, 124, 124},
            {180, 205, 230}};

    // 演示用的比例,實際使用中，即為外部傳入的比例參數
    List<Float> arrPer; //= new ArrayList<Float>();
    //float arrPer[] = new float[] { 10f, 10f, 10f, 25f, 5f, 5f, 15, 20 };
    List<String> textValue;// = new ArrayList<String>();

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this constructor used when creating view through XML
        doAdditionalConstructorWork(context);
    }

    public PieView(Context context) {
        super(context);
        doAdditionalConstructorWork(context);
    }

    private void doAdditionalConstructorWork(Context context) {
        pathArc = new Path();
        arcRF0 = new RectF();

        // 解決4.1版本 以下canvas.drawTextOnPath()不顯示問題
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        initTextValue();
        setArrPaintArc();

        PaintText = new Paint();
        PaintText.setColor(Color.BLUE);
        PaintText.setTextSize(60);
    }

    public void onDraw(Canvas canvas) {
        // view長寬
        ScrHeight = getHeight();
        ScrWidth = getWidth();

        // 畫布背景
        //canvas.drawColor(Color.WHITE);

        //半徑長度
        float radius = ScrHeight / 4;
        //中心點
        float cirX = ScrWidth / 2;
        float cirY = radius + 50;
        // 先畫個圓確定下顯示位置
        //canvas.drawCircle(cirX,cirY,radius,PaintArcRed);

        //上下左右 的點
        float arcLeft = cirX - radius;
        float arcTop = cirY - radius;
        float arcRight = cirX + radius;
        float arcBottom = cirY + radius;
        //arcRF0 = new RectF(arcLeft, arcTop, arcRight, arcBottom);
        arcRF0.set(arcLeft, arcTop, arcRight, arcBottom);

        // x,y,半徑 ,CW
        pathArc.addCircle(cirX, cirY, radius, Direction.CW);

        // 繪出餅圖大輪廓
        //canvas.drawPath(pathArc, arrPaintArc[0]);

        //長寬各幾格說明
//		int gridHeight;
//		int geidWidth;		
//		double gridSqrt = Math.sqrt(arrPer.length);
//		if(ResourceUtils.isInteger(gridSqrt)){
//			gridHeight = (int)gridSqrt;
//		}else{
//			gridHeight = (int)gridSqrt + 1;
//		}
//		geidWidth = (int)gridSqrt;

        float CurrPer = 0f; // 偏移角度
        float Percentage = 0f; // 當前所佔比例

        //間格
        float space = ScrWidth * (float) 0.05;

        //比例說明_色塊
        float scrOffsetW = space;
        float scrOffsetH = arcBottom + space;
        float scrOffsetT = (ScrHeight - scrOffsetH - space) / 4;
        float scrOffsetR = (ScrWidth / 4) - space;

        // Resources res = this.getResources();

        int i = 0;
        int eof = arrPer.size();
        for (i = 0; i < eof; i++) {
            // 將百分比轉換為餅圖顯示角度
            Percentage = 360 * (arrPer.get(i) / getArrPerTotal());
            //Percentage = (float) (Math.round(Percentage * 100)) / 100;

            // 在餅圖中顯示所佔比例
            canvas.drawArc(arcRF0, CurrPer, Percentage, true, arrPaintArc[i]);

            // 比例說明_色塊
            float noteLeft = scrOffsetW;
            float noteRight = scrOffsetR;
            float noteTop = scrOffsetH + (i % 4) * scrOffsetT;
            float noteBottom = scrOffsetH + (i % 4 + 1) * scrOffsetT - 20;

            if (i >= 4) {
                noteLeft = (ScrWidth / 2) + space;
                noteRight = (ScrWidth / 4 * 3) - space;
            }

            canvas.drawRect(noteLeft, noteTop, noteRight, noteBottom, arrPaintArc[i]);
            // 比例說明_文字
            //canvas.drawText(String.valueOf(arrPer[i]) + "%",scrOffsetW + 70, scrOffsetH + i * scrOffsetT, PaintText);
            float noteTextTop = noteTop + (noteBottom - noteTop) / 2;
            float noteTextLeft = noteRight + 5;
            String text = textValue.get(i) + getArrPerPercent(i) + "%";
            canvas.drawText(text, noteTextLeft, noteTextTop, PaintText);
            // 下次的起始角度
            CurrPer += Percentage;
        }
    }

    /**
     * 改變值
     */
    public void setValue(List<Float> value, List<String> textValue) {
        arrPer = value;
        this.textValue = textValue;
        setArrPaintArc();
        invalidate();
    }

    /**
     * 設定線條
     */
    private void setArrPaintArc() {
        int arrPaintArcLength = arrPer.size();

        arrPaintArc = new Paint[arrPaintArcLength];

        for (int i = 0; i < arrPaintArcLength; i++) {
            arrPaintArc[i] = new Paint();
            // arrPaintArc[i].setColor(res.getColor(colors[i] ));
            arrPaintArc[i].setARGB(255, arrColorRgb[i][0], arrColorRgb[i][1], arrColorRgb[i][2]);
            arrPaintArc[i].setStyle(Paint.Style.FILL);
            arrPaintArc[i].setStrokeWidth(2);
            arrPaintArc[i].setAntiAlias(true);
        }
    }

    /**
     * 初始化預設圖
     */
    private void initTextValue() {
        arrPer = new ArrayList<Float>();
        //float arrPer[] = new float[] { 10f, 10f, 10f, 25f, 5f, 5f, 15, 20 };
        textValue = new ArrayList<String>();

        arrPer.add(10f);
        arrPer.add(40f);
        arrPer.add(30f);
        arrPer.add(20f);
        arrPer.add(10f);
        arrPer.add(40f);
        arrPer.add(30f);
        arrPer.add(20f);

        textValue.clear();
        for (int i = 0; i < arrPer.size(); i++) {
            textValue.add("");
        }
    }

    private Float getArrPerTotal() {
        Float result = 0f;
        for (Float value : arrPer) {
            result += value;
        }
        return result;
    }

    private int getArrPerPercent(int index) {
        return (int) ((arrPer.get(index) / getArrPerTotal()) * 100);
    }
}
