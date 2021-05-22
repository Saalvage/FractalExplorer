package sal.fractalexplorer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.OutputStream;

import sal.fractalexplorer.fractals.Fractal;
import sal.fractalexplorer.fractals.FractalDualSlider;
import sal.fractalexplorer.fractals.FractalInput;
import sal.fractalexplorer.fractals.FractalSlider;

public class FractalView extends View {

	private final Paint PAINT = new Paint();
	private ScaleGestureDetector SGD;
	private GestureDetector GD;

	private FractalThreadManager threads;

	private boolean uiVisible = true;

	public final String[] FRACTAL_NAMES = new String[Fractals.ALL.length];

	private int currentFractalIndex;

	private void init(Context context) {
		SGD = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				threads.scale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
				return true;
			}
		});

		GD = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) { return false; }

			@Override
			public void onShowPress(MotionEvent e) {}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				System.out.println();
				uiVisible = !uiVisible;
				((Activity)getContext()).findViewById(R.id.ui_root).setVisibility(uiVisible ? VISIBLE : GONE);
				return true;
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) { return false; }

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				threads.offset(distanceX, distanceY);
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }
		});

		for (int i = 0; i < FRACTAL_NAMES.length; i++) {
			FRACTAL_NAMES[i] = Fractals.ALL[i].NAME;
		}
	}

	public FractalView(Context context) {
		super(context);
		init(context);
	}

	public FractalView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FractalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public FractalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		SGD.onTouchEvent(e);
		GD.onTouchEvent(e);
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int prevW, int prevH) {
		System.out.println("Size changed");
		if (threads != null) {
			threads.resizeScreen(w, h);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long time = System.currentTimeMillis();
		if (threads.getPixels() != null) {
			canvas.drawBitmap(threads.getPixels(), 0, getWidth(), 0f, 0f, getWidth(), getHeight(), false, PAINT);
		}
		System.out.println("Draw: " + (System.currentTimeMillis() - time));
	}

	@Override
	protected void onAttachedToWindow() {
		System.out.println("POGG");
		super.onAttachedToWindow();
		if (getContext() instanceof Activity) {
			threads = new FractalThreadManager(this, ((Activity) getContext()).findViewById(R.id.progressBar));
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		threads.kill();
	}

	public void screenshot() {
		threads.forceRun();
	}

	public void saveScreenshot(String filename) {
		try {
			// Add a specific media item.
			ContentResolver resolver = getContext().getContentResolver();

			// Find all audio files on the primary external storage device.
			Uri audioCollection;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				audioCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
			} else {
				audioCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			}

			// Publish a new song.
			ContentValues newSongDetails = new ContentValues();
			newSongDetails.put(MediaStore.Images.Media.TITLE,  "Cool Fractal I guess");
			newSongDetails.put(MediaStore.Images.Media.DISPLAY_NAME,  "Fractal.png cool");
			newSongDetails.put(MediaStore.Images.Media.DESCRIPTION, "BLA BLA BLA");
			newSongDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
			newSongDetails.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
			newSongDetails.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

			System.out.println(newSongDetails);

			Uri myFavoriteSongUri = resolver.insert(audioCollection, newSongDetails);

			OutputStream os = resolver.openOutputStream(myFavoriteSongUri);
			Bitmap.createBitmap(threads.getPixels(), getWidth(), getHeight(), Bitmap.Config.ARGB_8888).compress(Bitmap.CompressFormat.PNG, 100, os);
			os.close();

			((Activity)getContext()).runOnUiThread(() -> Toast.makeText(getContext(), "Screenshot taken!", Toast.LENGTH_SHORT).show());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getCurrentFractalIndex() {
		return currentFractalIndex;
	}

	public void setFractal(int index) {
		if (currentFractalIndex != index) {
			currentFractalIndex = index;
			if (threads.getFractal() != null && threads.getFractal().getViewGroupId() != 0) {
				((Activity)getContext()).findViewById(threads.getFractal().getViewGroupId()).setVisibility(GONE);
			}
			Fractal newFractal = Fractals.ALL[index];
			threads.setFractal(newFractal);
			if (newFractal.getViewGroupId() != 0) {
				((Activity)getContext()).findViewById(newFractal.getViewGroupId()).setVisibility(VISIBLE);
				if (newFractal instanceof FractalInput) {
					((EditText)((Activity)getContext()).findViewById(R.id.word)).setText(((FractalInput)newFractal).getWord());
				} else if (newFractal instanceof FractalSlider) {
					((SeekBar)((Activity)getContext()).findViewById(R.id.exponent)).setProgress((int) (10.0 * ((FractalSlider)newFractal).getSliderConstant()) + 100);
				} else if (newFractal instanceof FractalDualSlider) {
					double constantB = ((FractalDualSlider)newFractal).getSliderConstantB();
					double constantA = ((FractalDualSlider)newFractal).getSliderConstantA();
					((SeekBar)((Activity)getContext()).findViewById(R.id.imaginaryPart)).setProgress((int) ((constantB < 0 ? -100.0 : 100.0) * Math.sqrt(Math.abs(constantB)) + 200.0));
					((SeekBar)((Activity)getContext()).findViewById(R.id.realPart)).setProgress((int) ((constantA < 0 ? -100.0 : 100.0) * Math.sqrt(Math.abs(constantA)) + 200.0));
				}
			}
		}
	}

	public void updateSlider(int progress) {
		threads.updateMultibrot((progress - 100) / 10.0);
	}

	public void updateDualSlider(boolean real, int progress) {
		double n = (progress - 200) / 100.0;
		threads.updateJulia(real, n * n * (n < 0 ? -1 : 1));
	}

	public void updateInput(String newWord) {
		threads.updateLyapunov(newWord);
	}

	public boolean reset() {
		return threads.reset();
	}

	public void flip(boolean flipped) {
		threads.flip(flipped);
	}

	public void invalidateFromThread() {
		((Activity)getContext()).runOnUiThread(this::invalidate);
	}
}
