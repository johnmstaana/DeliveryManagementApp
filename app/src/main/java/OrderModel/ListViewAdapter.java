package OrderModel;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.johnm.DeliveryManagementMain.R;

import java.util.ArrayList;

/**
 * Created by johnm on 19/10/2016.
 */

public class ListViewAdapter extends ArrayAdapter{
   Context context;
    ArrayList<CheckList> checkList;


    public ListViewAdapter(Context context,int resources, ArrayList<CheckList> checkList ) {
        super(context,resources,checkList);
        this.context = context;
        this.checkList = checkList;
    }

    private class ViewHolder {
        TextView chk_address;
        CheckBox chk_box;
        ImageView chk_img;
    }

    public int getCount() {
        return checkList.size();
    }

    public Object getItem(int position) {
        return checkList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater)this.context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.checklist_layout, null);
            holder = new ViewHolder();

            holder.chk_address = (TextView) convertView
                    .findViewById(R.id.check_address);
            holder.chk_box = (CheckBox) convertView
                    .findViewById(R.id.check_box);
            holder.chk_img = (ImageView)convertView
                    .findViewById(R.id.check_logo);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        final CheckList _checkList = (CheckList) getItem(position);
        holder.chk_address.setText(_checkList.getAddress());
        holder.chk_box.setChecked(_checkList.isComplete());

        if(_checkList.getOrder_Status().equals("Customer")){
            holder.chk_img.setImageDrawable(ContextCompat.getDrawable(this.context,R.drawable.greenstamp));
        } else {
            holder.chk_img.setImageDrawable(ContextCompat.getDrawable(this.context,R.drawable.yellowstamp));
        }

        holder.chk_box.setOnCheckedChangeListener(onCheckedChangeListener(_checkList));

        return convertView;
    }
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener(final CheckList check) {
        return new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check.setComplete(true);
                } else {
                    check.setComplete(false);
                }
            }
        };
    }
}
