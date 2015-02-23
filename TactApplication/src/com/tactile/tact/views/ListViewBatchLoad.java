package com.tactile.tact.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tactile.tact.R;
import com.tactile.tact.consts.TactConst;


/**
 * Created by leyan on 1/6/15.
 */
public class ListViewBatchLoad extends LinearLayout {

    private ListView listView;
    private LinearLayout loadingDown;
    private LinearLayout loadingUp;
    private boolean loading = false;

    private AbsListView.OnScrollListener onScrollListener;
    private OnDetectScrollListener onDetectScrollListener;

    public interface onScrollListView {
        public void onScrollingDown();
        public void onScrollingUp();
    }


    public ListViewBatchLoad(Context context) {
        super(context);
        init();
    }

    public ListViewBatchLoad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListViewBatchLoad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public interface OnDetectScrollListener {

        void onUpScrolling();

        void onDownScrolling();
    }

    private void init() {
        inflate(getContext(), R.layout.list_view_batch_load, this);
        listView = (ListView)this.findViewById(R.id.lstv_batch_load);
        loadingUp = (LinearLayout)this.findViewById(R.id.lny_loading_up);
        loadingDown = (LinearLayout)this.findViewById(R.id.lny_loading_down);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int oldTop;
            private int oldFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (onScrollListener != null) {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                if (onDetectScrollListener != null) {
                    onDetectedListScroll(view, firstVisibleItem);
                }
            }

            private void onDetectedListScroll(AbsListView absListView, int firstVisibleItem) {
                View view = absListView.getChildAt(0);
                int top = (view == null) ? 0 : view.getTop();

                if (firstVisibleItem == oldFirstVisibleItem) {
                    if (top > oldTop) {
                        onDetectScrollListener.onUpScrolling();
                    } else if (top < oldTop) {
                        onDetectScrollListener.onDownScrolling();
                    }
                } else {
                    if (firstVisibleItem < oldFirstVisibleItem) {
                        onDetectScrollListener.onUpScrolling();
                    } else {
                        onDetectScrollListener.onDownScrolling();
                    }
                }

                oldTop = top;
                oldFirstVisibleItem = firstVisibleItem;
            }
        });
    }

    public void setOnDetectScrollListener(OnDetectScrollListener onDetectScrollListener) {
        this.onDetectScrollListener = onDetectScrollListener;
    }

    public void setLoadingDown(boolean loading) {
        this.loading = loading;
        if (loading) {
            loadingDown.setVisibility(View.VISIBLE);
        } else {
            loadingDown.setVisibility(View.GONE);
        }
    }

    public void setLoadingUp(boolean loading) {
        this.loading = loading;
        if (loading) {
            loadingUp.setVisibility(View.VISIBLE);
        } else {
            loadingUp.setVisibility(View.GONE);
        }
    }

    public void setListAdapter(BaseAdapter adapter) {
        if (listView != null) {
            listView.setAdapter(adapter);
        } else {
            listView = (ListView)this.findViewById(R.id.lstv_batch_load);
            listView.setAdapter(adapter);
        }
    }

    public void refreshListView() {
        if (listView.getAdapter() != null) {
            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        }
    }

    public ListView getListView() {
        return listView;
    }
}
