package naamakahvi.swingui;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class FrontPageTest extends FestSwingJUnitTestCase{
	private DummyCafeUI master;
	private FrameFixture panel;
	private FrontPage front;

	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				front = new FrontPage(ui);
				ui.add(front);
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
	public void showsCorrectBuyableProducts() {
		for (int i = 0; i < 5; i++){
			panel.label(JLabelMatcher.withText("BuyProd" + i)).requireVisible();
		}
	}
	
	@Test
	public void showsHelpText() {
		panel.label(JLabelMatcher.withText("Select a product from below to start face recognition")).requireVisible();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				front.setHelpText("This is a test string");
			}
		});
		panel.label(JLabelMatcher.withText("This is a test string")).requireVisible();
	}
	
	@Test
	public void startsManualLogin() {
		panel.button(JButtonMatcher.withText("Manual Login")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_MENU_PAGE, master.continueLocation);
				assertEquals(CafeUI.VIEW_USERLIST_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void startsRegistration() {
		panel.button(JButtonMatcher.withText("New User")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_REGISTRATION_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void startsBuyCartLogin() {
		panel.button(JButtonMatcher.withText("Buy Multiple Products")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_BUY_LIST_PAGE, master.continueLocation);
				assertEquals(CafeUI.VIEW_FACE_LOGIN_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void startsBringCartLogin() {
		panel.button(JButtonMatcher.withText("Bring Products")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_BRING_LIST_PAGE, master.continueLocation);
				assertEquals(CafeUI.VIEW_FACE_LOGIN_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void selectsBuyProduct(){
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 5; j++){
				panel.button(JButtonMatcher.withName("buy" +i + ":" + j)).click();
				String[] s = GuiActionRunner.execute(new GuiQuery<String[]>(){
					protected String[] executeInEDT(){
						String[] s = new String[2];
						s[0] = master.purchaseMode;
						s[1] = master.prods[0].getName();
						return s;
					}
				});
				Integer[] a = GuiActionRunner.execute(new GuiQuery<Integer[]>(){
					protected Integer[] executeInEDT(){
						Integer[] a = new Integer[3];
						a[0] = master.prods.length;
						a[1] = master.amounts.length;
						a[2] = master.amounts[0];
						return a;
					}
				});
				assertEquals(CafeUI.MODE_BUY, s[0]);
				assertEquals(1, (int)a[0]);
				assertEquals("BuyProd" + i, s[1]);
				assertEquals(1, (int)a[1]);
				assertEquals(j+1, (int)a[2]);
				GuiActionRunner.execute(new GuiTask(){
					protected void executeInEDT(){
						assertEquals(CafeUI.VIEW_CHECKOUT_PAGE, master.continueLocation);
						assertEquals(CafeUI.VIEW_FACE_LOGIN_PAGE, master.currentLocation);
					}
				});
			}
		}
	}
}
