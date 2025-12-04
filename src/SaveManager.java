import java.sql.*;

public class SaveManager {
    private static String saveFilePath;

    public static boolean initialize(String saveFilePath, boolean isNew) {
        SaveManager.saveFilePath = saveFilePath;
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            statement.setQueryTimeout(30);

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS progress (
                        level INTEGER,
                        health INTEGER,
                        position_x REAL,
                        position_y REAL
                        );
                    """);
            if (isNew) {
                statement.execute("INSERT INTO progress VALUES (0, 4, 0, 0)");
            }
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    public static int getLevel() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            var rs = statement.executeQuery("SELECT level FROM progress");
            if (rs.next()) return rs.getInt("level");
        } catch (SQLException e) {}
        return 0;
    }

    public static int getHealth() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            var rs = statement.executeQuery("SELECT health FROM progress");
            if (rs.next()) return rs.getInt("health");
        } catch (SQLException e) {}
        return 0;
    }

    public static double getPositionX() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            var rs = statement.executeQuery("SELECT position_x FROM progress");
            if (rs.next()) return rs.getDouble("position_x");
        } catch (SQLException e) {}
        return 0.0;
    }

    public static double getPositionY() {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.createStatement();
        ) {
            var rs = statement.executeQuery("SELECT position_y FROM progress");
            if (rs.next()) return rs.getDouble("position_y");
        } catch (SQLException e) {}
        return 0.0;
    }

    public static void setLevel(int currentLevel) {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.prepareStatement("UPDATE progress SET level = ?");
        ) {
            statement.setInt(1, currentLevel);
            statement.execute();
        } catch (SQLException e) {}
    }

    public static void setHealth(int health) {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.prepareStatement("UPDATE progress SET health = ?");
        ) {
            statement.setInt(1, health);
            statement.execute();
        } catch (SQLException e) {}
    }

    public static void setPosition(float x, float y) {
        try (
            var conn = DriverManager.getConnection("jdbc:sqlite:" + saveFilePath);
            var statement = conn.prepareStatement("UPDATE progress SET position_x = ?, position_y = ?");
        ) {
            statement.setDouble(1, (double)x);
            statement.setDouble(2, (double)y);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
