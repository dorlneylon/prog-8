package itmo.lab7.database;

import itmo.lab7.basic.baseclasses.Movie;
import itmo.lab7.basic.baseenums.MpaaRating;
import itmo.lab7.basic.moviecollection.MovieCollection;
import itmo.lab7.server.ServerLogger;
import itmo.lab7.utils.serializer.Serializer;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Database class for connecting to and interacting with a database.
 */
public class Database {
    private final Connection connection;

    /**
     * Constructor for Database class.
     *
     * @param url      The URL of the database to connect to.
     * @param user     The username for the database.
     * @param password The password for the database.
     * @throws SQLException If the connection to the database fails.
     */
    public Database(String url, String user, String password) throws SQLException {
        Connection connectionTry;
        try {
            connectionTry = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SQLException("Unable to connect to database: ", e);
        }
        if (connectionTry == null) throw new SQLException("Unable to connect to database.");
        this.connection = connectionTry;
    }

    /**
     * Adds a new user to the database.
     *
     * @param login    The login of the new user.
     * @param password The password of the new user.
     * @return true if the user was successfully added, false otherwise.
     */

    public boolean addNewUser(String login, String password) {
        if (isUserExist(login)) return false;
        byte flags = 0; // last 2 digits are flags
        try {
            // Hash the password using SHA-256
            String encryptedPassword = Encryptor.encryptString(password);

            // Create a prepared statement to insert a new user into the users table
            PreparedStatement userStatement = connection.prepareStatement("INSERT INTO \"user\" (login, password) VALUES (?, ?)");
            // Set the login and password parameters of the prepared statement
            userStatement.setString(1, login);
            userStatement.setString(2, encryptedPassword);
            // If the user was successfully added, set the first flag to 1
            if (userStatement.executeUpdate() > 0) flags |= 1;

            // Create a prepared statement to insert a new user into the user_history table
            PreparedStatement historyStatement = connection.prepareStatement("INSERT INTO \"user_history\" (user_login) VALUES (?)");
            // Set the login and command_history parameters of the prepared statement
            historyStatement.setString(1, login);
            // If the user was successfully added, set the second flag to 1
            if (historyStatement.executeUpdate() > 0) flags |= 2;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to add new user " + e.getMessage());
        } finally {
            try {
                // If both flags are not set, delete the user from the users table
                if (flags != 3) {
                    String sql = "DELETE FROM \"user\" WHERE login = ?";
                    PreparedStatement pre = connection.prepareStatement(sql);
                    pre.setString(1, login);
                    pre.executeUpdate();
                }
            } catch (Exception finalException) {
                // Log any errors that occur
                ServerLogger.getLogger().log(Level.INFO, "Unable to delete user " + finalException.getMessage());
            }
        }
        // Return true if both flags are set, false otherwise
        return flags == 3;
    }


    /**
     * Checks if a user exists in the database
     *
     * @param login the user's login
     * @return true if the user exists, false otherwise
     */
    public boolean isUserExist(String login) {
        try {
            String sql = "SELECT * FROM \"user\" WHERE login = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, login);
            return pre.executeQuery().next();
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to check user " + e.getMessage());
        }
        return false;
    }

