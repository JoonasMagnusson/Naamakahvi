package naamakahvi.naamakahviclient;

class Product implements IProduct {

    private String name;
    private double price;
    private int id;
    private String productGroup;

    Product(int id, String name, double price, String productGroup) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productGroup = productGroup;
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

	public int getId(){
	    return id;
	}

	@Override
    public String toString() {
        return this.name;
    }
    
    @Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof IProduct)){
			return false;
		}
		IProduct p2 = (IProduct) arg0;
		return this.getName() == p2.getName();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

    
}