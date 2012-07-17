package naamakahvi.naamakahviclient;

class User implements IUser {
    private String username;
    private String givenName;
    private String familyName;

    User(String username, String givenName, String familyName) {
        this.username = username;
        this.givenName = givenName;
        this.familyName = familyName;
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
}