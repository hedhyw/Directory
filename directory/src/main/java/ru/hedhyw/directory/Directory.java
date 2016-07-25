package ru.hedhyw.directory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by hedhyw on 7/24/16.
 */
public class Directory {

    private AlertDialog.Builder dialog_builder;
    private AlertDialog dialog;
    //private Context context;
    private DirectoryProperties mProperties;
    private OnSuccessDialogListener OnSuccessDialogObject;
    private OnCancelDialogListener OnCancelDialogObject;
    private View listview_layout;
    private View actionbar, toolbar;
    private FrameLayout frameToolbar;
    private ListView listview;
    private ArrayAdapter mListFileAdapter;
    private List<File> selected_files;
    private LinearLayout path_layout;
    private Drawable file_drawable, dir_drawable, check_drawable;
    private MultiChooseMode mMultiChooseMode;

    private class multiChooseModeListener
            implements MultiChooseMode.MultiChooseModeListener {

        TextView actionbar_title;

        public multiChooseModeListener() {
            actionbar_title = (TextView) actionbar.findViewById(R.id.actionbar_title);

            ImageButton ib = (ImageButton) actionbar.findViewById(R.id.actionbar_cancel);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMultiChooseMode.disable();
                }
            });
            setImageButtonColor(ib, mProperties.getButtonIconColor());

            ib = (ImageButton) actionbar.findViewById(R.id.actionbar_success);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    multiplySelectedFiles();
                    success();
                }
            });
            setImageButtonColor(ib, mProperties.getButtonIconColor());

            ib = (ImageButton) actionbar.findViewById(R.id.actionbar_selectall);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < mListFileAdapter.getCount(); ++i)
                        mMultiChooseMode.setChecked(i, true);
                }
            });
            setImageButtonColor(ib, mProperties.getButtonIconColor());
        }

        @Override
        public void onEnable(MultiChooseMode mcm) {
            frameToolbar.removeAllViews();
            frameToolbar.addView(actionbar);
        }

        @Override
        public void onDisable(MultiChooseMode mcm) {
            frameToolbar.removeAllViews();
            frameToolbar.addView(toolbar);
        }

        @Override
        public void onCheckedChange(MultiChooseMode mcm, int position, boolean checked) {
            actionbar_title.setText(String.valueOf(mcm.getCheckedCount()));
        }
    }

    private class listViewOnItemClickListener
            implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            File file = (File) mListFileAdapter.getItem(position);
            selected_files.clear();
            if (file.isFile() && mProperties.getType().equals(DirectoryProperties.OPEN_TYPE.FILE)) {
                selected_files.add(file);
                success();
            } else if (file.isDirectory()) {
                try {
                    openPath(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class listViewOnItemLongClickListener
            implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            File file = (File) mListFileAdapter.getItem(position);
            /*Toast.makeText(
                    context,
                    file.getName(),
                    Toast.LENGTH_SHORT
            ).show();*/
            if (mProperties.getMode().equals(DirectoryProperties.OPEN_MODE.MULTIPLE)) {
                mMultiChooseMode.setChecked(position, true);
                mMultiChooseMode.enable();
            }
            return true;
        }
    }

    private class ListFileAdapter
            extends ArrayAdapter {

        public ListFileAdapter(Context context, int resource, int textViewResourceId, List objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textview1 = (TextView) view.findViewById(android.R.id.text1);
            TextView textview2 = (TextView) view.findViewById(android.R.id.text2);
            ImageView imageview = (ImageView) view.findViewById(ru.hedhyw.directory.R.id.item_icon);
            File file = (File) mListFileAdapter.getItem(position);

            textview1.setText(file.getName());
            DirectoryProperties.FileSubText fileSubText = mProperties.getFileSubText();
            if (file.isDirectory()) imageview.setImageDrawable(dir_drawable);
            else imageview.setImageDrawable(file_drawable);
            if (mMultiChooseMode.isEnabled()) {
                if (mMultiChooseMode.getChecked(position))
                    imageview.setImageDrawable(check_drawable);
            }
            textview2.setText(fileSubText.getFileSubText(file));
            return view;
        }
    }

    public interface OnSuccessDialogListener {
        void onSuccessDialog(List<File> files);
    }

    public interface OnCancelDialogListener {
        void onCancel();
    }

    public Directory(Context context) {
        this(context, new DirectoryProperties());
    }

    public Directory(Context context, DirectoryProperties properties) {
        //this.context = context;

        mProperties = properties;

        file_drawable = context.getResources().getDrawable(
                ru.hedhyw.directory.R.drawable.ic_insert_drive_file_black_48dp
        );
        dir_drawable = context.getResources().getDrawable(
                ru.hedhyw.directory.R.drawable.ic_folder_black_48dp
        );
        check_drawable = context.getResources().getDrawable(
                ru.hedhyw.directory.R.drawable.ic_check_circle_black_48dp
        );

        selected_files = new ArrayList<>();
        dialog_builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listview_layout = inflater.inflate(ru.hedhyw.directory.R.layout.listview_layout, null);
        path_layout = (LinearLayout) listview_layout.findViewById(R.id.path_layout);
        ImageButton path_up_ib = (ImageButton) listview_layout.findViewById(
                ru.hedhyw.directory.R.id.path_up_imagebutton
        );
        path_up_ib.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            pathUp();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        setImageButtonColor(path_up_ib, mProperties.getButtonIconColor());

        listview = (ListView) listview_layout.findViewById(ru.hedhyw.directory.R.id.file_listview);

        List<File> files = new ArrayList<>();
        mListFileAdapter = new ListFileAdapter(
                context,
                ru.hedhyw.directory.R.layout.listview_item,
                android.R.id.text1,
                files);
        listview.setAdapter(mListFileAdapter);
        listview.setOnItemClickListener(new listViewOnItemClickListener());
        listview.setOnItemLongClickListener(new listViewOnItemLongClickListener());
        dialog_builder.setView(listview_layout);
        dialog_builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (OnCancelDialogObject != null)
                    OnCancelDialogObject.onCancel();
            }
        });

        String buttonValueCancel = mProperties.getButtonValueCancel(),
                buttonValueSelect = mProperties.getButtonValueSelect(),
                title = mProperties.getTitle();
        if (buttonValueCancel != null) setNegativeButton(buttonValueCancel);
        if (buttonValueSelect != null
                && mProperties.getType().equals(DirectoryProperties.OPEN_TYPE.DIRECTORY))
            setPositiveButton(buttonValueSelect);
        if (title != null) setTitle(title);
        setFileIconColor(mProperties.getFileIconColor());
        setDirectoryIconColor(mProperties.getDirectoryIconColor());
        setCheckIconColor(
                mProperties.getType().equals(DirectoryProperties.OPEN_TYPE.DIRECTORY)
                        ? mProperties.getDirectoryIconColor()
                        : mProperties.getFileIconColor()
        );

        frameToolbar = (FrameLayout) listview_layout.findViewById(ru.hedhyw.directory.R.id.toolbar);
        toolbar = frameToolbar.getChildAt(0);
        actionbar = inflater.inflate(ru.hedhyw.directory.R.layout.listview_actionbar, null);
        mMultiChooseMode = new MultiChooseMode(listview);
        mMultiChooseMode.setMultiChooseModeListener(new multiChooseModeListener());
        mMultiChooseMode.setAllowCheck(new MultiChooseMode.AllowCheck() {
            @Override
            public boolean isAllowed(int position) {
                File file = (File) mListFileAdapter.getItem(position);
                if (file.isDirectory() ^ mProperties.getType().equals(DirectoryProperties.OPEN_TYPE.DIRECTORY))
                    return false;
                return true;
            }
        });
    }

    private void multiplySelectedFiles() {
        selected_files.clear();
        Iterator<Integer> iterator = mMultiChooseMode.getCheckedItemPositions().iterator();
        while (iterator.hasNext()) {
            selected_files.add((File) mListFileAdapter.getItem(iterator.next()));
        }
    }

    private void setDirectoryIconColor(int color) {
        setDrawableColor(dir_drawable, color);
    }

    private void setFileIconColor(int color) {
        setDrawableColor(file_drawable, color);
    }

    private void setCheckIconColor(int color) {
        setDrawableColor(check_drawable, color);
    }

    private void setImageButtonColor(ImageButton btn, int color) {
        Drawable drawable = btn.getDrawable();
        setDrawableColor(drawable, color);
        btn.setImageDrawable(drawable);
    }

    private void setDrawableColor(Drawable drawable, int color) {
        float[] matrix = {
                0, 0, 0, 0, Color.red(color),
                0, 0, 0, 0, Color.green(color),
                0, 0, 0, 0, Color.blue(color),
                0, 0, 0, 1, 0
        };
        drawable.setColorFilter(
                new ColorMatrixColorFilter(matrix)
        );
    }

    public void show() throws IOException {
        dialog = dialog_builder.create();
        openPath(mProperties.getPath());
        dialog.show();
    }

    public void setOnSuccessDialogListener(OnSuccessDialogListener listener) {
        OnSuccessDialogObject = listener;
    }

    public void setOnCancelDialogListener(OnCancelDialogListener listener) {
        OnCancelDialogObject = listener;
    }

    private void pathUp() throws IOException {
        File path = mProperties.getPath().getParentFile();
        if (path == null) return;
        openPath(path);
    }

    private void addPathButtons(File path, LinearLayout path_layout) {
        path_layout.removeAllViews();
        Stack<File> stk = new Stack<>();
        File f = path;
        while (f != null) {
            stk.push(f);
            f = stk.peek().getParentFile();
        }

        while (!stk.empty()) {
            Button btn = new Button(listview_layout.getContext());
            btn.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
            );
            final File file = stk.pop();
            if (file.getName().length() == 0) btn.setText("/");
            else btn.setText(file.getName());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        openPath(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            path_layout.addView(btn);
        }
    }

    private void openPath(File path) throws IOException {
        if (!path.isDirectory()) throw new IOException();
        mProperties.setPath(path);

        addPathButtons(path, path_layout);

        mListFileAdapter.clear();
        List<File> files = new ArrayList<>(), dirs = new ArrayList<>();
        FilenameFilter fn_filter = mProperties.getFilenameFilter();
        File[] files_all = (
                fn_filter == null
                        ? path.listFiles()
                        : path.listFiles(fn_filter)
        );

        if (files_all != null) {
            for (int i = 0; i < files_all.length; ++i) {
                File file = files_all[i];
                if (file.isHidden() && !mProperties.getShowHiddenFlag()) continue;
                if (file.isDirectory()) dirs.add(file);
                else if (file.isFile()) files.add(file);
            }

            Comparator<File> comparator = new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    if (f1.isDirectory() && f2.isFile()) return 0;
                    String f1_name = f1.getName().toLowerCase(),
                            f2_name = f2.getName().toLowerCase();
                    return f1_name.compareTo(f2_name);
                }
            };
            Collections.sort(files, comparator);
            Collections.sort(dirs, comparator);

            mListFileAdapter.addAll(dirs);
            mListFileAdapter.addAll(files);
        }

        mListFileAdapter.notifyDataSetChanged();
    }

    private void success() {
        if (OnSuccessDialogObject != null)
            OnSuccessDialogObject.onSuccessDialog(selected_files);
        dialog.dismiss();
    }

    private void cancel() {
        if (OnCancelDialogObject != null)
            OnCancelDialogObject.onCancel();
        dialog.dismiss();
    }

    private void setTitle(CharSequence title) {
        dialog_builder.setTitle(title);
    }

    private void setPositiveButton(CharSequence text) {
        dialog_builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mMultiChooseMode.isEnabled()) {
                    multiplySelectedFiles();
                    success();
                } else {
                    if (mProperties.getType().equals(DirectoryProperties.OPEN_TYPE.DIRECTORY)) {
                        selected_files.clear();
                        selected_files.add(mProperties.getPath());
                        success();
                    } else {
                        cancel();
                    }
                }
            }
        });
    }

    private void setNegativeButton(CharSequence text) {
        dialog_builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancel();
            }
        });
    }

}
