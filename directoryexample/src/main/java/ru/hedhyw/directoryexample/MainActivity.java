package ru.hedhyw.directoryexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.hedhyw.directory.Directory;
import ru.hedhyw.directory.DirectoryProperties;

public class MainActivity extends AppCompatActivity {

    ListView listview;
    ToggleButton toggleButton;
    ArrayAdapter<String> arrayAdapter;
    Directory.OnCancelDialogListener onCancelDialogListener;
    Directory.OnSuccessDialogListener onSuccessDialogListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);
        toggleButton = (ToggleButton) findViewById(R.id.togglebtn);
        List<String> list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                list);
        listview.setAdapter(arrayAdapter);

        onSuccessDialogListener = new Directory.OnSuccessDialogListener() {
            @Override
            public void onSuccessDialog(List<File> files) {
                Iterator<File> iterator = files.iterator();
                arrayAdapter.clear();
                while (iterator.hasNext())
                    arrayAdapter.add(iterator.next().getAbsolutePath());
                arrayAdapter.notifyDataSetChanged();
            }
        };

        onCancelDialogListener = new Directory.OnCancelDialogListener() {
            @Override
            public void onCancel() {
                arrayAdapter.clear();
                Toast.makeText(
                        MainActivity.this,
                        R.string.dialog_canceled,
                        Toast.LENGTH_SHORT
                ).show();
            }
        };
    }

    public void openDir(View v) {
        DirectoryProperties properties = new DirectoryProperties();
        properties.setTitle(this, R.string.title_select_directory);
        properties.setButtonValueSelect(this, R.string.button_select_directory);
        properties.setButtonValueCancel(this, R.string.button_cancel);
        properties.setType(DirectoryProperties.OPEN_TYPE.DIRECTORY);
        properties.setMode(
                toggleButton.isChecked()
                        ? DirectoryProperties.OPEN_MODE.MULTIPLE
                        : DirectoryProperties.OPEN_MODE.SINGLE
        );
        properties.setDirectoryIconColor(getResources().getColor(R.color.colorPrimaryDark));
        properties.setFileIconColor(getResources().getColor(R.color.colorAccent));
        properties.setButtonIconColor(getResources().getColor(R.color.colorPrimary));
        Directory directory = new Directory(this, properties);
        directory.setOnSuccessDialogListener(onSuccessDialogListener);
        directory.setOnCancelDialogListener(onCancelDialogListener);
        try {
            directory.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openFile(View v) {
        DirectoryProperties properties = new DirectoryProperties();
        properties.setTitle(this, R.string.title_select_file);
        properties.setButtonValueCancel(this, R.string.button_cancel);
        properties.setType(DirectoryProperties.OPEN_TYPE.FILE);
        properties.setMode(
                toggleButton.isChecked()
                        ? DirectoryProperties.OPEN_MODE.MULTIPLE
                        : DirectoryProperties.OPEN_MODE.SINGLE
        );
        /*properties.setDirectoryIconColor(getResources().getColor(R.color.colorPrimaryDark));
        properties.setFileIconColor(getResources().getColor(R.color.colorAccent));*/
        Directory directory = new Directory(this, properties);
        directory.setOnSuccessDialogListener(onSuccessDialogListener);
        directory.setOnCancelDialogListener(onCancelDialogListener);
        try {
            directory.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
