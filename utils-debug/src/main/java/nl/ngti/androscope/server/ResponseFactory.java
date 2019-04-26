package nl.ngti.androscope.server;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import nl.ngti.androscope.menu.Menu;
import nl.ngti.androscope.menu.MenuItem;

public final class ResponseFactory {

    private final Context mContext;
    private final HashMap<String, BaseResponse> mResponses = new HashMap<>();

    private final EmptyResponse mEmptyResponse = new EmptyResponse();

    public ResponseFactory(Context context) {
        mContext = context;

        addResponses(mResponses);
        mResponses.put(null, mEmptyResponse);

        final Menu menu = new Menu();

        for (BaseResponse response : mResponses.values()) {
            response.init(mContext);

            if (response instanceof BaseHtmlResponse) {
                final BaseHtmlResponse htmlResponse = ((BaseHtmlResponse) response);
                htmlResponse.setMenu(menu);

                final MenuItem menuItem = htmlResponse.getMenuItem();
                if (menuItem != null) {
                    menu.addItem(menuItem);
                }
            }
        }
    }

    private static void addResponses(Map<String, BaseResponse> responses) {
        // HTML responses
        responses.put("", new HomeResponse());
        responses.put("files", new FileExplorerResponse());

        // System responses
        responses.put("asset", new AssetResponse());
    }

    @NonNull
    public BaseResponse getResponse(String path) {
        BaseResponse response = mResponses.get(path);
        if (response == null) {
            response = mEmptyResponse;
        }
        return response;
    }

//    @NonNull
//    public Menu getMenu() {
//        final Menu menu = new Menu();
//        menu.addItem("Home", "/");
//        for (BaseResponse response : mResponses.values()) {
//            //if (resp.isEnabled(mMetadata)) {
//            if (response instanceof BaseHtmlResponse) {
//                menu.addItem(((BaseHtmlResponse) response).getMenuItem());
//            }
//        }
//        return menu;
//    }
}
