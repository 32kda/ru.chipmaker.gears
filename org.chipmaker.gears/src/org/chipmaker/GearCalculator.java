package org.chipmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

public class GearCalculator {
	private static final String GEAR_PROP_ID = "gear";
	private static final String GEARS_ID = "gears";
	private static final String DEFCOUNT_ID = "defcount";
	protected static final double E = 0.00001;
	protected final Context context;
	protected List<Integer> gears;
	protected List<Integer> defCount;
	protected Map<Integer,Integer> savedCounts = new HashMap<Integer, Integer>();
	protected List<GearEditor> editors = new ArrayList<GearEditor>();

	public GearCalculator(Context context) {
		this.context = context;
		readGearConfig();
	}

	protected void readGearConfig() {
		try {
			readGearList("gears.txt");
			loadGearSet();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadGearSet() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(GearsActivity.SHARED_PROPS_ID,Activity.MODE_PRIVATE);
		for (Integer zCount : gears) {
			int value = sharedPreferences.getInt(GEAR_PROP_ID + zCount,-1);
			if (value > -1)
				savedCounts.put(zCount,value);
		}
	}

	public void createControls(LinearLayout layout) {
		ScrollView scrollView = new ScrollView(context);
		scrollView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		scrollView.setFillViewport(true);
		layout.addView(scrollView);
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		int size = gears.size();
		int defListSize = defCount.size();
		for (int i = 0; i < size; i++) {
			int defaultCount = 1;
			Integer currentZCount = gears.get(i);
			if (savedCounts.containsKey(currentZCount)) {
				defaultCount = savedCounts.get(currentZCount);
			} else if (size == defListSize)
				defaultCount = defCount.get(i);
			GearEditor editor = new GearEditor(currentZCount,defaultCount);
			linearLayout.addView(editor.createControl(context));
			editors.add(editor);
		}
		scrollView.addView(linearLayout);
	}
	
	public List<int[]> calculateGears(double step) {
		Map <Integer,Integer> gearCounts = new HashMap<Integer, Integer>();
		for (GearEditor editor : editors) {
			int gearCount = editor.getGearCount();
			if (gearCount > 0) {
				gearCounts.put(editor.getzCount(),gearCount);
			}
		}
		int[] zCounts = new int[gearCounts.size()];
		int[] counts = new int[gearCounts.size()];
		
		int i = 0;
		for (Integer zCount : gearCounts.keySet()) {
			zCounts[i] = zCount;
			counts[i++] = gearCounts.get(zCount);
		}
		
		List<int[]> bestSet = calculateBestSet(zCounts, counts, step);
		return bestSet;
	}

	protected List<int[]> calculateBestSet(int[] zCounts, int[] counts, double step) {
		ArrayList<int[]> resultList = new ArrayList<int[]>();
		int[] currentCounts = new int[counts.length];
		System.arraycopy(counts,0,currentCounts,0,counts.length);
		for (int i = 0; i < counts.length; i++) {
			if (zCounts[i] <= 60) { //TODO investigate & introduce better restrictions
				int a = zCounts[i]; //Take i th gear as possible gear for pos a and decrease rest count
				currentCounts[i] --;
				for (int j = 0; j < counts.length; j++) {
					if (currentCounts[j] > 0) {
						int b = zCounts[j]; //Take j th gear as possible gear for pos b and decrease rest count
						currentCounts[j] --;
						for (int k = 0; k < counts.length; k++) {
							if (currentCounts[k] > 0) {
								int c = zCounts[k];
								currentCounts[k] --;
								for (int l = 0; l < counts.length; l++) {
									if (currentCounts[l] > 0) {
										int d = zCounts[l];
										currentCounts[l] --;
										if (checkCombination(a,b,c,d,step)) {
											resultList.add(new int[]{a,b,c,d});
										}
										currentCounts[l] ++;
									}
								}
								currentCounts[k] ++;
							}
						}
						currentCounts[j] ++; //Increase rest count again after the end of checking current combination
					}
				}
				currentCounts[i] ++; //Increase rest count again after the end of checking current combination
			}
		}
		if (resultList.size() > 0)
			return resultList; //TODO Select ACTUALLY best or return all possible combinations
		return Collections.emptyList();
	}

	private boolean checkCombination(int a, int b, int c, int d, double step) {
		boolean availableCombination = (a + b) >= (c + 15) && (c + d) >= (b + 15);
		double actualStep = 3.0 * (a * 1.0 / b) * (c * 1.0 / d);
		return availableCombination && Math.abs(step - actualStep) < E; 
	}

	protected void readGearList(String assetFileName) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetFileName)));
		while (reader.ready()) {
			String line = reader.readLine();
			if (line.startsWith(DEFCOUNT_ID)) {
				line = line.substring(DEFCOUNT_ID.length());
				defCount = readIntList(line);
			} else {
				if (line.startsWith(GEARS_ID))
					line = line.substring(GEARS_ID.length());
				gears = readIntList(line);
			}
		}
	}

	protected List<Integer> readIntList(String line) {
		StringTokenizer st = new StringTokenizer(line,",");
		List<Integer> arrayList = new ArrayList<Integer>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			try {
				arrayList.add(Integer.parseInt(token));
			} catch (NumberFormatException e) {
				//Just ignore incorrect input
			}
		}
		return arrayList;
	}

	public void saveGearSet(Editor editor) {
		for (GearEditor gearEditor: editors) {
			editor.putInt(GEAR_PROP_ID + gearEditor.getzCount(),gearEditor.getGearCount());
		}
	}
}
