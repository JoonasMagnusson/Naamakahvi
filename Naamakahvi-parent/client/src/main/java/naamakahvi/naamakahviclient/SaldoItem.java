package naamakahvi.naamakahviclient;

public class SaldoItem {
    private String groupName;
    private double saldo;

    SaldoItem(String groupName, double saldo) {
        this.groupName = groupName;
        this.saldo = saldo;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public double getSaldo() {
        return this.saldo;
    }
}

