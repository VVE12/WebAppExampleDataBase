package project.example.db.dao;

import project.example.db.models.Car;
import project.example.db.models.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UsersDaoJdbcTemplateImpl implements UsersDao {

    private JdbcTemplate template;

    private final String SQL_SELECT_ALL =
            "SELECT * FROM new_user_database";

    private Map<Integer, User> usersMap = new HashMap<>();

    //language=SQL
    private final String SQL_SELECT_USER_WITH_CARS =
            "SELECT new_user_database.*, new_user_vehicle.id as car_id, new_user_vehicle.model FROM new_user_database LEFT JOIN new_user_vehicle ON new_user_database.id = new_user_vehicle.owner_id WHERE new_user_database.id = ?";

    private final String SQL_SELECT_ALL_BY_FIRST_NAME =
            "SELECT * FROM new_user_database WHERE first_name = ?";

    public UsersDaoJdbcTemplateImpl(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    private RowMapper<User> userRowMapper
            = (ResultSet resultSet, int i) -> {
        Integer id = resultSet.getInt("id");

        if (!usersMap.containsKey(id)) {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            User user = new User(id, firstName, lastName, new ArrayList<>());
            usersMap.put(id, user);
        }

        Car car = new Car(resultSet.getInt("car_id"),
                resultSet.getString("model"), usersMap.get(id));

        usersMap.get(id).getCars().add(car);

        return usersMap.get(id);
    };

    @Override
    public List<User> findAllByFirstName(String firstName) {
        return template.query(SQL_SELECT_ALL_BY_FIRST_NAME, userRowMapper, firstName);
    }

    @Override
    public Optional<User> find(Integer id) {
        template.query(SQL_SELECT_USER_WITH_CARS, userRowMapper, id);

        if (usersMap.containsKey(id)) {
            return Optional.of(usersMap.get(id));
        }
        return Optional.empty();
    }

    @Override
    public void save(User model) {

    }

    @Override
    public void update(User model) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public List<User> findAll() {
        return template.query(SQL_SELECT_ALL, userRowMapper);
    }
}
