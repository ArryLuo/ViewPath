package com.example.viewpath;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.provider.CalendarContract.CalendarAlerts;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class Myview extends View{
	   private float progressWidth;//  <!--圆弧宽度  -->
	    private String tempText;// <!-- 当前温度的度数值 -->
	    private float tempTextSize;//<!-- 当前温度数值大小 -->
	    private static final int PADDING = 15; // 进度的宽度
	    private int mSize; // 最终的大小
	    private Paint outCirclePaint;//外圆画笔
	    private Paint progressPaint; // 进度
	    private Paint scaleArcPaint; // 刻度弧
	    private Paint scalePaint; // 刻度
	    private Paint panelTextPaint; // 表盘文字
	    private Paint progressTextPaint; // 进度条上的文字
	    private Paint pointPaint; // 中心圆
	    private Paint leftPointerPaint; // 表针左半部分
	    private Paint rightPointerPaint; // 表针右半部分
	    private Paint pointerCirclePaint; // 表针的圆轴
	    private int progressRadius; // 进度弧的半径
	    private int scaleArcRadius; // 刻度弧的半径
	    private int mTikeCount = 40; // 40条刻度(包括长短)
	    private String scale; // 刻度数值
	    private int mLongTikeHeight = dp2px(10); // 长刻度
	    private int mShortTikeHeight = dp2px(5); // 短刻度
	    private int pointRadius = dp2px(17); // 中心圆半径
	    private float currentTemp;
	    private static final int OFFSET = 5;
	public Myview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//获取自定义属性
		TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.temp);
		progressWidth=ta.getDimension(R.styleable.temp_progressWidth, PADDING);
		tempText=ta.getString(R.styleable.temp_tempText);
		tempTextSize=ta.getDimension(R.styleable.temp_tempTextSize, sp2px(15));
		//释放资源
		ta.recycle();
		//初始化画笔，
		initPaint();
	}
	private void initPaint() {
		// TODO Auto-generated method stub
		//外圆的画笔
		outCirclePaint=new Paint();
		//抗锯齿
		outCirclePaint.setAntiAlias(true);
		//设置画笔的样式
		outCirclePaint.setStyle(Style.STROKE);
		//设置空心边框的宽度
		outCirclePaint.setStrokeWidth(5);
		//设置画笔的颜色
		outCirclePaint.setColor(getResources().getColor(R.color.temperatureBackground));
		//进度
		progressPaint=new Paint();
		progressPaint.setAntiAlias(true);
		progressPaint.setStrokeWidth(dp2px(PADDING));
		progressPaint.setStyle(Style.STROKE);
		progressPaint.setStrokeCap(Paint.Cap.ROUND);
		progressPaint.setStrokeJoin(Paint.Join.ROUND);
		//刻度上的文字
		progressTextPaint=new Paint();
		progressTextPaint.setAntiAlias(true);
		progressTextPaint.setStyle(Style.FILL);
		progressTextPaint.setColor(Color.BLACK);
		//Typeface字体黑体子
		progressPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		//刻度弧
		scaleArcPaint=new Paint();
		scaleArcPaint.setAntiAlias(true);
		scaleArcPaint.setStrokeWidth(dp2px(2));
		scaleArcPaint.setStyle(Style.STROKE);
		//刻度表盘上的字
		panelTextPaint=new Paint();
		panelTextPaint.setAntiAlias(true);
		panelTextPaint.setStyle(Style.FILL);
		panelTextPaint.setColor(Color.BLACK);
		//中心圆
		pointPaint=new Paint();
		pointPaint.setAntiAlias(true);
		pointPaint.setStyle(Style.FILL);
		pointPaint.setColor(Color.GRAY);
		//指针左边部分
		leftPointerPaint=new Paint();
		leftPointerPaint.setAntiAlias(true);
        leftPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        leftPointerPaint.setColor(getResources().getColor(R.color.leftPointer));
        //指针右边的部分
        rightPointerPaint=new Paint();
        rightPointerPaint.setAntiAlias(true);
        rightPointerPaint.setColor(getResources().getColor(R.color.rightPointer));
        rightPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //表针的圆；
        pointerCirclePaint=new Paint();
        pointerCirclePaint.setAntiAlias(true);
        pointerCirclePaint.setColor(Color.GRAY);
        pointerCirclePaint.setStyle(Paint.Style.FILL);
        pointerCirclePaint.setDither(true);
	}
	//测量大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int realWidth=startMeasure(widthMeasureSpec);
		int realHeight=startMeasure(heightMeasureSpec);
		/*
		 * 以正方形为基础
		 */
		//以最小的那个长度作为边
		mSize=Math.min(realWidth, realHeight);
		//测量
		setMeasuredDimension(mSize, mSize);
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//将画布移到中央位置
		canvas.translate(mSize/2, mSize/2);
		//画最外圆
		drawOutCircle(canvas);
		 // 画进度
        drawProgress(canvas);
        // 画进度上的文字
        drawProgressText(canvas);
        // 画表盘
        drawPanel(canvas);
	}
	private void drawPanel(Canvas canvas) {
		// TODO Auto-generated method stub
		  // 画刻度弧
        drawScaleArc(canvas);
        // 画中间圆
        drawInPoint(canvas);
        // 画指针
        drawPointer(canvas);
        // 绘制文字
        drawPanelText(canvas);
	}
	private void drawPanelText(Canvas canvas) {
		// TODO Auto-generated method stub
		 canvas.save();
	        String text = "当前温度";
	        float length = panelTextPaint.measureText(text);
	        panelTextPaint.setTextSize(sp2px(15));
	        canvas.drawText(text, -length/2, scaleArcRadius/2 + dp2px(20), panelTextPaint);
	        String temp = currentTemp + " ℃";
	        panelTextPaint.setTextSize(sp2px(15));
//	        panelTextPaint.setColor(tempTextColor);
	        float tempTextLength = panelTextPaint.measureText(temp);
	        canvas.drawText(temp, -tempTextLength/2, scaleArcRadius, panelTextPaint);
	        canvas.restore();

	}
	 /**
     * 指针（这里分为左右部分是为了画出来的指针有立体感）
     * @param canvas
     */
	private void drawPointer(Canvas canvas) {
		// TODO Auto-generated method stub
		RectF rectF=new RectF(-pointRadius/2, -pointRadius/2, pointRadius/2, pointRadius/2);
		canvas.save();
		 // 先将指针与刻度0位置对齐
		canvas.rotate(60, 0, 0);
		float angle=currentTemp*6.0f;
		canvas.rotate(angle, 0, 0);
		  // 表针左半部分
		Path leftPointerPath=new Path();
		leftPointerPath.moveTo(pointRadius/2, 0);
		leftPointerPath.addArc(rectF, 0, 360);
		leftPointerPath.lineTo(0, scaleArcRadius - mLongTikeHeight - dp2px(OFFSET) - dp2px(15));
	    leftPointerPath.lineTo(-pointRadius/2, 0);
	    leftPointerPath.close();
	    // 表针右半部分
        Path rightPointerPath = new Path();
        rightPointerPath.moveTo(-pointRadius/2, 0);
        rightPointerPath.addArc(rectF, 0, -180);
        rightPointerPath.lineTo(0, scaleArcRadius - mLongTikeHeight - dp2px(OFFSET) - dp2px(15));
        rightPointerPath.lineTo(0, pointRadius/2);
        rightPointerPath.close();
        // 表针的圆
        Path circlePath = new Path();
        circlePath.addCircle(0, 0, pointRadius/4, Path.Direction.CW);
        canvas.drawPath(leftPointerPath, leftPointerPaint);
        canvas.drawPath(rightPointerPath, rightPointerPaint);
        canvas.drawPath(circlePath, pointerCirclePaint);
        canvas.restore();
       
	}
	/**
     * 设置当前温度
     * @param currentTemp
     */
    public void setCurrentTemp(float currentTemp) {
        if (currentTemp < 0) {
            currentTemp = 0;
        } else if (currentTemp > 40) {
            currentTemp = 40;
        } else {
            this.currentTemp = currentTemp;
            postInvalidate();
        }
    }

    public float getCurrentTemp() {
        return currentTemp;
    }
	/**
	 * 中心圆
	 * @param canvas
	 */
	private void drawInPoint(Canvas canvas) {
		// TODO Auto-generated method stub
		 canvas.save();
	        canvas.drawCircle(0, 0, pointRadius, pointPaint);
	        canvas.restore();
	}
	private void drawScaleArc(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.save();
		//画弧
		RectF rectF=new RectF(-scaleArcRadius, -scaleArcRadius, scaleArcRadius, scaleArcRadius);
		canvas.drawArc(rectF, 150, 240, false, scaleArcPaint);
		//旋转角度
		float mAngle=240f/mTikeCount;
		for (int i = 0; i <=mTikeCount/2; i++) {
			if(i%5==0){
				scale=20+i+"";
				panelTextPaint.setTextSize(sp2px(15));
				float scaleWidth=panelTextPaint.measureText(scale);
				canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius+mLongTikeHeight, scaleArcPaint);
				canvas.drawText(scale, -scaleWidth/2, -scaleArcRadius+mLongTikeHeight+dp2px(15), panelTextPaint);
			}else{
				canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius+mShortTikeHeight, scaleArcPaint);
			}
			canvas.rotate(mAngle, 0, 0);
		}
		//画布回正;
		  // 画布回正
        canvas.rotate(-mAngle * mTikeCount/2-6, 0, 0);
		//画布左边的刻度
        for (int i = 0; i <=mTikeCount/2; i++) {
			if(i%5==0){
				scale=20-i+"";
				panelTextPaint.setTextSize(sp2px(15));
				float scaleWidth=panelTextPaint.measureText(scale);
				canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius+mLongTikeHeight, scaleArcPaint);
				canvas.drawText(scale, -scaleWidth/2, -scaleArcRadius+mLongTikeHeight+dp2px(15), panelTextPaint);
			}else{
				canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius+mShortTikeHeight, scaleArcPaint);
			}
			//
			canvas.rotate(-mAngle, 0, 0);
		}
      canvas.rotate(-mAngle * mTikeCount/2 + 6, 0, 0);
        canvas.restore();
	}
	/*
	 * 画刻度上的文字
	 */
	 private void drawProgressText(Canvas canvas) {
		// TODO Auto-generated method stub
		   // 刻度弧紧靠进度弧
	     scaleArcRadius = mSize/2 - (dp2px(15)+dp2px(PADDING)/4);
		canvas.save();
		String normal="正常";
		String warn="预警";
		String danger="警告";
		 // 因为文字在进度弧上，所以要旋转一定的角度
		canvas.rotate(-60, 0, 0);
		progressTextPaint.setTextSize(sp2px(12));
		canvas.drawText(normal, -dp2px(12), -scaleArcRadius-dp2px(4), progressTextPaint);
		canvas.rotate(90, 0, 0);
		canvas.drawText(warn, -dp2px(12), -scaleArcRadius-dp2px(4), progressTextPaint);
		canvas.rotate(60, 0, 0);
		canvas.drawText(danger, -dp2px(12), -scaleArcRadius-dp2px(4), progressTextPaint);
		//canvas.rotate(-60, 0, 0);
		canvas.restore();
	}
	/**
     * 进度弧
     * @param canvas
     */
	private void drawProgress(Canvas canvas) {
		// TODO Auto-generated method stub
		// dp2px(10):留一点位置（可有可无）
		progressRadius=mSize/2-dp2px(10);
		//将其保存起来
		canvas.save();
		RectF rectF=new RectF(-progressRadius, -progressRadius,progressRadius, progressRadius);
		//设置为圆角
		progressPaint.setStrokeCap(Paint.Cap.ROUND);
		progressPaint.setColor(Color.GREEN);
	   // 从150度位置开始，经过120度(顺时针的方向),
		canvas.drawArc(rectF, 150, 120, false, progressPaint);
		progressPaint.setColor(Color.RED);
		progressPaint.setStrokeCap(Cap.ROUND);
		//330-60;
		canvas.drawArc(rectF, 330, 60, false, progressPaint);
		progressPaint.setColor(Color.YELLOW);
		//接在上一头的下面以及下一头的上面.
		progressPaint.setStrokeCap(Cap.BUTT);
		canvas.drawArc(rectF, 270, 60, false, progressPaint);
		//归位
		canvas.restore();
		
	}
	//
	//背景圆
	private void drawOutCircle(Canvas canvas){
		// 已经将画布移到中心，所以圆心为（0,0）
		canvas.drawCircle(0, 0, mSize/2, outCirclePaint);
	}
	//测量大小
	private int startMeasure(int whSpec){
		int result=0;
		int size=MeasureSpec.getSize(whSpec);
		//全部填充
		int mode=MeasureSpec.getMode(whSpec);
		if(mode==MeasureSpec.EXACTLY){
			result=size;
		}else{
			result=dp2px(200);
		}
		return result;
				
	}
	/**
     * 将 dp 转换为 px
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
	//转化为sp单位
	 private int sp2px(int sp){
	        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
	    }
	 public float getProgressWidth() {
	        return progressWidth;
	    }

	    public void setProgressWidth(float progressWidth) {
	        this.progressWidth = progressWidth;
	    }

	    public String getTempText() {
	        return tempText;
	    }
	    public void setTempText(String tempText) {
	        this.tempText = tempText;
	    }

	    public float getTempTextSize() {
	        return tempTextSize;
	    }

	    public void setTempTextSize(float tempTextSize) {
	        this.tempTextSize = tempTextSize;
	    }
}
