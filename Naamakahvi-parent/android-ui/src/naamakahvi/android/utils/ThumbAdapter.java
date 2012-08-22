package naamakahvi.android.utils;

import java.util.List;

import naamakahvi.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * Adapter for thumbnail images
 */
public class ThumbAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	/**
	 * Creates a new ThumbnailAdapter
	 * @param context context for the adapter
	 * @param data List of images to display
	 */
	public ThumbAdapter(Context context, List<Bitmap> data) {
		this.inflater = LayoutInflater.from(context);
		this.mBitmaps = data;
	}

	private List<Bitmap> mBitmaps;

	public int getCount() {
		return 6;
	}

	public Bitmap getItem(int position) {
		if (position < 0 || position >= Math.min(mBitmaps.size(),6))
			return null;
		return mBitmaps.get(position);
	}

	public long getItemId(int position) {
		if (position < getCount() && position >= 0) {
			return position;
		}
		return -1;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final Bitmap bmp = getItem(position);

		if (convertView == null) {
			convertView = this.inflater
					.inflate(R.layout.thumb_layout, null);
		}

		ImageView thumb = ((ImageView) convertView
				.findViewById(R.id.imageView1));
		thumb.setImageBitmap(bmp);
		thumb.setMaxHeight(100);
		return convertView;
	}

}