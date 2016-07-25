package ru.hedhyw.directory;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashSet;

/**
 * Created by hedhyw on 7/25/16.
 */
public class MultiChooseMode {

    public interface MultiChooseModeListener{
        void onEnable(MultiChooseMode mcm);
        void onDisable(MultiChooseMode mcm);
        void onCheckedChange(MultiChooseMode mcm, int position, boolean checked);
    }

    public interface AllowCheck{
        boolean isAllowed(int position);
    }

    private boolean enabled = false;
    private ListView listView;
    //private ArrayAdapter listAdapter;
    private AdapterView.OnItemClickListener lastOnItemClickListener;
    private AdapterView.OnItemLongClickListener lastOnItemLongClickListener;
    private HashSet<Integer> checked;
    private MultiChooseModeListener listener;
    private AllowCheck mAllowCheck = new AllowCheck(){
        @Override
        public boolean isAllowed(int position) {
            return true;
        }
    };

    private class onItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            toggleChecked(position);
            if (checked.isEmpty()) disable();
        }
    }

    public void toggleChecked(int position) {
        setChecked(position, !getChecked(position));
    }

    public int getCheckedCount() {
        return checked.size();
    }

    public void setChecked(int position, boolean val) {
        if (!mAllowCheck.isAllowed(position)) return;
        if (val) checked.add(position);
        else checked.remove(position);
        if (enabled) listView.invalidateViews();
        if (listener != null) listener.onCheckedChange(this, position, val);
    }

    public HashSet<Integer> getCheckedItemPositions() {
        return (HashSet<Integer>) checked.clone();
    }

    public boolean getChecked(int position) {
        return checked.contains(position);
    }

    public void enable() {
        if (checked.isEmpty()){
            return;
        }
        enabled = true;
        lastOnItemClickListener = listView.getOnItemClickListener();
        lastOnItemLongClickListener = listView.getOnItemLongClickListener();
        listView.setOnItemClickListener(new onItemClickListener());
        listView.setOnItemLongClickListener(null);
        if (listener != null) listener.onEnable(this);
        listView.invalidateViews();
    }

    public void disable() {
        if (!enabled) return;
        checked.clear();
        enabled = false;
        listView.setOnItemClickListener(lastOnItemClickListener);
        listView.setOnItemLongClickListener(lastOnItemLongClickListener);
        if (listener != null) listener.onDisable(this);
        listView.invalidateViews();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setAllowCheck(AllowCheck allowCheck) {
        if (allowCheck != null) this.mAllowCheck = allowCheck;
    }

    public void setMultiChooseModeListener(MultiChooseModeListener listener) {
        this.listener = listener;
    }

    MultiChooseMode(ListView listView) {
        this.listView = listView;
        //listAdapter = (ArrayAdapter) listView.getAdapter();
        checked = new HashSet<>();
    }


}
