package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.crash.rettina.R;
import com.crash.rettina.Schedule;
  
public class VerticalSeekBar extends SeekBar {  
	
	
	private int[] dotPositions = null;
	private Bitmap dotBitmap = null;
	Schedule sched;
  
    public VerticalSeekBar(Schedule activity) {  
        super(activity.getActivity());  
        init(null);
        sched = activity;
    }  
  
    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init(attrs);

    }  
  
    public VerticalSeekBar(Context context, AttributeSet attrs) {  
        super(context, attrs);
        init(attrs);

    }  
  
    public void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(h, w, oldh, oldw);  
    }  
  
   private void init(final AttributeSet attributeSet) {
       final TypedArray attrsArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.DottedSeekBar, 0, 0);

       final int dotsArrayResource = attrsArray.getResourceId(R.styleable.DottedSeekBar_dots_positions, 0);

       if (0 != dotsArrayResource) {
           dotPositions = getResources().getIntArray(dotsArrayResource);
       }

       final int dotDrawableId = attrsArray.getResourceId(R.styleable.DottedSeekBar_dots_drawable, 0);

       if (0 != dotDrawableId) {
           dotBitmap = BitmapFactory.decodeResource(getResources(), dotDrawableId);
       }
   }

   /**
    * @param dots to be displayed on this SeekBar
    */
   public void setDots(final int[] dots) {
       dotPositions = dots;
       invalidate();
   }

   /**
    * @param dotsResource resource id to be used for dots drawing
    */
   public void setDotsDrawable(final int dotsResource) {
       dotBitmap = BitmapFactory.decodeResource(getResources(), dotsResource);
       invalidate();
   }
  
    @Override  
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);  
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());  
    }  
  
    protected void onDraw(Canvas c) {  
     c.rotate(90);  
     c.translate(0, - getWidth());  
       
        super.onDraw(c); 
        
        final int width = getMeasuredWidth();
      //  final int step = width / getMax();
        int step = 50;

        if (null != dotPositions && 0 != dotPositions.length && null != dotBitmap) {
            // draw dots if we have ones
            for (int position : dotPositions) {
            	
                c.drawBitmap(dotBitmap, position , 5, null);

 }
        }
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
//        if (!isEnabled()) {  
//            return false;  
//        }  
//  
//        switch (event.getAction()) {  
//            case MotionEvent.ACTION_DOWN:  
//            case MotionEvent.ACTION_MOVE:  
//            case MotionEvent.ACTION_UP:  
//             setProgress((int) (getMax() * event.getY() / getHeight()) - 0);  
//                onSizeChanged(getWidth(), getHeight(), 0, 0);  
//                break;  
//  
//            case MotionEvent.ACTION_CANCEL:  
//                break;  
//        }  
        return false;  
    }  
}  
