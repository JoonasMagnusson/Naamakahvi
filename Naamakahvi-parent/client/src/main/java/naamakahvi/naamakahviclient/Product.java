package naamakahvi.naamakahviclient;

class Product implements IProduct {

    private String name;
    private double price;
    private int id;

    Product(int id, String name, double price) {
        this.id = id;
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
    
    public int getId(){
        return id;
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