package uriolus.com.leds1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
  private TextView textIp;
  private static final String TAG = MainActivity.class.getSimpleName();

  private Gpio mLedGpio;
  private ButtonInputDriver mButtonInputDriver;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViews();
    configureHardware();
  }

  private void configureHardware() {
    PeripheralManagerService pioService = new PeripheralManagerService();
    try {
      Log.i(TAG, "Configuring GPIO pins");
      mLedGpio = pioService.openGpio(BoardDefaults.getGPIOForLED());
      mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

      Log.i(TAG, "Registering button driver");
      // Initialize and register the InputDriver that will emit SPACE key events
      // on GPIO state changes.
      mButtonInputDriver = new ButtonInputDriver(BoardDefaults.getGPIOForButton(),
          Button.LogicState.PRESSED_WHEN_LOW, KeyEvent.KEYCODE_SPACE);
      mButtonInputDriver.register();
    } catch (IOException e) {
      Log.e(TAG, "Error configuring GPIO pins", e);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    showIp();
  }

  private void showIp() {
    //String ip=NetUtils.getIp(this);
    String ip = NetUtils.getLocalIpAddress(true);
    textIp.setText(ip);
  }

  private void findViews() {
    textIp = (TextView) findViewById(R.id.text_ip);
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_SPACE) {
      Log.d(TAG, "Button pressed");
      // Turn on the LED

      setLedValue(true);
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_SPACE) {
      // Turn off the LED
      setLedValue(false);
      return true;
    }

    return super.onKeyUp(keyCode, event);
  }

  /**
   * Update the value of the LED output.
   */
  private void setLedValue(boolean value) {
    try {
      mLedGpio.setValue(value);
    } catch (IOException e) {
      Log.e(TAG, "Error updating GPIO value", e);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    if (mButtonInputDriver != null) {
      mButtonInputDriver.unregister();
      try {
        mButtonInputDriver.close();
      } catch (IOException e) {
        Log.e(TAG, "Error closing Button driver", e);
      } finally {
        mButtonInputDriver = null;
      }
    }

    if (mLedGpio != null) {
      try {
        mLedGpio.close();
      } catch (IOException e) {
        Log.e(TAG, "Error closing LED GPIO", e);
      } finally {
        mLedGpio = null;
      }
      mLedGpio = null;
    }
  }
}
