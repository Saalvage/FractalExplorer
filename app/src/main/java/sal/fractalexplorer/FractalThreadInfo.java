package sal.fractalexplorer;

import sal.fractalexplorer.fractals.Fractal;

public class FractalThreadInfo {
	private final FractalThreadManager FTM;

	public Fractal fractal;

	private final FractalThread[] THREADS;
	private volatile int activeThreads;

	public int prevPrecision;
	public int precision;
	public int lowestPrecision;

	public final int WIDTH;
	public final int HEIGHT;

	public double offX;
	public double offY;
	public double scalar;
	public boolean flip;

	public double tempOffX;
	public double tempOffY;
	public double tempScalar;
	public boolean tempFlip;

	public boolean running = true;
	public boolean stop = false;

	private boolean done = false;

	public boolean screenshot = false;

	private final int[] pixelsOut;
	private final int[] pixelsWrite;

	public FractalThreadInfo(FractalThreadManager ftm, int numThreads, int[] pixels, int lowestPrecision, int width, int height) {
		fractal = ftm.getFractal();
		FTM = ftm;

		THREADS = new FractalThread[numThreads];
		activeThreads = numThreads;

		this.lowestPrecision = lowestPrecision;
		this.precision = lowestPrecision;
		this.WIDTH = width;
		this.HEIGHT = height;

		pixelsOut = pixels;
		pixelsWrite = new int[pixels.length];

		for (int i = 0; i < numThreads; i++) {
			if (i == 0) {
				THREADS[i] = new FractalThread(this, pixelsWrite, 0, width, 0, height/2);
			} else if (i == 1) {
				THREADS[i] = new FractalThread(this, pixelsWrite, 0, width, height/2, height);
			}
			THREADS[i].setPriority(Thread.MAX_PRIORITY);
			THREADS[i].start();
		}
	}

	public void stop() {
		if (done) {
			done = false;
			activeThreads = THREADS.length;
			precision = lowestPrecision;
			update();
			wakeUp();
		} else {
			stop = true;
		}
	}

	public synchronized boolean reportThreadFinish() {
		System.out.println("THREAD FINISHED");
		if (--activeThreads <= 0) {
			// Something changed, we'll rerender from the lowest precision, don't draw.
			if (stop && !screenshot) {
				update();
				stop = false;
				// We rendered the lowest quality image and something changed, display it!
				if (precision == lowestPrecision) {
					draw();
				} else {
					precision = lowestPrecision;
				}
			} else {
				draw();
				// We finished a render iteration normally, let's continue!
				if (precision > 1) {
					prevPrecision = precision;
					precision /= 2;
				// We rendered every pixel, we can relax now.
				} else {
					done = true;
					if (screenshot) {
						FTM.screenshot();
						screenshot = false;
					}
					return true;
				}
			}
			activeThreads = THREADS.length;
			wakeUp();
			return false;
		}
		return true;
	}

	private void wakeUp() {
		for (FractalThread ft : THREADS) {
			synchronized (ft) {
				ft.notify();
			}
		}
	}

	private void draw() {
		System.arraycopy(pixelsWrite, 0, pixelsOut, 0, pixelsOut.length);
		FTM.lol();
	}

	public boolean isDone() {
		return done;
	}

	private void update() {
		offX = tempOffX;
		offY = tempOffY;
		scalar = tempScalar / WIDTH;
		flip = tempFlip;
	}
}
