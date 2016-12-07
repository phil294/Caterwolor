import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


public class GuiMain {

	private static int			_WIDTH;
	private static int			_HEIGHT;

	private static CustomPanel image_Panel = null;
	private static PixelWork image_pw = null;
	private static JFrame jf = null;

	public static void writeWH() {
		int w = (_WIDTH > 600) ? 600 : _WIDTH;
		int h = (_HEIGHT > 400) ? 400 : _HEIGHT;
		Options.storeWH(w, h);
	}

	public static void closeGui() {
		image_pw.savePixelWorkValues();
		writeWH();
		jf.dispose();
	}

	public static void startGui() {
		_WIDTH = Integer.parseInt((String) Options.getOptions().get("_WIDTH").value);
		_HEIGHT = Integer.parseInt((String) Options.getOptions().get("_HEIGHT").value);

		SwingUtilities.invokeLater(() -> {
			jf = new JFrame("Random Image Generator - 2015 eochgls@web.de");

			JPanel mainPanel = new JPanel(new FlowLayout());

			JPanel bigPictureOptions_Panel = new JPanel(new GridLayout(5, 1));
			JTextField resolution = new JTextField(_WIDTH + "x" + _HEIGHT);
			JButton generatePanel = new JButton("Generate new Random Image");
			generatePanel.addActionListener(e -> {
				if (generatePanel.getText().contains("CANCEL") || generatePanel.getText().contains("cancel")) {
					image_pw.stopExecution();
					Sleep.sleep(300);
					generatePanel.setText("Generate new Random Image");
					return;
				}
				String[] resxresy = resolution.getText().split("x");
				if ((_WIDTH != Integer.valueOf(resxresy[0])) || (_HEIGHT != Integer.valueOf(resxresy[1]))) {
					_WIDTH = Integer.valueOf(resxresy[0]);
					_HEIGHT = Integer.valueOf(resxresy[1]);
					image_Panel.setSeiz(_WIDTH, _HEIGHT);
					image_Panel.setSize(_WIDTH, _HEIGHT);
					jf.pack();
					jf.setVisible(true);
				}
				generatePanel.setText("CANCEL ACTION");
				new Thread(() -> {
					image_Panel.render(image_pw.recreateMem(_HEIGHT, _WIDTH));
					generatePanel.setText("Generate new Random Image");
				}).start();
			});
			JButton save = new JButton("save .png to disk...");
			save.addActionListener(e -> image_Panel.saveToDisk());
			int stretchFactor = 2;
			JButton stretch = new JButton("stretch * " + stretchFactor + " (useless)");
			stretch.addActionListener(e -> {
				_WIDTH *= stretchFactor;
				_HEIGHT *= stretchFactor;
				image_Panel.setSeiz(_WIDTH, _HEIGHT);
				image_Panel.setSize(_WIDTH, _HEIGHT);
				image_pw.stretch(stretchFactor);
				image_Panel.render(image_pw.getMem());
				resolution.setText(_WIDTH + "x" + _HEIGHT);
				jf.pack();
				jf.setVisible(true);
				JOptionPane.showMessageDialog(null, "done");
			});
			JButton loadImage = new JButton("load external image & replace black");
			loadImage.addActionListener(e -> {
				JFileChooser fileChooser = new JFileChooser(new File("."));
				int result;
				while ((result = fileChooser.showOpenDialog(jf)) != JFileChooser.APPROVE_OPTION)
					if(result == JFileChooser.CANCEL_OPTION)
						return;
				File selectedFile = fileChooser.getSelectedFile();
				BufferedImage image = null;
				try {
					image = ImageIO.read(selectedFile);
				} catch (Exception e1) {
					System.err.println(e1.getMessage());
					return;
				}
				//BufferedImage image = null;
				//try{ image = ImageIO.read(new File("z.png")); } catch(Exception e3) { System.err.println(e3.getMessage()); }

				_WIDTH = image.getWidth();
				_HEIGHT = image.getHeight();
				image_Panel.setSeiz(_WIDTH, _HEIGHT);
				image_Panel.setSize(_WIDTH, _HEIGHT);
				image_pw.createMemFromImage(image);
				image_Panel.render(image_pw.getMem());
				resolution.setText(_WIDTH + "x" + _HEIGHT);
				jf.pack();
				jf.setVisible(true);
			});
			bigPictureOptions_Panel.add(resolution);
			bigPictureOptions_Panel.add(generatePanel);
			bigPictureOptions_Panel.add(stretch);
			bigPictureOptions_Panel.add(save);
			bigPictureOptions_Panel.add(loadImage);


			image_Panel = new CustomPanel(_WIDTH, _HEIGHT);
			image_pw = new PixelWork(_WIDTH, _HEIGHT);
			image_Panel.render(image_pw.getMem());


			JPanel optionsPanel = new JPanel(new GridLayout(15, 1));

			CustomPanel preview_Panel = new CustomPanel(50, 50);
			PixelWork preview_pw = new PixelWork(50, 50);
			preview_Panel.render(preview_pw.getMem());

			JLabel label_startingPoints = new JLabel("Starting points amount:");
			JSlider slider_startingPoints = new JSlider(0, 10);
			slider_startingPoints.setMajorTickSpacing(1);
			slider_startingPoints.setSnapToTicks(true);
			slider_startingPoints.setPaintTicks(true);
			slider_startingPoints.setPaintLabels(true);
			slider_startingPoints.setValue(preview_pw.getSTARTING_POINTS_CNT());
			slider_startingPoints.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					preview_pw.setSTARTING_POINTS_CNT(slider_startingPoints.getValue());
					image_pw.setSTARTING_POINTS_CNT(slider_startingPoints.getValue());
					preview_Panel.render(preview_pw.recreateMem());
				}@Override public void mouseClicked(MouseEvent e) {}	@Override	public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override	public void mousePressed(MouseEvent e) {}
			});
			JLabel label_RectangleSidelength = new JLabel("Blurring rectangle sidelength:");
			JSlider slider_RectangleSidelength = new JSlider(1, 40);
			slider_RectangleSidelength.setValue(preview_pw.getRECTANGLE_SIDELENGTH());
			slider_RectangleSidelength.setPaintTicks(true);
			slider_RectangleSidelength.setPaintLabels(true);
			slider_RectangleSidelength.setMajorTickSpacing(10);
			slider_RectangleSidelength.setMinorTickSpacing(2);
			slider_RectangleSidelength.setSnapToTicks(true);
			slider_RectangleSidelength.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					preview_pw.setRECTANGLE_SIDELENGTH(slider_RectangleSidelength.getValue());
					image_pw.setRECTANGLE_SIDELENGTH(slider_RectangleSidelength.getValue());
					preview_Panel.render(preview_pw.recreateMem());
				}@Override public void mouseClicked(MouseEvent e) {}	@Override	public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override	public void mousePressed(MouseEvent e) {}
			});
			JLabel label_RandomizerFactor = new JLabel("Randomizer factor: (" + Math.round(preview_pw.getRANDOMIZER_FACTOR() * 1000) + "/1000)");
			JSlider slider_RandomizerFactor = new JSlider(0, 50);
			slider_RandomizerFactor.setValue((int) Math.round(preview_pw.getRANDOMIZER_FACTOR() * 1000));
			slider_RandomizerFactor.setPaintTicks(true);
			slider_RandomizerFactor.setPaintLabels(true);
			slider_RandomizerFactor.setMajorTickSpacing(10);
			slider_RandomizerFactor.setSnapToTicks(false);
			slider_RandomizerFactor.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					preview_pw.setRANDOMIZER_FACTOR(((double) slider_RandomizerFactor.getValue()) / 1000);
					image_pw.setRANDOMIZER_FACTOR(((double) slider_RandomizerFactor.getValue()) / 1000);
					label_RandomizerFactor.setText("Randomizer factor: (" + Math.round(preview_pw.getRANDOMIZER_FACTOR() * 1000) + "/1000)");
					preview_Panel.render(preview_pw.recreateMem());
				}@Override public void mouseClicked(MouseEvent e) {}	@Override	public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override	public void mousePressed(MouseEvent e) {}
			});
			JLabel label_Hue = new JLabel("Hue: (" + Math.round(preview_pw.getHUE_FACTOR() * 1000) + "/1000)");
			JSlider slider_Hue = new JSlider(750, 1250);
			slider_Hue.setValue(Math.round(preview_pw.getHUE_FACTOR() * 1000));
			slider_Hue.setPaintTicks(true);
			slider_Hue.setPaintLabels(true);
			slider_Hue.setMajorTickSpacing(100);
			slider_Hue.setSnapToTicks(false);
			slider_Hue.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					preview_pw.setHUE_FACTOR(((float) slider_Hue.getValue()) / 1000);
					image_pw.setHUE_FACTOR(((float) slider_Hue.getValue()) / 1000);
					label_Hue.setText("Hue: (" + Math.round(preview_pw.getHUE_FACTOR() * 1000) + "/1000)");
					preview_Panel.render(preview_pw.recreateMem());
				}@Override public void mouseClicked(MouseEvent e) {}	@Override	public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override	public void mousePressed(MouseEvent e) {}
			});
			JLabel label_Saturation = new JLabel("Saturation: (" + Math.round(preview_pw.getSATURATION_FACTOR() * 1000) + "/1000)");
			JSlider slider_Saturation = new JSlider(750, 1250);
			slider_Saturation.setValue(Math.round(preview_pw.getSATURATION_FACTOR() * 1000));
			slider_Saturation.setPaintTicks(true);
			slider_Saturation.setPaintLabels(true);
			slider_Saturation.setMajorTickSpacing(100);
			slider_Saturation.setSnapToTicks(false);
			slider_Saturation.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					preview_pw.setSATURATION_FACTOR(((float) slider_Saturation.getValue()) / 1000);
					image_pw.setSATURATION_FACTOR(((float) slider_Saturation.getValue()) / 1000);
					label_Saturation.setText("Saturation: (" + Math.round(preview_pw.getSATURATION_FACTOR() * 1000) + "/1000)");
					preview_Panel.render(preview_pw.recreateMem());
				}@Override public void mouseClicked(MouseEvent e) {}	@Override	public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override	public void mousePressed(MouseEvent e) {}
			});
			JLabel label_Brightness = new JLabel("Brightness: (" + Math.round(preview_pw.getBRIGHTNESS_FACTOR() * 1000) + "/1000)");
			JSlider slider_Brightness = new JSlider(750, 1250);
			slider_Brightness.setValue(Math.round(preview_pw.getBRIGHTNESS_FACTOR() * 1000));
			slider_Brightness.setPaintTicks(true);
			slider_Brightness.setPaintLabels(true);
			slider_Brightness.setMajorTickSpacing(100);
			slider_Brightness.setSnapToTicks(false);
			slider_Brightness.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					preview_pw.setBRIGHTNESS_FACTOR(((float) slider_Brightness.getValue()) / 1000);
					image_pw.setBRIGHTNESS_FACTOR(((float) slider_Brightness.getValue()) / 1000);
					label_Brightness.setText("Brightness: (" + Math.round(preview_pw.getBRIGHTNESS_FACTOR() * 1000) + "/1000)");
					preview_Panel.render(preview_pw.recreateMem());
				}@Override public void mouseClicked(MouseEvent e) {}	@Override	public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {} @Override	public void mousePressed(MouseEvent e) {}
			});
			JCheckBox overflow = new JCheckBox("Enable fancy pixel overflow", preview_pw.getENABLE_OVERFLOW());
			overflow.addActionListener(e -> {
				preview_pw.setENABLE_OVERFLOW(overflow.isSelected());
				image_pw.setENABLE_OVERFLOW(overflow.isSelected());
				preview_Panel.render(preview_pw.recreateMem());
			});

			JButton reset = new JButton("reset values");
			reset.addActionListener(e -> {
				preview_pw.setSTARTING_POINTS_CNT(Integer.valueOf((String) Options.getOptions().get("STARTING_POINTS_CNT").defaul));
				preview_pw.setRECTANGLE_SIDELENGTH(Integer.valueOf((String) Options.getOptions().get("RECTANGLE_SIDELENGTH").defaul));
				preview_pw.setRANDOMIZER_FACTOR(Double.valueOf((String) Options.getOptions().get("RANDOMIZER_FACTOR").defaul));
				preview_pw.setHUE_FACTOR(Float.valueOf((String) Options.getOptions().get("HUE_FACTOR").defaul));
				preview_pw.setSATURATION_FACTOR(Float.valueOf((String) Options.getOptions().get("SATURATION_FACTOR").defaul));
				preview_pw.setBRIGHTNESS_FACTOR(Float.valueOf((String) Options.getOptions().get("BRIGHTNESS_FACTOR").defaul));
				preview_pw.setENABLE_OVERFLOW((boolean) Options.getOptions().get("ENABLE_OVERFLOW").defaul);

				image_pw.setSTARTING_POINTS_CNT(Integer.valueOf((String) Options.getOptions().get("STARTING_POINTS_CNT").defaul));
				image_pw.setRECTANGLE_SIDELENGTH(Integer.valueOf((String) Options.getOptions().get("RECTANGLE_SIDELENGTH").defaul));
				image_pw.setRANDOMIZER_FACTOR(Double.valueOf((String) Options.getOptions().get("RANDOMIZER_FACTOR").defaul));
				image_pw.setHUE_FACTOR(Float.valueOf((String) Options.getOptions().get("HUE_FACTOR").defaul));
				image_pw.setSATURATION_FACTOR(Float.valueOf((String) Options.getOptions().get("SATURATION_FACTOR").defaul));
				image_pw.setBRIGHTNESS_FACTOR(Float.valueOf((String) Options.getOptions().get("BRIGHTNESS_FACTOR").defaul));
				image_pw.setENABLE_OVERFLOW((boolean) Options.getOptions().get("ENABLE_OVERFLOW").defaul);

				label_RandomizerFactor.setText("Randomizer factor: (" + Math.round(preview_pw.getRANDOMIZER_FACTOR() * 1000) + "/1000)");
				label_Hue.setText("Hue: (" + Math.round(preview_pw.getHUE_FACTOR() * 1000) + "/1000)");
				label_Saturation.setText("Saturation: (" + Math.round(preview_pw.getSATURATION_FACTOR() * 1000) + "/1000)");
				label_Brightness.setText("Brightness: (" + Math.round(preview_pw.getBRIGHTNESS_FACTOR() * 1000) + "/1000)");

				slider_startingPoints.setValue(preview_pw.getSTARTING_POINTS_CNT());
				slider_RectangleSidelength.setValue(preview_pw.getRECTANGLE_SIDELENGTH());
				slider_RandomizerFactor.setValue((int) Math.round(preview_pw.getRANDOMIZER_FACTOR() * 1000));
				slider_Hue.setValue(Math.round(preview_pw.getHUE_FACTOR() * 1000));
				slider_Brightness.setValue(Math.round(preview_pw.getBRIGHTNESS_FACTOR() * 1000));
				slider_Saturation.setValue(Math.round(preview_pw.getSATURATION_FACTOR() * 1000));

				overflow.setSelected(preview_pw.getENABLE_OVERFLOW());

				preview_Panel.render(preview_pw.recreateMem());
			});



			optionsPanel.add(preview_Panel);
			optionsPanel.add(label_startingPoints);
			optionsPanel.add(slider_startingPoints);
			optionsPanel.add(label_RectangleSidelength);
			optionsPanel.add(slider_RectangleSidelength);
			optionsPanel.add(label_RandomizerFactor);
			optionsPanel.add(slider_RandomizerFactor);
			optionsPanel.add(label_Hue);
			optionsPanel.add(slider_Hue);
			optionsPanel.add(label_Saturation);
			optionsPanel.add(slider_Saturation);
			optionsPanel.add(label_Brightness);
			optionsPanel.add(slider_Brightness);
			optionsPanel.add(reset);

			JPanel optionsPanel2 = new JPanel(new GridLayout(30, 1));
			optionsPanel2.add(overflow);



			mainPanel.add(bigPictureOptions_Panel);
			mainPanel.add(image_Panel);
			mainPanel.add(optionsPanel);
			mainPanel.add(optionsPanel2);
			jf.add(mainPanel);

			jf.pack();
			jf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			jf.addWindowListener(new WindowAdapter() {
				@Override public void windowClosing(WindowEvent e) {
					Start.exit();
				}});
			jf.setVisible(true);
		});
	}
}
