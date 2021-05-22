package sal.fractalexplorer.fractals;

import sal.fractalexplorer.R;

public abstract class FractalSlider extends Fractal {
	public final double INITIAL_VALUE;

	// Incase the slider value is modified in a child.
	private double sliderConstant;
	protected double slider;

	public FractalSlider(String name, int iterations, double escape, double initialValue) {
		super(name, iterations, escape);
		INITIAL_VALUE = initialValue;
		setSlider(initialValue);
	}

	@Override
	public final int getViewGroupId() {
		return R.id.multibrot_group;
	}

	// Super should always be called.
	public void setSlider(double val) {
		sliderConstant = val;
		slider = val;
	}

	public final double getSliderConstant() {
		return sliderConstant;
	}
}
