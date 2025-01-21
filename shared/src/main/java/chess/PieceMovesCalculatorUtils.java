package chess;

import java.util.ArrayList;
import java.util.Collection;

public final class PieceMovesCalculatorUtils {
    public static Collection<ChessMove> GeneralMoves(ChessBoard board, ChessPosition position, Collection<int[]> directionArray, boolean extendable) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).teamColor;
        for (int[] direction : directionArray) {
            int[] scaledDirection = new int[]{direction[0], direction[1]};
            while (true) {
                if (OutOfBounds(position.getRow(), position.getColumn(), scaledDirection)) {break;}
                ChessPosition testPosition = new ChessPosition(position.getRow() + direction[0], position.getColumn() + direction[1]);
                ChessPiece testPiece = board.getPiece(testPosition);
                if (testPiece != null) {
                    if (testPiece.teamColor != myColor) {
                        moves.add(new ChessMove(testPosition, testPosition, null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(testPosition, testPosition, null));
                    scaledDirection[0] += direction[0];
                    scaledDirection[1] += direction[1];
                }
                if (!extendable) {break;}
            }
        }
        return moves;
    }

    public static Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).teamColor;
        int orientation = myColor == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean check_ahead = AddPawnMoves(board, moves, position, new int[]{0, orientation}, false);
        if (check_ahead && ((position.getRow() == 2 && myColor == ChessGame.TeamColor.WHITE) || (position.getRow() == 7 && myColor == ChessGame.TeamColor.BLACK))) {
            AddPawnMoves(board, moves, position, new int[]{0, 2*orientation}, false);
        }
        AddPawnMoves(board, moves, position, new int[]{-1, orientation}, true);
        AddPawnMoves(board, moves, position, new int[]{1, orientation}, true);
        return moves;

    }

    private static boolean AddPawnMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition position, int[] direction, boolean mustTake) {
        boolean check_ahead = false;
        ChessGame.TeamColor myColor = board.getPiece(position).teamColor;
        ChessPosition testPosition = new ChessPosition(position.getRow()+direction[0],position.getColumn()+direction[1]);
        ChessPiece testPiece = board.getPiece(testPosition);

        if (!OutOfBounds(position.getRow(), position.getColumn(), new int[]{direction[0], direction[1]})) {
            return false;
        }
        if (testPiece != null) {
            if (testPiece.teamColor != myColor) {
                if (testPosition.getRow() == 1 || testPosition.getRow() == 8) {
                    moves.add(new ChessMove(testPosition, testPosition, null));
                    moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.QUEEN));
                } else {
                    moves.add(new ChessMove(testPosition, testPosition, null));
                }
            }
        } else {
            if (mustTake) {return false;}
            moves.add(new ChessMove(testPosition, testPosition, null));
            if (testPosition.getRow() == 1 || testPosition.getRow() == 8) {
                moves.add(new ChessMove(testPosition, testPosition, null));
                moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(testPosition, testPosition, ChessPiece.PieceType.QUEEN));
            } else {
                moves.add(new ChessMove(testPosition, testPosition, null));
                check_ahead = true;
            }

        }
        return check_ahead;
    }



    private static boolean OutOfBounds(int row, int col, int[] direction) {
        int newRow = row + direction[0];
        int newCol = col + direction[1];
        return newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8;
    }
}