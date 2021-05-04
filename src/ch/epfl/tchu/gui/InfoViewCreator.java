package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.control.Separator;
import javafx.scene.text.TextFlow;

import javax.naming.Binding;
import java.util.List;
import java.util.Map;

final class InfoViewCreator {
    private InfoViewCreator(){}

    public static Node createInfoView(PlayerId id, Map<PlayerId , String> namesMap, ObservableGameState observableGameState, ObservableList<Text> observableList){
        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css", "colors.css");
        Separator separator = new Separator(Orientation.HORIZONTAL);
        VBox vboxSon = new VBox();
        vboxSon.setId("player-stats");
        TextFlow gameInfo = new TextFlow();
        gameInfo.setId("game-info");
        Bindings.bindContent(gameInfo.getChildren(), observableList);
        vBox.getChildren().addAll(vboxSon, separator, gameInfo);

        for (PlayerId playerId: List.of(id, id.next())) {
            TextFlow textFlow = new TextFlow();
            textFlow.getStyleClass().add(playerId.name());
            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");

            Text text = new Text();
            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,
                    namesMap.get(playerId),
                    observableGameState.playerTicketCountProperty(playerId),
                    observableGameState.playerCardCountProperty(playerId),
                    observableGameState.playerWagonCountProperty(playerId),
                    observableGameState.playerPointCountProperty(playerId)));

            textFlow.getChildren().addAll(circle,text);
            vboxSon.getChildren().add(textFlow);
        }
        return vBox;
    }
}
