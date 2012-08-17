package naamakahvi.android.utils;

import java.util.List;

import naamakahvi.android.R;
import naamakahvi.android.R.layout;
import naamakahvi.naamakahviclient.SaldoItem;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SaldoItemAdapter extends BaseAdapter {
	private List<SaldoItem> items;
	private LayoutInflater inflater;
	private double[] deltas;
	private String mSaldoString;

	public SaldoItemAdapter(Context context, List<SaldoItem> items,
			double[] deltas) {
		this.items = items;
		this.deltas = deltas;
		inflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		mSaldoString = context.getString(R.string.saldo_item);
	}

	public int getCount() {
		return items.size();
	}

	public SaldoItem getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View v, ViewGroup parent) {

		if (v == null) {
			v = inflater.inflate(
					naamakahvi.android.R.layout.new_list_bigger_text, null);
		}

		SaldoItem item = getItem(position);
		TextView colorListView = (TextView) v.findViewById(android.R.id.text1);
		String saldoString = String.format(mSaldoString, item.getGroupName());

		double saldo = item.getSaldo();
		double delta = deltas[position];
		double resultSaldo = saldo + delta;

		if (delta != 0) {
			colorListView.setText(Html.fromHtml(saldoString
					+ "<font color='"
					+ ((saldo < 0) ? "red" : "green")
					+ "'>"
					+ saldo
					+ "</font>"
					+ ((delta < 0) ? " " : " +") + delta
					+ (" = " + "<font color='"
							+ ((resultSaldo < 0) ? "red" : "green") + "'>"
							+ resultSaldo + "</font>")));
		} else {
			colorListView.setText(Html.fromHtml(saldoString 
					+ "<font color='"
					+ ((saldo < 0) ? "red" : "green") + 
					"'>"
					+ saldo
					+ "</font>"));
		}

		return v;
	}

}
