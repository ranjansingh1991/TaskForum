package in.enzen.taskforum.sliding;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Rupesh on 06-12-2017.
 */
@SuppressWarnings("ALL")
public class SlidingTabStrip extends LinearLayout {
    private static final int nDEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 0;
    private static final int nSELECTED_INDICATOR_THICKNESS_DIPS = 3;
    private static final int nDEFAULT_SELECTED_INDICATOR_COLOR = 0xFFFF9229;

    private static final byte nDEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26;

    private final int nBottomBorderThickness;
    private final Paint nBottomBorderPaint;

    private final int nSelectedIndicatorThickness;
    private final Paint nSelectedIndicatorPaint;

    private final int nDefaultBottomBorderColor;

    private int nSelectedPosition;
    private float nSelectionOffset;

    private SlidingTabLayout.TabColorizer stlCustomTabColorizer;
    private final SimpleTabColorizer stcDefaultTabColorizer;

    SlidingTabStrip(Context context) {
        this(context, null);
    }

    SlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        final float density = getResources().getDisplayMetrics().density;

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeForegroundColor = outValue.data;

        nDefaultBottomBorderColor = setColorAlpha(themeForegroundColor, nDEFAULT_BOTTOM_BORDER_COLOR_ALPHA);

        stcDefaultTabColorizer = new SimpleTabColorizer();
        stcDefaultTabColorizer.setIndicatorColors(nDEFAULT_SELECTED_INDICATOR_COLOR);

        nBottomBorderThickness = (int) (nDEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density);
        nBottomBorderPaint = new Paint();
        nBottomBorderPaint.setColor(nDefaultBottomBorderColor);

        nSelectedIndicatorThickness = (int) (nSELECTED_INDICATOR_THICKNESS_DIPS * density);
        nSelectedIndicatorPaint = new Paint();
    }

    void setCustomTabColorizer(SlidingTabLayout.TabColorizer customTabColorizer) {
        stlCustomTabColorizer = customTabColorizer;
        invalidate();
    }

    void setSelectedIndicatorColors(int... colors) {
        // Make sure that the custom colorizer is removed
        stlCustomTabColorizer = null;
        stcDefaultTabColorizer.setIndicatorColors(colors);
        invalidate();
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        nSelectedPosition = position;
        nSelectionOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();
        final SlidingTabLayout.TabColorizer tabColorizer = stlCustomTabColorizer != null ? stlCustomTabColorizer : stcDefaultTabColorizer;

        // Thick colored underline below the current selection
        if (childCount > 0) {
            View selectedTitle = getChildAt(nSelectedPosition);
            int left = selectedTitle.getLeft();
            int right = selectedTitle.getRight();
            int color = tabColorizer.getIndicatorColor(nSelectedPosition);

            if (nSelectionOffset > 0f && nSelectedPosition < (getChildCount() - 1)) {
                int nextColor = tabColorizer.getIndicatorColor(nSelectedPosition + 1);
                if (color != nextColor) {
                    color = blendColors(nextColor, color, nSelectionOffset);
                }

                // Draw the selection partway between the tabs
                View nextTitle = getChildAt(nSelectedPosition + 1);
                left = (int) (nSelectionOffset * nextTitle.getLeft() + (1.0f - nSelectionOffset) * left);
                right = (int) (nSelectionOffset * nextTitle.getRight() + (1.0f - nSelectionOffset) * right);
            }

            nSelectedIndicatorPaint.setColor(color);

            canvas.drawRect(left, height - nSelectedIndicatorThickness, right, height, nSelectedIndicatorPaint);
        }

        // Thin underline along the entire bottom edge
        canvas.drawRect(0, height - nBottomBorderThickness, getWidth(), height, nBottomBorderPaint);
    }

    private static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    private static class SimpleTabColorizer implements SlidingTabLayout.TabColorizer {
        private int[] mIndicatorColors;

        @Override
        public final int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        void setIndicatorColors(int... colors) {
            mIndicatorColors = colors;
        }
    }
}
