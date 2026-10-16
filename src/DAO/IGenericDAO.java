package DAO;

import java.util.List;
import java.util.Optional;

public interface IGenericDAO<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean save(T entity);
    boolean update(T entity);
    boolean deleteById(ID id);
}

