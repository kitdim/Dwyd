package kit.org.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    /**
     * Сохраняет объект
     * @param value объект для сохранения
     * @return true если сохранение успешно
     */
    boolean save(T value);

    /**
     * Обновляет объект
     * @param value объект для обновления
     * @return true если обновление успешно
     */
    boolean update(T value);

    /**
     * Удаляет объект
     * @param value объект для удаления
     * @return true если удаление успешно
     */
    boolean delete(T value);

    /**
     * Находит объект по идентификатору
     * @param id идентификатор объекта
     * @return найденный объект или Optional.empty()
     */
    Optional<T> findById(Long id);

    /**
     * Получает все объекты
     * @return список всех объектов
     */
    List<T> findAll();
}