package fr.nemolovich.apps.mavendependenciesdownloader;

/**
 *
 * @author Nemolovich
 */
public abstract class DependenciesException extends Exception {

    /**
     * UID
     */
    private static final long serialVersionUID = 2108600027205267476L;

    /**
     * Exception constructor.
     *
     * @param message {@link String}: The description of the exception.
     * @param ex {@link Exception}: The exception cause.
     */
    public DependenciesException(String message, Exception ex) {
        super(message, ex);
    }
}
