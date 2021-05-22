package sal.fractalexplorer.fractals;

import sal.fractalexplorer.R;

public abstract class FractalDualSlider extends Fractal {
	public final double INITIAL_VALUE_A;
	public final double INITIAL_VALUE_B;

	private double sliderConstantA;
	private double sliderConstantB;
	protected double sliderA;
	protected double sliderB;

	public FractalDualSlider(String name, int iterations, double escape, double initialValueA, double initialValueB) {
		super(name, iterations, escape);
		INITIAL_VALUE_A = initialValueA;
		INITIAL_VALUE_B = initialValueB;
		setSlider(true, initialValueA);
		setSlider(false, initialValueB);
	}

	@Override
	public final int getViewGroupId() {
		return R.id.julia_group;
	}

	// Super should always be called.
	public void setSlider(boolean which, double val) {
		System.out.println("AAAAAAAAAAAAAA" + val);
		if (which) {
			sliderConstantA = val;
			sliderA = val;
		} else {
			sliderConstantB = val;
			sliderB = val;
		}
	}

	public final double getSliderConstantA() {
		return sliderConstantA;
	}

	public final double getSliderConstantB() {
		return sliderConstantB;
	}
}
