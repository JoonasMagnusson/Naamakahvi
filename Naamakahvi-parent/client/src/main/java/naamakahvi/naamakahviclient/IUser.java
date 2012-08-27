package naamakahvi.naamakahviclient;

import java.util.List;

/**
 * Interface for users.
 */

public interface IUser {
    String getUserName();
    String getGivenName();
    String getFamilyName();
    List<SaldoItem> getBalance();
}
