package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ExecuteQueryHandler<T> {
    T handle(ResultSet rs) throws SQLException;
}
