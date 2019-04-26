package nl.ngti.androscope.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    List<MenuItem> items = new ArrayList<>();

    private final String name;
    private final String uri;

    public MenuItem(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public void render(StringBuilder html) {
        if (!items.isEmpty()) {
            html.append("<div class=\"dropdown\">")
                .append("<button class=\"dropbtn\">")
                .append(name)
                .append("&nbsp;&#x25BE;")
                .append("</button>\n")
                .append("<div class=\"dropdown-content\">");
            for (MenuItem item : items) {
                item.render(html);
            }
            html.append("</div>");
            html.append("</div>");
        } else {
            html.append("<a href=\"")
                .append(uri)
                .append("\">")
                .append(name)
                .append("</a>");
        }
    }


    public MenuItem subItem(String name, String uri) {
        items.add(new MenuItem(name, uri));
        return this;
    }

    public void addToMenu(Menu menu) {
        menu.addItem(this);
    }
}
