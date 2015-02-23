package com.tactile.tact.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by leyan on 1/12/15.
 */
public class ListViewLazyLoad extends ListView {

    private OnListViewLazyListListener listViewLazyListListener;
    private Context mContext;
    private boolean mBlockLayoutChildren;

    public interface OnListViewLazyListListener {
        public void onScrollingDown();
        public void onScrollingUp();
        public void onScrollStateChanged();
        public void onOverScrollUp();
        public void onOverScrollDown();
    }

    public void setListViewLazyListListener(OnListViewLazyListListener listener) {
        this.listViewLazyListListener = listener;
    }

    public ListViewLazyLoad(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ListViewLazyLoad(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ListViewLazyLoad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

//    public ListViewLazyLoad(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if(getFirstVisiblePosition()==0) {
            //list is at top
            if (listViewLazyListListener != null) {
                listViewLazyListListener.onOverScrollUp();
            }
        } else {
            //bottom end
            if (listViewLazyListListener != null) {
                listViewLazyListListener.onOverScrollDown();
            }
        }
    }

    private void init() {
        this.setOnScrollListener(new OnScrollListener() {

            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listViewLazyListListener != null) {
                    listViewLazyListListener.onScrollStateChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int itemsDownNotVisible = totalItemCount - (firstVisibleItem + visibleItemCount);
                int itemsUpNotVisible = totalItemCount - (itemsDownNotVisible + visibleItemCount);
                if(mLastFirstVisibleItem < firstVisibleItem) {
                    //SCROLLING DOWN
                    if (listViewLazyListListener != null && itemsDownNotVisible < 10) {
                        listViewLazyListListener.onScrollingDown();
                    }
                }
                if(mLastFirstVisibleItem > firstVisibleItem) {
                    //SCROLLING UP
                    if (listViewLazyListListener != null && itemsUpNotVisible < 10) {
                        listViewLazyListListener.onScrollingUp();
                    }
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });


    }

    public void setBlockLayoutChildren(boolean block) {
        mBlockLayoutChildren = block;
    }

    @Override
    protected void layoutChildren() {
        if (!mBlockLayoutChildren) {
            super.layoutChildren();
        }
    }
}
