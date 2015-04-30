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

	public OutputDependcyFileException(String outputFilePath,
		Exception ex) {
		super(String.format("Can not write in the output dependency file [%s]",
			outputFilePath), ex);
	}

}
