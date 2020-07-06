package ca.nait.dmit2504.soundboardapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class AudioSpinnerAdapter extends ArrayAdapter<Audio> {

    private Context mContext;
    private List<Audio> mAudioList;

    public AudioSpinnerAdapter(@NonNull Context context, int resource, List<Audio> mAudioList) {
        super(context, resource);
        this.mContext = context;
        this.mAudioList = mAudioList;
    }

    @Override
    public int getCount() {
        return mAudioList.size();
    }

    @Nullable
    @Override
    public Audio getItem(int position) {
        return mAudioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView audioSpinnerItemTextView = (TextView) super.getView(position, convertView, parent);
        audioSpinnerItemTextView.setText(mAudioList.get(position).getName());
        return audioSpinnerItemTextView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Use simple_spinner_dropdown_item
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }
        ((TextView) convertView).setText(mAudioList.get(position).getName());
        return convertView;
    }
}
