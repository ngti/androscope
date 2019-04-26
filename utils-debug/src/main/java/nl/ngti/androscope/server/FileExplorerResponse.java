package nl.ngti.androscope.server;

import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

import nl.ngti.androscope.menu.MenuItem;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static nl.ngti.androscope.responses.filebrowser.HtmlResponseShowFolder.getUrlFolder;

public class FileExplorerResponse extends BaseHtmlResponse {

    @NonNull
    @Override
    protected MenuItem getMenuItem() {
        return new MenuItem("File Explorer", null)
                .subItem("Application Data", getUrlFolder(new File(getContext().getApplicationInfo().dataDir)))
                .subItem("External Storage", getUrlFolder(Environment.getExternalStorageDirectory()))
                .subItem("Root Directory", getUrlFolder(Environment.getRootDirectory()))
                .separator()
                .subItem("Downloads", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)))
                .subItem("Photos", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)))
                .subItem("Movies", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)))
                .subItem("Pictures", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)))
                .subItem("Music", getUrlFolder(getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
    }

    @Override
    protected void onProvideContent(StringBuilder content) {

    }
}
