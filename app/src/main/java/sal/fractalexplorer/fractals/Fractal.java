package sal.fractalexplorer.fractals;

import android.graphics.Color;
import android.icu.math.BigDecimal;
import android.icu.math.MathContext;
import android.os.Build;

import androidx.annotation.RequiresApi;

import sal.fractalexplorer.FractalThreadManager;
import sal.fractalexplorer.R;

public abstract class Fractal {
	public final String NAME;

	protected final int ITERATIONS;
	protected final double ESCAPE;
	protected final double ESCAPE_SQUARE;

	public Fractal(String name, int iterations, double escape) {
		NAME = name;

		ITERATIONS = iterations;
		ESCAPE = escape;
		ESCAPE_SQUARE = escape * escape;
	}

	public int getViewGroupId() {
		return 0;
	}

	public abstract int getColor(double ca, double cb);
}

