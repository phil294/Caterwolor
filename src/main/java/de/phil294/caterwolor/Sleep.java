package de.phil294.caterwolor;



public class Sleep {

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}

}
