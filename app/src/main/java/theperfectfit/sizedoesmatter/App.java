package theperfectfit.sizedoesmatter;

import android.app.Application;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

/**
 * Created by plaga on 2/21/2016.
 */
public class App extends Application{

    @Override
    public void onCreate(){
        super.onCreate();

        SystemClock.sleep(TimeUnit.MILLISECONDS.toMillis(900));
    }
}
