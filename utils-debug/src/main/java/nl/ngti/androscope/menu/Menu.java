package nl.ngti.androscope.menu;

import java.util.ArrayList;
import java.util.List;

import static nl.ngti.androscope.responses.filebrowser.HtmlResponseShowFolder.getAssetsExplorerLink;

public class Menu {

    List<MenuItem> items = new ArrayList<>();

    public void addItem(String name, String uri) {
        items.add(new MenuItem(name, uri));
    }

    public void render(StringBuilder html) {
        html.append("<link href=\"" + getAssetsExplorerLink("navbar.css") + "\" rel=\"stylesheet\" type=\"text/css\">");
        html.append("<div class=\"navbar\">");
        for (MenuItem item : items) {
            item.render(html);
        }
        html.append("</div>");
    }

    public String render() {
        StringBuilder html = new StringBuilder();
        render(html);
        return html.toString();
    }

    protected void addItem(MenuItem menuItem) {
        items.add(menuItem);
    }
}
