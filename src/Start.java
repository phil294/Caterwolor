import java.io.IOException;

public class Start {

	public static void exit() {
		try {
			GuiMain.closeGui();
			Options.writeConfigurationFile();
			System.exit(0);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	public static void main(final String[] args) {
		try {
			Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
				System.err.println(e.getMessage() + "in Thread " + t.getName() + " (" + t.getClass().getName() + ")");
				e.printStackTrace(System.err);
			});
			Options.readConfig_configureOptions();
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			GuiMain.startGui();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}
