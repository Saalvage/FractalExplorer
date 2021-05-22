package sal.fractalexplorer.fractals;

import sal.fractalexplorer.R;

public abstract class FractalInput extends Fractal {
	public final String INITIAL_VALUE;

	private String word;

	public FractalInput(String name, int iterations, double escape, String initialWord) {
		super(name, iterations, escape);
		INITIAL_VALUE = initialWord;
		setWord(initialWord);
	}

	@Override
	public final int getViewGroupId() {
		return R.id.lyapunov_group;
	}

	public void setWord(String val) {
		word = val;
	}

	public String getWord() {
		return word;
	}
}
