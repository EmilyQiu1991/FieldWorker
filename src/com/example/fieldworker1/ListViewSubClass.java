package com.example.fieldworker1;

import android.content.Context;  
import android.util.AttributeSet;  
import android.view.GestureDetector;  
import android.view.GestureDetector.OnGestureListener;  
import android.view.LayoutInflater;  
import android.view.MotionEvent;  
import android.view.View;  
import android.view.View.OnTouchListener;  
import android.view.ViewGroup;  
import android.widget.ListView;  
import android.widget.RelativeLayout;  
  
public class ListViewSubClass extends ListView implements OnGestureListener,OnTouchListener {  
    private GestureDetector mGestureDetector;  
    private OnDeleteListener mDeleteListener;  
    private View mDeleteView;  
    private ViewGroup mListViewItemViewGroup;  
    private int selectedItem;  
    private boolean isDeleteShowing;  
      
    public ListViewSubClass(Context context) {  
        super(context);  
    } 
    
      
    public ListViewSubClass(Context context, AttributeSet attrs) {  
        super(context, attrs);
        this.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(context, this);  
    }  
      
    public ListViewSubClass(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
      
    public void setOnDeleteListener(OnDeleteListener onDeleteListener){  
        mDeleteListener=onDeleteListener;  
    }  
    
    public interface OnDeleteListener {  
        void onDelete(int index);  
    }  
    
    @Override  
    public boolean onTouch(View v, MotionEvent event) {
        if (isDeleteShowing) {  
            mListViewItemViewGroup.removeView(mDeleteView);  
            mDeleteView = null;  
            isDeleteShowing = false;  
            return false;
        } else {  
            return mGestureDetector.onTouchEvent(event);  
        }  
  
    }  
 
    @Override  
    public boolean onDown(MotionEvent e) {  
        if (!isDeleteShowing) {  
            selectedItem = pointToPosition((int) e.getX(), (int) e.getY());  
        }  
        return false;  
    }  
  
    @Override  
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {  

        if (!isDeleteShowing && Math.abs(velocityX) > Math.abs(velocityY)) {  
            mDeleteView = LayoutInflater.from(getContext()).inflate(R.layout.delete, null);  
            mDeleteView.setOnClickListener(new OnClickListener() {  
                @Override  
                public void onClick(View v) {  
                    mListViewItemViewGroup.removeView(mDeleteView);  
                    mDeleteView = null;  
                    isDeleteShowing = false;  
                    mDeleteListener.onDelete(selectedItem);  
                }  
            });
            mListViewItemViewGroup = (ViewGroup) getChildAt(selectedItem-getFirstVisiblePosition());  
            RelativeLayout.LayoutParams params = new   
            RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);  
            params.addRule(RelativeLayout.CENTER_VERTICAL);  
            mListViewItemViewGroup.addView(mDeleteView, params);  
            isDeleteShowing = true;  
        }  
          
        return false;  
  
    }  
  
    @Override  
    public void onLongPress(MotionEvent e) {  
  
    }  
  
    @Override  
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {  
        return false;  
    }  
  
    @Override  
    public void onShowPress(MotionEvent e) {  
  
    }  
  
    @Override  
    public boolean onSingleTapUp(MotionEvent e) {  
        return false;  
    }   
  
}  
