package naamakahvi.naamakahviclient;

import java.util.List;

class User implements IUser {
    private String username;
    private String givenName;
    private String familyName;
    private List<SaldoItem> balance;

    User(String username, String givenName, String familyName, List<SaldoItem> balance) {
        this.username = username;
        this.givenName = givenName;
        this.familyName = familyName;
        this.balance = balance;
    }

    public String getUserName() {
        return this.username;
    }

    @Override
    public String toString() {
        return this.username;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public List<SaldoItem> getBalance() {
        return balance;
    }
}
