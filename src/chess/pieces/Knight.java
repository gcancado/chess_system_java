package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {

    public Knight(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "N";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        //pra cima na esquerda
        p.setValues(this.position.getRow() - 1, this.position.getColumn() - 2);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        p.setValues(this.position.getRow() - 2, this.position.getColumn() - 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //pra cima na direita
        p.setValues(this.position.getRow() - 1, this.position.getColumn() + 2);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        p.setValues(this.position.getRow() - 2, this.position.getColumn() + 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //Pra baixo na esquerda
        p.setValues(this.position.getRow() + 1, this.position.getColumn() - 2);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        p.setValues(this.position.getRow() + 2, this.position.getColumn() - 1);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //Pra baixo na direita
        p.setValues(this.position.getRow() + 1, this.position.getColumn() + 2);
        if(this.getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        //Diagonal direita baixo
        p.setValues(this.position.getRow() + 2, this.position.getColumn() + 1);
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
