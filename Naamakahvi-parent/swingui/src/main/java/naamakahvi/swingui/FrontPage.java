package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;

/*
 * Käyttöliittymän aloitussivu
 */
public class FrontPage extends JPanel implements ActionListener{
	private JButton register, manlogin, buycart, bringcart;
	private JButton[][] buyprodButtons, bringprodButtons;
	private IProduct[] buyproducts, bringproducts;
	private JLabel helpText;
	private JPanel buyProdView, bringProdView;
	private CafeUI master;
	private String defaulthelp = "Select a product from below to start face recognition";
	
	public FrontPage(CafeUI master){
		this.master = master;
		FlowLayout layout = new FlowLayout();
		//layout.setHgap(0);
		//layout.setVgap(0);
		setLayout(layout);
		
		helpText = new JLabel(defaulthelp, SwingConstants.CENTER);
		helpText.setFont(master.UI_FONT);
		helpText.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
				master.Y_RES/10 - layout.getVgap()));
		add(helpText);
		
		/*
		canvas = master.getCanvas();
		canvas.setPreferredSize(new Dimension(master.X_RES/3, master.Y_RES/3));
		add(canvas);*/
		/*
		actionPanel = new JPanel();
		actionPanel.setPreferredSize(new Dimension(master.X_RES/3*2-20, master.Y_RES/5*3));
		add(actionPanel);*/
		
		buyProdView = new JPanel();
		buyProdView.setLayout(new GridBagLayout());
		buyProdView.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/10*7 - layout.getVgap()));
		add(buyProdView);
		
		bringProdView = new JPanel();
		bringProdView.setLayout(new GridBagLayout());
		bringProdView.setPreferredSize(new Dimension(master.X_RES/2 - layout.getHgap(),
				master.Y_RES/10*7 - layout.getVgap()));
		add(bringProdView);
		
		getProducts();
		manlogin = new JButton("Manual Login");
		manlogin.setFont(master.UI_FONT_BIG);
		manlogin.addActionListener(this);
		manlogin.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
				master.Y_RES/10 - layout.getVgap()));
		
		register = new JButton("New User");
		register.setFont(master.UI_FONT_BIG);
		register.addActionListener(this);
		register.setPreferredSize(new Dimension(master.X_RES - layout.getHgap(),
				master.Y_RES/10 - layout.getVgap()));
		
		add(manlogin);
		add(register);
	}
	/*
	protected void resetPage(){
		canvas.activate();
	}*/
	
	private void getProducts(){
		
		List<IProduct> tempbuy = master.getBuyableProducts();
		List<IProduct> tempbring = master.getRawProducts();
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		GridBagLayout buyLayout = (GridBagLayout)buyProdView.getLayout();
		GridBagLayout bringLayout = (GridBagLayout)bringProdView.getLayout();
		
		buyProdView.removeAll();
		bringProdView.removeAll();
		
		JLabel buyheader = new JLabel("Buy Products:");
		buyheader.setFont(master.UI_FONT);
		buyLayout.setConstraints(buyheader, c);
		buyProdView.add(buyheader);
		
		JLabel bringheader = new JLabel("Bring Products:");
		bringheader.setFont(master.UI_FONT);
		bringLayout.setConstraints(bringheader, c);
		bringProdView.add(bringheader);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		buycart = new JButton("Buy Multiple Products");
		buycart.setFont(master.UI_FONT_SMALL);
		buycart.addActionListener(this);
		buyLayout.setConstraints(buycart, c);
		buyProdView.add(buycart);
		
		bringcart = new JButton("Bring Multiple Products");
		bringcart.setFont(master.UI_FONT_SMALL);
		bringcart.addActionListener(this);
		bringLayout.setConstraints(bringcart, c);
		bringProdView.add(bringcart);
		
		buyproducts = new IProduct[tempbuy.size()];
		buyprodButtons = new JButton[tempbuy.size()][5];
		tempbuy.toArray(buyproducts);
		
		bringproducts = new IProduct[tempbring.size()];
		bringprodButtons = new JButton[tempbring.size()][5];
		tempbring.toArray(bringproducts);
		
		for (int i = 0; i < buyproducts.length; i++){
			c.gridwidth = 1;
			
			JLabel prodname = new JLabel(buyproducts[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			buyLayout.setConstraints(prodname, c);
			buyProdView.add(prodname);
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			buyLayout.setConstraints(prodline, c);
			buyProdView.add(prodline);
			
			for (int j = 0; j < 5; j++){
				buyprodButtons[i][j] = new JButton("" + (j+1));
				buyprodButtons[i][j].setFont(master.UI_FONT_SMALL);
				buyprodButtons[i][j].addActionListener(this);
				prodline.add(buyprodButtons[i][j]);
			}
		}
		
		buyProdView.revalidate();
		
		for (int i = 0; i <bringproducts.length; i++){
			c.gridwidth = 1;
			
			JLabel prodname = new JLabel(bringproducts[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			bringLayout.setConstraints(prodname, c);
			bringProdView.add(prodname);
			
			c.gridwidth = GridBagConstraints.REMAINDER;
			
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			bringLayout.setConstraints(prodline, c);
			bringProdView.add(prodline);
			
			for (int j = 0; j < 5; j++){
				bringprodButtons[i][j] = new JButton("" + (j+1));
				bringprodButtons[i][j].setFont(master.UI_FONT_SMALL);
				bringprodButtons[i][j].addActionListener(this);
				prodline.add(bringprodButtons[i][j]);
			}
		}
		bringProdView.revalidate();
	}
	
	protected void setHelpText(String text){
		helpText.setText(text);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == manlogin){
			//canvas.deactivate();
			setHelpText(defaulthelp);
			master.continueLocation = CafeUI.VIEW_MENU_PAGE;
			master.switchPage(CafeUI.VIEW_USERLIST_PAGE);
		}
		if (s == register){
			//canvas.deactivate();
			setHelpText(defaulthelp);
			master.switchPage(CafeUI.VIEW_REGISTRATION_PAGE);
		}
		if (s == buycart){
			//canvas.deactivate();
			setHelpText(defaulthelp);
			master.setContinueLocation(CafeUI.VIEW_BUY_LIST_PAGE);
			master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
		}
		if (s == bringcart){
			//canvas.deactivate();
			setHelpText(defaulthelp);
			master.setContinueLocation(CafeUI.VIEW_BRING_LIST_PAGE);
			master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
		}
		for (int i = 0; i < buyprodButtons.length; i++){
			for (int j = 0; j < buyprodButtons[i].length; j++){
				if (s == buyprodButtons[i][j]){
					//canvas.deactivate();
					setHelpText(defaulthelp);
					IProduct[] prods = new IProduct[1];
					prods[0] = buyproducts[i];
					int[] amounts = new int[1];
					amounts[0] = j+1;
					master.setPurchaseMode(CafeUI.MODE_BUY);
					master.selectProduct(prods, amounts);
					master.setContinueLocation(CafeUI.VIEW_CHECKOUT_PAGE);
					master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
				}
			}
		}
		for (int i = 0; i < bringprodButtons.length; i++){
			for (int j = 0; j < bringprodButtons[i].length; j++){
				if (s == bringprodButtons[i][j]){
					//canvas.deactivate();
					setHelpText(defaulthelp);
					IProduct[] prods = new IProduct[1];
					prods[0] = bringproducts[i];
					int[] amounts = new int[1];
					amounts[0] = j+1;
					master.setPurchaseMode(CafeUI.MODE_BRING);
					master.selectProduct(prods, amounts);
					master.setContinueLocation(CafeUI.VIEW_CHECKOUT_PAGE);
					master.switchPage(CafeUI.VIEW_FACE_LOGIN_PAGE);
				}
			}
		}
	}
}