package nl.ngti.androscope.menu;

final class Separator implements IMenuItem {

    @Override
    public void render(StringBuilder html) {
        html.append("<p/>");
    }
}
