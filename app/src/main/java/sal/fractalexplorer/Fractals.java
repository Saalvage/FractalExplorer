package sal.fractalexplorer;

import android.graphics.Color;

import sal.fractalexplorer.fractals.Fractal;
import sal.fractalexplorer.fractals.FractalDualSlider;
import sal.fractalexplorer.fractals.FractalInput;
import sal.fractalexplorer.fractals.FractalSlider;

public class Fractals {
	private static final double LOG_10_2 = Math.log10(2.0);
	private static final double PI_2 = Math.PI * 2.0;

	private static int hueToColor(float degree) {
		if (degree < 0 || degree >= 360) {
			degree = 0;
		}
		float hh = degree / 60f;
		int i = (int) hh;
		float t = hh - i;

		switch (i) {
			case 0:
				return 0xffff0000 | ((int) (t * 256f) << 8);
			case 1:
				return 0xff00ff00 | (255 - (int) (t * 255f) << 16);
			case 2:
				return 0xff00ff00 | ((int) (t * 256f));
			case 3:
				return 0xff0000ff | (255 - (int) (t * 256f) << 8);
			case 4:
				return 0xff0000ff | ((int) (t * 256f) << 16);
			case 5:
			default:
				return 0xffff0000 | (255 - (int) (t * 256f));
		}
	}

	private static int valueToColor(int value) {
		return value << 16 | value << 8 | value;
	}

