package ru.hedhyw.directory;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hedhyw on 7/24/16.
 */
public class DirectoryProperties {

    public interface FileSubText {
        String getFileSubText(File file);
    }

    public static final OPEN_MODE DEFAULT_MODE = OPEN_MODE.SINGLE;
    public static final OPEN_TYPE DEFAULT_TYPE = OPEN_TYPE.DIRECTORY;
    public static final boolean DEFAUL_SHOW_HIDDEN = false;
    public static final File DEFAUL_PATH = Environment.getExternalStorageDirectory();
    public static final int DEFAULT_FILE_ICON_COLOR = Color.parseColor("#989898");
    public static final int DEFAULT_BUTTON_ICON_COLOR = Color.parseColor("#989898");
    public static final int DEFAULT_DIRECTORY_ICON_COLOR = Color.BLACK;
    public static final FileSubText DEFAULT_FILE_SUB_TEXT = new FileSubText() {
        @Override
        public String getFileSubText(File file) {
            if (file.isDirectory()) {
                SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
                return date.format(file.lastModified());
            } else if (file.isFile()) {
                long file_size = file.length();
                int range = (int) Math.floor(Math.log(file_size) / Math.log(file_size));
                String suffix;
                switch (range) {
                    case 0:
                        suffix = "B";
                        break;
                    case 1:
                        suffix = "KiB";
                        break;
                    case 2:
                        suffix = "MiB";
                        break;
                    case 3:
                        suffix = "GiB";
                        break;
                    default:
                        suffix = "TiB";
                }
                file_size /= Math.pow(1024, Math.min(range, 4));
                return String.format(Locale.getDefault(), "%d %s", file_size, suffix);
            }
            return "";
        }
    };

    public enum OPEN_MODE {
        SINGLE,
        MULTIPLE
    }

    public enum OPEN_TYPE {
        DIRECTORY,
        FILE
    }

    private OPEN_MODE mode;
    private OPEN_TYPE type;
    private boolean show_hidden;
    private File path;
    private FilenameFilter fileNameFilter;
    private String title,
            button_value_select,
            button_value_cancel;
    private int file_icon_color, directory_icon_color, button_icon_color;
    private FileSubText fileSubText;

    public OPEN_MODE setMode(OPEN_MODE mode) {
        if (mode != null) this.mode = mode;
        else this.mode = DEFAULT_MODE;
        return this.mode;
    }

    public OPEN_TYPE setType(OPEN_TYPE type) {
        if (type != null) this.type = type;
        else this.type = DEFAULT_TYPE;
        return this.type;
    }

    public boolean setShowHiddenFlag(Boolean show_hidden) {
        if (show_hidden != null) this.show_hidden = show_hidden;
        else this.show_hidden = DEFAUL_SHOW_HIDDEN;
        return this.show_hidden;
    }

    public File setPath(File path) {
        if (path != null) this.path = path;
        else this.path = DEFAUL_PATH;
        return this.path;
    }

    public String setTitle(Context context, int strId) {
        this.title = context.getString(strId);
        return this.title;
    }

    public String setTitle(String title) {
        this.title = title;
        return this.title;
    }

    public String setButtonValueSelect(Context context, int strId) {
        this.button_value_select = context.getString(strId);
        return this.button_value_select;
    }

    public String setButtonValueSelect(String str) {
        this.button_value_select = str;
        return this.button_value_select;
    }

    public String setButtonValueCancel(Context context, int strId) {
        this.button_value_cancel = context.getString(strId);
        return this.button_value_cancel;
    }

    public String setButtonValueCancel(String str) {
        this.button_value_cancel = str;
        return this.button_value_cancel;
    }

    public int setDirectoryIconColor(int color) {
        this.directory_icon_color = color;
        return this.directory_icon_color;
    }

    public int setFileIconColor(int color) {
        this.file_icon_color = color;
        return this.file_icon_color;
    }

    public int setButtonIconColor(int color) {
        this.button_icon_color = color;
        return this.button_icon_color;
    }

    public FileSubText setFileSubText(FileSubText fileSubText) {
        if (fileSubText == null) this.fileSubText = DEFAULT_FILE_SUB_TEXT;
        else this.fileSubText = fileSubText;
        return this.fileSubText;
    }

    public FilenameFilter setFilenameFilter(FilenameFilter filter) {
        fileNameFilter = filter;
        return fileNameFilter;
    }

    public OPEN_MODE getMode() {
        return this.mode;
    }

    public OPEN_TYPE getType() {
        return this.type;
    }

    public boolean getShowHiddenFlag() {
        return this.show_hidden;
    }

    public File getPath() {
        return this.path;
    }

    public String getTitle() {
        return this.title;
    }

    public String getButtonValueSelect() {
        return this.button_value_select;
    }

    public String getButtonValueCancel() {
        return this.button_value_cancel;
    }

    public int getDirectoryIconColor() {
        return this.directory_icon_color;
    }

    public int getFileIconColor() {
        return this.file_icon_color;
    }

    public int getButtonIconColor() {
        return this.button_icon_color;
    }

    public FileSubText getFileSubText() {
        return this.fileSubText;
    }

    public FilenameFilter getFilenameFilter() {
        return fileNameFilter;
    }


    public DirectoryProperties() {
        this(
                DEFAULT_MODE,
                DEFAULT_TYPE,
                DEFAUL_SHOW_HIDDEN,
                DEFAUL_PATH
        );
    }

    public DirectoryProperties(
            OPEN_MODE mode,
            OPEN_TYPE type,
            Boolean show_hidden,
            File path
    ) {
        setMode(mode);
        setType(type);
        setShowHiddenFlag(show_hidden);
        setPath(path);
        file_icon_color = DEFAULT_FILE_ICON_COLOR;
        directory_icon_color = DEFAULT_DIRECTORY_ICON_COLOR;
        fileSubText = DEFAULT_FILE_SUB_TEXT;
    }

}
