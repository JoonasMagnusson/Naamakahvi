package naamakahvi.naamakahviclient;

class Product implements IProduct {

    private String name;

    Product(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
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