	public static final Fractal[] ALL = new Fractal[] {
		new Fractal("Mandelbrot", 100, 4.0) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				double as = a * a;
				double bs = b * b;

				int n = 0;
				while (n < ITERATIONS) {
					double ia = as - bs + ca;
					b = 2.0 * a * b + cb;
					a = ia;

					as = a * a;
					bs = b * b;

					if (as + bs > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1.0 - Math.log(Math.log10(Math.sqrt(as + bs))/ LOG_10_2)) / ITERATIONS * 360f);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(as + bs) / ESCAPE * 255f));
			}
		},


		new FractalDualSlider("Mandeltrap", 100, 0.0, 1.0, 1.0) {
			private double pog;

			@Override
			public int getColor(double ca, double cb) {
				double distance = 1e20;

				double a = ca;
				double b = cb;

				for (int n = 0; n < ITERATIONS; n++) {
					double ia = a * a - b * b + ca;
					b = 2.0 * a * b + cb;
					a = ia;

					double newDist = Math.abs(a * sliderA + b * sliderB)/pog;
					if (newDist < distance) {
						distance = newDist;
					}
				}

				return hueToColor((float) (distance*1000));
			}

			@Override
			public void setSlider(boolean which, double val) {
				super.setSlider(which, val);
				pog = Math.sqrt(sliderA * sliderA + sliderB * sliderB);
			}
		},


		new FractalSlider("Multibrot", 100, 4.0, 3.0) {
			private double sliderHalf;

			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				int n = 0;
				while (n < ITERATIONS) {
					double i = Math.pow(a * a + b * b, sliderHalf);
					double tan = Math.atan2(b, a);
					double ia = i * Math.cos(slider * tan);
					b = i * Math.sin(slider * tan) + cb;
					a = ia + ca;


					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b))/ LOG_10_2)) / ITERATIONS * 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) / ESCAPE * 255f));
			}

			@Override
			public void setSlider(double val) {
				super.setSlider(val);
				sliderHalf = val / 2.0;
			}
		},


		new Fractal("Cosinebrot", 100, 10.0 * Math.PI) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				int n = 0;
				while (n < ITERATIONS) {
					double ia = Math.cos(a) * Math.cosh(b);
					b = -Math.sin(a) * Math.sinh(b) + cb;
					a = ia + ca;

					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b))/ LOG_10_2)) / ITERATIONS * 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) /  ESCAPE * 255f));
			}
		},


		new Fractal("Eulerbrot", 100, 50.0) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				int n = 0;
				while (n < ITERATIONS) {
					double pow = Math.pow(Math.E, a);
					double ib = pow * Math.sin(b) + cb;
					a = pow * Math.cos(b) + ca;
					b = ib;

					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b)) / LOG_10_2)) / ITERATIONS * 1500 % 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) / ESCAPE * 255f));
			}
		},


		new Fractal("Celticbrot", 100, 4.0) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				int n = 0;
				while (n < ITERATIONS) {
					double ia = Math.abs(a * a - b * b);
					b = 2.0 * a * b + cb;
					a = ia + ca;

					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b))/ LOG_10_2)) / ITERATIONS * 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) / ESCAPE * 255f));
			}
		},


		new Fractal("Tricorn", 100, 4.0) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				double as = a * a;
				double bs = b * b;

				int n = 0;
				while (n < ITERATIONS) {
					double ia = as - bs + ca;
					b = -2.0 * a * b + cb;
					a = ia;

					as = a * a;
					bs = b * b;

					if (as + bs > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1.0 - Math.log(Math.log10(Math.sqrt(as + bs))/ LOG_10_2)) / ITERATIONS * 360f);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(as + bs) / ESCAPE * 255f));
			}
		},


		new Fractal("Burning Ship", 100, 4.0) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				int n = 0;
				while (n < ITERATIONS) {
					double ia = a * a - b * b;
					b = Math.abs(2.0 * a * b) + cb;
					a = ia + ca;

					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b))/ LOG_10_2)) / ITERATIONS * 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) / ESCAPE * 255f));
			}
		},


		new Fractal("Buffalo", 100, 4.0) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				int n = 0;
				while (n < ITERATIONS) {
					a = a > 0 ? a : -a;
					b = b > 0 ? b : -b;

					double ia = a * a - b * b - a;
					b = 2.0 * a * b + cb - b;
					a = ia + ca;

					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b))/ LOG_10_2)) / ITERATIONS * 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) / ESCAPE * 255f));
			}
		},


		new Fractal("Colatz", 100, 4.0) {
			@Override
			public int getColor(double ca, double cb) {
				double a = ca;
				double b = cb;

				int n = 0;
				while (n < ITERATIONS) {
					double pa = Math.PI * a;
					double pb = Math.PI * b;
					double sin = Math.sin(pa) * Math.sinh(pb);
					double cos = Math.cos(pa) * Math.cosh(pb);
					double ia = -0.5*(b*sin + a*cos + 0.5*cos) + a + 0.25;
					b = 0.5*(a*sin - b*cos + 0.5*sin) + b;
					a = ia;

					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b))/ LOG_10_2)) / ITERATIONS * 1500 % 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) / ESCAPE * 255f));
			}
		},


		new FractalDualSlider("Julia", 100, 4.0, 0.0, 0.0) {
			@Override
			public int getColor(double a, double b) {
				int n = 0;
				while (n < ITERATIONS) {
					double ia = a * a - b * b;
					b = 2 * a * b + sliderA;
					a = ia + sliderB;

					if (a * a + b * b > ESCAPE_SQUARE) {
						return hueToColor((float) (n + 1 - Math.log(Math.log10(Math.sqrt(a * a + b * b))/ LOG_10_2)) / ITERATIONS * 360);
					}

					n++;
				}

				return valueToColor((int) (Math.sqrt(a * a + b * b) / ESCAPE * 255f));
			}
		},


		new Fractal("Arnold Tongues (experimental)", 250, 0.03) {
			@Override
			public int getColor(double ca, double cb) {
				int n = 0;

				double o = 0;
				while (n < ITERATIONS) {
					double rest = o % 1;
					double newO = rest + ca - cb * Math.sin(PI_2 * rest);
					if (Math.abs(o - newO) < ESCAPE) {
						return hueToColor((float) n / ITERATIONS * 360f);
					}
					o = newO;

					n++;
				}

				return valueToColor(0);
			}
		},


		new FractalInput("Lyapunov", 100, 4.0, "AB") {
			private boolean[] word;

			@Override
			public int getColor(double ca, double cb) {
				double funcX = 0.5;
				double finalVal = 0;
				for (int i = 1; i < ITERATIONS; i++) {
					double r = (word[i % word.length] ? ca : cb);
					funcX = r * funcX * (1 - funcX);
					finalVal += Math.log(Math.abs(r * (1 - 2 * funcX))) / LOG_10_2;

					if (finalVal == Double.POSITIVE_INFINITY) {
						break;
					}
				}
				finalVal /= ITERATIONS;

				if (finalVal > 0) {
					return Color.rgb(0, 0, Math.min(255, (int) (Math.abs(finalVal) * 255f)));
				} else {
					return Color.rgb(0, Math.min(255, (int) (finalVal * 255f)), 0);
				}
			}

			@Override
			public void setWord(String val) {
				super.setWord(val);
				String upper = val.toUpperCase();
				word = new boolean[upper.length()];
				for (int i = 0; i < upper.length(); i++) {
					word[i] = upper.charAt(i) == 'A';
				}
			}
		}
	};
}
