package sal.fractalexplorer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

	FractalView fractalView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 42);

		fractalView = findViewById(R.id.fracView);

		((ProgressBar)findViewById(R.id.progressBar)).setProgress(0);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> {
			fractalView.reset();
		});

		((SeekBar)findViewById(R.id.realPart)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				fractalView.updateDualSlider(true, progress);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		((SeekBar)findViewById(R.id.imaginaryPart)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				fractalView.updateDualSlider(false, progress);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		((SeekBar)findViewById(R.id.exponent)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				fractalView.updateSlider(progress);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		EditText word = findViewById(R.id.word);
		word.addTextChangedListener(new TextWatcher() {
			private boolean invalid = false;
			private Drawable deflt = null;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() != 0) {
					fractalView.updateInput(s.toString());
					if (invalid) {
						word.setBackground(deflt);
						invalid = false;
					}
				} else {
					if (!invalid) {
						if (deflt == null) {
							deflt = word.getBackground();
						}
						word.setBackgroundColor(Color.RED);
						invalid = true;
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_invert) {
			// TODO: Ugly.
			item.setChecked(!item.isChecked());
			fractalView.flip(item.isChecked());
			return true;
		} else if (id == R.id.action_screenshot) {
			fractalView.screenshot();
			return true;
		} else if (id == R.id.action_change) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setTitle(R.string.alert_change);
			builder.setSingleChoiceItems(fractalView.FRACTAL_NAMES, fractalView.getCurrentFractalIndex(), (DialogInterface dialog, int which) -> {
				fractalView.setFractal(which);
			});
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
		}

		return false;
	}

	@Override
	public void onBackPressed() {
		if (!fractalView.reset()) {
			super.onBackPressed();
		}
	}
}
