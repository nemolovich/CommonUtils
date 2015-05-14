package fr.nemolovich.apps.mavendependenciesdownloader;

/**
 *
 * @author Nemolovich
 */
public class DownloadSecurityException extends DependenciesException {

    /**
     * UID
     */
    private static final long serialVersionUID = 919423876225474073L;

    /**
     * Exception constructor.
     *
     * @param dependencyURL {@link String}: The dependency URL that could not be
     * download.
     * @param ex {@link Exception}: The exception cause.
     */
    public DownloadSecurityException(String dependencyURL,
        Exception ex) {
        super(String.format("Can not retreive dependency at [%s]",
            dependencyURL), ex);
    }

}
