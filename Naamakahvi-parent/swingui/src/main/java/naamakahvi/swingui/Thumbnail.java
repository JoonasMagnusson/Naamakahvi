package naamakahvi.swingui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
/**A Swing component that shows and scales an image.
 * Note that the component doesn't preserve aspect ratio, but rather
 * assumes that the image is square.
 * 
 * @author Antti Hietasaari
 *
 */
public class Thumbnail extends JPanel{
	private BufferedImage img;
	/**
	 * Creates a new thumbnail
	 */
	public Thumbnail(){
		super();
	}
	/**Sets the image shown by this thumbnail.
	 * 
	 * @param img	A BufferedImage representing the image to be drawn.
	 */
	public void drawImage(BufferedImage img){
		this.img = img;
		repaint();
	}
	@Override
	public void paint(Graphics g){
		int size = Math.min(this.getWidth(), this.getHeight());
		BufferedImage resize = new BufferedImage(size,
				size, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = resize.createGraphics();
		g2.drawImage(img, 0, 0, size, size, null);
		g2.dispose();
		g.drawImage(resize, 0, 0, null);
	}
	@Override
	public void update(Graphics g){
		paint(g);
	}
}
