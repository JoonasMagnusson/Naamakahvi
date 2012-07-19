package naamakahvi.android;

import naamakahvi.android.R;
import naamakahvi.android.utils.ExtraNames;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginwithusernameActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginwithusername);
        
        ListView userlistView = (ListView) findViewById(R.id.userListView);
        // t�h�n string-taulukkoon importataan clientist� k�ytt�j�lista Varautuminen: ei k�ytt�ji� / ei yhteytt�
        String[] testiKayttajat = new String[] {"aapeli", "kahvikonkari", "moikkaaja", "testi"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            	android.R.layout.simple_list_item_1, android.R.id.text1, testiKayttajat);
        userlistView.setAdapter(adapter);
        
        userlistView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        		int position, long id) {
        		String item = (String) parent.getAdapter().getItem(position);
        		Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
        		Intent i = new Intent();
        		i.putExtra(ExtraNames.SELECTED_USER, item);
        		setResult(RESULT_OK,i);
    			finish();
        	}
        }); 
	}
}
