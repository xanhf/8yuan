package com.trade.eight.view.expandListView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;

public class PinnedHeaderListView extends ListView implements OnScrollListener {
    String TAG = "PinnedHeaderListView";
    private OnScrollListener mOnScrollListener;
    private PinnedSectionHeadClick pinnedSectionHeadClick;

    public void setPinnedSectionHeadClick(PinnedSectionHeadClick pinnedSectionHeadClick) {
        this.pinnedSectionHeadClick = pinnedSectionHeadClick;
    }

    public static interface PinnedSectionedHeaderAdapter {
        public boolean isSectionHeader(int position);

        public int getSectionForPosition(int position);

        public View getSectionHeaderView(int section, View convertView, ViewGroup parent);

        public int getSectionHeaderViewType(int section);

        public int getCount();

    }

    private PinnedSectionedHeaderAdapter mAdapter;
    private View mCurrentHeader;
    private int mCurrentHeaderViewType = 0;
    private float mHeaderOffset;
    private boolean mShouldPin = true;
    private int mCurrentSection = 0;
    private int mWidthMode;
    private int mHeightMode;

    public PinnedHeaderListView(Context context) {
        super(context);
        super.setOnScrollListener(this);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnScrollListener(this);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setOnScrollListener(this);
    }

    public void setPinHeaders(boolean shouldPin) {
        mShouldPin = shouldPin;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mCurrentHeader = null;
        mAdapter = (PinnedSectionedHeaderAdapter) adapter;
        super.setAdapter(adapter);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (mAdapter == null || mAdapter.getCount() == 0 || !mShouldPin || (firstVisibleItem < getHeaderViewsCount())) {
            mCurrentHeader = null;
            mHeaderOffset = 0.0f;
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                View header = getChildAt(i);
                if (header != null) {
                    header.setVisibility(VISIBLE);
                }
            }
            return;
        }

        firstVisibleItem -= getHeaderViewsCount();

        int section = mAdapter.getSectionForPosition(firstVisibleItem);
        int viewType = mAdapter.getSectionHeaderViewType(section);
//        if (mCurrentHeader == null || section != mCurrentSection) {
            mCurrentHeader = getSectionHeaderView(section, mCurrentHeaderViewType != viewType ? null : mCurrentHeader);
//            mCurrentHeader = getSectionHeaderView(section,null);
            ensurePinnedHeaderLayout(mCurrentHeader);
//        }

        mCurrentHeaderViewType = viewType;

        mHeaderOffset = 0.0f;

       /* for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            if (mAdapter.isSectionHeader(i)) {
                View header = getChildAt(i - firstVisibleItem);
                float headerTop = header.getTop();
                float pinnedHeaderHeight = mCurrentHeader.getMeasuredHeight();
                header.setVisibility(VISIBLE);
                if (pinnedHeaderHeight >= headerTop && headerTop > 0) {
                    mHeaderOffset = headerTop - header.getHeight();
                } else if (headerTop <= 0) {
                    header.setVisibility(INVISIBLE);
                }
            }
        }*/

        invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = section != mCurrentSection || oldView == null;

        View view = mAdapter.getSectionHeaderView(section, oldView, this);
        if (shouldLayout&&view!=null) {
            ensurePinnedHeaderLayout(view);
            mCurrentSection = section;
        }
        return view;
    }

    private void ensurePinnedHeaderLayout(View header) {
        if (header != null && header.isLayoutRequested()) {
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), mWidthMode);

            int heightSpec;
            ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
            if (layoutParams != null && layoutParams.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            } else {
                heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }
            header.measure(widthSpec, heightSpec);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mAdapter == null || !mShouldPin || mCurrentHeader == null)
            return;
        int saveCount = canvas.save();
        canvas.translate(0, mHeaderOffset);
        canvas.clipRect(0, 0, getWidth(), mCurrentHeader.getMeasuredHeight()); // needed
        // for
        // <
        // HONEYCOMB
        mCurrentHeader.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        /**
         * 如果当前有吸顶 并且点击区域在吸顶控件  打断touch事件
         */
        if (mCurrentHeader != null && pinnedSectionHeadClick != null) {
            if (y > mHeaderOffset && y < mHeaderOffset + mCurrentHeader.getMeasuredHeight()) {
                pinnedSectionHeadClick.onCurrentClick(mCurrentHeader, mCurrentSection);
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        mHeightMode = MeasureSpec.getMode(heightMeasureSpec);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    public static abstract class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int rawPosition, long id) {
            SectionedBaseAdapter adapter;
            if (adapterView.getAdapter().getClass().equals(HeaderViewListAdapter.class)) {
                HeaderViewListAdapter wrapperAdapter = (HeaderViewListAdapter) adapterView.getAdapter();
                adapter = (SectionedBaseAdapter) wrapperAdapter.getWrappedAdapter();
            } else {
                adapter = (SectionedBaseAdapter) adapterView.getAdapter();
            }
            int section = adapter.getSectionForPosition(rawPosition);
            int position = adapter.getPositionInSectionForPosition(rawPosition);

            if (position == -1) {
                onSectionClick(adapterView, view, section, id);
            } else {
                onItemClick(adapterView, view, section, position, id);
            }
        }

        public abstract void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id);

        public abstract void onSectionClick(AdapterView<?> adapterView, View view, int section, long id);

    }


}
