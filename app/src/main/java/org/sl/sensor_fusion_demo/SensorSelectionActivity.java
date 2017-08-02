package org.sl.sensor_fusion_demo;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import java.util.Locale;

/**
 * The main activity where the user can select which sensor-fusion he wants to try out
 *
 * @author Stanislav Lelyuk
 */
public class SensorSelectionActivity extends AppCompatActivity {

  Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sensor_selection);

    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mToolbar.setTitle(getPageTitle(R.id.action_section1));
    mToolbar.inflateMenu(R.menu.sensor_selection);
    mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, getItem(item.getItemId()), "fragment_sensors")
            .commit();

        mToolbar.setTitle(getPageTitle(item.getItemId()));
        return true;
      }
    });

    // Check if device has a hardware gyroscope
    SensorChecker checker = new HardwareChecker((SensorManager) getSystemService(SENSOR_SERVICE));
    if (!checker.IsGyroscopeAvailable()) {
      // If a gyroscope is unavailable, display a warning.
      displayHardwareMissingWarning();
    }

    getSupportFragmentManager().beginTransaction()
        .replace(R.id.container, getItem(R.id.action_section1), "fragment_sensors")
        .commit();
  }

  private void displayHardwareMissingWarning() {
    AlertDialog ad = new AlertDialog.Builder(this).create();
    ad.setCancelable(false); // This blocks the 'BACK' button
    ad.setTitle(getResources().getString(R.string.gyroscope_missing));
    ad.setMessage(getResources().getString(R.string.gyroscope_missing_message));
    ad.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    ad.show();
  }

  public Fragment getItem(int id) {
    // getItem is called to instantiate the fragment for the given page.
    // Return a DummySectionFragment (defined as a static inner class
    // below) with the page number as its lone argument.
    Fragment fragment = new OrientationVisualisationFragment();
    Bundle args = new Bundle();
    args.putInt(OrientationVisualisationFragment.ARG_SECTION_NUMBER, id);
    args.putString(OrientationVisualisationFragment.ARG_SECTION_TITLE,
        String.valueOf(getPageTitle(id)));
    fragment.setArguments(args);
    return fragment;
  }

  public CharSequence getPageTitle(int id) {
    Locale l = Locale.getDefault();
    switch (id) {
      case R.id.action_section1:
        return getString(R.string.title_section1).toUpperCase(l);
      case R.id.action_section2:
        return getString(R.string.title_section2).toUpperCase(l);
      case R.id.action_section3:
        return getString(R.string.title_section3).toUpperCase(l);
      case R.id.action_section4:
        return getString(R.string.title_section4).toUpperCase(l);
      case R.id.action_section5:
        return getString(R.string.title_section5).toUpperCase(l);
      case R.id.action_section6:
        return getString(R.string.title_section6).toUpperCase(l);
    }
    return null;
  }
}
