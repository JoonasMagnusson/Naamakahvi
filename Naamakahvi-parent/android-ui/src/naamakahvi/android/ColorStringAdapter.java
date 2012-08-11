package naamakahvi.android;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ColorStringAdapter extends BaseAdapter {
	private String[] items;
	
	public ColorStringAdapter(String[] items) {
	    this.items = items;
	}
	
	public int getCount() {
		return items.length;
	}

	public String getItem(int position) {
		return items[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
	    if(v == null) {
	        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = vi.inflate(R.layout.new_list_bigger_text, null);
	    }
		
		String text = items[position];
		TextView dur = (TextView) v.findViewById(R.id.bi);
		if (text.contains("-"))
			dur.setTextColor(Color.RED);
		else
			dur.setTextColor(Color.GREEN);
		return v;
	}

}
