package naamakahvi.android.test;

import junit.framework.TestCase;
import naamakahvi.android.R;
import naamakahvi.android.MainActivity;
import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;

public class AndroidUiTester extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;

	public AndroidUiTester() {
		super(MainActivity.class);
	}
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testPreferenceIsSaved() throws Exception {
		solo.assertCurrentActivity("wrong activiy", MainActivity.class);
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
