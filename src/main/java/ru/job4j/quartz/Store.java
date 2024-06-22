package ru.job4j.quartz;

import java.sql.SQLException;
import java.util.List;

public interface Store extends AutoCloseable {
    void save(Post post);

    List<Post> getAll() throws SQLException;

    Post findById(int id);

}
