package hu.skrivoo.whac_a_mole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ToplistPlayerAdapter extends RecyclerView.Adapter<ToplistPlayerAdapter.ViewHolder> {

    private final ArrayList<Player> playerArrayList;
    private final ArrayList<Player> playerArrayListAll;
    private final Context context;
    private final int lastPos = -1;

    ToplistPlayerAdapter(Context context, ArrayList<Player> playerList) {
        this.playerArrayList = playerList;
        this.playerArrayListAll = playerList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ToplistPlayerAdapter.ViewHolder holder, int position) {
        Player currentPlayer = playerArrayList.get(position);
        holder.bindTo(currentPlayer);
    }

    @Override
    public int getItemCount() {
        return playerArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView nameOfPlayer;
        private TextView higestScoreOfPlayer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.topListPlayerAvatar);
            nameOfPlayer = itemView.findViewById(R.id.nameOfPlayer);
            higestScoreOfPlayer = itemView.findViewById(R.id.higestScoreOfPlayer);
        }

        public void bindTo(Player currentPlayer) {
            //avatar.setImageBitmap(currentPlayer.getFirebaseUser().getPhotoUrl());
            nameOfPlayer.setText(currentPlayer.getFirebaseUser().getDisplayName());
            higestScoreOfPlayer.setText(currentPlayer.getHighestScore());
        }
    }

}
