import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class CustomPanel extends javax.swing.JPanel {
	private static final long	serialVersionUID	= -8678338602911250069L;
	private Image src = null;	// ???
	private Image src_origSize = null;
	private int					_WIDTH;
	private int					_HEIGHT;

	public CustomPanel(int w, int h) {
		this.setPreferredSize(new Dimension(Math.min(600, w), Math.min(400, h)));
		this._WIDTH = w;
		this._HEIGHT = h;
	}

	public void setSeiz(int w, int h) {
		this._WIDTH = w;
		this._HEIGHT = h;
	}

	public void render(int[] mem) {
		
		int w, h;
		double f;
		double wf = this._WIDTH;
		double hf = this._HEIGHT;
		if (this._WIDTH > 600) {
			f = 600.0 / this._WIDTH;
			wf = wf * f;
			hf = hf * f;
		}
		if (this._HEIGHT > 400) {
			f = 400.0 / this._HEIGHT;
			wf = wf * f;
			hf = hf * f;
		}
		w = (int) Math.round(wf);
		h = (int) Math.round(hf);
		
		final Image img = this.createImage(new MemoryImageSource(w, h, mem, 0, this._WIDTH));
		
		final Image img_orig = this.createImage(new MemoryImageSource(this._WIDTH, this._HEIGHT, mem, 0, this._WIDTH));
		
/*
		final BufferedImage bi = new BufferedImage(this._WIDTH, this._HEIGHT, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = bi.createGraphics();
		g2.drawImage(img, 0, 0, null);
		g2.dispose();
		this.publish(bi);
 */

		
		this.src = img;
		this.src_origSize = img_orig;
		this.repaint();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (this.src != null)
			g.drawImage(this.src, 0, 0, this);
	}
	
	public void  saveToDisk() {
		try {
			BufferedImage buffi = null;
			if (this.src_origSize instanceof BufferedImage)
				buffi = (BufferedImage) this.src_origSize;
			else {
				buffi = new BufferedImage(this.src_origSize.getWidth(null), this.src_origSize.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics2D bGr = buffi.createGraphics();
				bGr.drawImage(this.src_origSize, 0, 0, null);
				bGr.dispose();
				File outputFile = new File("randomImage_" + new SimpleDateFormat("yyyy_MM_dd HH_mm_ss").format(new Date()) + ".png");
				ImageIO.write(buffi, "png", outputFile);
				JOptionPane.showMessageDialog(null, "Created new file successfully");
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "FAIL: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}














