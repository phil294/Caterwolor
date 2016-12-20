package de.phil294.caterwoler;



public class Sleep {

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}

}
