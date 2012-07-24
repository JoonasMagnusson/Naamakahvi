package naamakahvi.naamakahviclient;

class BuyableProduct implements IProduct {

    private int id;
    private String name;
    private double price;
    private double size;


    BuyableProduct(int id, String name, double price, double size) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public double getSize() {
        return this.size;
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