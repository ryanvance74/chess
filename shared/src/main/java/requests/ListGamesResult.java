package requests;

import java.util.Collection;

public record ListGamesResult(Collection<ListGameResultSingle> games) {}
