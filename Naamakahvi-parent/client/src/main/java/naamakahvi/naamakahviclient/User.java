package naamakahvi.naamakahviclient;

class User implements IUser {
    private String username;
    private String givenName;
    private String familyName;

    User(String username, String givenName, String familyName, ImageData imagedata) {
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
    
    

    /*
     * // Varsinainen osto ja tuonti tapahtuvat näillä metodeilla public void
     * osta(tuote, määrä) throws JokuException {
     *
     * }
     *
     * public void tuo(tuote, määrä) throws JokuException {
     *
     * }
     */

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }
}