package at.ac.tuwien.sepm.ss16.qse18.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementResultsetCloser {

    private static final Logger logger = LogManager.getLogger();

    public static void closeStatementsAndResultSets(Statement[] statements, ResultSet[] resultSets)
        throws DaoException {
        if (statements != null) {
            for (Statement s : statements) {
                if (s != null) {
                    try {
                        s.close();
                    } catch (SQLException e) {
                        logger.error("Could not close statement " + e.getMessage());
                        throw new DaoException("Could not close statement " + e.getMessage());
                    }
                }
            }
        }
        if (resultSets != null) {
            for (ResultSet rs : resultSets) {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.error("Could not close resultset " + e.getMessage());
                        throw new DaoException("Could not close resultset " + e.getMessage());
                    }
                }
            }
        }
    }
}
