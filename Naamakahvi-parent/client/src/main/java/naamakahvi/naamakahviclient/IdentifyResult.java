package naamakahvi.naamakahviclient;

public class IdentifyResult {
    
    public final String username;
    public final boolean goodmatch;
    
    public IdentifyResult(String username, boolean goodmatch) {
        this.username = username;
        this.goodmatch = goodmatch;
    }
}

