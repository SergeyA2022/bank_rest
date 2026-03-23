package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 * Репозиторий для работы с сущностями банковских карт {@link Card}.
 * Обеспечивает стандартные CRUD операции и специализированные поисковые запросы к базе данных.
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Поиск карт конкретного владельца с фильтрацией по статусу и поддержкой пагинации.
     *
     * @param ownerId  идентификатор владельца карты
     * @param status   статус карты (например, ACTIVE, BLOCKED)
     * @param pageable параметры пагинации и сортировки
     * @return страница со списком найденных карт
     */
    Page<Card> findAllByOwnerIdAndStatus(Long ownerId, CardStatus status, Pageable pageable);

    /**
     * Поиск всех карт, принадлежащих конкретному пользователю.
     *
     * @param id       идентификатор владельца карты
     * @param pageable параметры пагинации и сортировки
     * @return страница со всеми картами указанного владельца
     */
    Page<Card> findAllByOwnerId(Long id, Pageable pageable);

    /**
     * Получение списка всех карт в системе, отфильтрованных по статусу (для администрирования).
     *
     * @param status   целевой статус карты
     * @param pageable параметры пагинации и сортировки
     * @return страница со списком карт
     */
    Page<Card> findAllByStatus(CardStatus status, Pageable pageable);

    /**
     * Поиск карты по идентификатору с наложением пессимистической блокировки на запись (PESSIMISTIC_WRITE).
     * Используется для безопасного обновления баланса в транзакциях, предотвращая состояние гонки (race conditions).
     *
     * @param id уникальный идентификатор карты
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.id = :id")
    void findByIdForUpdate(Long id);
}