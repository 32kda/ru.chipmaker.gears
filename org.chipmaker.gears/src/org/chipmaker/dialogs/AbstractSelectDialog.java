package org.chipmaker.dialogs;

import java.util.Collection;

import org.chipmaker.tv16gears.R;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class AbstractSelectDialog<T> extends CustomHeaderDialog {

	protected Collection<T> items;
	protected T result = null;
	protected boolean cancellable = true;
	protected ArrayAdapter<T> adapter;
	private AdapterView<?> listView;

	public AbstractSelectDialog(Context context, int theme,
			Collection<T> items) {
		super(context, theme);
		this.items = items;
	}

	public AbstractSelectDialog(Context context, Collection<T> items
			) {
		this(context, 0, items);
	}
	
	public AbstractSelectDialog(Context context, Collection<T> items,
			boolean cancellable) {
		this(context, 0, items);
		this.cancellable = cancellable;
	}

	@Override
	protected View createContents() {
		final Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		createTopControls(layout);
		LayoutParams wrapParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		wrapParams.weight = 0;
		listView = createAdapterView(context);
		if (items != null) {
			adapter = this.configureAdapter(context, listView);
		}
		LinearLayout ll0 = new LinearLayout(context);
		ll0.addView(listView, new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		LayoutParams gridParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		gridParams.weight = 2;
		ll0.setMinimumHeight((int) (new TextView(context).getTextSize() * 8));
		layout.addView(ll0, gridParams);
		if (cancellable) {
			Button cancelButton = new Button(context);
			cancelButton.setText(R.string.cancel);
			cancelButton.setOnClickListener(new View.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					AbstractSelectDialog.this.result = null;
					AbstractSelectDialog.this.dismiss();
				}
			});
			layout.addView(cancelButton, wrapParams);
		}
		layout.setMinimumWidth(300);
		// layout.setMinimumHeight((int) (new TextView(context).getTextSize() *
		// 4));
		return layout;
	}

	protected void createTopControls(LinearLayout layout) {
		//Do nothing; Override if needed
	}

	protected AdapterView<ListAdapter> createAdapterView(Context context) {
		return new ListView(context);
	}

	protected abstract ArrayAdapter<T> configureAdapter(Context context,
			AdapterView<?> listView);

	public T getResult() {
		return this.result;
	}

	public Collection<T> getItems() {
		return items;
	}

	public void setItems(Collection<T> items) {
		this.items = items;
		if (listView != null) {
			adapter = configureAdapter(listView.getContext(),listView);
		}
	}

}
