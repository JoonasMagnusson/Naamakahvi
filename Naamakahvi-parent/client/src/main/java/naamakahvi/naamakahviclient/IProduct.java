package naamakahvi.naamakahviclient;

public interface IProduct {
    String getName();
    double getPrice();
    int getId();
    String getProductGroup();
    boolean isBuyable();
}