package com.salesforce.ChatterSentimentsApp;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow.LayoutParams;
import android.widget.Toast;

public class DetailActivity extends ListActivity {

	public static final String TYPE = "type";

	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.RED, Color.CYAN };

	private CategorySeries mSeries = new CategorySeries("Sentiments");

	private DefaultRenderer mRenderer = new DefaultRenderer();

	private String mDateFormat;

	private Button mAdd;

	private EditText mX;

	private GraphicalView mChartView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		@SuppressWarnings("unchecked")
		ArrayList<String> sList = (ArrayList<String>) b.get("sList");
		ListView l = getListView();
		
		/* listView.addFooterView(footer); */
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_gallery_item, android.R.id.text1, sList));

		setContentView(R.layout.detail_topic);

		// mRenderer.setApplyBackgroundColor(true);
		// mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setChartTitle("Sentiments Distribution");
		mRenderer.setShowLabels(true);

		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 190, 30, 15, 0 });
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(90);

		mSeries.add(
				"Positive",
				Double.valueOf(sList.get(4).substring(
						sList.get(4).indexOf(":") + 1)));
		mSeries.add(
				"Negative",
				Double.valueOf(sList.get(2).substring(
						sList.get(2).indexOf(":") + 1)));

		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(Color.parseColor("#88B131"));
		mRenderer.addSeriesRenderer(renderer);
		renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[2]);
		mRenderer.addSeriesRenderer(renderer);
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			RelativeLayout header = (RelativeLayout) findViewById(R.id.header1);
			
			Button btn = (Button) findViewById(R.id.back_button);
			//btn.setVisibility(View.VISIBLE);
			mChartView = ChartFactory.getPieChartView(getApplicationContext(),
					mSeries, mRenderer);
			// mRenderer.setClickEnabled(true);
			//mRenderer.setSelectableBuffer(10);
			/*mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(DetailActivity.this,
								"No chart element was clicked",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								DetailActivity.this,
								"Chart element data point index "
										+ seriesSelection.getPointIndex()
										+ " was clicked" + " point value="
										+ seriesSelection.getValue(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});*/
			/*
			 * mChartView.setOnLongClickListener(new View.OnLongClickListener()
			 * {
			 * 
			 * @Override public boolean onLongClick(View v) { SeriesSelection
			 * seriesSelection = mChartView.getCurrentSeriesAndPoint(); if
			 * (seriesSelection == null) {
			 * Toast.makeText(AndroidUIPlaygroundActivity.this,
			 * "No chart element was long pressed", Toast.LENGTH_SHORT); return
			 * false; // no chart element was long pressed, so let something //
			 * else handle the event } else {
			 * Toast.makeText(AndroidUIPlaygroundActivity.this,
			 * "Chart element data point index " +
			 * seriesSelection.getPointIndex() + " was long pressed",
			 * Toast.LENGTH_SHORT); return true; // the element was long pressed
			 * - the event has been // handled } } });
			 */
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	/**
	 * Called when "Home" button is clicked.
	 * 
	 * @param v
	 */
	public void onHomeClick(View v) {
		
		Intent myIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		finish();
		startActivity(myIntent);
	}
	
	/**
	 * Called when "Home" button is clicked.
	 * 
	 * @param v
	 */
	public void onBackClick(View v) {
		
		Intent myIntent = new Intent(getApplicationContext(),
				MainActivity.class);
		finish();
		setContentView(R.layout.results);
	}

}
