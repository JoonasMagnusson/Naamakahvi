package naamakahvi.android.utils;

import java.util.List;

import naamakahvi.android.R;
import naamakahvi.naamakahviclient.SaldoItem;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SaldoItemAdapter extends BaseAdapter {
	private final List<SaldoItem> items;
	private final LayoutInflater inflater;
	private final double[] deltas;
	private final String mSaldoString;

	public SaldoItemAdapter(final Context context, final List<SaldoItem> items, final double[] deltas) {
		this.items = items;
		this.deltas = deltas;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mSaldoString = context.getString(R.string.saldo_item);
	}

	public int getCount() {
		return this.items.size();
	}

	public SaldoItem getItem(final int position) {
		return this.items.get(position);
	}

	public long getItemId(final int position) {
		return position;
	}

	public View getView(final int position, View v, final ViewGroup parent) {

		if (v == null) {
			v = this.inflater.inflate(naamakahvi.android.R.layout.new_list_bigger_text, null);
		}

		final SaldoItem item = getItem(position);
		final TextView colorListView = (TextView) v.findViewById(android.R.id.text1);
		final String saldoString = String.format(this.mSaldoString, item.getGroupName());

		final double saldo = item.getSaldo();
		final double delta = this.deltas[position];
		final double resultSaldo = saldo + delta;

		if (delta != 0) {
			colorListView
					.setText(Html
							.fromHtml(saldoString
									+ " <font color='"
									+ ((saldo < 0) ? "red" : "lime")
									+ "'>"
									+ saldo
									+ "</font>"
									+ ((delta < 0) ? " " : " +")
									+ delta
									+ (" = " + "<font color='" + ((resultSaldo < 0) ? "red" : "lime") + "'>"
											+ resultSaldo + "</font>")));
		} else {
			colorListView.setText(Html.fromHtml(saldoString + " <font color='" + ((saldo < 0) ? "red" : "lightgreen")
					+ "'>" + saldo + "</font>"));
		}

		return v;
	}

}
