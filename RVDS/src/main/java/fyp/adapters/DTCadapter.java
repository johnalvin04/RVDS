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


public class DTCadapter extends RecyclerView.Adapter<DTCadapter.ViewHolder> {

    private List<TroubleCodes> list;
    private final Context context;


    public DTCadapter(List<TroubleCodes> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.troubleCodes.setText(list.get(holder.getAdapterPosition()).getTroubleCode());
        holder.codedescription.setText(list.get(holder.getAdapterPosition()).getDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView troubleCodes;
        TextView codedescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            troubleCodes = itemView.findViewById(R.id.dtccode);
            codedescription = itemView.findViewById(R.id.dtcdesc);
        }
    }
}
