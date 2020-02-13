package gh.chig.com.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder>  implements Filterable {

    private List<RetroUsers> dataList;
    private List<RetroUsers> filteredDataList;

    public MyAdapter(List<RetroUsers> dataList){

        this.dataList = dataList;
        filteredDataList = new ArrayList<RetroUsers>();
        filteredDataList.addAll(dataList);

    }


   public class CustomViewHolder extends RecyclerView.ViewHolder {


        public final View myView;

        TextView textCode;
        TextView textName;
        TextView textNumber;
        TextView textLocation;

        CustomViewHolder(View itemView) {
            super(itemView);
            myView = itemView;

            textCode = myView.findViewById(R.id.code);
            textName = myView.findViewById(R.id.name);
            textNumber = myView.findViewById(R.id.number);
            textLocation = myView.findViewById(R.id.location);

        }
    }



    @Override

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_layout, parent, false);
        return new CustomViewHolder(view);
    }

    @Override

    public void onBindViewHolder(CustomViewHolder holder, int position) {

        holder.textCode.setText(
                dataList.get(position).getCode()
        );

        holder.textName.setText(
                dataList.get(position).getName()
        );

        holder.textNumber.setText(
                dataList.get(position).getPhone()
        );

        holder.textLocation.setText(
                dataList.get(position).getLocation()
        );

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredDataList = dataList;
                } else {
                    List<RetroUsers> filteredList = new ArrayList<>();
                    for (RetroUsers row : dataList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    filteredDataList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDataList = (ArrayList<RetroUsers>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void filter(String text) {
        dataList.clear();
        if(text.isEmpty()){
            dataList.addAll(filteredDataList);
        } else{
            text = text.toLowerCase();
            for(RetroUsers user: filteredDataList){
                if(user.getPhone().toLowerCase().contains(text) || user.getName().toLowerCase().contains(text) || user.getCode().toLowerCase().contains(text)){
                    dataList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

}
