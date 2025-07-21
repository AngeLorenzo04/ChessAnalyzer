package org.example.UI.board;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.UI.MainApp;

import java.util.*;

public class ChessGuiApp  {

    private Board board;
    private final String[] uciMoves;
    private final String[] comments;
    private final String[] bestMove;
    private int moveIndex = 0;
    private final List<Move> moveHistory = new ArrayList<>();
    private GridPane grid;
    private VBox commentPanel;
    private ScrollPane scrollPane;
    private MainApp mainApp;

    private final int TILE_COUNT = 8;
    private final double TILE_SIZE = 80;

    private final Map<String, Color> originalColors = new HashMap<>();
    private final Map<Square, StackPane> squareMap = new HashMap<>();
    private String lastHighlightedSquare = null;


    public ChessGuiApp(String[] uciMoves, String[] comments, String[] bestMoves) {
        this.uciMoves = uciMoves;
        this.comments = comments;
        this.bestMove = bestMoves;
    }

    public void initializeUI(Stage stage, MainApp mainApp) {
        this.mainApp = mainApp;
        board = new Board();
        System.out.println("mosse: "+ Arrays.toString(uciMoves));

        // Layout principale (board + commenti)
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setAlignment(Pos.CENTER);
        ChessStyles.applyMainLayoutStyle(mainLayout);

        // Pannello scacchiera
        VBox boardBox = new VBox(15);
        boardBox.setAlignment(Pos.CENTER);
        boardBox.setPadding(new Insets(15));
        ChessStyles.applyBoardBoxStyle(boardBox);

        Label titleLabel = new Label("Chess Analyzer");
        ChessStyles.applyTitleStyle(titleLabel);

        grid = new GridPane();
        grid.setPrefSize(TILE_SIZE * TILE_COUNT, TILE_SIZE * TILE_COUNT);
        createChessboard();

        // Pannello commenti
        commentPanel = new VBox(10);
        commentPanel.setPrefWidth(350);
        ChessStyles.applyCommentPanelStyle(commentPanel);

        scrollPane = new ScrollPane(commentPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(TILE_SIZE * TILE_COUNT + 50);
        ChessStyles.applyScrollPaneStyle(scrollPane);

        Label commentTitle = new Label("Commenti delle Mosse");
        ChessStyles.applyCommentTitleStyle(commentTitle);
        commentPanel.getChildren().add(commentTitle);

        // Pulsanti di controllo
        Button nextMoveBtn = new Button("→");
        ChessStyles.applyButtonStyle(nextMoveBtn, "next");
        nextMoveBtn.setOnAction(e -> nextMove());

        Button prevMoveBtn = new Button("←");
        ChessStyles.applyButtonStyle(prevMoveBtn, "prev");
        prevMoveBtn.setOnAction(e -> prevMove());

        Button resetBtn = new Button("⟳");
        ChessStyles.applyButtonStyle(resetBtn, "reset");
        resetBtn.setOnAction(e -> resetBoard());

        Button homeBtn = new Button("\uD83C\uDFE0");
        ChessStyles.applyButtonStyle(homeBtn, "next");
        homeBtn.setOnAction(e -> mainApp.showScene1());

        HBox buttons = new HBox(15, prevMoveBtn, nextMoveBtn, resetBtn , homeBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(15, 0, 0, 0));

        boardBox.getChildren().addAll(titleLabel, grid, buttons);
        mainLayout.getChildren().addAll(boardBox, scrollPane);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("ChessAnalyzer");
        stage.setResizable(true);
        stage.show();
    }
    private void createChessboard() {
        grid.getChildren().clear();
        originalColors.clear();
        squareMap.clear();

        for (int row = 0; row < TILE_COUNT; row++) {
            for (int col = 0; col < TILE_COUNT; col++) {
                StackPane square = new StackPane();
                square.setPrefSize(TILE_SIZE, TILE_SIZE);
                square.setMinSize(TILE_SIZE, TILE_SIZE);
                square.setMaxSize(TILE_SIZE, TILE_SIZE);
                square.setStyle("-fx-border-color: #7f8c8d; -fx-border-width: 0.5;");

                Color color = (row + col) % 2 == 0 ? Color.web("#f0d9b5") : Color.web("#b58863");
                square.setBackground(new Background(new BackgroundFill(color, null, null)));

                int boardRow = 7 - row;
                Square squarePos = Square.values()[boardRow * 8 + col];
                originalColors.put(squarePos.name(), color);
                squareMap.put(squarePos, square);

                Piece piece = board.getPiece(squarePos);
                if (piece != null && !piece.equals(Piece.NONE)) {
                    Text pieceText = new Text(pieceSymbol(piece));
                    pieceText.setFont(Font.font("Arial", FontWeight.BOLD, TILE_SIZE * 0.6));
                    pieceText.setFill(piece.getPieceSide() == Side.WHITE ? Color.WHITE : Color.BLACK);
                    pieceText.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 1, 0, 1, 1);");
                    square.getChildren().add(pieceText);
                }

                grid.add(square, col, row);
            }
        }
    }

    private void nextMove() {
        // Rimuovi evidenziazione precedente
        resetLastHighlight();

        if (moveIndex < uciMoves.length) {
            applyNextMove();
            addComment();
            highlightCurrentSquare();
        } else {
            showEndGameAlert();
        }
    }

