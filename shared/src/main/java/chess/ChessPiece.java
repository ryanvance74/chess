package chess;

import java.util.ArrayList;
import java.util.Collection;

interface checkDirectionCondition {
    boolean isValid(int x, int y);
}

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
        checkDirectionCondition preApprovedDirections = (x,y) -> true;
        switch (pieceType) {
            case KING:
                checkDirectionCondition kingDirections = (x,y) -> true;
                calculatePieceMoves(board, moves, generalDirectionArray, kingDirections, myPosition, "no");
            case QUEEN:
                checkDirectionCondition queenDirections = (x,y) -> true;
                calculatePieceMoves(board, moves, generalDirectionArray, queenDirections, myPosition, "yes");
            case BISHOP:
                checkDirectionCondition bishopDirections = (x,y) -> (Math.abs(x)+Math.abs(y)) == 2;
                calculatePieceMoves(board, moves, generalDirectionArray, bishopDirections, myPosition, "yes");
            case KNIGHT:
                Collection<int[]> knightDirectionArray = new ArrayList<>();
                // refactor this
                knightDirectionArray.add(new int[]{-1,2});
                knightDirectionArray.add(new int[]{1,2});
                knightDirectionArray.add(new int[]{-1,-2});
                knightDirectionArray.add(new int[]{1,-2});
                knightDirectionArray.add(new int[]{-2,1});
                knightDirectionArray.add(new int[]{-2,-1});
                knightDirectionArray.add(new int[]{2,1});
                knightDirectionArray.add(new int[]{2,-1});
                calculatePieceMoves(board, moves, knightDirectionArray, preApprovedDirections, myPosition, "no");
            case ROOK:
                checkDirectionCondition rookDirections = (x,y) -> x == 0 || y == 0;
                calculatePieceMoves(board, moves, generalDirectionArray, rookDirections, myPosition, "yes");
            case PAWN:
                Collection<int[]> pawnDirectionArray = new ArrayList<>();
                pawnDirectionArray.add(new int[]{0,1});
                pawnDirectionArray.add(new int[]{0,2});
                checkDirectionCondition pawnDirections = (x,y) -> x == 0 && (y == 1 || y == 2);
                calculatePieceMoves(board, moves, pawnDirectionArray, pawnDirections, myPosition, "pawn");
        }
        return moves;
    }


    private void calculatePieceMoves(ChessBoard board, Collection<ChessMove> moves, Collection<int[]> directionArray, checkDirectionCondition pieceDirection, ChessPosition myPosition, String extendable) {
        for (int[] direction : directionArray) {
            if (pieceDirection.isValid(direction[0],direction[1])) {
                int[] scaledDirection = new int[]{direction[0], direction[1]};
                while (true) {
                    boolean[] result = checkDirection(board, myPosition.Row, myPosition.Col, teamColor, scaledDirection);
                    if (result[0]) {
                        ChessPosition newPosition = new ChessPosition(myPosition.Row + scaledDirection[0], myPosition.Col + scaledDirection[1]);
                        moves.add(new ChessMove(myPosition, newPosition, null));
                        scaledDirection[0] += direction[0];
                        scaledDirection[1] += direction[1];
                        if (!result[1] && extendable.equals("pawn")) {return;}
                        else if (!result[1]) {break;}
                    } else {
                        break;
                    }
                    if (extendable.equals("no")) {break;}
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
