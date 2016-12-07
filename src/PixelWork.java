import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PixelWork {

	private static final Random	random					= new Random();

	private Color[][]			pixels					= null;		// pixels + mem doppeltgemoppelt
	private int[]				mem						= null;
	private boolean mayExecute = true;
	private static final int STANDARD_THREADAMOUNT = 1; // multihreading slows it down because banana

	private static final double MIXING_FACTOR_START = 1;	// TODO ??
	private static final double MIXING_FACTOR_END = 0.1;
	private static final int MIXING_FACTOR_EXPONENT = 4; // in terms of distance

	private int					STARTING_POINTS_CNT		= Integer.parseInt((String) Options.getOptions().get("STARTING_POINTS_CNT").value);
	private int					RECTANGLE_SIDELENGTH	= Integer.parseInt((String) Options.getOptions().get("RECTANGLE_SIDELENGTH").value);
	private double				RANDOMIZER_FACTOR		= Double.parseDouble((String) Options.getOptions().get("RANDOMIZER_FACTOR").value);
	private float				HUE_FACTOR				= Float.parseFloat((String) Options.getOptions().get("HUE_FACTOR").value);
	private float				SATURATION_FACTOR		= Float.parseFloat((String) Options.getOptions().get("SATURATION_FACTOR").value);
	private float				BRIGHTNESS_FACTOR		= Float.parseFloat((String) Options.getOptions().get("BRIGHTNESS_FACTOR").value);
	private boolean ENABLE_OVERFLOW = Boolean.parseBoolean(String.valueOf(Options.getOptions().get("ENABLE_OVERFLOW").value));

	/** there is no startExecution() or allowExecution(). if stopExecution() is called, all current threads will be cancelled and object goes back into idle status to be awakened to life again by recreate() or similar */
	public void stopExecution() {
		this.mayExecute = false;
	}

	public boolean getENABLE_OVERFLOW() {
		return this.ENABLE_OVERFLOW;
	}

	public void setENABLE_OVERFLOW(boolean v) {
		this.ENABLE_OVERFLOW = v;
	}
	public int getSTARTING_POINTS_CNT() {
		return this.STARTING_POINTS_CNT;
	}

	public void setSTARTING_POINTS_CNT(int sTARTING_POINTS_CNT) {
		this.STARTING_POINTS_CNT = sTARTING_POINTS_CNT;
	}

	public int getRECTANGLE_SIDELENGTH() {
		return this.RECTANGLE_SIDELENGTH;
	}

	public void setRECTANGLE_SIDELENGTH(int rECTANGLE_SIDELENGTH) {
		this.RECTANGLE_SIDELENGTH = rECTANGLE_SIDELENGTH;
	}

	public double getRANDOMIZER_FACTOR() {
		return this.RANDOMIZER_FACTOR;
	}

	public void setRANDOMIZER_FACTOR(double rANDOMIZER_FACTOR) {
		this.RANDOMIZER_FACTOR = rANDOMIZER_FACTOR;
	}

	public float getHUE_FACTOR() {
		return this.HUE_FACTOR;
	}

	public void setHUE_FACTOR(float hUE_FACTOR) {
		this.HUE_FACTOR = hUE_FACTOR;
	}

	public float getSATURATION_FACTOR() {
		return this.SATURATION_FACTOR;
	}

	public void setSATURATION_FACTOR(float sATURATION_FACTOR) {
		this.SATURATION_FACTOR = sATURATION_FACTOR;
	}

	public float getBRIGHTNESS_FACTOR() {
		return this.BRIGHTNESS_FACTOR;
	}

	public void setBRIGHTNESS_FACTOR(float bRIGHTNESS_FACTOR) {
		this.BRIGHTNESS_FACTOR = bRIGHTNESS_FACTOR;
	}

	public void savePixelWorkValues() {
		Options.storePixelWorkConfigs(this);
	}

	public int[] getMem() {
		return this.mem;
	}

	public PixelWork(int w, int h) {
		this.pixels = new Color[w][h];
		//this.fillMem_Handler(STANDARD_THREADAMOUNT, true);
	}

	/** either get with this's @return or call getMem() afterwards. Does about the same thing as PixelWork(w,h)-constructor, just without the waste of recreating the whole object. and static variables will be preserved like lastFixPick */
	public int[] recreateMem(int h, int w) {
		long s = System.currentTimeMillis();
		this.pixels = new Color[h][w];
		this.fillMem_Handler(STANDARD_THREADAMOUNT, true);
		System.out.println("Time needed for calculation:  " + ((System.currentTimeMillis() - s) / 1000) + " seconds");
		return this.mem;
	}

	/** keep width+height. // see recreateMem(int h,intw) */
	public int[] recreateMem() {
		this.pixels = new Color[this.pixels.length][this.pixels[0].length];
		this.fillMem_Handler(STANDARD_THREADAMOUNT, true);
		return this.mem;
	}

	/** either get with this's @return or call getMem() afterwards */
	public int[] stretch(int factor) {

		Color[][] newArr = new Color[this.pixels.length * factor][this.pixels[0].length * factor];
		for (int i = 0; i < this.pixels.length; i++)
			for(int j=0;j<this.pixels[0].length;j++)
				newArr[i*factor][j*factor]=this.pixels[i][j];

		this.pixels = newArr;
		this.fillMem_Handler(STANDARD_THREADAMOUNT, false); // TODO randomize?
		return this.mem;
	}

	public int[] createMemFromImage(BufferedImage image) {
		this.pixels = new Color[image.getHeight()][image.getWidth()];
		int rgb = 0;
		for (int i = 0; i < image.getHeight(); i++)
			for (int j = 0; j < image.getWidth(); j++) {
				rgb = image.getRGB(j, i);
				if (rgb != -16777216)
					this.pixels[i][j] = new Color(rgb);
			}
		this.fillMem_Handler(STANDARD_THREADAMOUNT, true);
		return this.mem;
	}

	/** uses fillMemWorker()  */
	private void fillMem_Handler(int amount_of_threads_to_use, boolean randomizePixels) {
		this.mayExecute = true;
		this.mem = new int[this.pixels[0].length * this.pixels.length];

		int partialTODOAmount = ((this.missingPixelsAmount()) / amount_of_threads_to_use);
		ExecutorService es = Executors.newFixedThreadPool(amount_of_threads_to_use);
		int startingPoints_counterDownwards = this.STARTING_POINTS_CNT;
		boolean log;
		if (amount_of_threads_to_use == 1)
			log = true;
		else
			log = false;
		// loop, taskAmount //
		for (int i42663 = 0; i42663 < amount_of_threads_to_use; i42663++) {
			if(i42663!=0)
				startingPoints_counterDownwards = -1;
			final int startingPoints_counterDownwards_final = startingPoints_counterDownwards;
			final int i42663_final = i42663;
			es.execute(() -> PixelWork.this.fillMemWorker(log, partialTODOAmount, startingPoints_counterDownwards_final, randomizePixels, true, i42663_final + 1)); // TODO neighboursWanted == true here. unten auch.
		}
		es.shutdown();
		try {
			while (!es.awaitTermination(300, TimeUnit.MILLISECONDS))
				if(this.mayExecute)
					System.out.println("... waiting ... awaiting termination of pixelWork-threads");
				else {
					System.out.println("STOPPING EXECUTION NOW");
					es.shutdownNow();
					this.mayExecute = true; // (reset)
					return; // (cancel)
				}
		} catch (InterruptedException e) {
			System.err.println("timeout 2015_07_20_4.05 executorpool.");
			System.exit(-1); // TODO wtf
		}
		int missingPixelsAmount = this.missingPixelsAmount();
		if (missingPixelsAmount > 0)
			this.fillMemWorker(false, missingPixelsAmount, -1, randomizePixels, true, -1);

		for (int i=0; i<this.pixels.length;i++)
			for (int j = 0; j < this.pixels[i].length; j++)
				try { // TODO
					this.mem[j + (i * this.pixels[0].length)] = this.pixels[i][j].getRGB();
				} catch (Exception e) {
				}

	}


	/**
	 * used to be called work() 2015_07_22_20.22
	 *
	 * @param startingpoints
	 *            == -1 -> no own starting points (for multithreading) amount is important to be precise, if too less: gaps. just even 1 too much infinite loop -> "could ot find any neighbours":
	 */
	private void fillMemWorker(boolean log, int amount, int startingPoints, boolean randomizePixels, boolean neighboursWanted, int threadNumber) {
		System.out.println("Starting worker thread no. " + threadNumber + ". Amount: " + amount + ". Available starting points: " + startingPoints);
		int ry = 0, rx = 0;
		int usedStartingPoints = 0;
		int filledPixels = 0;
		double progressCounter = 0.0d;
		final int pixelAmount = this.pixels[0].length * this.pixels.length;

		// TODO
		neighboursWanted = true;

		for (;;) {
			boolean found = false;
			for (int i = 0; i < (this.pixels.length * this.pixels[0].length); i++) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("Thread no " + threadNumber + " got interrupted!!!");
					return;
				}
				ry = random.nextInt(this.pixels.length);
				rx = random.nextInt(this.pixels[0].length);
				if (this.pixels[ry][rx] == null) {
					if (!neighboursWanted) {
						found = true;
						break;
					}
					//boolean a1,a2,a3,b1,b3,c1,c2,c3;
					//a1= random.nextInt(2)==1;a2= random.nextInt(2)==1;a3= random.nextInt(2)==1;b1= random.nextInt(2)==1;b3= random.nextInt(2)==1;c1= random.nextInt(2)==1;c2= random.nextInt(2)==1;c3= random.nextInt(2)==1;
					//if (this.hasDirectNeighbourAt(a1, a2, a3, b1, b3, c1, c2, c3, ry, rx)) {
					if (this.hasDirectNeighbourAt(true, true, true, true, true, true, true, true, ry, rx)) {
						found = true;
						break;
					} else if (usedStartingPoints < startingPoints) {
						found = true;
						usedStartingPoints++;
						break;
					}
				}
			}
			if (!found) {
//				System.err.println("FEHLELR dasdfalsdf");
			}

			final Color newColor = this.mixUpColors(ry, rx, randomizePixels);
			//System.out.println(newColor.getRGB());

			filledPixels++;
			if (filledPixels >= amount)
				//	System.out.println(1);
				break;

			this.pixels[ry][rx] = newColor;

			progressCounter++;
			if (progressCounter > 10000) {
				final int progress = (filledPixels * 100) / (pixelAmount);
				if (log)
					System.out.println("Progress: " + filledPixels + "/" + pixelAmount + " - " + progress + "%");
				progressCounter = 0;
			}
		}
		System.out.println("Finished working thread no. " + threadNumber);
	}

	private int missingPixelsAmount() {
		int missingPixels = 0;
		for (Color[] pixel : this.pixels)
			for(int j=0;j<this.pixels[0].length;j++)
				if (pixel[j] == null)
					missingPixels++;
		return missingPixels;
	}

	private ArrayList<Pair<Color, Double>> getColoredNeighbours_withFactor(final int y, final int x) {
		final ArrayList<Pair<Color, Double>> arrL = new ArrayList<>();
		for (int y1 = y - (this.RECTANGLE_SIDELENGTH / 2); y1 <= (y + (this.RECTANGLE_SIDELENGTH / 2)); y1++)
			for (int x1 = x - (this.RECTANGLE_SIDELENGTH / 2); x1 <= (x + (this.RECTANGLE_SIDELENGTH / 2)); x1++) {
				try {
					if (this.pixels[y1][x1] == null)
						continue;
				} catch (final Exception e) {
					continue;
				}
				if ((x1 == x) && (y1 == y))
					continue;
				final double distance = Math.abs(Math.sqrt(Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2)));
				final double rectangle_diagonale = Math.sqrt(Math.pow(this.RECTANGLE_SIDELENGTH, 2) + Math.pow(this.RECTANGLE_SIDELENGTH, 2));
				final double factor = PixelWork.MIXING_FACTOR_START - Math.pow(((distance / (rectangle_diagonale / 2)) * (PixelWork.MIXING_FACTOR_START - PixelWork.MIXING_FACTOR_END)), MIXING_FACTOR_EXPONENT);
//				final double factor = PixelWork.MIXING_FACTOR_START - ((distance / (rectangle_diagonale / 2)) * (PixelWork.MIXING_FACTOR_START - PixelWork.MIXING_FACTOR_END));
				arrL.add(new Pair<>(this.pixels[y1][x1], factor));
			}
		return arrL;
	}

	public static Color randomColor() {
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	private Color mixUpColors(final int y, final int x, boolean randomizePixels) {
		double r, g, b, denom;
		r = g = b = denom = 0;
		Color newColor = null;
		final ArrayList<Pair<Color, Double>> arrNeighbours = this.getColoredNeighbours_withFactor(y, x);
		for (final Pair<Color, Double> elem : arrNeighbours) {
			r += elem.first().getRed() * elem.second();
			g += elem.first().getGreen() * elem.second();
			b += elem.first().getBlue() * elem.second();
			denom += elem.second();
		}
		final Color newRandColor = randomColor();

		double randomizerFactor = this.RANDOMIZER_FACTOR;
		/*
		this.randomizer_counter += 5;
		if (random.nextInt(Math.max(2, 1000 - this.randomizer_counter)) == (Math.max(1, 1000 - this.randomizer_counter))) {
			System.out.println("rand an stelle " + y +", "+x);
			randomizerFactor = 100;
			this.randomizer_counter = 0;
		}
		 */
		if (randomizePixels) {
			r += newRandColor.getRed() * randomizerFactor;
			g += newRandColor.getGreen() * randomizerFactor;
			b += newRandColor.getBlue() * randomizerFactor;
			denom += randomizerFactor;
		}
		if (denom == 0)
			denom = 5000;
		// MIXING TOGETHER IN SET RADIUS AND SET FORCE
		final double r_a = r / denom; // average
		final double g_a = g / denom;
		final double b_a = b / denom;
		//newColor = new Color((int) r_a, (int) g_a, (int) b_a); // changed to float -> exact value which is important when working with random factor == 0.0 bc otherwise randomness comes into the picture. can now be configured 100% exactly by rand fac
		newColor = new Color((float) r_a / 255, (float) g_a / 255, (float) b_a / 255);
		if (newColor.getRGB() == -16777216) // only happens if randomizer_factor==0
			newColor = newRandColor;
		// APPLYING #NOFILTER
		if (randomizePixels) {
			final float[] hsb = Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), null);
			float changed_hue = hsb[0] * this.HUE_FACTOR;
			float changed_satu = hsb[1] * this.SATURATION_FACTOR;
			float changed_bright = hsb[2] * this.BRIGHTNESS_FACTOR;
			int rgb;
			if (this.ENABLE_OVERFLOW)
				rgb = Color.HSBtoRGB(changed_hue, changed_satu, changed_bright);
			else
				rgb = Color.HSBtoRGB(Math.min(changed_hue, 1.0f), Math.min(1.0f, changed_satu), Math.min(1.0f, changed_bright));
			// ^ math min bc eg for sat > 0.9 -> creates wave effect
			newColor = new Color(rgb);
			//System.out.println(hsb[1] + "*" + this.SATURATION_FACTOR + "=" + changed_satu + " or " + (Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getRed(), null)[1]));
		}
		return newColor;
		//return new Color(Color.BLACK.getRGB());
	}

	//private int randomizer_counter = 0;

	/** a1-c3: where may a neighbour be accepted? */
	private boolean hasDirectNeighbourAt(
			boolean a1,boolean a2,boolean a3,
			boolean b1,boolean b3,
			boolean c1,boolean c2,boolean c3,
			final int y, final int x) {
		if (a1)
			try {
				if (this.pixels[y - 1][x - 1] != null)
					return true;
			} catch (final Exception e) {}
		if (a2)
			try {
				if (this.pixels[y - 1][x] != null)
					return true;
			} catch (final Exception e) {}
		if (a3)
			try {
				if (this.pixels[y - 1][x + 1] != null)
					return true;
			} catch (final Exception e) {}
		if (b1)
			try {
				if (this.pixels[y][x - 1] != null)
					return true;
			} catch (final Exception e) {}
		if (b3)
			try {
				if (this.pixels[y][x + 1] != null)
					return true;
			} catch (final Exception e) {}
		if (c1)
			try {
				if (this.pixels[y + 1][x - 1] != null)
					return true;
			} catch (final Exception e) {}
		if (c2)
			try {
				if (this.pixels[y + 1][x] != null)
					return true;
			} catch (final Exception e) {}
		if (c3)
			try {
				if (this.pixels[y + 1][x + 1] != null)
					return true;
			} catch (final Exception e) {}
		return false;
	}

}
