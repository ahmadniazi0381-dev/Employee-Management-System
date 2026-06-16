package database;

import java.util.List;

public abstract class BaseDAO<T, ID> {

    public abstract List<T> getAll();

    public abstract T getById(ID id);

    public abstract boolean save(T entity);

    public abstract boolean update(T entity);

    public abstract boolean delete(ID id);
}