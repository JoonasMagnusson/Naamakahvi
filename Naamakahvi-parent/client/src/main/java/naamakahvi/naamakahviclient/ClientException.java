package naamakahvi.naamakahviclient;

public class ClientException extends Exception {

    private String reason;

    ClientException(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
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

class GeneralClientException extends ClientException {
        public GeneralClientException(String s) {
            super(s);
        }
        
}