package naamakahvi.swingui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import naamakahvi.naamakahviclient.IProduct;

import java.util.List;
import java.util.ArrayList;

/*
 * Kälin erikoistoimintosivu
 */
public class PurchaseCartPage extends JPanel implements ActionListener{
	private JButton cancelButton, confirmButton, clearButton;
	
	private JButton[] buyButtons, bringButtons;
	private IProduct[] buyProducts, bringProducts;
	
	private ArrayList<IProduct> buyCartContents, bringCartContents;
	private ArrayList<Integer> buyAmounts, bringAmounts;
	private JLabel buyHeader, bringHeader;
	private ArrayList<JLabel> buyCartLabels, bringCartLabels;
	
	private JPanel cartPanel, buyMenuView, bringMenuView, productPanel,
		buyCartView, bringCartView;
	private JScrollPane cartBuyScroll, cartBringScroll, menuBuyScroll, menuBringScroll;
	private CafeUI master;
	
	public PurchaseCartPage(CafeUI master){
		this.master = master;
		
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(master.UI_FONT_BIG);
		cancelButton.addActionListener(this);
		cancelButton.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/8));
		
		confirmButton = new JButton("Buy selected products");
		confirmButton.setFont(master.UI_FONT_BIG);
		confirmButton.addActionListener(this);
		confirmButton.setPreferredSize(new Dimension(master.X_RES/2-10, master.Y_RES/8));
		
		//Ostoskori
		cartPanel = new JPanel();
		cartPanel.setPreferredSize(new Dimension(master.X_RES/3-10, master.Y_RES/8*6+10));
		
		clearButton = new JButton("Clear cart");
		clearButton.setFont(master.UI_FONT);
		clearButton.addActionListener(this);
		clearButton.setPreferredSize(new Dimension(master.X_RES/3-10, master.Y_RES/40*3));
		cartPanel.add(clearButton);
		
		buyCartView = new JPanel();
		buyCartView.setLayout(new GridLayout(0,1));
		cartBuyScroll = new JScrollPane(buyCartView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cartBuyScroll.setPreferredSize(new Dimension(master.X_RES/3-10, master.Y_RES/80*28));
		cartPanel.add(cartBuyScroll);
		
		buyHeader = new JLabel("Buying:");
		buyHeader.setFont(master.UI_FONT);
		
		bringCartView = new JPanel();
		bringCartView.setLayout(new GridLayout(0,1));
		cartBringScroll = new JScrollPane(bringCartView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cartBringScroll.setPreferredSize(new Dimension(master.X_RES/3-10, master.Y_RES/80*28));
		cartPanel.add(cartBringScroll);
		
		bringHeader = new JLabel("Bringing:");
		bringHeader.setFont(master.UI_FONT);
		
		clearCart();
		
		//Tuotelista
		productPanel = new JPanel();
		productPanel.setPreferredSize(new Dimension(master.X_RES/3*2-10, master.Y_RES/8*6+10));
		
		buyMenuView = new JPanel();
		buyMenuView.setLayout(new GridLayout(0, 2));
		menuBuyScroll = new JScrollPane(buyMenuView, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		menuBuyScroll.setPreferredSize(new Dimension(master.X_RES/3*2-10, master.Y_RES/8*3));
		productPanel.add(menuBuyScroll);
		
		bringMenuView = new JPanel();
		bringMenuView.setLayout(new GridLayout(0, 2));
		menuBringScroll = new JScrollPane(bringMenuView, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		menuBringScroll.setPreferredSize(new Dimension(master.X_RES/3*2-10, master.Y_RES/8*3));
		productPanel.add(menuBringScroll);
		
		//väliaikainen, testausta varten
		/*
		buyButtons = new JButton[1000];
		for (int i = 0; i < 1000; i++){
			buyButtons[i] = initProductButton("test", buyMenuView);
		}
		*/
		loadProducts();
		
		add(cartPanel);
		add(productPanel);
		add(confirmButton);
		add(cancelButton);
	}
	
	protected void clearCart(){
		buyCartView.removeAll();
		bringCartView.removeAll();
		buyCartView.add(buyHeader);
		bringCartView.add(bringHeader);

		buyCartContents = new ArrayList<IProduct>();
		bringCartContents = new ArrayList<IProduct>();
		buyAmounts = new ArrayList<Integer>();
		bringAmounts = new ArrayList<Integer>();
		buyCartLabels = new ArrayList<JLabel>();
		bringCartLabels = new ArrayList<JLabel>();
		
		buyCartView.revalidate();
		bringCartView.revalidate();
	}
	
	protected void loadProducts(){
		
		List<IProduct> buylist = master.getBuyableProducts();
		List<IProduct> bringlist = master.getRawProducts();
		if (buylist == null || bringlist == null){
			return;
		}

		buyMenuView.removeAll();
		bringMenuView.removeAll();
		
		buyProducts = new IProduct[buylist.size()];
		buylist.toArray(buyProducts);
		
		bringProducts = new IProduct[bringlist.size()];
		bringlist.toArray(bringProducts);
		
		
		buyButtons = new JButton[buyProducts.length];
		for (int i = 0; i < buyProducts.length; i++){
			initProductButton(buyProducts[i].getName(), buyMenuView);
		}
		
		bringButtons = new JButton[bringProducts.length];
		for (int i = 0; i < bringProducts.length; i++){
			initProductButton(bringProducts[i].getName(), bringMenuView);
		}
	}
	
	private JButton initProductButton(String name, JPanel panel){
		JButton button = new JButton(name);
		button.setFont(master.UI_FONT);
		panel.add(button);
		button.addActionListener(this);
		return button;
	}

	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if (s == cancelButton){
			master.switchPage(CafeUI.VIEW_MENU_PAGE);
		}
		if (s == clearButton){
			clearCart();
		}
		if (s == confirmButton){
			for (int i = 0; i< buyCartContents.size(); i++){
				master.buyProduct(buyCartContents.get(i), buyAmounts.get(i));
			}
			for (int i = 0; i< bringCartContents.size(); i++){
				master.buyProduct(bringCartContents.get(i), bringAmounts.get(i));
			}
			master.switchPage(CafeUI.VIEW_FRONT_PAGE);
		}
		for (int i = 0; i < buyButtons.length; i++){
			if (s == buyButtons[i]){
				JLabel label = new JLabel("" + i);
				buyCartLabels.add(label);
				buyCartView.add(label);
				buyCartView.revalidate();
			}
		}/*
		for (int i = 0; i < bringButtons.length; i++){
			if (s == bringButtons[i]){
				confirmButton.setText("" +i);
			}
		}*/
	}

}
