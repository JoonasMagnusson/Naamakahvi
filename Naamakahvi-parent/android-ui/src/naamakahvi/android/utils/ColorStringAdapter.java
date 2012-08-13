package naamakahvi.android.utils;


import naamakahvi.android.R;
import naamakahvi.android.R.layout;
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
	private String[] posOrNeg;
	private LayoutInflater inflater;
	
	public ColorStringAdapter(Context context, String[] items, String[] posOrNeg) {
	    this.items = items;
	    this.posOrNeg = posOrNeg;
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
		TextView colorListView = (TextView) v.findViewById(android.R.id.text1);
		colorListView.setText(text);
		if (posOrNeg[position].equals("positive"))
			colorListView.setTextColor(Color.GREEN);
		else
			colorListView.setTextColor(Color.RED);
		return v;
	}

}
