package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class MenuPageTest extends FestSwingJUnitTestCase {

	private DummyCafeUI master;
	private FrameFixture panel;
	private MenuPage menu;
	
	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				menu = new MenuPage(ui);
				ui.add(menu);
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
				menu.setUser(names);
			}
		});
		
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).requireVisible();
		}
	}
	
	@Test
	public void showsCorrectBuyableProducts() {
		for (int i = 0; i < 5; i++){
			panel.label(JLabelMatcher.withText("BuyProd" + i)).requireVisible();
		}
	}
	
	@Test
	public void startsBuyCart() {
		panel.button(JButtonMatcher.withText("Buy Multiple Products")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_BUY_LIST_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void startsBringCart() {
		panel.button(JButtonMatcher.withText("Bring Products")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_BRING_LIST_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void startsAddImage() {
		panel.button(JButtonMatcher.withText("Add Images to Account")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_ADD_PICTURE_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void logsOut() {
		panel.button(JButtonMatcher.withText("Log Out")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(CafeUI.VIEW_FRONT_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void selectsBuyProduct(){
		for (int i = 0; i < 5; i++){
			for (int j = 0; j < 5; j++){
				panel.button(JButtonMatcher.withName("buy" +i + ":" + j)).click();
				String s = GuiActionRunner.execute(new GuiQuery<String>(){
					protected String executeInEDT(){
						return master.prods[0].getName();
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
				assertEquals(1,(int)a[0]);
				assertEquals("BuyProd" + i, s);
				assertEquals(1, (int)a[1]);
				assertEquals(j+1, (int)a[2]);
				GuiActionRunner.execute(new GuiTask(){
					protected void executeInEDT(){
						assertEquals(CafeUI.VIEW_CHECKOUT_PAGE, master.currentLocation);
						assertEquals(CafeUI.MODE_BUY, master.purchaseMode);
					}
				});
			}
		}
	}
}
