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

}