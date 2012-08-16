package naamakahvi.swingui;

import java.awt.Dimension;

import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import static org.junit.Assert.*;

public class AddPicturePageTest extends FestSwingJUnitTestCase {
	private DummyCafeUI master;
	private FrameFixture panel;
	private AddPicturePage addpic;

	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				addpic = new AddPicturePage(ui);
				ui.add(addpic);
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
	public void displaysUsers(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				addpic.setUsers(names);
			}
		});
		
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).requireVisible();
		}
	}
	
	@Test
	public void showsDefaultHelp(){
		panel.label(JLabelMatcher.withText(AddPicturePage.DEFAULT_HELP)).requireVisible();
	}
	
	@Test
	public void changesHelpText(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				addpic.setHelpText("This is a test String");
			}
		});
		panel.label(JLabelMatcher.withText("This is a test String")).requireVisible();
	}
	
	@Test
	public void takesPictures(){
		panel.button(JButtonMatcher.withText("Take Picture")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(true, master.pictureTaken);
			}
		});
	}
	
	@Test
	public void preventsOKWhenNoPicturesTaken(){
		panel.button(JButtonMatcher.withText("Add Pictures")).click();
		panel.label(JLabelMatcher.withText(
				"You must take pictures before adding them to your account")
				).requireVisible();
	}
	
	@Test
	public void cancelButtonWorks(){
		panel.button(JButtonMatcher.withText("Back to Menu")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_MENU_PAGE, master.currentLocation);
			}
		});
	}

}
