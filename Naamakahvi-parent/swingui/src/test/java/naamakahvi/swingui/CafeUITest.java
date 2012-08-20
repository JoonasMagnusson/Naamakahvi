package naamakahvi.swingui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import naamakahvi.naamakahviclient.IProduct;

import org.fest.swing.core.matcher.JButtonMatcher;
import org.fest.swing.core.matcher.JLabelMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import static org.junit.Assert.*;

public class CafeUITest extends FestSwingJUnitTestCase{
	private FrameFixture window;
	private CafeUI master;
	private DummyClient cli;

	@Override
	protected void onSetUp() {
		master = GuiActionRunner.execute(new GuiQuery<CafeUI>(){
			protected CafeUI executeInEDT(){
				CafeUI ui = new CafeUI(0, 15, false, true, "999.999.999.999", -1);
				cli = new DummyClient();
				ui.createStore(cli);
				return ui;
			}
		});
		window = new FrameFixture(robot(), master);
		
	}
	@Override
	protected void onTearDown() {
		master.dispose();
	}
	
	@Test
	public void setsProperIP(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals("999.999.999.999",master.ADDRESS_IP);
				assertEquals(-1, master.ADDRESS_PORT);
			}
		});
	}
	
	@Test
	public void startsOnFrontPage(){
		window.show(new Dimension(640, 480));
		window.panel("front").requireVisible();
	}
	
	@Test
	public void switchesToContinueLocation(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.continueLocation = CafeUI.VIEW_ADD_PICTURE_PAGE;
				master.switchPage(CafeUI.CONTINUE);
				assertEquals(CafeUI.VIEW_ADD_PICTURE_PAGE,master.getCurrentLocation());
			}
		});
		window.panel("add").requireVisible();
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.switchPage(CafeUI.CONTINUE);
				assertEquals(CafeUI.VIEW_FRONT_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("front").requireVisible();
	}
	
	@Test
	public void switchesToFrontPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.switchPage(CafeUI.VIEW_ADD_PICTURE_PAGE);
				master.switchPage(CafeUI.VIEW_FRONT_PAGE);
				assertEquals(CafeUI.VIEW_FRONT_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("front").requireVisible();
	}
	
	@Test
	public void switchesToMenuPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				master.switchPage(CafeUI.VIEW_MENU_PAGE);
				assertEquals(CafeUI.VIEW_MENU_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("menu").requireVisible();
		window.label(JLabelMatcher.withText("testUN")).requireVisible();
	}
	
	@Test
	public void switchesToRegistrationPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.switchPage(CafeUI.VIEW_REGISTRATION_PAGE);
				assertEquals(CafeUI.VIEW_REGISTRATION_PAGE,master.getCurrentLocation());
			}
		});
		window.panel("register").requireVisible();
	}
	
	@Test
	public void switchesToBuyCartPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				master.switchPage(CafeUI.VIEW_BUY_LIST_PAGE);
				assertEquals(CafeUI.VIEW_BUY_LIST_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("buycart").requireVisible();
		window.label(JLabelMatcher.withText("Buying:")).requireVisible();
		window.label(JLabelMatcher.withText("testUN")).requireVisible();
	}
	
	@Test
	public void switchesToBringCartPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				master.switchPage(CafeUI.VIEW_BRING_LIST_PAGE);
				assertEquals(CafeUI.VIEW_BRING_LIST_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("bringcart").requireVisible();
		window.label(JLabelMatcher.withText("testUN")).requireVisible();
	}
	
	@Test
	public void switchesToCheckoutPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				master.setPurchaseMode(CafeUI.MODE_BUY);
				IProduct[] prods = new IProduct[2];
				prods[0] = new FakeProduct("prod1", 0, 0, 0, true);
				prods[1] = new FakeProduct("prod2", 0, 0, 0, true);
				int[] amounts = new int[2];
				amounts[0] = 1;
				amounts[1] = 2;
				master.selectProduct(prods, amounts);
				master.switchPage(CafeUI.VIEW_CHECKOUT_PAGE);
				assertEquals(CafeUI.VIEW_CHECKOUT_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("checkout").requireVisible();
		window.label(JLabelMatcher.withText("testUN")).requireVisible();
		window.label(JLabelMatcher.withText("prod1 x 1")).requireVisible();
		window.label(JLabelMatcher.withText("prod2 x 2")).requireVisible();
	}
	
	@Test
	public void switchesToFaceLoginPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
				assertEquals(CafeUI.VIEW_FACE_LOGIN_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("facelogin").requireVisible();
	}
	
	@Test
	public void switchesToUserListPage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.switchPage(CafeUI.VIEW_USERLIST_PAGE);
				assertEquals(CafeUI.VIEW_USERLIST_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("userlist").requireVisible();
		for (int i = 0; i < 10; i++){
			window.button(JButtonMatcher.withText("user"+i)).requireVisible();
		}
	}
	
	@Test
	public void switchesToAddPicturePage(){
		window.show(new Dimension(640, 480));
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				master.switchPage(CafeUI.VIEW_ADD_PICTURE_PAGE);
				assertEquals(CafeUI.VIEW_ADD_PICTURE_PAGE, master.getCurrentLocation());
			}
		});
		window.panel("add").requireVisible();
		window.label(JLabelMatcher.withText("testUN")).requireVisible();
	}
	
	@Test
	public void altersPurchaseMode(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.setPurchaseMode(CafeUI.MODE_BUY);
				assertEquals(CafeUI.MODE_BUY, master.getPurchaseMode());
				master.setPurchaseMode(CafeUI.MODE_BRING);
				assertEquals(CafeUI.MODE_BRING, master.getPurchaseMode());
				try{
					master.setPurchaseMode("this should not work");
					fail();
				}
				catch (IllegalArgumentException e){
					//pass test if exception is thrown
				}
			}
		});
	}
	
	@Test
	public void givesNewCanvas(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertNotNull(master.getCanvas());
			}
		});
	}
	
	@Test
	public void buysProduct(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				FakeProduct prod = new FakeProduct("prod", 0, 0, 0, true);
				master.setPurchaseMode(CafeUI.MODE_BUY);
				master.buyProduct(prod, 3);
				assertEquals("testUN bought 3x prod", cli.purchase);
			}
		});
	}
	
	@Test
	public void bringsProduct(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				FakeProduct prod = new FakeProduct("prod", 0, 0, 0, false);
				master.setPurchaseMode(CafeUI.MODE_BRING);
				master.buyProduct(prod, 3);
				assertEquals("testUN brought 3x prod", cli.purchase);
			}
		});
	}
	
	@Test
	public void failsPurchaseWithNoPurchaseMode(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				FakeProduct prod = new FakeProduct("prod", 0, 0, 0, true);
				try{
					master.buyProduct(prod, 3);
					fail();
				}
				catch (RuntimeException e){
					//Pass test if exception is thrown
				}
			}
		});
	}
	
	@Test
	public void registersUser(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.registerUser("testUN", "testFN", "testLN", new BufferedImage[1]);
				assertEquals("testUN", cli.registeredUN);
				assertEquals("testFN", cli.registeredFN);
				assertEquals("testLN", cli.registeredLN);
			}
		});
	}
	
	@Test
	public void LogsInUser(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				master.loginUser("testUN");
				assertEquals("testUN", cli.loggedIn);
			}
		});
	}
	
	@Test
	public void getsCorrectBuyableProducts(){
		List<IProduct> l1, l2;
		l1 = GuiActionRunner.execute(new GuiQuery<List<IProduct>>(){
			protected List<IProduct> executeInEDT(){
				return master.getBuyableProducts();
			}
		});
		l2 = cli.listBuyableProducts();
		for (int i = 0; i < l2.size(); i++){
			assertEquals(l2.get(i).getName(), l1.get(i).getName());
		}
	}
	
	@Test
	public void getsCorrectRawProducts(){
		List<IProduct> l1, l2;
		l1 = GuiActionRunner.execute(new GuiQuery<List<IProduct>>(){
			protected List<IProduct> executeInEDT(){
				return master.getRawProducts();
			}
		});
		l2 = cli.listRawProducts();
		for (int i = 0; i < l2.size(); i++){
			assertEquals(l2.get(i).getName(), l1.get(i).getName());
		}
	}
	
	@Test
	public void returnsNullPicWithNoCamera(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(null, master.takePic());
			}
		});
	}
	
	@Test
	public void imageValidationFailsWithNoCamera(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				assertEquals(false, master.validateImage());
			}
		});
	}
	
	@Test
	public void resizesImageToProperSize(){
		GuiActionRunner.execute(new GuiTask(){
			protected void executeInEDT(){
				BufferedImage i1 = new BufferedImage(500,
						500, BufferedImage.TYPE_4BYTE_ABGR);
				BufferedImage i2 = master.resizeImage(i1);
				assertEquals(200, i2.getHeight());
				assertEquals(200, i2.getWidth());
			}
		});
	}
}
