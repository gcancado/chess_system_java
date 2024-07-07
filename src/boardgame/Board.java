package boardgame;

public class Board {

    private int rows;
    private int columns;
    private Piece[][] pieces;

    //Constructor
    public Board(int rows, int columns) {
        if(rows < 1 || columns < 1) {
            throw new BoardException("Erro ao criar o tabuleiro, deve haver ao menos 1 linha e 1 coluna");
        }
        this.rows = rows;
        this.columns = columns;
        this.pieces = new Piece[rows][columns];
    }

    //Getters and Setters
    public int getRows() {
        return rows;
    }
    public int getColumns() {
        return columns;
    }

    public Piece piece(int row, int column) {
        if(!this.positionExists(row, column)) {
            throw new BoardException("Essa posicao nao existe");
        }

        return this.pieces[row][column];
    }

    public Piece piece(Position position) {
        if(!this.positionExists(position)) {
            throw new BoardException("Essa posicao nao existe");
        }

        return this.pieces[position.getRow()][position.getColumn()];
    }

    public void placePiece(Piece piece, Position position) {
        if(this.thereIsAPiece(position)) {
            throw new BoardException("Ja existe uma peca nessa posicao");
        }

        this.pieces[position.getRow()][position.getColumn()] = piece;
        piece.position = position;
    }

    public Piece removePiece(Position position) {
        if(!positionExists(position)) {
            throw new BoardException("Posicao nao existe");
        }
        if(this.piece(position) == null) {
            return null;
        }
        Piece aux = piece(position);
        aux.position = null;
        pieces[position.getRow()][position.getColumn()] = null;
        return aux;
    }

    private boolean positionExists(int row, int column) {
        return (row >= 0 && row < this.rows && column >= 0 && column < this.columns);
    }

    public boolean positionExists(Position position) {
        return positionExists(position.getRow(), position.getColumn());
    }

    public boolean thereIsAPiece(Position position) {
        if(!this.positionExists(position)) {
            throw new BoardException("Essa posicao nao existe");
        }
        return this.piece(position) != null;
    }

}
