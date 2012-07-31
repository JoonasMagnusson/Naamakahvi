package naamakahvi.naamakahviclient;

class Product implements IProduct {

	private final String name;
	private final double price;
	private final int id;
	private final String productGroup;
	private final boolean buyable;

	Product(int id, String name, double price, boolean buyable,
			String productGroup) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.productGroup = productGroup;
		this.buyable = buyable;
	}

	public String getName() {
		return this.name;
	}

	public double getPrice() {
		return this.price;
	}

	public String getProductGroup() {
		return this.productGroup;
	}

	public int getId() {
		return id;
	}
	
	public boolean isBuyable(){
		return buyable;
	}

	@Override
	public String toString() {
		return (buyable?"Buyable":"Raw")+this.id;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof IProduct)) {
			return false;
		}
		IProduct p2 = (IProduct) arg0;
		return this.toString().equals(p2.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}