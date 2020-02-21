package org.chipmaker;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GearEditor {
	
	protected final int zCount;
	protected int gearCount;
	protected EditText countEdit;
	protected final int defaultCount;
	private ImageButton plusButton;
	private ImageButton minusButton;
	private ImageButton xButton;

	public GearEditor(int zCount, int defaultCount) {
		this.zCount = zCount;
		this.gearCount = this.defaultCount = defaultCount;
	}

	public int getzCount() {
		return zCount;
	}
	
	public int getGearCount() {
		return gearCount;
	}
	
	public View createControl(Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		TextView view = new TextView(context);
		view.setText(zCount + "");
		view.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
		view.setLayoutParams(new LinearLayout.LayoutParams(20,LayoutParams.WRAP_CONTENT,0.2f));
		view.setGravity(Gravity.CENTER);
		layout.addView(view);
		countEdit = new EditText(context);
		countEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
		countEdit.setText(defaultCount + "");
		countEdit.setLayoutParams(new LinearLayout.LayoutParams(20,LayoutParams.WRAP_CONTENT,0.2f));
		countEdit.setGravity(Gravity.CENTER);
		countEdit.addTextChangedListener(new  TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
				int count = Integer.parseInt(countEdit.getText().toString());
				xButton.setEnabled(count > 0);
				minusButton.setEnabled(count > 0);
			}
			
			@Override
			public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
			}
			
			@Override
			public void afterTextChanged(Editable paramEditable) {
				gearCount = Integer.parseInt(countEdit.getText().toString());
			}
		});
		layout.addView(countEdit);
		plusButton = createButton(context,org.chipmaker.tv16gears.R.drawable.add, Gravity.CENTER, 20, LayoutParams.WRAP_CONTENT, 0.2f);
		plusButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				increaseCount();
			}
		});
		minusButton = createButton(context,org.chipmaker.tv16gears.R.drawable.remove, Gravity.CENTER, 20, LayoutParams.WRAP_CONTENT, 0.2f);
		minusButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				decreaseCount();
			}
		});
		xButton = createButton(context,org.chipmaker.tv16gears.R.drawable.delete, Gravity.CENTER, 20, LayoutParams.WRAP_CONTENT, 0.2f);
		xButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nullCount();
			}
		});
		layout.addView(plusButton);
		layout.addView(minusButton);
		layout.addView(xButton);
		
		xButton.setEnabled(defaultCount > 0);
		minusButton.setEnabled(defaultCount > 0);
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.FILL_PARENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(layoutParams);
		return layout;
	}
	
	protected void nullCount() {
		countEdit.setText("0");
	}

	protected void increaseCount() {
		int oldCount = Integer.parseInt(countEdit.getText().toString());
		countEdit.setText(oldCount + 1 + "");
	}
	
	protected void decreaseCount() {
		int oldCount = Integer.parseInt(countEdit.getText().toString());
		if (oldCount > 0)
			countEdit.setText(oldCount - 1 + "");
	}

	protected ImageButton createButton(Context context, int drawable, int gravity, int wConstraint, int hConstraint, float weight) {
		ImageButton view = new ImageButton(context);
		view.setImageResource(drawable);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wConstraint,hConstraint);
		layoutParams.weight = weight;
		view.setLayoutParams(layoutParams);
		return view;
	}
}
