package naamakahvi.android;

import naamakahvi.android.R;
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
        // tähän string-taulukkoon importataan clientistä käyttäjälista Varautuminen: ei käyttäjiä / ei yhteyttä
        String[] testiKayttajat = new String[] {"aapeli", "kahvikonkari", "moikkaaja", "testi"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            	android.R.layout.simple_list_item_1, android.R.id.text1, testiKayttajat);
        userlistView.setAdapter(adapter);
        
        userlistView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        		int position, long id) {
        		String item = (String) parent.getAdapter().getItem(position);
        		Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
        		
        		setResult(RESULT_OK);
    			finish();
        	}
        }); 
	}
}
