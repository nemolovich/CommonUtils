package fr.nemolovich.apps.mavendependenciesdownloader;

/**
 *
 * @author Nemolovich
 */
public class OutputDependcyFileException extends DependenciesException {

    /**
     * UID
     */
    private static final long serialVersionUID = 6978768558724214308L;

    /**
     * Exception constructor.
     *
     * @param outputFilePath {@link String}: The file path that could not be
     * used to write data.
     * @param ex {@link Exception}: The exception cause.
     */
    public OutputDependcyFileException(String outputFilePath,
        Exception ex) {
        super(String.format("Can not write in the output dependency file [%s]",
            outputFilePath), ex);
    }

}
