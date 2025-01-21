package chess;

import java.util.ArrayList;
import java.util.Collection;







/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessPiece.PieceType pieceType;
    ChessGame.TeamColor teamColor;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        pieceType = type;
        teamColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currRow = myPosition.getRow();
        int currCol = myPosition.getColumn();
        Collection<int[]> generalDirectionArray = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (!(x == 0 && y == 0)) {
                    generalDirectionArray.add(new int[]{x,y});
                }
            }
        }
    }




    /**
     * @return Two booleans. First boolean is whether you can move to the position. Second boolean is whether you could potentially go further.
     * You cannot go further if there is a piece blocking you. If there is a piece blocking you, and it is of the opposite color then you can move there
     * but not further.
     */
    private boolean[] checkDirection(ChessBoard board, int currRow, int currCol, ChessGame.TeamColor myColor, int[] direction) {
        ChessPosition testPosition = new ChessPosition(currRow + direction[0], currCol + direction[1]);
        ChessPiece piece = board.getPiece(testPosition);
        if (piece != null) {
            if (piece.teamColor == myColor) {
                return new boolean[]{false, false};
            } else {
                return new boolean[]{true, false};
            }
        } else {
            return new boolean[]{true, true};
        }

    }
}
