package naamakahvi.naamakahviclient;

public class RawProduct implements IProduct {

    private String name;
    private double price;
    private int id;

    public boolean isBuyable(){
    	return false;
    }
    
    RawProduct(String name, double price, int id) {
        this.name = name;
        this.price = price;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String toString(){
    	return "Raw"+this.id;    	
    }

}