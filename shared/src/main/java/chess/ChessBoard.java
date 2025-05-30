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
        populateBoard(board, ChessGame.TeamColor.WHITE, false);
        populateBoard(board, ChessGame.TeamColor.WHITE, true);
        populateBoard(board, ChessGame.TeamColor.BLACK, false);
        populateBoard(board, ChessGame.TeamColor.BLACK, true);
    }

    private void populateBoard(ChessPiece[][] board, ChessGame.TeamColor color, boolean pawns) {
        int row;
        if (color == ChessGame.TeamColor.WHITE && pawns) {
            row = 1;
        } else if (color == ChessGame.TeamColor.WHITE) {
            row = 0;
        } else if (pawns) {
            row = 6;
        } else {
            row = 7;
        }
        if (pawns) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
            }
        } else {
            for (int col = 0; col < 8; col++) {
                board[row][col] = new ChessPiece(color, BOARD_PIECE_MAP.get((char)(col+'0')));
            }
        }
    }

    private static final Map<Character, ChessPiece.PieceType> BOARD_PIECE_MAP = Map.of(
            '0', ChessPiece.PieceType.ROOK,
            '1', ChessPiece.PieceType.KNIGHT,
            '2', ChessPiece.PieceType.BISHOP,
            '3', ChessPiece.PieceType.QUEEN,
            '4', ChessPiece.PieceType.KING,
            '5', ChessPiece.PieceType.BISHOP,
            '6', ChessPiece.PieceType.KNIGHT,
            '7', ChessPiece.PieceType.ROOK
    );

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
