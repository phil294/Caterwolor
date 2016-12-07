import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.ini4j.Ini;

/**
 * .___. <br>
 * {o,o} <br>
 * /)__) <br>
 * -"-"-- <br>
 * 2015_01 phil eochls@web.de<br>
 * reading, parsing, writig, setting options (ini file)
 */
public class Options {

	private static Ini					ini;
	private static Map<String, Option>	options;
	
	public static Map<String, Option> getOptions() {
		return options;
	}

	public static void readConfig_configureOptions() throws IOException {

		options = new TreeMap<>();

		File configFile = new File("randomImage_config.ini");
		if (!configFile.exists())
			configFile.createNewFile();
		ini = new Ini(configFile);

		String section;

		//		------------------------------------------------------------------
		// 			SECTION			VAR_NAME		INI_KEY			VAR_DEFAULT
		//				|				|				|				|
		section = "Window";
		options.put("_WIDTH", new Option(section, "_WIDTH", "600"));
		options.put("_HEIGHT", new Option(section, "_HEIGHT", "400"));

		//		------------------------------------------------------------------
		// 			SECTION			VAR_NAME		INI_KEY			VAR_DEFAULT
		//				|				|				|				|
		section = "Pixelwork";
		options.put("STARTING_POINTS_CNT", new Option(section, "STARTING_POINTS_CNT", "1"));
		options.put("RECTANGLE_SIDELENGTH", new Option(section, "RECTANGLE_SIDELENGTH", "3"));
		options.put("RANDOMIZER_FACTOR", new Option(section, "RANDOMIZER_FACTOR", "0.010"));
		options.put("HUE_FACTOR", new Option(section, "HUE_FACTOR", "1.000f"));
		options.put("SATURATION_FACTOR", new Option(section, "SATURATION_FACTOR", "1.000f"));
		options.put("BRIGHTNESS_FACTOR", new Option(section, "BRIGHTNESS_FACTOR", "1.000f"));
		options.put("ENABLE_OVERFLOW", new Option(section, "ENABLE_OVERFLOW", true));

		readConfigurationFile();	// and store into options

	}

	private static void readConfigurationFile() {

		Object tmp_object;
		for (Map.Entry<String, Option> entry : options.entrySet()) {
			tmp_object = ini.get(entry.getValue().section, entry.getValue().key);
			if (tmp_object == null)
				options.get(entry.getKey()).value = entry.getValue().defaul;
			else
				options.get(entry.getKey()).value = tmp_object;
		}

	}

	public static void writeConfigurationFile() throws IOException {

		// put in here any methods which retrieve and store option values in the options.-treemap:
		//	get_and_store_guiPosition_and_dimensions();

		// .

		for (Map.Entry<String, Option> entry : options.entrySet())
			ini.put(entry.getValue().section, entry.getValue().key, entry.getValue().value);
		ini.store();
	}

	public static void storePixelWorkConfigs(PixelWork pw) {
		int STARTING_POINTS_CNT = pw.getSTARTING_POINTS_CNT();
		int RECTANGLE_SIDELENGTH = pw.getRECTANGLE_SIDELENGTH();
		double RANDOMIZER_FACTOR = pw.getRANDOMIZER_FACTOR();
		float HUE_FACTOR = pw.getHUE_FACTOR();
		float SATURATION_FACTOR = pw.getSATURATION_FACTOR();
		float BRIGHTNESS_FACTOR = pw.getBRIGHTNESS_FACTOR();
		boolean ENABLE_OVERFLOW = pw.getENABLE_OVERFLOW();
		options.get("STARTING_POINTS_CNT").value = STARTING_POINTS_CNT;
		options.get("RECTANGLE_SIDELENGTH").value = RECTANGLE_SIDELENGTH;
		options.get("RANDOMIZER_FACTOR").value = RANDOMIZER_FACTOR;
		options.get("HUE_FACTOR").value = HUE_FACTOR;
		options.get("SATURATION_FACTOR").value = SATURATION_FACTOR;
		options.get("BRIGHTNESS_FACTOR").value = BRIGHTNESS_FACTOR;
		options.get("ENABLE_OVERFLOW").value = ENABLE_OVERFLOW;
	}

	public static void storeWH(int w, int h) {
		options.get("_WIDTH").value = w;
		options.get("_HEIGHT").value = h;
	}

/*
	public static void get_and_store_guiPosition_and_dimensions() {

		Point p = Gui_main.frame.getLocationOnScreen();
		options.get("gui_posX").value = (p.getX());
		options.get("gui_posY").value = (p.getY());
		options.get("gui_width").value = (Gui_main.frame.getWidth());
		options.get("gui_height").value = (Gui_main.frame.getHeight());

	}
 */

}
