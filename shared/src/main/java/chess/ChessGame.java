package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;
    public ChessGame() {
        this.setTeamTurn(TeamColor.WHITE);
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> goodMoves = new ArrayList<>();
        if (startPosition == null) {return null;}
        ChessPiece testPiece = board.getPiece(startPosition);
        Collection<ChessMove> testMoves = testPiece.pieceMoves(board, startPosition);
        for (ChessMove move : testMoves) {
            if (validateSingleMove(move)) {
                goodMoves.add(move);
            }
        }
        return goodMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!validateSingleMove(move)) {throw Exception.InvalidMoveException;}
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingTeamColor = TeamColor.values()[teamColor.ordinal() + 1 % 2];

        // use getAllMoves instead of getAllValidMoves because it doesn't matter if the piece can actually execute
        // the move. It is just the threat of the move that makes a check.
        Collection<Collection<ChessMove>> opposingPieceSet = getAllMoves(opposingTeamColor);
        for (Collection<ChessMove> testMoveSet : opposingPieceSet) {
            for (ChessMove testMove : testMoveSet) {
                if (board.getPiece(testMove.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && deepEmpty(getAllValidMoves(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition testPosition;
        ChessPiece testPiece;
        Collection<ChessMove> testMoves;
        for (int i=1; i < 9; i++) {
            for (int j=1; i < 9; i++) {
                testPosition = new ChessPosition(i,j);
                testPiece = this.board.getPiece(testPosition);
                if (testPiece == null || testPiece.getTeamColor() != teamColor) {continue;}
                testMoves = testPiece.pieceMoves(this.board, testPosition);
                if (testMoves.isEmpty()) {continue;}
                for (ChessMove forwardMove : testMoves) {
                    ChessPiece targetPiece = this.board.getPiece(forwardMove.getEndPosition());
                    ChessMove reverseMove = new ChessMove(forwardMove.getEndPosition(), forwardMove.getStartPosition(), null);
                    this.makeMove(forwardMove);
                    boolean validMove = !this.isInCheck(teamColor);
                    this.makeMove(reverseMove);
                    this.board.addPiece(forwardMove.getEndPosition(), targetPiece);
                    if (validMove) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    private Collection<Collection<ChessMove>> getAllValidMoves(TeamColor teamColor) {
        // double for loop
    }

    private Collection<Collection<ChessMove>> getAllMoves(TeamColor teamColor) {

    }

    private boolean deepEmpty(Collection<Collection<ChessMove>> pieceSet) {
        for (Collection<ChessMove> moves : pieceSet) {
            if (!moves.isEmpty()) {return false;}
        }
        return true;
    }

    private boolean validateSingleMove(ChessMove move) {

    }
}
