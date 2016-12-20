package de.phil294.caterwoler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Deprecated extends Canvas {

	private static final long	serialVersionUID		= 7287164612273529048L;
	private static final int	WIDTH1				= 400;
	private static final int	HEIGHT1				= 400;

	// -------------------
	private static final int	EINSTIEGSPUNKTE		= 1;
	private static final float	FARBTONFAKTOR		= 1.000f;
	private static final float SAETTIGUNGSFAKTOR = 0.975f;
	private static final float HELLIGKEITSFAKTOR = 1.010f;

	private static final float	RANDOMIZER			= 0.000f;
	// -------------------

	private static final Random	random	= new Random();

/*
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		for (int x = 0; x < WIDTH; x += RECTANGLE_SIDELENGTH) {
			for (int y = 0; y < HEIGHT; y += RECTANGLE_SIDELENGTH) {
				g.setColor(this.randomColor());
				for (int i = 0; i < RECTANGLE_SIDELENGTH; i++) {
					for (int j = 0; j < RECTANGLE_SIDELENGTH; j++) {
						g.drawLine(x, y, x, y);
						//JOptionPane.showMessageDialog(0, x + ", " + y);
						y++;
					}
					y -= RECTANGLE_SIDELENGTH;
					x++;
				}
				x -= RECTANGLE_SIDELENGTH;
			}
		}
	}
 */
	@Override
	public void paint(final Graphics g) {
		super.paint(g);

		final Color[][] colors = new Color[WIDTH1][HEIGHT1];
		Color a1, a2, a3, b1, b3, c1, c2, c3;

		//for (int x = 0; x < WIDTH1; x++) {
		//	for (int y = 0; y < HEIGHT1; y++) {

		//g.setColor(this.randomColor());

		int x, y, c = -1, d = 0;
		/* loop, 50000 */for (int i9451 = 0; i9451 < (WIDTH1 * HEIGHT1 * 1000); i9451++) {
			x = random.nextInt(WIDTH1);
			y = random.nextInt(HEIGHT1);
			if (colors[x][y] != null) {
				c++;
				if (c > (WIDTH1 * HEIGHT1 * 10))
					break;
				continue;
			}
			try {
				a1 = colors[x - 1][y - 1];
			} catch (final Exception e) {
				a1 = new Color(0, 0, 0);
			}
			if (a1 == null)
				a1 = new Color(0, 0, 0);
			try {
				a2 = colors[x - 1][y];
			} catch (final Exception e) {
				a2 = new Color(0, 0, 0);
			}
			if (a2 == null)
				a2 = new Color(0, 0, 0);
			try {
				a3 = colors[x - 1][y + 1];
			} catch (final Exception e) {
				a3 = new Color(0, 0, 0);
			}
			if (a3 == null)
				a3 = new Color(0, 0, 0);
			try {
				b1 = colors[x][y - 1];
			} catch (final Exception e) {
				b1 = new Color(0, 0, 0);
			}
			if (b1 == null)
				b1 = new Color(0, 0, 0);
			try {
				b3 = colors[x][y + 1];
			} catch (final Exception e) {
				b3 = new Color(0, 0, 0);
			}
			if (b3 == null)
				b3 = new Color(0, 0, 0);
			try {
				c1 = colors[x + 1][y - 1];
			} catch (final Exception e) {
				c1 = new Color(0, 0, 0);
			}
			if (c1 == null)
				c1 = new Color(0, 0, 0);
			try {
				c2 = colors[x + 1][y];
			} catch (final Exception e) {
				c2 = new Color(0, 0, 0);
			}
			if (c2 == null)
				c2 = new Color(0, 0, 0);
			try {
				c3 = colors[x + 1][y + 1];
			} catch (final Exception e) {
				c3 = new Color(0, 0, 0);
			}
			if (c3 == null)
				c3 = new Color(0, 0, 0);

			// has to be at least one pixel set next to it! (apart from very first one)
			if ((-16777216 == a1.getRGB()) && (-16777216 == a2.getRGB()) && (-16777216 == a3.getRGB()) && (-16777216 == b1.getRGB()) && (-16777216 == b3.getRGB()) && (-16777216 == c1.getRGB()) && (-16777216 == c2.getRGB()) && (-16777216 == c3.getRGB())) {
				if (d >= EINSTIEGSPUNKTE)
					continue;
				d++;
			}
			c = 0;

			final Color newColor = calculatedRandomColor(a1, a2, a3, b1, b3, c1, c2, c3);

			g.setColor(newColor);
			colors[x][y] = newColor;

			g.drawLine(x, y, x, y);

		}

		//	}
		//}
	}

	private static Color randomColor() {
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	private static Color calculatedRandomColor(final Color a1, final Color a2, final Color a3, final Color b1, final Color b3, final Color c1, final Color c2, final Color c3) {
		int i = 8;
		if (a1.getRGB() == -16777216)
			i--;
		if (a2.getRGB() == -16777216)
			i--;
		if (a3.getRGB() == -16777216)
			i--;
		if (b1.getRGB() == -16777216)
			i--;
		if (b3.getRGB() == -16777216)
			i--;
		if (c1.getRGB() == -16777216)
			i--;
		if (c2.getRGB() == -16777216)
			i--;
		if (c3.getRGB() == -16777216)
			i--;
		float blue_average, red_average, green_average;
		final Color randColor = randomColor();
		try {
			final float randBlue = randColor.getBlue() * RANDOMIZER;
			final float abcBlue = a1.getBlue() + a2.getBlue() + a3.getBlue() + b1.getBlue() + b3.getBlue() + c1.getBlue() + c2.getBlue() + c3.getBlue();
			final float denom = i + RANDOMIZER;
			blue_average = (randBlue + abcBlue) / denom;
		} catch (final Exception e) {
			blue_average = random.nextInt(256);
			System.out.println(123);
		}
		if (Float.isNaN(blue_average))
			blue_average = random.nextInt(256);
		try {
			red_average = ((randColor.getRed() * RANDOMIZER) + a1.getRed() + a2.getRed() + a3.getRed() + b1.getRed() + b3.getRed() + c1.getRed() + c2.getRed() + c3.getRed()) / (i + RANDOMIZER);
		} catch (final Exception e) {
			red_average = random.nextInt(256);
		}
		if (Float.isNaN(red_average))
			red_average = random.nextInt(256);
		try {
			green_average = ((randColor.getGreen() * RANDOMIZER) + a1.getGreen() + a2.getGreen() + a3.getGreen() + b1.getGreen() + b3.getGreen() + c1.getGreen() + c2.getGreen() + c3.getGreen()) / (i + RANDOMIZER);
		} catch (final Exception e) {
			green_average = random.nextInt(256);
		}
		if (Float.isNaN(green_average))
			green_average = random.nextInt(256);
		//System.out.println(i + ", " + blue_average + ", " + red_average + ", " + green_average);

		final Color c = new Color(Math.round(red_average), Math.round(green_average), Math.round(blue_average));
		//c.get
		final float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		//System.out.println(c.getRed() + "|" + c.getGreen() + "|" + c.getBlue());
		//System.out.println(hsb[0] + "|" + hsb[1] + "|" + hsb[2]);
		//int rgb = Color.HSBtoRGB(hsb[0], random.nextFloat(), random.nextFloat());
		final int rgb = Color.HSBtoRGB(hsb[0] * FARBTONFAKTOR, hsb[1] * SAETTIGUNGSFAKTOR, hsb[2] * HELLIGKEITSFAKTOR);
		final Color ret = new Color(rgb);
		//System.out.println(hsb[0]);
		return ret;
	}

	public static void main(final String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(WIDTH1, HEIGHT1);
		final JPanel pane = (JPanel) frame.getContentPane();
		final Canvas canvas = new Deprecated();
		frame.add(canvas);

		frame.setVisible(true);

		final InputMap iMap = pane.getInputMap();
		final ActionMap aMap = pane.getActionMap();
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "R");
		aMap.put("R", new AbstractAction() {
			private static final long	serialVersionUID	= 3205299646057459152L;

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				frame.remove(canvas);
				frame.add(canvas);
			}
		});
	}
}


