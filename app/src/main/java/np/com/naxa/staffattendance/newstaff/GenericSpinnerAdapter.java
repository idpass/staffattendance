package np.com.naxa.staffattendance.newstaff;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GenericSpinnerAdapter<T> extends ArrayAdapter<List<String>> {

    private List<List<String>> values;

    GenericSpinnerAdapter(Context context, int textViewResourceId,
                          List<List<String>> values) {
        super(context, textViewResourceId, values);
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public List<String> getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        String text = "";
        text = values.get(position).get(1);
        label.setText(text);
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NotNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        String text = "";
        text = values.get(position).get(1);
        label.setText(text);
        return label;
    }
}
