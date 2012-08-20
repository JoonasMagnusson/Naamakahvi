package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.core.matcher.JLabelMatcher;

public class RegistrationPageTest extends FestSwingJUnitTestCase {

	private DummyCafeUI master;
	private FrameFixture panel;
	private RegistrationPage reg;
	
	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				reg = new RegistrationPage(ui);
				ui.add(reg);
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
	public void helpTextVisible(){
		panel.label(JLabelMatcher.withText(RegistrationPage.DEFAULT_HELP)).requireVisible();
	}
	
	@Test
	public void canSetHelpText(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				reg.setHelpText("This is a test String");
			}
		});
		panel.label(JLabelMatcher.withText("This is a test String")).requireVisible();
	}
	
	@Test
	public void canvasesVisible(){
		panel.panel("canvas").requireVisible();
		for (int i = 0; i < RegistrationPage.MAX_THUMBCOUNT; i++){
			panel.panel("thumb"+i).requireVisible();
		}
	}
	
	@Test
	public void takePicWorks(){
		panel.button(JButtonMatcher.withText("Take Picture")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(true, master.pictureTaken);
			}
		});
		panel.label(JLabelMatcher.withText("No faces detected")).requireVisible();
	}
	
	@Test
	public void cancelButtonWorks(){
		panel.button(JButtonMatcher.withText("Cancel")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_FRONT_PAGE, master.getCurrentLocation());
			}
		});
	}
	
	@Test
	public void registersUser(){
		panel.textBox("un").enterText("UserName");
		panel.textBox("fn").enterText("FirstName");
		panel.textBox("ln").enterText("LastName");
		panel.button(JButtonMatcher.withText("Register")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals("UserName", master.selectedUserUN);
				assertEquals("FirstName", master.selectedUserFN);
				assertEquals("LastName", master.selectedUserLN);
			}
		});
	}

}
