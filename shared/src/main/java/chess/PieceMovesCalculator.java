package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}

class QueenMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<int[]> directionArray = getDirectionArray();
        return PieceMovesCalculatorUtils.GeneralMoves(board, position, directionArray, true);
    }

    private Collection<int[]> getDirectionArray() {
        Collection<int[]> directionArray = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (!(x == 0 && y == 0)) {
                    directionArray.add(new int[]{x,y});
                }
            }
        }
        return directionArray;
    }
}

class KingMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<int[]> directionArray = getDirectionArray();
        return PieceMovesCalculatorUtils.GeneralMoves(board, position, directionArray, false);
    }

    private Collection<int[]> getDirectionArray() {
        Collection<int[]> directionArray = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (!(x == 0 && y == 0)) {
                    directionArray.add(new int[]{x,y});
                }
            }
        }
        return directionArray;
    }
}

class BishopMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<int[]> directionArray = getDirectionArray();
        return PieceMovesCalculatorUtils.GeneralMoves(board, position, directionArray, true);
    }

    private Collection<int[]> getDirectionArray() {
        Collection<int[]> directionArray = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                // only accept diagonals
                if (!(x == 0 && y == 0) && (Math.abs(x) + Math.abs(y) == 2) ) {
                    directionArray.add(new int[]{x,y});
                }
            }
        }
        return directionArray;
    }
}

class KnightMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<int[]> directionArray = getDirectionArray();
        return PieceMovesCalculatorUtils.GeneralMoves(board, position, directionArray, false);
    }

    private Collection<int[]> getDirectionArray() {
        Collection<int[]> knightDirectionArray = new ArrayList<>();
        knightDirectionArray.add(new int[]{-1,2});
        knightDirectionArray.add(new int[]{1,2});
        knightDirectionArray.add(new int[]{-1,-2});
        knightDirectionArray.add(new int[]{1,-2});
        knightDirectionArray.add(new int[]{-2,1});
        knightDirectionArray.add(new int[]{-2,-1});
        knightDirectionArray.add(new int[]{2,1});
        knightDirectionArray.add(new int[]{2,-1});
        return knightDirectionArray;
    }

}

class RookMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<int[]> directionArray = getDirectionArray();
        return PieceMovesCalculatorUtils.GeneralMoves(board, position, directionArray, true);
    }

    private Collection<int[]> getDirectionArray() {
        Collection<int[]> rookDirectionArray = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                // only accept diagonals
                if (!(x == 0 && y == 0) && (x == 0 || y == 0) ) {
                    rookDirectionArray.add(new int[]{x,y});
                }
            }
        }
        return rookDirectionArray;
    }
}

class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myColor = board.getPiece(position).teamColor;


    }

}


