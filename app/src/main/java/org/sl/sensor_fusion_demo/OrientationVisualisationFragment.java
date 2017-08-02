package org.sl.sensor_fusion_demo;

import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import com.androidplot.Plot;
import com.androidplot.util.PlotStatistics;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import java.util.Arrays;
import org.sl.sensor_fusion_demo.orientationProvider.AccelerometerCompassProvider;
import org.sl.sensor_fusion_demo.orientationProvider.CalibratedGyroscopeProvider;
import org.sl.sensor_fusion_demo.orientationProvider.GravityCompassProvider;
import org.sl.sensor_fusion_demo.orientationProvider.ImprovedOrientationSensor1Provider;
import org.sl.sensor_fusion_demo.orientationProvider.ImprovedOrientationSensor2Provider;
import org.sl.sensor_fusion_demo.orientationProvider.OrientationProvider;
import org.sl.sensor_fusion_demo.orientationProvider.RotationVectorProvider;
import org.sl.sensor_fusion_demo.representation.EulerAngles;

/**
 * A fragment that contains the same visualisation for different orientation providers
 */
public class OrientationVisualisationFragment extends Fragment implements OnDrawFrameListener {
  public static final String ARG_SECTION_TITLE = "section_title";
  /**
   * The fragment argument representing the section number for this
   * fragment.
   */
  public static final String ARG_SECTION_NUMBER = "section_number";
  /**
   * The chart that will show values from sensor
   */
  //	private GLSurfaceView mChartGLSurfaceView;
  private static final int HISTORY_SIZE = 300;
  /**
   * The surface that will be drawn upon
   */
  private GLSurfaceView mGLSurfaceView;
  /**
   * The class that renders the cube
   */
  private CubeRenderer mRenderer;
  /**
   * The current orientation provider that delivers device orientation.
   */
  private OrientationProvider currentOrientationProvider;
  private XYPlot aprHistoryPlot = null;
  private SimpleXYSeries azimuthHistorySeries = null;
  //	private SimpleXYSeries pitchHistorySeries = null;
  //	private SimpleXYSeries rollHistorySeries = null;

  private Redrawer redrawer;

  @Override
  public void onResume() {
    // Ideally a game should implement onResume() and onPause()
    // to take appropriate action when the activity looses focus
    super.onResume();
    mGLSurfaceView.onResume();
  }

