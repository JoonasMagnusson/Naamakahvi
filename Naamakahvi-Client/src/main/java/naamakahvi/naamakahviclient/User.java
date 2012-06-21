package naamakahvi.naamakahviclient;

class User implements IUser {
    private String username;

    User(String username, ImageData imagedata) {
        this.username = username;
    }

    public String getUserName() {
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
}