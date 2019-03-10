package in.enzen.taskforum.sliding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Rupesh on 06-12-2017.
 */

public class SlidingTabLayout extends HorizontalScrollView {

    public interface TabColorizer
    {
        int getIndicatorColor(int position);
    }

    private static final int nTITLE_OFFSET_DIPS = 24;
    private static final int nTAB_VIEW_PADDING_DIPS = 16;
    private static final int nTAB_VIEW_TEXT_SIZE_SP = 12;

    private int nTitleOffset;
    private int nTabViewLayoutId;
    private int nTabViewTextViewId;
    private boolean nDistributeEvenly;

    private ViewPager vpPager;
    private SparseArray<String> mContentDescriptions = new SparseArray<String>();
    private ViewPager.OnPageChangeListener vpPagerPageChangeListener;

    private final SlidingTabStrip stsTabStrip;

    public SlidingTabLayout(Context context)
    {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        nTitleOffset = (int) (nTITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        stsTabStrip = new SlidingTabStrip(context);
        addView(stsTabStrip, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setCustomTabColorizer(TabColorizer tabColorizer)
    {
        stsTabStrip.setCustomTabColorizer(tabColorizer);
    }

    public void setDistributeEvenly(boolean distributeEvenly)
    {
        nDistributeEvenly = distributeEvenly;
    }

    public void setSelectedIndicatorColors(int... colors) {
        stsTabStrip.setSelectedIndicatorColors(colors);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener)
    {
        vpPagerPageChangeListener = listener;
    }

    public void setCustomTabView(int layoutResId, int textViewId)
    {
        nTabViewLayoutId = layoutResId;
        nTabViewTextViewId = textViewId;
    }

    @SuppressWarnings("deprecation")
    public void setViewPager(ViewPager viewPager)
    {
        stsTabStrip.removeAllViews();
        vpPager = viewPager;
        if (viewPager != null)
        {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    @SuppressLint("NewApi")
    protected TextView createDefaultTabView(Context context)
    {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, nTAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,outValue, true);
        textView.setBackgroundResource(outValue.resourceId);
        textView.setAllCaps(true);

        int padding = (int) (nTAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    private void populateTabStrip()
    {
        final PagerAdapter adapter = vpPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++)
        {
            View tabView = null;
            TextView tabTitleView = null;

            if (nTabViewLayoutId != 0)
            {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(nTabViewLayoutId, stsTabStrip,false);
                tabTitleView = (TextView) tabView.findViewById(nTabViewTextViewId);
            }

            if (tabView == null)
            {
                tabView = createDefaultTabView(getContext());
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView))
            {
                tabTitleView = (TextView) tabView;
            }

            if (nDistributeEvenly)
            {
                LinearLayout.LayoutParams lpLayoutParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lpLayoutParams.width = 0;
                lpLayoutParams.weight = 1;
            }

            tabTitleView.setText(adapter.getPageTitle(i));
            tabView.setOnClickListener(tabClickListener);

            String desc = mContentDescriptions.get(i, null);
            if (desc != null)
            {
                tabView.setContentDescription(desc);
            }

            stsTabStrip.addView(tabView);
            if (i == vpPager.getCurrentItem())
            {
               /* int color = ContextCompat.getColor(tabView.getContext(), android.R.color.white);
                tabTitleView.setTextColor(color);*/
                tabView.setSelected(true);
                // tabTitleView.setTextColor(getResources().getColorStateList(R.color.tab_text_color));
            }
        }
    }

    public void setContentDescription(int i, String desc)
    {
        mContentDescriptions.put(i, desc);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (vpPager != null)
        {
            scrollToTab(vpPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset)
    {
        final int tabStripChildCount = stsTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount)
        {
            return;
        }

        View selectedChild = stsTabStrip.getChildAt(tabIndex);
        if (selectedChild != null)
        {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0)
            {
                // If we're not at the home_list_menu child and are mid-scroll, make sure we obey the offset
                targetScrollX -= nTitleOffset;
            }
            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener
    {
        private int nScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            int tabStripChildCount = stsTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount))
            {
                return;
            }

            stsTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = stsTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)? (int) (positionOffset * selectedTitle.getWidth()): 0;
            scrollToTab(position, extraOffset);

            if (vpPagerPageChangeListener != null)
            {
                vpPagerPageChangeListener.onPageScrolled(position, positionOffset,positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {
            nScrollState = state;

            if (vpPagerPageChangeListener != null)
            {
                vpPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position)
        {
            if (nScrollState == ViewPager.SCROLL_STATE_IDLE)
            {
                stsTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }
            for (int i = 0; i < stsTabStrip.getChildCount(); i++)
            {
                stsTabStrip.getChildAt(i).setSelected(position == i);
            }
            if (vpPagerPageChangeListener != null)
            {
                vpPagerPageChangeListener.onPageSelected(position);
            }
        }
    }

    private class TabClickListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            for (int i = 0; i < stsTabStrip.getChildCount(); i++)
            {
                if (v == stsTabStrip.getChildAt(i))
                {
                    vpPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }
}
