package nl.ngti.androscope.menu;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MenuItem implements IMenuItem {

    private final String mName;
    private final String mUri;
    private final List<IMenuItem> mItems = new ArrayList<>();

    public MenuItem(String name, String uri) {
        mName = name;
        mUri = uri;
    }

    @Override
    public void render(StringBuilder html) {
        if (!mItems.isEmpty()) {
            html.append("<div class=\"dropdown\">")
                    .append("<button class=\"dropbtn\">")
                    .append(mName)
                    .append("&nbsp;&#x25BE;")
                    .append("</button>\n")
                    .append("<div class=\"dropdown-content\">");
            for (IMenuItem item : mItems) {
                item.render(html);
            }
            html.append("</div>");
            html.append("</div>");
        } else {
            html.append("<a href=\"")
                    .append(mUri)
                    .append("\">")
                    .append(mName)
                    .append("</a>");
        }
    }

    @NonNull
    public MenuItem separator() {
        mItems.add(new Separator());
        return this;
    }

    @NonNull
    public MenuItem subItem(String name, String uri) {
        mItems.add(new MenuItem(name, uri));
        return this;
    }
}
