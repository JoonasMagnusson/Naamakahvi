package naamakahvi.android;

import naamakahvi.android.utils.Basket;
import naamakahvi.android.utils.ExtraNames;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RecogActivity extends Activity {
	public static final short SELECT_USERNAME = 0;
	private Basket mOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recog_user);
		mOrder = getIntent().getParcelableExtra(ExtraNames.PRODUCTS);
	}
	
	public void userListClick(View v){
			Intent i = new Intent(this, LoginwithusernameActivity.class);
			startActivityForResult(i, SELECT_USERNAME);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_USERNAME) {
			switch (resultCode) {
			case RESULT_OK:
				Intent i = new Intent();
				i.putExtra(ExtraNames.SELECTED_USER, data.getExtras().getString(ExtraNames.SELECTED_USER));
				i.putExtra(ExtraNames.PRODUCTS, mOrder);
				setResult(RESULT_OK,i);
				finish();
				break;
			case RESULT_CANCELED:
				break;
			}

		}
	}
	
}