  @Override
  public void onPause() {
    // Ideally a game should implement onResume() and onPause()
    // to take appropriate action when the activity looses focus
    super.onPause();
    //		currentOrientationProvider.stop();
    mGLSurfaceView.onPause();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Initialise the orientationProvider
    switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
      case R.id.action_section1:
        currentOrientationProvider = new ImprovedOrientationSensor1Provider((SensorManager) getActivity()
            .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
        break;
      case R.id.action_section2:
        currentOrientationProvider = new ImprovedOrientationSensor2Provider((SensorManager) getActivity()
            .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
        break;
      case R.id.action_section3:
        currentOrientationProvider = new RotationVectorProvider((SensorManager) getActivity().getSystemService(
            SensorSelectionActivity.SENSOR_SERVICE));
        break;
      case R.id.action_section4:
        currentOrientationProvider = new CalibratedGyroscopeProvider((SensorManager) getActivity()
            .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
        break;
      case R.id.action_section5:
        currentOrientationProvider = new GravityCompassProvider((SensorManager) getActivity().getSystemService(
            SensorSelectionActivity.SENSOR_SERVICE));
        break;
      case R.id.action_section6:
        currentOrientationProvider = new AccelerometerCompassProvider((SensorManager) getActivity()
            .getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
        break;
      default:
        break;
    }

    View v = inflater.inflate(R.layout.fragment_visualization, container, false);

    //		mChartGLSurfaceView = (GLSurfaceView) v.findViewById(R.id.glChartSurfaceView);
    //		mChartGLSurfaceView.setEGLContextClientVersion(2);
    //		mChartGLSurfaceView.setPreserveEGLContextOnPause(true);
    //		mChartGLSurfaceView.setRenderer(new ChartRenderer());

    // Create our Preview view and set it as the content of our Activity
    mRenderer = new CubeRenderer();
    mRenderer.setOrientationProvider(currentOrientationProvider);
    mRenderer.setDrawFrameListener(this);
    mGLSurfaceView = (GLSurfaceView) v.findViewById(R.id.glSurfaceView);
    mGLSurfaceView.setRenderer(mRenderer);

    mGLSurfaceView.setOnLongClickListener(new OnLongClickListener() {

      @Override
      public boolean onLongClick(View v) {
        mRenderer.toggleShowCubeInsideOut();
        return true;
      }
    });

    setupChart(v);

    return v;
  }

  private void setupChart(View v) {
    aprHistoryPlot = (XYPlot) v.findViewById(R.id.aprHistoryPlot);

    azimuthHistorySeries = new SimpleXYSeries("Значення відносного позиціонування");
    azimuthHistorySeries.useImplicitXVals();
    //		pitchHistorySeries = new SimpleXYSeries("Pitch");
    //		pitchHistorySeries.useImplicitXVals();
    //		rollHistorySeries = new SimpleXYSeries("Roll");
    //		rollHistorySeries.useImplicitXVals();

    aprHistoryPlot.setTitle(getArguments().getString(ARG_SECTION_TITLE));
    aprHistoryPlot.setRangeBoundaries(-1, 1, BoundaryMode.AUTO);
    aprHistoryPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.AUTO);
    aprHistoryPlot.addSeries(azimuthHistorySeries,
        new LineAndPointFormatter(
            Color.rgb(100, 100, 200), null, null, null));
    //		aprHistoryPlot.addSeries(pitchHistorySeries,
    //				new LineAndPointFormatter(
    //						Color.rgb(100, 200, 100), null, null, null));
    //		aprHistoryPlot.addSeries(rollHistorySeries,
    //				new LineAndPointFormatter(
    //						Color.rgb(200, 100, 100), null, null, null));
    aprHistoryPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
    aprHistoryPlot.setDomainStepValue(HISTORY_SIZE / 15);
    aprHistoryPlot.setTicksPerRangeLabel(3);
    //		aprHistoryPlot.setDomainLabel("Sample Index");
    //		aprHistoryPlot.getDomainLabelWidget().pack();
    //		aprHistoryPlot.setRangeLabel("Angle (Degs)");
    //		aprHistoryPlot.getRangeLabelWidget().pack();

    //		aprHistoryPlot.setRangeValueFormat(new DecimalFormat("#"));
    //		aprHistoryPlot.setDomainValueFormat(new DecimalFormat("#"));

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.BLACK);
    paint.setTextSize(30);

    //		paint.setTextAlign(Paint.Align.CENTER);
    aprHistoryPlot.getLegendWidget().setTextPaint(paint);

    Paint paint1 = new Paint(paint);
    paint1.setTextAlign(Paint.Align.CENTER);
    paint1.setColor(Color.WHITE);
    aprHistoryPlot.getGraphWidget().setRangeLabelPaint(paint1);

    Paint paint2 = new Paint(paint1);
    paint2.setTextAlign(Paint.Align.CENTER);
    paint2.setColor(Color.WHITE);
    aprHistoryPlot.getGraphWidget().setDomainLabelPaint(paint2);

    final PlotStatistics histStats = new PlotStatistics(1000, false);
    aprHistoryPlot.addListener(histStats);

    redrawer = new Redrawer(
        Arrays.asList(new Plot[] {aprHistoryPlot}),
        100, false);
  }

  @Override
  public void onDrawFrame() {
    // get rid the oldest sample in history:
    if (azimuthHistorySeries.size() > HISTORY_SIZE) {
      //			rollHistorySeries.removeFirst();
      //			pitchHistorySeries.removeFirst();
      azimuthHistorySeries.removeFirst();
    }

    // add the latest history sample:
    EulerAngles values = currentOrientationProvider.getEulerAngles();
    azimuthHistorySeries.addLast(null, (values.getYaw() + values.getPitch() + values.getRoll()) / 3);
    //		pitchHistorySeries.addLast(null, values.getPitch());
    //		rollHistorySeries.addLast(null, values.getRoll());
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onStart() {
    super.onStart();
    currentOrientationProvider.start();
    mGLSurfaceView.onResume();
    redrawer.start();
  }

  @Override
  public void onStop() {
    redrawer.pause();
    currentOrientationProvider.stop();
    mGLSurfaceView.onPause();
    super.onStop();
  }
}