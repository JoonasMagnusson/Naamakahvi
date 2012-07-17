package naamakahvi.naamakahviclient;

class Product implements IProduct {

    private String name;
    private double price;

    Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
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