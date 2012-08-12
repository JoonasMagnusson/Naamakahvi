package naamakahvi.android.test;

import junit.framework.TestCase;
import naamakahvi.android.NewUserActivity;
import naamakahvi.android.R;
import naamakahvi.android.MainActivity;
import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.EditText;

public class AndroidUiTester extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;

	public AndroidUiTester() {
		super(MainActivity.class);
	}
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testIfRightActivity() throws Exception {
		solo.assertCurrentActivity("wrong activiy", MainActivity.class);
	}
	
	public void testPressingButtons() {
		solo.assertCurrentActivity("Activity not main activity 1", MainActivity.class);
		solo.clickOnButton("New user");
		solo.assertCurrentActivity("Activity not new user activity", NewUserActivity.class);
		solo.goBack();
		solo.assertCurrentActivity("Activity not main activity 2", MainActivity.class);
		
		solo.clickOnButton("Other order");
		solo.assertCurrentActivity("Activity not main activity 3", MainActivity.class);
		solo.clickOnButton("Other payment...");
		solo.assertCurrentActivity("Activity not main activity 4", MainActivity.class);
	}
	
	public void testNewUserCreation() {
		solo.assertCurrentActivity("Activity not main activity", MainActivity.class);
		solo.clickOnButton("New user");
		solo.enterText(0, "plaa0");
		solo.enterText(1, "plaa1");
		solo.enterText(2, "plaa2");
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
}
