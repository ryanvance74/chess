package chess;

import java.util.ArrayList;
import java.util.Collection;

public final class PieceMovesCalculatorUtils {
    public static Collection<ChessMove> generalMoves(ChessBoard board, ChessPosition position, Collection<int[]> directionArray, boolean extendable) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).teamColor;
        for (int[] direction : directionArray) {
            int[] scaledDirection = {direction[0], direction[1]};
            while (true) {
                if (outOfBounds(position.getRow(), position.getColumn(), scaledDirection)) {break;}
                ChessPosition testPosition = new ChessPosition(position.getRow() + scaledDirection[0], position.getColumn() + scaledDirection[1]);
                ChessPiece testPiece = board.getPiece(testPosition);
                if (testPiece != null) {
                    if (testPiece.teamColor != myColor) {
                        moves.add(new ChessMove(position, testPosition, null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(position, testPosition, null));
                    scaledDirection[0] += direction[0];
                    scaledDirection[1] += direction[1];
                }
                if (!extendable) {break;}
            }
        }
        return moves;
    }

    public static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).teamColor;
        int orientation = myColor == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean checkAhead = addPawnMoves(board, moves, position, new int[]{orientation, 0}, false);
        boolean conditionOne = ((position.getRow() == 7 && myColor == ChessGame.TeamColor.BLACK) || (position.getRow() == 2 && myColor == ChessGame.TeamColor.WHITE));
        if (checkAhead && conditionOne) {
            addPawnMoves(board, moves, position, new int[]{2*orientation, 0}, false);
        }

        addPawnMoves(board, moves, position, new int[]{orientation, -1}, true);
        addPawnMoves(board, moves, position, new int[]{orientation, 1}, true);
        return moves;

    }

    private static boolean addPawnMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition position, int[] direction, boolean canTake) {
        boolean checkAhead = false;
        //printLoop(position.getRow(), position.getColumn(), direction);
        if (outOfBounds(position.getRow(), position.getColumn(), new int[]{direction[0], direction[1]})) {
            return false;
        }
        ChessGame.TeamColor myColor = board.getPiece(position).teamColor;

        ChessPosition testPosition = new ChessPosition(position.getRow()+direction[0],position.getColumn()+direction[1]);
        ChessPiece testPiece = board.getPiece(testPosition);


        if (testPiece != null) {
            if (!canTake) {return checkAhead;}
            if (testPiece.teamColor != myColor) {
                if (testPosition.getRow() == 1 || testPosition.getRow() == 8) {
                    addMovesHelper(moves, position, testPosition);
                } else {
                    moves.add(new ChessMove(position, testPosition, null));
                }
            }
        } else {
            if (canTake) {return false;}
            if (testPosition.getRow() == 1 || testPosition.getRow() == 8) {
                addMovesHelper(moves, position, testPosition);
            } else {
                moves.add(new ChessMove(position, testPosition, null));
                checkAhead = true;
            }

        }
        return checkAhead;
    }

    private static boolean outOfBounds(int row, int col, int[] direction) {
        int newRow = row + direction[0];
        int newCol = col + direction[1];
        //printLoop(row-1, col-1, direction);
        return newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8;
    }

    private static void addMovesHelper(Collection<ChessMove> moves, ChessPosition position, ChessPosition testPosition) {
        moves.add(new ChessMove(position, testPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(position, testPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(position, testPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(position, testPosition, ChessPiece.PieceType.QUEEN));
    }
}