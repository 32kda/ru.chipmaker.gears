package org.chipmaker.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import org.chipmaker.tv16gears.R;

import android.content.Context;
import android.opengl.Visibility;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CalculationResultDialog extends AbstractSelectDialog<int[]> {
	
	class SortingComparator implements Comparator<int[]> {
		
		int column = 0;
		
		boolean ascending = true;

		@Override
		public int compare(int[] lhs, int[] rhs) {
			int res = lhs[column] - rhs[column];
			if (!ascending)
				res = -res;
			return res;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}

		public boolean isAscending() {
			return ascending;
		}

		public void setAscending(boolean ascending) {
			this.ascending = ascending;
		}
		
	}
	
	protected SortingComparator sortingComparator = new SortingComparator();
	
	protected int currentSortColumn = -1;
	protected boolean ascending = false;

	protected TextView statusTextView;

	private View headerView;

	public CalculationResultDialog(Context context, Collection<int[]> items) {
		super(context, items, false);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ArrayAdapter<int[]> configureAdapter(final Context context, AdapterView listView) {
		final ArrayAdapter<int[]> adapter = new ArrayAdapter<int[]>(context,
				listView.getId(), new ArrayList<int[]>(this.items)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView instanceof LinearLayout) {
					int[] item = this.getItem(position);
					if (((LinearLayout) convertView).getChildCount() == item.length) { 
						for (int i = 0; i < item.length; i++) {
							((TextView) ((LinearLayout)convertView).getChildAt(i)).setText(""+item[i]);
						}
						return convertView;
					} else
						convertView = null;
				}
				if (convertView == null) {
					LinearLayout layout = new LinearLayout(context);
					int[] item = this.getItem(position);
					float weight = 1.0f / item.length;
					for (int i = 0; i < item.length; i++) {
						layout.addView(createTextView(item[i],context,weight));
					}
					return layout;
				}
				return super.getView(position, convertView, parent);
			}
		};
		listView.setAdapter(adapter);
		return adapter;
	}
	
	@Override
	protected void createTopControls(LinearLayout layout) {
		headerView = createTableHeaderView(layout.getContext());
		headerView.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.addView(headerView);
		statusTextView = new TextView(layout.getContext());
		statusTextView.setGravity(Gravity.CENTER);
		statusTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
		statusTextView.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.addView(statusTextView);
	}

	protected View createTableHeaderView(Context context) {
		LinearLayout layout = new LinearLayout(context);
		final Button buttonA = createSortButton(context,"A",0.25f);
		final Button buttonB = createSortButton(context,"B",0.25f);
		final Button buttonC = createSortButton(context,"C",0.25f);
		final Button buttonD = createSortButton(context,"D",0.25f);
		View.OnClickListener sortClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				int newSortColumn = -1;
				if (paramView == buttonA)
					newSortColumn = 0;
				else if (paramView == buttonB)
					newSortColumn = 1;
				else if (paramView == buttonC)
					newSortColumn = 2;
				else if (paramView == buttonD)
					newSortColumn = 3;
				if (newSortColumn != currentSortColumn) {
					currentSortColumn = newSortColumn;
					ascending = true;
				} else {
					ascending = !ascending;
				}
				updateSorting();
			}
		};
		buttonA.setOnClickListener(sortClickListener);
		buttonB.setOnClickListener(sortClickListener);
		buttonC.setOnClickListener(sortClickListener);
		buttonD.setOnClickListener(sortClickListener);
		layout.addView(buttonA);
		layout.addView(buttonB);
		layout.addView(buttonC);
		layout.addView(buttonD);
		return layout;
	}
	
	protected void updateSorting() {
		sortingComparator.setColumn(currentSortColumn);
		sortingComparator.setAscending(ascending);
		adapter.sort(sortingComparator);
	}

	protected Button createSortButton(Context context, String text, float weight) {
		Button button = new Button(context);
		button.setText(text);
		button.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
		button.setLayoutParams(new LinearLayout.LayoutParams(20,LayoutParams.WRAP_CONTENT,weight));
		button.setGravity(Gravity.CENTER);
		return button;
	}

	protected TextView createTextView(int value, Context context, float weight) {
		TextView textView = new TextView(context);
		textView.setText(value + "");
		textView.setLayoutParams(new LinearLayout.LayoutParams(20,LayoutParams.WRAP_CONTENT,weight));
		textView.setTextSize(18,TypedValue.COMPLEX_UNIT_SP);
		textView.setGravity(Gravity.CENTER);
		textView.setPadding(4, 5, 4, 5);
		return textView;
	}
	
	@Override
	protected boolean needScrollView() {
		return false;
	}

	@Override
	public void setItems(Collection<int[]> items) {
		if (items.size() == 0) {
			headerView.setVisibility(View.GONE);
			statusTextView.setVisibility(View.VISIBLE);
			statusTextView.setText(R.string.no_result);
		} else {
			statusTextView.setVisibility(View.GONE);
			headerView.setVisibility(View.VISIBLE);
			statusTextView.setText("");
		}
		super.setItems(items);
	}
}
