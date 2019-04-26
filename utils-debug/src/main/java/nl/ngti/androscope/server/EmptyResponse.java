package nl.ngti.androscope.server;

import androidx.annotation.Nullable;

import nl.ngti.androscope.menu.MenuItem;

public class EmptyResponse extends BaseHtmlResponse {

    @Nullable
    @Override
    protected MenuItem getMenuItem() {
        return null;
    }

    @Override
    protected void onProvideContent(StringBuilder content) {
        content.append("Empty");
    }
}
