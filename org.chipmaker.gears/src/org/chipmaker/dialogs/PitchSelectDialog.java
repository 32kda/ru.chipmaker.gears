package org.chipmaker.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.chipmaker.tv16gears.R;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Thread pitch select dialog
 * @author 32kda
 * 
 */
public class PitchSelectDialog extends AbstractSelectDialog<String> {

	View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View paramView) {
			setResult(((TextView)paramView).getText().toString());
		}
	};
	
	public PitchSelectDialog(Context context, Collection<String> items) {
		this(context, 0, items);
	}

	public PitchSelectDialog(Context context, int theme,
			Collection<String> items) {
		super(context, theme, items);
		this.setTitle(R.string.select_pitch);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ArrayAdapter<String> configureAdapter(final Context context,
			AdapterView listView) {
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				listView.getId(), new ArrayList<String>(this.items)) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					Button textView = new Button(context);
					textView.setText(this.getItem(position));
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
					textView.setPadding(4, 10, 4, 10);
					textView.setOnClickListener(clickListener);
					return textView;
				}
				return super.getView(position, convertView, parent);
			}
		};
		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				setResult(adapter.getItem(position));
//				StringSelectDialog.this.dismiss();
//			}
//		});
		return adapter;
	}
	
	@Override
	protected AdapterView<ListAdapter> createAdapterView(Context context) {
		GridView gridView = new GridView(context);
		gridView.setColumnWidth(50);
		gridView.setNumColumns(GridView.AUTO_FIT);
		return gridView;
	}

	protected void setResult(String item) {
		result = item;
		dismiss();
	}

}
