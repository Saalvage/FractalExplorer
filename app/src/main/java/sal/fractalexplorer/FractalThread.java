package sal.fractalexplorer;

import android.util.Log;

import java.lang.reflect.Method;

public class FractalThread extends Thread {
	private final FractalThreadInfo info;
	private final int[] pixels;

	private final int startX;
	private final int stopX;
	private final int startY;
	private final int stopY;

	public FractalThread(FractalThreadInfo info, int[] pixels, int startX, int stopX, int startY, int stopY) {
		super("Fractal Renderer " + startX + " | " + startY);

		this.info = info;
		this.pixels = pixels;

		this.startX = startX;
		this.stopX = stopX;
		this.startY = startY;
		this.stopY = stopY;
	}

	@Override
	public void run() {
		while (info.running) {
			long time = System.currentTimeMillis();
			// TODO: Split x and y overreach to make task difference smaller?
			// TODO: Maybe even render every n-th pixel instead to better spread load of heavy areas.
			int precisionStartX = startX - (startX % info.precision);
			int precisionStopX = stopX == info.WIDTH ? stopX : stopX - (stopX % info.precision);
			int precisionStartY = startY - (startY % info.precision);
			int precisionStopY = stopY == info.HEIGHT ? stopY : stopY - (stopY % info.precision);
			for (int y = precisionStartY; y < precisionStopY; y += info.precision) {
				for (int x = precisionStartX; x < precisionStopX; x += info.precision) {
					if (info.precision == info.lowestPrecision || x % info.prevPrecision != 0 || y % info.prevPrecision != 0) {
						// This might not be optimal, a function pointer would probably be ideal, damn you Java!
						double ca = (info.offX + x) * info.scalar;
						double cb = (info.offY + y) * info.scalar;
						if (info.flip) {
							double divisor = ca * ca + cb * cb;
							ca /= divisor;
							cb /= divisor;
						}
						int color = info.fractal.getColor(ca, cb);
						for (int y1 = 0; y1 < info.precision; y1++) {
							for (int x1 = 0; x1 < info.precision; x1++) {
								int pos = x + x1 + (y + y1) * info.WIDTH;
								if (x + x1 < precisionStopX && y + y1 < precisionStopY) {
									pixels[pos] = color;
								}
							}
						}
					}
				}
				if (info.stop && info.precision != info.lowestPrecision) {
					break;
				}
				// TODO: What do??
				// 0.125 longer with no modulo
				// 0.025 longer with 64
				//if (y % 64 == 0)
				//PB.setProgress(y);
			}
			Log.d(null, "Calc " + getName() + ": (" + info.precision + ") " + (System.currentTimeMillis() - time));
			if (info.reportThreadFinish()) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException ignored) {}
			}
		}
	}

}
