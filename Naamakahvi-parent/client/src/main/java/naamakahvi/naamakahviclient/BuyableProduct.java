package naamakahvi.naamakahviclient;

class BuyableProduct implements IProduct {

    private int id;
    private String name;
    private double price;

    
    public boolean isBuyable(){
    	return true;
    }
    
    BuyableProduct(int id, String name, double price) {
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
        return "Buyable"+this.id;
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
		return this.toString() == p2.toString();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

    
}