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
	private Context context;
	
	public ColorStringAdapter(Context context, String[] items) {
	    this.items = items;
	    this.context = context;
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
	    	LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = vi.inflate(naamakahvi.android.R.layout.new_list_bigger_text, null);
	    }
		
		String text = items[position];
		TextView dur = (TextView) v.findViewById(R.id.text1);
		dur.setText(items[position]);
		if (text.contains("-"))
			dur.setTextColor(Color.RED);
		else
			dur.setTextColor(Color.GREEN);
		return v;
	}

}
