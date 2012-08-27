package naamakahvi.android.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Subclass of GridView with scrolling functionality disabled
 */
public class NoScrollGridView extends GridView {

	public NoScrollGridView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, expandSpec);
		getLayoutParams().height = getMeasuredHeight();

	}

}
