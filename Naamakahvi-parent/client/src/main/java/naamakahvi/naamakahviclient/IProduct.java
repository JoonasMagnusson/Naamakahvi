package naamakahvi.naamakahviclient;

public interface IProduct {
    String getName();
    double getPrice();
    int getId();
    int getProductGroup();
    boolean isBuyable();
    int getSizeId();
}