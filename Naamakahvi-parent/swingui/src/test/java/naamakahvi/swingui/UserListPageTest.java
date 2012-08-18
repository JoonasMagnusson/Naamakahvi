package naamakahvi.swingui;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class UserListPageTest extends FestSwingJUnitTestCase {
	private DummyCafeUI master;
	private FrameFixture panel;
	private UserListPage list;

	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				list = new UserListPage(ui);
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				list.listUsers(names);
				ui.add(list);
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
	public void showsUsers(){
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).requireVisible();
		}
	}
	
	@Test
	public void showsHelpText(){
		panel.label(JLabelMatcher.withText("Please select your username from the list below:")).requireVisible();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				list.setHelpText("This is a test String");
			}
		});
		panel.label(JLabelMatcher.withText("This is a test String")).requireVisible();
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
	public void selectsUsers(){
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).click();
			String s = GuiActionRunner.execute(new GuiQuery<String>(){
				protected String executeInEDT(){
					return master.selectedUserUN;
				}
			});
			assertEquals("user"+i, s);
			GuiActionRunner.execute(new GuiTask(){
				protected void executeInEDT(){
					assertEquals(CafeUI.CONTINUE, master.getCurrentLocation());
				}
			});
		}
	}

}
