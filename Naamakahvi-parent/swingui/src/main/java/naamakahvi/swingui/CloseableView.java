package naamakahvi.swingui;

/**An interface indicating that the implementing class is a UI view with
 * resources that should be disposed of before its internal components attempt
 * to change the state of the program.
 * 
 * @author Antti Hietasaari
 *
 */
public interface CloseableView {
	/**
	 * Closes the view, freeing any volatile resources associated with it.
	 */
	public abstract void closeView();
}
