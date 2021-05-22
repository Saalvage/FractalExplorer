package sal.fractalexplorer;

import android.widget.ProgressBar;

public class ProgressBarThread extends Thread {
	private final ProgressBar PB;
	private final FractalThreadInfo FTI;

	public ProgressBarThread(ProgressBar pb, FractalThreadInfo fti) {
		super("ProgressBar Updater");
		PB = pb;
		FTI = fti;
	}

	@Override
	public void run() {
		while (FTI.running) {
			if (FTI.isDone()) {
				PB.setProgress(PB.getMax());
			} else {
				int lowPrecLog = log2(FTI.lowestPrecision);
				PB.setProgress((int) ((float) (lowPrecLog - log2(FTI.precision) - 1) / lowPrecLog * PB.getMax()));
			}
			try {
				synchronized (this) {
					wait(100);
				}
			} catch (InterruptedException ignored) {}
		}
	}

	private int log2(int val) {
		int log = 0;
		while (val > 1) {
			log++;
			val /= 2;
		}
		return log;
	}
}
