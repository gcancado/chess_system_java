package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;

    private List<Piece> piecesOnTheBoard;
    private List<Piece> capturedPieces;

    public ChessMatch() {
        this.board = new Board(8, 8);
        this.turn = 1;
        this.currentPlayer = Color.BRANCO;
        this.piecesOnTheBoard = new ArrayList<>();
        this.capturedPieces = new ArrayList<>();
        this.check = false;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[this.board.getRows()][this.board.getColumns()];
        for(int i = 0; i < this.board.getRows(); i++) {
            for(int j = 0; j < this.board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);

        if(capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }

        //Roque Pequeno
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }

        //Roque grande
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(targetT);
            board.placePiece(rook, sourceT);
            rook.decreaseMoveCount();
        }

        // en passant
        if(p instanceof Pawn) {
            if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
                ChessPiece pawn = (ChessPiece)board.removePiece(target);
                Position pawnPosition;
                if(p.getColor() == Color.BRANCO) {
                    pawnPosition = new Position(3, target.getColumn());
                } else {
                    pawnPosition = new Position(4, target.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
            }
        }
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) this.board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = this.board.removePiece(target);
        if (capturedPiece != null) {
            this.piecesOnTheBoard.remove(capturedPiece);
            this.capturedPieces.add(capturedPiece);
        }
        this.board.placePiece(p, target);

        //Roque Pequeno
        if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }

        //Roque grande
        if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(rook, targetT);
            rook.increaseMoveCount();
        }

        // en passant
        if(p instanceof Pawn) {
            if(source.getColumn() != target.getColumn() && capturedPiece == null) {
                Position pawnPosition;
                if(p.getColor() == Color.BRANCO) {
                    pawnPosition = new Position(target.getRow() + 1, target.getColumn());
                } else {
                    pawnPosition = new Position(target.getRow() - 1, target.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }


        return capturedPiece;
    }

    private void validateSourcePosition(Position position) {
        if(!this.board.thereIsAPiece(position)) {
            throw new ChessException("Nao existe peca na posicao de origem");
        }
        if(this.currentPlayer != ((ChessPiece)(board.piece(position))).getColor()) {
            throw new ChessException("Essa peca nao eh sua");
        }
        if(!this.board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("Nao existe movimento possivel para a peca escolhida");
        }
    }

    public void validateTargetPosition(Position source, Position target) {
        if (!this.board.piece(source).possibleMove(target)) {
            throw new ChessException("A peca escolhida nao pode se mover para a posicao de destino");
        }
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("Voce nao pode se colocar em xeque");
        }

        ChessPiece movedPiece = (ChessPiece)board.piece(target);

        this.check = testCheck(opponent(currentPlayer));

        if(testCheckMate(opponent(currentPlayer))) {
            this.checkMate = true;
        } else {
            nextTurn();
        }

        // en passant
        if(movedPiece instanceof Pawn && ( (target.getRow() == source.getRow() - 2) || (target.getRow() == source.getRow() + 2))) {
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null;
        }

        return (ChessPiece)capturedPiece;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
        this.piecesOnTheBoard.add(piece);
    }

    public void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.BRANCO) ? Color.PRETO : Color.BRANCO;
    }

    private Color opponent(Color color) {
        if(color == Color.BRANCO) {
            return Color.PRETO;
        }
        return Color.BRANCO;
    }

    private ChessPiece king(Color color) {
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).toList();
        for (Piece p : list) {
            if (p instanceof King) {
                return (ChessPiece)p;
            }
        }
        throw new IllegalStateException("Nao existe o rei da cor " + color + " no tabuleiro");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).toList();
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).toList();
        for(Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if(!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void initialSetup() {
        //Reis
        placeNewPiece('e', 1, new King(board, Color.BRANCO, this));
        placeNewPiece('e', 8, new King(board, Color.PRETO, this));

        //Rainhas
        placeNewPiece('d', 1, new Queen(board, Color.BRANCO));
        placeNewPiece('d', 8, new Queen(board, Color.PRETO));

        //Torres
        placeNewPiece('a', 1, new Rook(board, Color.BRANCO));
        placeNewPiece('h', 1, new Rook(board, Color.BRANCO));
        placeNewPiece('a', 8, new Rook(board, Color.PRETO));
        placeNewPiece('h', 8, new Rook(board, Color.PRETO));

        //Peoes
        placeNewPiece('a', 2, new Pawn(board, Color.BRANCO, this));
        placeNewPiece('b', 2, new Pawn(board, Color.BRANCO, this));
        placeNewPiece('c', 2, new Pawn(board, Color.BRANCO, this));
        placeNewPiece('d', 2, new Pawn(board, Color.BRANCO, this));
        placeNewPiece('e', 2, new Pawn(board, Color.BRANCO, this));
        placeNewPiece('f', 2, new Pawn(board, Color.BRANCO, this));
        placeNewPiece('g', 2, new Pawn(board, Color.BRANCO, this));
        placeNewPiece('h', 2, new Pawn(board, Color.BRANCO, this));

        placeNewPiece('a', 7, new Pawn(board, Color.PRETO, this));
        placeNewPiece('b', 7, new Pawn(board, Color.PRETO, this));
        placeNewPiece('c', 7, new Pawn(board, Color.PRETO, this));
        placeNewPiece('d', 7, new Pawn(board, Color.PRETO, this));
        placeNewPiece('e', 7, new Pawn(board, Color.PRETO, this));
        placeNewPiece('f', 7, new Pawn(board, Color.PRETO, this));
        placeNewPiece('g', 7, new Pawn(board, Color.PRETO, this));
        placeNewPiece('h', 7, new Pawn(board, Color.PRETO, this));

        //Bispos
        placeNewPiece('c', 1, new Bishop(board, Color.BRANCO));
        placeNewPiece('f', 1, new Bishop(board, Color.BRANCO));

        placeNewPiece('c', 8, new Bishop(board, Color.PRETO));
        placeNewPiece('f', 8, new Bishop(board, Color.PRETO));

        //Cavalos
        placeNewPiece('b', 1, new Knight(board, Color.BRANCO));
        placeNewPiece('g', 1, new Knight(board, Color.BRANCO));

        placeNewPiece('b', 8, new Knight(board, Color.PRETO));
        placeNewPiece('g', 8, new Knight(board, Color.PRETO));
    }

}
