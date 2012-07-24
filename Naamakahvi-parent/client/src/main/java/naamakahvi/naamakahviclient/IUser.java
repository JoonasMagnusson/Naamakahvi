package naamakahvi.naamakahviclient;

import java.util.List;

public interface IUser {
    String getUserName();
    String getGivenName();
    String getFamilyName();
    List<SaldoItem> getSaldos() throws ClientException;
}
