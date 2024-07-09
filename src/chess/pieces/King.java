package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

    public King(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "K";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        //acima da peca
        p.setValues(this.position.getRow() - 1, this.position.getColumn());
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //abaixo da peca
        p.setValues(this.position.getRow() + 1, this.position.getColumn());
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //A esqueda da peca
        p.setValues(this.position.getRow(), this.position.getColumn() - 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //A direita da peca
        p.setValues(this.position.getRow(), this.position.getColumn() + 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //Diagonal esquerda cima
        p.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //Diagonal esquerda baixo
        p.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //Diagonal direita cima
        p.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //Diagonal direita baixo
        p.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        return mat;
    }

    public boolean canMove(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return (p == null) || p.getColor() != this.getColor();
    }

}
