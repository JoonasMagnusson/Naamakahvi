package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class ShortListTest extends FestSwingJUnitTestCase {
	private DummyCafeUI master;
	private FrameFixture panel;
	private ShortList list;
	
	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				list = new ShortList(ui, ui);
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
	public void displaysCorrectUsernames(){
		
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				list.setUsers(names);
			}
		});
		
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).requireVisible();
		}
	}
	
	@Test
	public void displaysSelectedUsername(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				list.setUsers(names);
			}
		});
		panel.label(JLabelMatcher.withText("user0")).requireVisible();
	}
	
	@Test
	public void userListButtonWorks(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				list.setUsers(names);
			}
		});
		panel.button(JButtonMatcher.withText("User List")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(true, master.closed);
				assertEquals("initial", master.continueLocation);
				assertEquals(CafeUI.VIEW_USERLIST_PAGE, master.getCurrentLocation());
			}
		});
	}
	
	@Test
	public void switchingUserWorks(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				list.setUsers(names);
				assertEquals(false, master.closed);
			}
		});
		for ( int i = 0; i< 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).click();
			String s = GuiActionRunner.execute(new GuiQuery<String>(){
				protected String executeInEDT(){
					return master.selectedUserUN;
				}
			});
			assertEquals("user"+i, s);
		}
		
	}

}
