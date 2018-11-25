package fi.iki.elonen.velocity;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class VelocityAsset {

    private VelocityContext velocityContext;
    private InputStream file;

    public void initMainAsset(Context mContext) throws IOException {
        initAsset(mContext, "main.html");
    }

    public void initAsset(Context mContext, String assetPath) throws IOException {
        Velocity.init();
        velocityContext = new VelocityContext();
        file = mContext.getAssets().open(assetPath);
    }

    public void put(String name, String value) {
        velocityContext.put(name, value);
    }

    public String html() {
        StringWriter swOut = new StringWriter();
        Velocity.evaluate(velocityContext, swOut, "androscope", new InputStreamReader(file));
        return swOut.getBuffer().toString();
    }
}
