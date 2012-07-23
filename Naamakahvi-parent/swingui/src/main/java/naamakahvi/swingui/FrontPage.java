package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;
import naamakahvi.swingui.FaceCapture.FaceCanvas;

/*
 * Käyttöliittymän aloitussivu
 */
public class FrontPage extends JPanel implements ActionListener{
	private JButton register, manlogin, cart;
	private JButton[][] prodButtons;
	private IProduct[] products;
	private JLabel quantity, helpText;
	private JPanel prodView, actionPanel;
	private JSpinner spinner;
	private CafeUI master;
	private FaceCanvas canvas;
	
	public FrontPage(CafeUI master){
		this.master = master;
		
		helpText = new JLabel();
		helpText.setPreferredSize(new Dimension(master.X_RES-20, master.Y_RES/12));
		add(helpText);
		
		canvas = master.getCanvas();
		canvas.setPreferredSize(new Dimension(master.X_RES/3, master.Y_RES/3));
		add(canvas);
		
		actionPanel = new JPanel();
		actionPanel.setPreferredSize(new Dimension(master.X_RES/3*2-20, master.Y_RES/5*3));
		add(actionPanel);
		/*
		quantity = new JLabel("Product quantity:");
		quantity.setFont(master.UI_FONT_BIG);
				
		spinner = new JSpinner(new SpinnerNumberModel(1, -99, 99, 1));
		spinner.setFont(master.UI_FONT_BIG);
		
		actionPanel.add(quantity);
		actionPanel.add(spinner);
		*/
		prodView = new JPanel();
		prodView.setLayout(new GridLayout(0, 2));
		prodView.setPreferredSize(new Dimension(master.X_RES/3*2-20, master.Y_RES/2));
		actionPanel.add(prodView);
		/*
		prodButtons = new JButton[3];
		prodButtons[0] = new JButton("Coffee");
		prodButtons[1] = new JButton("Espresso");
		prodButtons[2] = new JButton("Double Espresso");
		for (int i = 0; i < prodButtons.length; i++){
			prodButtons[i].setFont(CafeUI.UI_FONT);
			prodButtons[i].addActionListener(this);
			prodView.add(prodButtons[i]);
		}*/
		
		getProducts();
		manlogin = new JButton("Manual Login");
		manlogin.setFont(master.UI_FONT);
		manlogin.addActionListener(this);
		manlogin.setPreferredSize(new Dimension(master.X_RES - 20, master.Y_RES/12));
		
		register = new JButton("New User");
		register.setFont(master.UI_FONT);
		register.addActionListener(this);
		register.setPreferredSize(new Dimension(master.X_RES - 20, master.Y_RES/12));
		
		add(manlogin);
		add(register);
	}
	
	protected void resetPage(){
		//spinner.setValue(1);
		canvas.activate();
	}
	
	private void getProducts(){
		
		List<IProduct> temp = master.getDefaultProducts();
		
		if (temp == null || temp.size() < 1){
			return;
			//throw new IllegalArgumentException("No default products received from server");
		}
		
		prodView.removeAll();
		
		JLabel header = new JLabel("Quick Buy");
		header.setFont(master.UI_FONT);
		
		
		products = new IProduct[temp.size()];
		prodButtons = new JButton[temp.size()][5];
		temp.toArray(products);
		prodView.removeAll();
		//TODO: korjaa layout
		for (int i = 0; i < products.length; i++){

			JLabel prodname = new JLabel(products[i].getName());
			prodname.setFont(master.UI_FONT_SMALL);
			prodView.add(prodname);
			
			JPanel prodline = new JPanel();
			prodline.setLayout(new GridLayout(1,0));
			prodView.add(prodline);
			
			
			for (int j = 0; j < 5; j++){
				prodButtons[i][j] = new JButton("" + (j+1));
				prodButtons[i][j].setFont(master.UI_FONT);
				prodButtons[i][j].addActionListener(this);
				prodline.add(prodButtons[i][j]);
			}
		}
		cart = new JButton("Shopping Cart View");
		cart.setFont(master.UI_FONT);
		cart.addActionListener(this);
		prodView.add(cart);
		
		prodView.revalidate();
	
	}
	
	protected void setHelpText(String text){
		helpText.setText(text);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == manlogin){
			canvas.deactivate();
			master.continueLocation = CafeUI.VIEW_MENU_PAGE;
			master.switchPage(CafeUI.VIEW_USERLIST_PAGE);
		}
		if (s == register){
			canvas.deactivate();
			master.switchPage(CafeUI.VIEW_REGISTRATION_PAGE);
		}
		if (s == cart){
			canvas.deactivate();
			master.setContinueLocation(CafeUI.VIEW_PROD_LIST_PAGE);
			master.validateImage();
		}
		for (int i = 0; i < prodButtons.length; i++){
			for (int j = 0; j < prodButtons[i].length; j++){
				if (s == prodButtons[i][j]){
					canvas.deactivate();
					master.selectProduct(products[i], j+1);
					master.setContinueLocation(CafeUI.VIEW_CHECKOUT_PAGE);
					if (!master.validateImage()){
						canvas.activate();
					}	
				}
			}
		}
	}
}