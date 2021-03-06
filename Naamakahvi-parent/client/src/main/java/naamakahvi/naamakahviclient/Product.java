package naamakahvi.naamakahviclient;

class Product implements IProduct {
    private final String name;
    private final double price;
    private final int id;
    private final int sizeId;
    private final int productGroup;
    private final boolean buyable;

    Product(int id, String name, double price, boolean buyable,
            int productGroup, int sizeId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.productGroup = productGroup;
        this.buyable = buyable;
        this.sizeId = sizeId;
    }

    public String getName() {
        return Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
    }

    public double getPrice() {
        return this.price;
    }

    public int getProductGroup() {
        return this.productGroup;
    }

    public int getId() {
        return id;
    }

    public int getSizeId() {
        return sizeId;
    }

    public boolean isBuyable() {
        return buyable;
    }

    @Override
    public String toString() {
        return this.sizeId + (buyable ? "Buyable" : "Raw") + this.id;
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