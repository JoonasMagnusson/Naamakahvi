package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class FaceLoginPageTest extends FestSwingJUnitTestCase {

	private DummyCafeUI master;
	private FrameFixture panel;
	private FaceLoginPage login;

	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				login = new FaceLoginPage(ui);
				ui.add(login);
				return ui;
				
			}
		});
		panel = new FrameFixture(robot(), master);
		panel.show(new Dimension(640, 480));

	}
	
	@Override
	public void onTearDown(){
		master.dispose();
	}
	
	@Test
	public void canvasIsVisible(){
		panel.panel("canvas").requireVisible();
	}
	
	@Test
	public void helpTextShown(){
		panel.label(JLabelMatcher.withText(FaceLoginPage.DEFAULT_HELP)).requireVisible();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				login.setHelpText("This is a test String");
			}
		});
		panel.label(JLabelMatcher.withText("This is a test String")).requireVisible();
	}
	
	@Test
	public void cancelButtonWorks(){
		panel.button(JButtonMatcher.withText("Cancel")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_FRONT_PAGE, master.currentLocation);
				assertEquals(CafeUI.VIEW_FRONT_PAGE, master.continueLocation);
			}
		});
	}
	
	@Test
	public void okButtonWorks(){
		panel.button(JButtonMatcher.withText("Take Picture")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(true, master.pictureTaken);
			}
		});
	}
}
