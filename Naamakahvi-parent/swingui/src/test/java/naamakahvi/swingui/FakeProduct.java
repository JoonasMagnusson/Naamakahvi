package naamakahvi.swingui;

import naamakahvi.naamakahviclient.IProduct;

public class FakeProduct implements IProduct{
	private String name;
	private double price;
	private int id, group;
	private boolean buyable;
	
	public FakeProduct(String name, int group, double price,
			int id, boolean buyable){
		this.name = name;
		this.group = group;
		this.price = price;
		this.id = id;
		this.buyable = buyable;
	}
	
	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public int getId() {
		return id;
	}

	public int getProductGroup() {
		return group;
	}

	public boolean isBuyable() {
		return buyable;
	}

	public int getSizeId() {
		return 0;
	}
	
}