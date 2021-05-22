package sal.fractalexplorer;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.util.concurrent.Executor;

import sal.fractalexplorer.fractals.Fractal;
import sal.fractalexplorer.fractals.FractalDualSlider;
import sal.fractalexplorer.fractals.FractalInput;
import sal.fractalexplorer.fractals.FractalSlider;

public class FractalThreadManager {
	public void lol() {
		VIEW.invalidateFromThread();
	}

	public Fractal fractal = Fractals.ALL[0];

	private int[] pixels;

	private int width;
	private int height;

	private final FractalView VIEW;
	private final ProgressBar PB;

	public FractalThreadManager(FractalView view, ProgressBar pb) {
		VIEW = view;
		PB = pb;
	}

	public void setFractal(Fractal fractal) {
		this.fractal = fractal;
		XD.fractal = fractal;
		updateRenderer();
	}

	public Fractal getFractal() {
		return fractal;
	}

	public void forceRun() {
		if (XD.isDone()) {
			new Thread(this::screenshot).start();
		} else {
			XD.screenshot = true;
		}
	}

	public void screenshot() {
		VIEW.saveScreenshot("lol");
	}

	public void kill() {
		XD.running = false;
		updateRenderer();
	}

	FractalThreadInfo XD;

	private void updateRenderer() {
		XD.stop();
	}

	public boolean reset() {
		// TODO: Better screenshot.
		if (XD.screenshot) {
			return true;
		}
		if (XD.tempOffX == 0.0 && XD.tempOffY == 0.0 && XD.tempScalar == 1.0) {
			return false;
		}
		XD.tempOffX = 0.0;
		XD.tempOffY = 0.0;
		XD.tempScalar = 1.0;
		updateRenderer();
		return true;
	}

	public void resizeScreen(int newWidth, int newHeight) {
		if (width == newWidth && height == newHeight)
			return;
		width = newWidth;
		height = newHeight;
		pixels = new int[width * height];
		if (XD != null) {
			XD.running = false;
		}
		XD = new FractalThreadInfo(this, 2, pixels, 32, width, height);
		ProgressBarThread pbt = new ProgressBarThread(PB, XD);
		pbt.setPriority(Thread.MIN_PRIORITY);
		pbt.start();
		reset();
		updateRenderer();
	}

	public void offset(double dx, double dy) {
		if (XD.screenshot) {
			return;
		}
		if (dx == 0.0 && dy == 0.0)
			return;
		XD.tempOffX += dx;
		XD.tempOffY += dy;
		updateRenderer();
	}

	public void scale(double dScale, float focusX, float focusY) {
		if (XD.screenshot) {
			return;
		}
		if (dScale == 1.0) {
			return;
		}
		XD.tempOffX = (XD.tempOffX + focusX) * dScale - focusX;
		XD.tempOffY = (XD.tempOffY + focusY) * dScale - focusY;
		XD.tempScalar /= dScale;
		updateRenderer();
	}

	public void flip(boolean flipped) {
		XD.tempFlip = flipped;
		updateRenderer();
	}

	public int[] getPixels() {
		return pixels;
	}

	// TODO: Cache these.
	public void updateJulia(boolean real, double n) {
		if (XD.screenshot) {
			return;
		}
		if (fractal instanceof FractalDualSlider) {
			// !real because setSlider sets A on true, while A represents imaginary.
			((FractalDualSlider)fractal).setSlider(!real, n);
		}
		updateRenderer();
	}

	public void updateMultibrot(double exponent) {
		if (XD.screenshot) {
			return;
		}
		if (fractal instanceof FractalSlider) {
			((FractalSlider)fractal).setSlider(exponent);
		}
		updateRenderer();
	}

	public void updateLyapunov(String newWord) {
		if (XD.screenshot) {
			return;
		}
		if (fractal instanceof FractalInput) {
			((FractalInput)fractal).setWord(newWord);
		}
		updateRenderer();
	}
}
