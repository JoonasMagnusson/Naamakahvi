package naamakahvi.swingui;

import java.util.List;

import naamakahvi.naamakahviclient.IUser;
import naamakahvi.naamakahviclient.SaldoItem;

public class FakeUser implements IUser{
	private String un, fn, ln;
	
	public FakeUser(String un){
		this.un = un;
		this.fn = un + "FN";
		this.ln = un + "LN";
	}

	public String getUserName() {
		return un;
	}

	public String getGivenName() {
		return fn;
	}

	public String getFamilyName() {
		return ln;
	}

	public List<SaldoItem> getBalance() {
		return null;
	}
	
}