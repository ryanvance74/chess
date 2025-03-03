package service;

import model.GameData;
import java.util.Collection;

public record ListGamesResult(Collection<ListGameResultSingle> games) {}
