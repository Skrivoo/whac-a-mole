package hu.skrivoo.whac_a_mole;

import java.util.List;

public interface FirestoreCallback {

    void onCallbackOne(Player player);

    void onCallbackMore(List<Player> players);

}
