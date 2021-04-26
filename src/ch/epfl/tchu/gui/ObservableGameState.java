package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;

public class ObservableGameState {
    private PlayerId id;
    private PublicGameState publicGameState;
    private PlayerState ps;

    public ObservableGameState(PlayerId id) {
        this.id = id;
    }

    public void setState(PublicGameState gs, PlayerState ps){
        this.publicGameState = gs;
        this.ps = ps;
    }

    public PlayerState getPs() {
        return ps;
    }
}
