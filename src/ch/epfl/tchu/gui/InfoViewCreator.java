package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
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



/**
 * La classe InfoViewCreator, finale, représante la vue des informations
 *
 * @author Shangeeth Poobalasingam (329307)
 * @author Marvin Koch (324448)
 */
final class InfoViewCreator {
    /**
     * Constructeur privée
     */
    private InfoViewCreator(){}

    /**
     * La classe InfoViewCreator  non instanciable et package private (!),
     * contient une unique méthode publique permettant de créer la vue des informations
     * @param id l'identité du joueur auquel l'interface correspond
     * @param namesMap la table associative des noms des joueurs
     * @param observableGameState l'état de jeu observable
     * @param observableList une liste (observable) contenant les informations sur le déroulement de la partie, sous la forme d'instances de Text
     * @throws IllegalArgumentException si la liste d'infos est plus grande que 5
     * @return Node vue des infos
     */
    public static Node createInfoView(PlayerId id, Map<PlayerId , String> namesMap, ObservableGameState observableGameState, ObservableList<Text> observableList){
        Preconditions.checkArgument(observableList.size() <=5 );
        VBox vBox = new VBox();
        vBox.getStylesheets().addAll("info.css", "colors.css");

        Separator separator = new Separator(Orientation.HORIZONTAL);

        VBox vBoxSon = new VBox();
        vBoxSon.setId("player-stats");

        TextFlow gameInfo = new TextFlow();
        gameInfo.setId("game-info");
        Bindings.bindContent(gameInfo.getChildren(), observableList);

        vBox.getChildren().addAll(vBoxSon, separator, gameInfo);

        List<PlayerId> idList = PlayerId.is3Players() ? List.of(id, id.next(),id.doubleNext()) : List.of(id, id.next());
        for (PlayerId playerId: idList) {//todo 3 player
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
            vBoxSon.getChildren().add(textFlow);
        }
        return vBox;
    }
}
