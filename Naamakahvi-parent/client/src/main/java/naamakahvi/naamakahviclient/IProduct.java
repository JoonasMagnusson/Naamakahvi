package naamakahvi.naamakahviclient;

/**
 * Interface for buyable and raw products.
 */

public interface IProduct {
    String getName();
    double getPrice();
    int getId();
    int getProductGroup();
    boolean isBuyable();
    int getSizeId();
}