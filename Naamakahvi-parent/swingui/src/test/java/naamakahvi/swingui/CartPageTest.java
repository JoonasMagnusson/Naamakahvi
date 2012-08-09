package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.regex.Pattern;

import org.fest.swing.edt.*;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class CartPageTest extends FestSwingJUnitTestCase {

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
	public void displaysUsers(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				cart.setUsers(names);
			}
		});
		
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).requireVisible();
		}
	}
	
	@Test
	public void addsToCart(){
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.label(JLabelMatcher.withText("BuyProd0 x 1")).requireVisible();
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.label(JLabelMatcher.withText("BuyProd0 x 2")).requireVisible();
	}
	
	@Test
	public void addsMultipleProdsToCart(){
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.label(JLabelMatcher.withText("BuyProd0 x 1")).requireVisible();
		panel.button(JButtonMatcher.withText("BuyProd1: 1.5")).click();
		panel.label(JLabelMatcher.withText("BuyProd1 x 1")).requireVisible();
	}
	
	@Test
	public void decrementsProduct(){
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.label(JLabelMatcher.withText("BuyProd0 x 2")).requireVisible();
		panel.button("remove_BuyProd0").click();
		panel.label(JLabelMatcher.withText("BuyProd0 x 1")).requireVisible();
	}
	
	@Test
	public void removesProduct(){
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd0 x .*"))).requireVisible();
		panel.button("remove_BuyProd0").click();
		try {
			panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd0 x .*")));
			fail();
		}
		catch (ComponentLookupException e){
			//Pass test if the product is no longer in the cart
		}
	}
	
	@Test
	public void clearsCart(){
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd0: 0.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd1: 1.5")).click();
		panel.button(JButtonMatcher.withText("BuyProd2: 2.5")).click();
		panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd0 x .*"))).requireVisible();
		panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd1 x .*"))).requireVisible();
		panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd2 x .*"))).requireVisible();
		panel.button(JButtonMatcher.withText("Clear Cart")).click();
		int i = 0; //Prevent compiler from optimizing try/catch blocks
		try {
			panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd0 x .*")));
			fail();
		}
		catch (ComponentLookupException e){
			//Pass test if the product is no longer in the cart
			i = 1;
		}
		try {
			panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd1 x .*")));
			fail();
		}
		catch (ComponentLookupException e){
			//Pass test if the product is no longer in the cart
			i = 2;
		}
		i++;
		try {
			panel.label(JLabelMatcher.withText(Pattern.compile("BuyProd2 x .*")));
			fail();
		}
		catch (ComponentLookupException e){
			//Pass test if the product is no longer in the cart
			i = 3;
		}
		System.out.println(i);
	}
	
	@Test
	public void cancelButtonWorks(){
		panel.button(JButtonMatcher.withText("Cancel")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
					assertEquals(CafeUI.VIEW_MENU_PAGE, master.currentLocation);
			}
		});
	}

}
