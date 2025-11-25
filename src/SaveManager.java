import java.sql.*;

public class SaveManager {
    private static String saveFilePath;

    public static void initialize(String saveFilePath) {
        this.saveFilePath = saveFilePath;

        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS progress (
                        level INTEGER,
                        point INTEGER,
                        grid_x INTEGER,
                        grid_y INTEGER,
                        position_x REAL,
                        position_y REAL
                    );
                    """);
        } catch (SQLException e) {
        }
    }

    public static int getLevel() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);
            
            var rs = statement.executeQuery("SELECT level FROM progress");
            if (rs.next()) {
                return rs.getInt("level");
            }
        } catch (SQLException e) {
        }
    }

    public static int getPoint() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);
            
            var rs = statement.executeQuery("SELECT point FROM progress");
            if (rs.next()) {
                return rs.getInt("point");
            }
        } catch (SQLException e) {
        }
    }

    public static int getGridX() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);
            
            var rs = statement.executeQuery("SELECT grid_x FROM progress");
            if (rs.next()) {
                return rs.getInt("grid_x");
            }
        } catch (SQLException e) {
        }
    }

    public static int getGridY() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);
            
            var rs = statement.executeQuery("SELECT grid_x FROM progress");
            if (rs.next()) {
                return rs.getInt("grid_x");
            }
        } catch (SQLException e) {
        }
    }

    public static int getPositionX() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);
            
            var rs = statement.executeQuery("SELECT position_x FROM progress");
            if (rs.next()) {
                return rs.getInt("position_x");
            }
        } catch (SQLException e) {
        }
    }

    public static int getPositionY() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);
            
            var rs = statement.executeQuery("SELECT position_y FROM progress");
            if (rs.next()) {
                return rs.getInt("position_y");
            }
        } catch (SQLException e) {
        }
    }
}
