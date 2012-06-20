/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package naamakahvi.naamakahviclient;

/**
 *
 * @author jronkone
 */
public class ClientException extends Exception {

    private String reason;

    ClientException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}

class AuthenticationException extends ClientException {

    AuthenticationException(String reason) {
        super(reason);
    }
}

class RegistrationException extends ClientException {

    RegistrationException(String reason) {
        super(reason);
    }
}
