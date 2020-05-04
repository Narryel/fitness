package com.narryel.fitness.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    public EntityNotFoundException(Long id, Class<?> clazz) {
        super(String.format("Не удалось найти сущность [%s] по id [%s]", clazz.getSimpleName(), id));
    }

    /**
     * Пробрасывать когда искали сущность не по ID, в остальных случаях использовать
     * @see #EntityNotFoundException(Long id, Class clazz)
     */
    public EntityNotFoundException(String fieldName, Object id, Class<?> clazz) {
        super(String.format("Не удалось найти сущность [%s] по полю [%s] = [%s]", clazz, fieldName, id.toString()));
    }

}
