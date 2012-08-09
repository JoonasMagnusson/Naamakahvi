package naamakahvi.naamakahviclient;

public class SaldoItem {
    private String groupName;
    private int groupId;
    private double saldo;

    SaldoItem(String groupName, int groupId, double saldo) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.saldo = saldo;
    }

    public String getGroupName() {
        return this.groupName;
    }
    
    public int getGroupId() {
        return this.groupId;
    }

    public double getSaldo() {
        return this.saldo;
    }
}

