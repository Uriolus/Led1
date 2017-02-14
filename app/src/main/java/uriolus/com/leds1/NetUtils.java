package uriolus.com.leds1;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by oriolfernandez on 6/2/17.
 */

public class NetUtils {
  public static String getWifiIp(Context context) {
    WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    return  ip;
  }
  public static String getLocalIpAddress(boolean useIPv4) {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
          if (!addr.isLoopbackAddress()) {
            String sAddr = addr.getHostAddress();
            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
            boolean isIPv4 = sAddr.indexOf(':')<0;

            if (useIPv4) {
              if (isIPv4)
                return sAddr;
            } else {
              if (!isIPv4) {
                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
              }
            }
          }
        }
      }
    } catch (Exception ex) { } // for now eat exceptions
    return "";
  }

}
