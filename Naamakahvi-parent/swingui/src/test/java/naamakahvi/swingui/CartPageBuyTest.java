package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JButtonMatcher;

public class CartPageBuyTest extends FestSwingJUnitTestCase {
	
	private DummyCafeUI master;
	private FrameFixture panel;
	private PurchaseCartPage cart;
	
	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				cart = new PurchaseCartPage(ui, CafeUI.MODE_BUY);
				ui.add(cart);
				return ui;
				
			}
		});
		panel = new FrameFixture(robot(), master);
		panel.show(new Dimension(640,480));

	}
	
	@Override
	public void onTearDown(){
		master.dispose();
	}
	
	@Test
	public void showsProducts(){
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("BuyProd"+i+": "+(i+0.5))).requireVisible();
		}
	}
	
	@Test
	public void confirmButtonWorks(){
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd1: 1.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd1: 1.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd2: 2.5")).click();
		panel.button(JButtonMatcher.withText("Buy Selected Products")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(3, master.prods.length);
				assertEquals(3, master.amounts.length);
				for (int i = 0; i < 3; i++){
					assertEquals("BuyProd"+i, master.prods[i].getName());
					assertEquals(3-i, master.amounts[i]);
				}
				assertEquals(CafeUI.MODE_BUY, master.purchaseMode);
				assertEquals(CafeUI.VIEW_CHECKOUT_PAGE, master.currentLocation);
			}
		});
	}

}
