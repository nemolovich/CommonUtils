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

	public DependenciesException(String message, Exception ex) {
		super(message, ex);
	}
}
