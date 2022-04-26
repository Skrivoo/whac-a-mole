package hu.skrivoo.whac_a_mole;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ToplistPlayerAdapter extends RecyclerView.Adapter<ToplistPlayerAdapter.ViewHolder> {

    private List<Player> playerArrayList;
    private List<Player> playerArrayListAll;
    private Context context;

    ToplistPlayerAdapter(Context context, List<Player> playerList) {
        this.playerArrayList = playerList;
        this.playerArrayListAll = playerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ToplistPlayerAdapter.ViewHolder holder, int position) {
        Player currentPlayer = playerArrayList.get(position);
        holder.bindTo(currentPlayer, context, position);
    }

    @Override
    public int getItemCount() {
        return playerArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView nameOfPlayer;
        private TextView higestScoreOfPlayer;
        private TextView numberOfPlace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.topListPlayerAvatar);
            nameOfPlayer = (TextView) itemView.findViewById(R.id.nameOfPlayer);
            higestScoreOfPlayer = (TextView) itemView.findViewById(R.id.higestScoreOfPlayer);
            numberOfPlace = (TextView) itemView.findViewById(R.id.numberOfPlace);
        }

        @SuppressLint("SetTextI18n")
        public void bindTo(Player currentPlayer, Context context, int pos) {
            Glide.with(context).load(currentPlayer.getAvatar()).into(avatar);
            nameOfPlayer.setText(currentPlayer.getName());
            higestScoreOfPlayer.setText(currentPlayer.getHighestScore().toString());
            numberOfPlace.setText(String.valueOf(pos + 1) + ".");
        }
    }

}
