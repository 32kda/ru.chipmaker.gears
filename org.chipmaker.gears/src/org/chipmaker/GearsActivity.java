package org.chipmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.chipmaker.dialogs.CalculationResultDialog;
import org.chipmaker.dialogs.PitchSelectDialog;
import org.chipmaker.tv16gears.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class GearsActivity extends Activity {
	
	public static final String SHARED_PROPS_ID = "tv16gear";

	private static final String CURRENT_STEP_PREF_ID = "currentStep";
	
	private static final int RESULT_DIALOG_ID = 1;
	
    private GearCalculator calculator;
	private TextView resultView;
	private EditText stepText;
	private List<String> possibleSteps;
	private List<int[]> result;


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        setContentView(createMainView());
    }

	private View createMainView() {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView view = createTextView(R.string.pitch_title, Gravity.CENTER,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		layout.addView(view);
		LinearLayout topLine = new LinearLayout(this);
		topLine.setOrientation(LinearLayout.HORIZONTAL);
		stepText = new EditText(this);
		stepText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		stepText.setText(getDefaultStep());
		stepText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0.5f));
		topLine.addView(stepText);
		Button selectButton = new Button(this);
		selectButton.setText(R.string.select_title);
		selectButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
		selectButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0.25f));
		selectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				showSelectStepDialog();
			}
		});
		topLine.addView(selectButton);
		Button calculateButton = new Button(this);
		calculateButton.setText(R.string.calc_title);
		calculateButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
		calculateButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,0.25f));
		calculateButton.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				showDialog(RESULT_DIALOG_ID);
//				CalculationResultDialog resultDialog = new CalculationResultDialog(GearsActivity.this,result);
//				resultDialog.setTitle("иру " + stepText.getText().toString().trim());
//				resultDialog.show();
			}
		});
		topLine.addView(calculateButton);
		layout.addView(topLine);
		resultView = createTextView("", Gravity.CENTER,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		layout.addView(resultView);
		view = createTextView(R.string.gear_title, Gravity.CENTER,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		layout.addView(view);
		calculator = new GearCalculator(this);
		calculator.createControls(layout);
		calculateButton.setFocusable(true);
		calculateButton.setFocusableInTouchMode(true);
		calculateButton.requestFocus();
		return layout;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == RESULT_DIALOG_ID) {
			CalculationResultDialog resultDialog = new CalculationResultDialog(GearsActivity.this,result);
			return resultDialog;
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (dialog instanceof CalculationResultDialog) {
			CalculationResultDialog resultDialog = (CalculationResultDialog) dialog;
			result = calculator.calculateGears(Double.parseDouble(stepText.getText().toString()));
			resultDialog.setItems(result);
			resultDialog.setTitle(getResources().getString(R.string.pitch) + " " + stepText.getText().toString().trim());
		}
		super.onPrepareDialog(id, dialog);
	}

	protected CharSequence getDefaultStep() {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PROPS_ID,0);
		return sharedPreferences.getString(CURRENT_STEP_PREF_ID,"1");
	}

	protected void showSelectStepDialog() {
		final PitchSelectDialog dialog = new PitchSelectDialog(this, getPossibleSteps());
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface paramDialogInterface) {
				String result = dialog.getResult();
				if (result != null && !result.trim().isEmpty()) {
					stepText.setText(result);
//					showDialog(RESULT_DIALOG_ID);
				}
			}
		});
		dialog.show();
	}

	protected Collection<String> getPossibleSteps() {
		if (possibleSteps == null) {
			try {
				possibleSteps = new ArrayList<String>();
				BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("steps.txt")));
				while (reader.ready()) {
					String line = reader.readLine();
					StringTokenizer tokenizer = new StringTokenizer(line, ",");
					while (tokenizer.hasMoreTokens()) {
						String str = tokenizer.nextToken().trim();
						try {
							Double.parseDouble(str);
							possibleSteps.add(str);
						} catch (NumberFormatException e) {
							// Ignore; Used to filter incorrect value from input
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return possibleSteps;
	}

	protected TextView createTextView(String text, int gravity, int wConstraint, int hConstraint) {
		TextView view = new TextView(this);
		view.setGravity(gravity);
		view.setText(text);
		view.setLayoutParams(new LinearLayout.LayoutParams(wConstraint,hConstraint));
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		return view;
	}
	
	protected TextView createTextView(int strId, int gravity, int wConstraint, int hConstraint) {
		TextView view = new TextView(this);
		view.setGravity(gravity);
		view.setText(strId);
		view.setLayoutParams(new LinearLayout.LayoutParams(wConstraint,hConstraint));
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		return view;
	}
	
	@Override
	protected void onPause() {
		SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PROPS_ID,Activity.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(CURRENT_STEP_PREF_ID, stepText.getText().toString());
		calculator.saveGearSet(editor);
		editor.commit();
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(R.string.about);
		item.setIcon(R.drawable.info);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				final Dialog dialog = new Dialog(GearsActivity.this);
				dialog.setTitle(R.string.about);
				LinearLayout view = new LinearLayout(GearsActivity.this);
				view.setOrientation(LinearLayout.VERTICAL);
				view.addView(createTextView(R.string.author,Gravity.CENTER,android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				view.addView(createTextView("32kda, OnPositive",Gravity.CENTER,android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				view.addView(createTextView(R.string.license,Gravity.CENTER,android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				view.addView(createTextView("GNU GPL v 2",Gravity.CENTER,android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				view.addView(createTextView("2012",Gravity.CENTER,android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				Button dismissButton = new Button(GearsActivity.this);
				dismissButton.setText(R.string.close);
				dismissButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
				dismissButton.setGravity(Gravity.CENTER);
				dismissButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				view.addView(dismissButton);
				dialog.setContentView(view);
				dialog.show();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
}