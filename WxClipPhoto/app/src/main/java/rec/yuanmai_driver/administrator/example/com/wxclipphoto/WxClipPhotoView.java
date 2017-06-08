package rec.yuanmai_driver.administrator.example.com.wxclipphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by ${wdgan} on 2017/5/18 0018.
 * 邮箱18149542718@163
 * 模微信头像裁剪
 */

public class WxClipPhotoView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener {
    private Matrix mMatrix;
    private boolean isFirst;
    private int mViewWidth;
    private int mViewHeight;
    private int mTopAndBottomPadding;
    private DisplayMetrics mDisplayMetrics;
    private int mLeftAndRightPadding;
    private float mInitScale;
    private Bitmap mCoverBitmap;
    private Canvas mCoverCanvas;
    private float mLastX;
    private float mLastY;
    private int mLastPoint;
    private float mMinScale;
    private boolean isNeedDoubleLarge;
    private final float[] mMatrixValues = new float[9];
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private float mMaxScale;
    private float mLargeFator;
    private float mSmallFator;
    private int mClipLength;
    private Canvas mClipCanvas;
    private Bitmap mClipBitmap;
    private Bitmap mBitmap;

    public WxClipPhotoView(Context context) {
        this(context,null);
    }

    public WxClipPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WxClipPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        mMatrix = new Matrix();
        isFirst = true;
        mInitScale = 1;
        mLargeFator = 1.25f;
        mSmallFator = 0.8f;
        isNeedDoubleLarge = true;
        mLeftAndRightPadding = (int) (20*mDisplayMetrics.density);
        mTopAndBottomPadding = (int) (40*mDisplayMetrics.density);
        this.setOnTouchListener(this);
        mScaleGestureDetector = new ScaleGestureDetector(this.getContext(),this);
        isNeedDoubleLarge = true;
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float factor = isNeedDoubleLarge?mLargeFator:mSmallFator;
                isNeedDoubleLarge=!isNeedDoubleLarge;
                float scale = factor*getMatrixScale();
                if (scale>mMinScale&&scale<mMaxScale){
                    mMatrix.postScale(factor,factor,e.getX(),e.getY());
                }
                setImageMatrix(mMatrix);
                return super.onDoubleTap(e);
            }
        });



    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mCoverBitmap,0,0,null);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onGlobalLayout() {
        Drawable d = getDrawable();
        if (d==null) {
            return;
        }
        if (!isFirst) {
           return;
        }

        isFirst = false;
        int dx=0,dy=0,dWidth=0,dHeight = 0;
        dHeight = d.getIntrinsicHeight();
        dWidth = d.getIntrinsicWidth();
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mClipLength = (int) (mViewWidth - 80*mDisplayMetrics.density);
        if (mClipLength>dWidth && mViewHeight>dHeight){
            mInitScale = Math.min(mViewHeight*1.0f/dHeight,(mClipLength*1.0f)/dWidth);
        }else if (mClipLength>dWidth && mViewHeight<dHeight){
            mInitScale = (dHeight*1.0f)/mViewHeight;
        }else if (mClipLength<dWidth && mViewHeight>dHeight){
            mInitScale = (mClipLength*1.0f)/dWidth;
        }else {
            mInitScale = Math.min((mViewHeight*1.0f)/dHeight,(mClipLength*1.0f)/dWidth);
        }

        dx =  (mViewWidth - dWidth)/2;
        dy = (mViewHeight-dHeight)/2;
        mMatrix.setTranslate(dx,dy);
        mMatrix.postScale(mInitScale, mInitScale,mViewWidth/2,mViewHeight/2);


        createOverImage();
        mMinScale = mInitScale /2;
        mMaxScale = 2* mInitScale;
        setImageMatrix(mMatrix);
    }

    private void createOverImage() {
        mCoverBitmap = Bitmap.createBitmap(mViewWidth,mViewHeight, Bitmap.Config.ARGB_4444);
        mCoverCanvas = new Canvas(mCoverBitmap);
        mCoverCanvas.drawColor(0xaa000000);
        mClipBitmap = Bitmap.createBitmap(mClipLength,mClipLength,Bitmap.Config.ARGB_4444);
        mClipCanvas = new Canvas(mClipBitmap);
        mClipCanvas.drawColor(0xffffffff);
        Paint mClipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mClipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        int xStart = (mViewWidth - mClipLength)/2;
        int yStart = (mViewHeight-mClipLength)/2;
        mCoverCanvas.drawBitmap(mClipBitmap,xStart,yStart,mClipPaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        Point point =  getPoint(event);
        mGestureDetector.onTouchEvent(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mLastX = point.x;
                mLastY = point.y;
                mLastPoint = event.getPointerCount();

                break;
            case MotionEvent.ACTION_MOVE:

                if (mLastPoint!=event.getPointerCount()){
                    mLastPoint = event.getPointerCount();
                    mLastPoint = event.getPointerCount();
                    mLastX = point.x;
                    mLastY = point.y;
                    return true;
                }

                float dx = point.x- mLastX;
                float dy = point.y-mLastY;
                mLastX = point.x;
                mLastY = point.y;
                mMatrix.postTranslate(dx,dy);
                setImageMatrix(mMatrix);
           mScaleGestureDetector.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     *  获取event.x和event.y平均值
     * @param event
     * @return
     */
    private Point getPoint(MotionEvent event) {
        Point point =new Point();
        float sumX = 0;
        float sumY = 0;
        for (int i=0;i<event.getPointerCount();i++){
            sumX += event.getX(i);
            sumY += event.getY(i);
        }
        point.x = sumX/(event.getPointerCount());
        point.y = sumY/(event.getPointerCount());
        return  point;
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scale = (detector.getScaleFactor()*getMatrixScale());
        if (scale<mMaxScale && scale>mMinScale){
            mMatrix.postScale(detector.getScaleFactor(),
                    detector.getScaleFactor(),
                    detector.getFocusX(),
                    detector.getFocusY());
            setImageMatrix(mMatrix);
            isNeedDoubleLarge = true;
        }
        return true;
    }
    private  float getMatrixScale(){
        mMatrix.getValues(mMatrixValues);
        float sclae =  mMatrixValues[Matrix.MSCALE_X];
        return sclae;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
    private class Point{
        float x;
        float y;

    }
    public Bitmap getClipBitmap(){
        Bitmap mOrgBitmap = Bitmap.createBitmap(mViewWidth,mViewHeight,Bitmap.Config.ARGB_8888);
        Canvas mOrgCanvas = new Canvas(mOrgBitmap);
        draw(mOrgCanvas);
        int xStart = (mViewWidth - mClipLength)/2;
        int yStart = (mViewHeight-mClipLength)/2;
        mBitmap = Bitmap.createBitmap(mOrgBitmap,xStart,yStart,mClipLength,mClipLength);
        return mBitmap;

    }
}
