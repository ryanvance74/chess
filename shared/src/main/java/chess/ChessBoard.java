package chess;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.lang.StringBuilder;
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (board[position.getRow()-1][position.getColumn()-1] == null) {
            return null;
        }
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        for (int col = 0; col < 8; col++) {
            board[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        for (int col = 0; col < 8; col++) {
            board[6][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        //printBoard(board);
    }


    private static void printTest(String str) {
        System.out.println(str);
    }

    private static void printBoard(ChessPiece[][] board) {
        StringBuilder sb = new StringBuilder();
        sb.append("observing new board...\n");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece == null) {
                    sb.append("L");
                } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    if (piece.teamColor == ChessGame.TeamColor.WHITE) {
                        sb.append("P");
                    } else {
                        sb.append("p");
                    }
                } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                    if (piece.teamColor == ChessGame.TeamColor.WHITE) {
                        sb.append("R");
                    } else {
                        sb.append("r");
                    }
                } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                    if (piece.teamColor == ChessGame.TeamColor.WHITE) {
                        sb.append("N");
                    } else {
                        sb.append("n");
                    }
                } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                    if (piece.teamColor == ChessGame.TeamColor.WHITE) {
                        sb.append("B");
                    } else {
                        sb.append("b");
                    }
                } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                    if (piece.teamColor == ChessGame.TeamColor.WHITE) {
                        sb.append("Q");
                    } else {
                        sb.append("q");
                    }
                } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (piece.teamColor == ChessGame.TeamColor.WHITE) {
                        sb.append("K");
                    } else {
                        sb.append("k");
                    }
                }
            }
            sb.append("\n");
        }
        //printTest(sb.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
