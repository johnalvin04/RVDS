package fyp.adapters;

// Coded by : John Alvin Joseph

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fyp.model.TroubleCodes;
import fyp.ui_activities.R;

public class PastScanAdapter extends RecyclerView.Adapter<PastScanAdapter.ViewHolder>  {

    private List<TroubleCodes> list;
    private final Context context;


    public PastScanAdapter(List<TroubleCodes> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull

    public PastScanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PastScanAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.pastscan_list, parent, false));
    }

    public void onBindViewHolder(@NonNull PastScanAdapter.ViewHolder holder, int position) {
        holder.troubleCodes.setText(list.get(holder.getAdapterPosition()).getTroubleCode());
        holder.codedescription.setText(list.get(holder.getAdapterPosition()).getDescription());
        holder.date.setText(list.get(holder.getAdapterPosition()).getDate());
    }


    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView troubleCodes;
        TextView codedescription;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            troubleCodes = itemView.findViewById(R.id.dtccode);
            codedescription = itemView.findViewById(R.id.dtcdesc);
        }
    }
}
