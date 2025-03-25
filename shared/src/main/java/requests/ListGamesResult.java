package requests;

import java.util.Collection;

public record ListGamesResult(Collection<GameResultSingle> games) {}
