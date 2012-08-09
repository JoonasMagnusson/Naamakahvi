package naamakahvi.swingui;

import static org.junit.Assert.*;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.fest.swing.edt.*;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.*;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.fest.swing.core.Robot;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.core.matcher.JButtonMatcher;

public class CheckoutPageTest extends FestSwingJUnitTestCase{
	private DummyCafeUI master;
	private FrameFixture panel;
	private CheckoutPage checkout;

	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<DummyCafeUI>() {
			protected DummyCafeUI executeInEDT() {
				DummyCafeUI ui = new DummyCafeUI();
				checkout = new CheckoutPage(ui);
				ui.add(checkout);
				
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
	public void timerStarts(){
		assertEquals(false, checkout.countingDown);
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				checkout.startCountdown();
				assertEquals(true, checkout.countingDown);
			}
		});
		
	}
	
	@Test
	public void timerStops(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				checkout.startCountdown();
				assertEquals(true, checkout.countingDown);
				checkout.closeView();
				assertEquals(false, checkout.countingDown);
			}
		});
		
	}
	
	@Test
	public void displaysUsernames(){
		
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				String[] names = new String[5];
				for (int i =0; i < 5; i++){
					names[i] = "user" + i;
				}
				checkout.setUsers(names);
			}
		});
		
		for (int i = 0; i < 5; i++){
			panel.button(JButtonMatcher.withText("user"+i)).requireVisible();
		}
	}
	
	@Test
	public void displayProducts(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				int[] amounts = new int[5];
				for (int i =0; i < 5; i++){
					amounts[i] = i+1;
				}
				checkout.setProducts(master.generateProducts(), amounts, CafeUI.MODE_BUY);
			}
		});
		for(int i = 0; i < 5; i++){
			panel.label(JLabelMatcher.withText("prod"+i+ " x " + (i+1))).requireVisible();
		}
	}
	
	@Test
	public void displaysHelpText(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				checkout.setHelpText("This text is for testing purposes");
			}
		});
		panel.label(JLabelMatcher.withText("This text is for testing purposes")).requireVisible();
	}
	
	@Test
	public void cancelButtonWorks(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				checkout.startCountdown();
			}
		});
		panel.button(JButtonMatcher.withText("Cancel")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(false, checkout.countingDown);
				assertEquals(CafeUI.VIEW_FRONT_PAGE, master.currentLocation);
			}
		});
	}
	
	@Test
	public void okButtonWorks(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				int[] amounts = new int[5];
				for (int i =0; i < 5; i++){
					amounts[i] = i+1;
				}
				checkout.setProducts(master.generateProducts(), amounts, CafeUI.MODE_BUY);
			}
		});
		panel.button(JButtonMatcher.withText("OK")).click();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(5, master.boughtprods.size());
				assertEquals(5, master.boughtamounts.size());
				for (int i=0; i < 5; i++){
					assertEquals("prod" + i, master.boughtprods.get(i).getName());
					assertEquals(i+1, master.boughtamounts.get(i).intValue());
				}
			}
		});
	}
	
}