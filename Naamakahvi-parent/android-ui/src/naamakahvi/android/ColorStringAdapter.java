package naamakahvi.android;


import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ColorStringAdapter extends BaseAdapter {
	private String[] items;
	private LayoutInflater inflater;
	
	public ColorStringAdapter(Context context, String[] items) {
	    this.items = items;
	    inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
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

	public View getView(int position, View v, ViewGroup parent) {
		
	    if(v == null) {
	        v = inflater.inflate(naamakahvi.android.R.layout.new_list_bigger_text, null);
	    }
		
		String text = getItem(position);
		TextView dur = (TextView) v.findViewById(R.id.text1);
		dur.setText(text);
		if (text.contains("-"))
			dur.setTextColor(Color.RED);
		else
			dur.setTextColor(Color.GREEN);
		return v;
	}

}