    private void prevMove() {
        // Rimuovi evidenziazione precedente
        resetLastHighlight();

        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.removeLast();
            board.undoMove();
            moveIndex--;

            // Aggiorna le caselle interessate dal movimento
            updateSquare(lastMove.getFrom());
            updateSquare(lastMove.getTo());

            removeLastComment();
            highlightCurrentSquare(); // Evidenzia la casella della mossa corrente
        }
    }

    private void resetBoard() {
        // Rimuovi evidenziazione precedente
        resetLastHighlight();

        board = new Board();
        moveIndex = 0;
        moveHistory.clear();
        lastHighlightedSquare = null;
        commentPanel.getChildren().clear();
        commentPanel.getChildren().add(new Label("Commenti delle Mosse"));
        createChessboard();
    }

    private void applyNextMove() {
        String uci = uciMoves[moveIndex];
        if (uci.contains("Z")) {
            showIllegalMoveAlert(uci);
        } else {
            try {
                Square from = Square.valueOf(uci.substring(0, 2).toUpperCase());
                Square to = Square.valueOf(uci.substring(2, 4).toUpperCase());
                Move move = new Move(from, to);

                // Esegui la mossa
                board.doMove(move);
                moveHistory.add(move);
                moveIndex++;

                // Aggiorna le caselle interessate
                updateSquare(from);
                updateSquare(to);

            } catch (Exception e) {
                showIllegalMoveAlert(uci);
            }
        }
    }

    private void updateSquare(Square squarePos) {
        StackPane square = squareMap.get(squarePos);
        if (square == null) return;

        square.getChildren().clear();
        Piece piece = board.getPiece(squarePos);

        if (piece != null && !piece.equals(Piece.NONE)) {
            Text pieceText = new Text(pieceSymbol(piece));
            pieceText.setFont(Font.font("Arial", FontWeight.BOLD, TILE_SIZE * 0.6));
            pieceText.setFill(piece.getPieceSide() == Side.WHITE ? Color.WHITE : Color.BLACK);
            pieceText.setStyle("-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.5), 1, 0, 1, 1);");
            square.getChildren().add(pieceText);
        }

        // Ripristina colore originale
        Color baseColor = originalColors.get(squarePos.name());
        square.setBackground(new Background(new BackgroundFill(baseColor, null, null)));
    }

    private void showIllegalMoveAlert(String uci) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("MOSSA ILLEGALE");
        alert.setHeaderText(null);
        alert.setContentText("Mossa non valida: " + uci);
        alert.showAndWait();
        mainApp.showScene1();

    }

    private void showEndGameAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partita Terminata");
        alert.setHeaderText(null);
        alert.setContentText("L'analisi della partita è terminata");
        alert.showAndWait();
    }

    private void addComment() {
        if (moveIndex > 0 && moveIndex <= comments.length) {
            Label commentLabel = new Label(moveIndex + ". " + comments[moveIndex - 1]);
            commentLabel.setWrapText(true);
            commentLabel.setStyle("-fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0; -fx-background-color: #ffffff; -fx-background-radius: 5;");
            commentLabel.setFont(Font.font("Verdana", 14));
            commentPanel.getChildren().add(commentLabel);

            scrollToLastComment();
        }
    }

    private void scrollToLastComment() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        javafx.animation.KeyValue kv = new javafx.animation.KeyValue(scrollPane.vvalueProperty(), 1.0);
        javafx.animation.KeyFrame kf = new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }


    private void removeLastComment() {
        if (commentPanel.getChildren().size() > 1) {
            commentPanel.getChildren().removeLast();
        }
    }

    private void highlightCurrentSquare() {
        if (moveIndex > 0 && moveIndex <= bestMove.length) {
            String uci = bestMove[moveIndex - 1].toUpperCase();
            if (uci.length() < 4) return;

            String fromSquare = uci.substring(0, 2);
            String toSquare = uci.substring(2, 4);

            Square from = Square.valueOf(fromSquare);
            Square to = Square.valueOf(toSquare);

            highlightSquare(from);
            highlightSquare(to);

            // Salva entrambe per il reset
            lastHighlightedSquare = from.name() + "," + to.name();
        }
    }

    private void highlightSquare(Square squarePos) {
        StackPane square = squareMap.get(squarePos);
        if (square != null) {
            square.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)));
            square.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 2; -fx-effect: dropshadow(three-pass-box, rgba(46, 204, 113, 0.8), 10, 0, 0, 0);");
        }
    }

    private void resetLastHighlight() {
        if (lastHighlightedSquare != null) {
            String[] squares = lastHighlightedSquare.split(",");
            for (String squareName : squares) {
                Square squarePos = Square.valueOf(squareName);
                StackPane square = squareMap.get(squarePos);
                if (square != null) {
                    Color baseColor = originalColors.get(squarePos.name());
                    square.setBackground(new Background(new BackgroundFill(baseColor, null, null)));
                    square.setStyle("-fx-border-color: #7f8c8d; -fx-border-width: 0.5; -fx-effect: null;");
                }
            }
            lastHighlightedSquare = null;
        }
    }

    private String pieceSymbol(Piece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getPieceSide() == Side.WHITE ? "♚" : "♔";
            case QUEEN -> piece.getPieceSide() == Side.WHITE ? "♛" : "♕" ;
            case ROOK -> piece.getPieceSide() == Side.WHITE ? "♜" : "♖";
            case BISHOP -> piece.getPieceSide() == Side.WHITE ? "♝" : "♗";
            case KNIGHT -> piece.getPieceSide() == Side.WHITE ? "♞" : "♘";
            case PAWN -> piece.getPieceSide() == Side.WHITE ? "♟" : "♙";
            default -> "";
        };
    }
}