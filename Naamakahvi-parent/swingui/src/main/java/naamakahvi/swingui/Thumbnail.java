package naamakahvi.swingui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Thumbnail extends JPanel{
	private BufferedImage img;
	
	public Thumbnail(){
		super();
	}
	
	public void drawImage(BufferedImage img){
		this.img = img;
		repaint();
	}
	
	public void paint(Graphics g){
		int size = Math.min(this.getWidth(), this.getHeight());
		BufferedImage resize = new BufferedImage(size,
				size, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = resize.createGraphics();
		g2.drawImage(img, 0, 0, size, size, null);
		g2.dispose();
		g.drawImage(resize, 0, 0, null);
	}
	
	public void update(Graphics g){
		paint(g);
	}
}