    /**
     * Adds a command to the user's command history.
     *
     * @param login   The user's login.
     * @param command The command to add to the user's history.
     * @return true if the command was added successfully, false otherwise.
     */
    public boolean addCommandToHistory(String login, String command) {
        try {
            // This update query works like sized stack.
            String sql = """
                    UPDATE "user_history"
                    SET command_history =
                            CASE
                                WHEN array_length(command_history, 1) = 7
                                    THEN command_history[2:7] || ARRAY [?]::TEXT[]
                                ELSE command_history || ARRAY [?]::TEXT[]
                                END
                    WHERE user_login = ?;""";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, command);
            pre.setString(2, command);
            pre.setString(3, login);
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to add command to history " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves the command history for the specified user.
     *
     * @param login The user's login.
     * @return An array of strings containing the user's command history.
     */
    public String[] getCommandHistory(String login) {
        try {
            String sql = "SELECT command_history FROM \"user_history\" WHERE user_login = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, login);
            ResultSet result = pre.executeQuery();
            if (result.next()) return (String[]) result.getArray(1).getArray();
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to get command history " + e.getMessage());
        }
        return new String[0];
    }

    /**
     * Checks if the user is registered in the database.
     *
     * @param login    The user's login.
     * @param password The user's password.
     * @return true if the user is registered, false otherwise.
     */
    public boolean userSignIn(String login, String password) {
        try {
            String hashedPassword = Encryptor.encryptString(password);
            String sql = "SELECT * FROM \"user\" WHERE login = ? AND password = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, login);
            pre.setString(2, hashedPassword);
            ResultSet result = pre.executeQuery();
            if (result.next()) return true;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to check user " + e.getMessage());
        }
        return false;
    }

    /**
     * Inserts a movie into the collection of a user
     *
     * @param login the login of the user
     * @param movie the movie to be inserted
     * @return true if the movie was successfully inserted, false otherwise
     */
    public boolean insertToCollection(String login, Movie movie) {
        try {
            String sql = "INSERT INTO \"collection\" (id, editor, movie, last_modified_date) VALUES (?, ?, ?, ?)";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setInt(1, Math.toIntExact(movie.getId()));
            pre.setString(2, login);
            pre.setBytes(3, Serializer.serialize(movie));
            pre.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to insert to collection " + e.getMessage());
        }
        return false;
    }

    /**
     * Removes an item from the collection by its key and the editor's login.
     *
     * @param key   The key of the item to be removed.
     * @param login The login of the editor who is removing the item.
     * @return True if the item was removed, false otherwise.
     */
    public boolean removeByKey(Long key, String login) {
        try {
            String sql = "DELETE FROM \"collection\" WHERE id = ? AND editor = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setLong(1, key);
            pre.setString(2, login);
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to remove by key " + e.getMessage());
        }
        return false;
    }

    /**
     * Clears the collection of a given user.
     *
     * @param login The login of the user whose collection is to be cleared.
     * @return True if the collection was successfully cleared, false otherwise.
     */
    public boolean clearCollection(String login) {
        try {
            String sql = "DELETE FROM \"collection\" WHERE editor = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, login);
            return pre.executeUpdate() > 0;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to clear collection " + e.getMessage());
        }
        return false;
    }

    /**
     * Removes all movies from the collection with the given MPAA rating.
     *
     * @param username   The username of the user whose collection is being modified.
     * @param mpaaRating The MPAA rating of the movies to be removed.
     */
    public void removeByMpaaRating(String username, MpaaRating mpaaRating) {
        try {
            String selectSql = "SELECT id FROM \"collection\" WHERE editor = ?";
            PreparedStatement selectPre = connection.prepareStatement(selectSql);
            selectPre.setString(1, username);
            ResultSet selectResult = selectPre.executeQuery();

            List<Integer> movieIds = new ArrayList<>();
            while (selectResult.next()) {
                movieIds.add(selectResult.getInt(1));
            }

            String deleteSql = "DELETE FROM \"collection\" WHERE id = ?";
            PreparedStatement deletePre = connection.prepareStatement(deleteSql);
            for (int id : movieIds) {
                Movie movie = getMovieById(username, id);
                if (movie != null && movie.getRating().equals(mpaaRating)) {
                    deletePre.setInt(1, id);
                    deletePre.executeUpdate();
                }
            }
        } catch (SQLException e) {
            ServerLogger.getLogger().log(Level.INFO, "Unable to remove by mpaa rating " + e.getMessage());
        }
    }

    public boolean replaceLower(Integer key, Movie movie, String username) {
        try {
            String sql = "SELECT id FROM \"collection\" WHERE editor = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, username);
            ResultSet resultSet = pre.executeQuery();
            for (int id = 0; resultSet.next(); id = resultSet.getInt(1)) {
                Movie movie1 = getMovieById(username, id);
                if (movie1 != null && movie1.getOscarsInt() < key)
                    return updateById(username, Math.toIntExact(movie.getId()), movie);
            }
            return true;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to replace lower " + e.getMessage());
        }
        return false;
    }

    public boolean removeGreater(Integer key, String username) {
        try {
            String sql = "SELECT id FROM \"collection\" WHERE editor = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setString(1, username);
            ResultSet resultSet = pre.executeQuery();
            for (int id = 0; resultSet.next(); id = resultSet.getInt(1)) {
                Movie movie = getMovieById(username, id);
                if (movie != null && movie.getOscarsInt() > key)
                    removeByKey(movie.getId(), username);
            }
            return true;
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to remove greater " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves a movie from the collection by its ID.
     *
     * @param username The username of the editor of the movie.
     * @param id       The ID of the movie.
     * @return The movie object, or null if the movie does not exist.
     * @throws SQLException If an error occurs while executing the query.
     */
    private Movie getMovieById(String username, int id) throws SQLException {
        String sql = "SELECT movie FROM \"collection\" WHERE id = ? AND editor = ?";
        PreparedStatement pre = connection.prepareStatement(sql);
        pre.setInt(1, id);
        pre.setString(2, username);
        ResultSet result = pre.executeQuery();
        if (result.next()) {
            return (Movie) Serializer.deserialize(result.getBytes(1));
        }
        return null;
    }

    public boolean updateById(String username, int id, Movie movie) {
        try {
            String sql = "UPDATE \"collection\" SET movie = ? WHERE id = ? AND editor = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setBytes(1, Serializer.serialize(movie));
            pre.setInt(2, id);
            pre.setString(3, username);
            pre.execute();
            return true;
        } catch (SQLException sqle) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to update by id " + sqle.getMessage());
        }
        return false;
    }

    public boolean isUserEditor(String username, int id) {
        try {
            String sql = "SELECT id FROM \"collection\" WHERE id = ? AND editor = ?";
            PreparedStatement pre = connection.prepareStatement(sql);
            pre.setInt(1, id);
            pre.setString(2, username);
            ResultSet result = pre.executeQuery();
            return result.next();
        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().warning("Unable to check editor " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves the collection of movies from the database.
     *
     * @return A MovieCollection object containing all the movies in the database.
     */
    public MovieCollection getCollection() {
        try {
            // Get all rows from the collection table
            String sql = "SELECT id, movie FROM \"collection\"";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Create a new MovieCollection object to hold the results
            MovieCollection collection = new MovieCollection();

            // Iterate over each row in the result set
            while (resultSet.next()) {
                // Get the id and byte array value of the movie column
                long id = resultSet.getLong("id");
                // get the byte array from the result set
                byte[] movieBytes = resultSet.getBytes("movie");
                // Deserialize the movie object from the byte array
                Movie movie = (Movie) Serializer.deserialize(movieBytes);

                // Add the movie object to the collection
                collection.insert(id, movie);
            }
            // Return the populated MovieCollection object
            return collection;

        } catch (SQLException e) {
            // Log any errors that occur
            ServerLogger.getLogger().log(Level.INFO, "Unable to get collection " + e.getMessage());
        }
        return null;
    }
}
