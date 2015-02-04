/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.mavendependenciesdownloader;

/**
 *
 * @author Nemolovich
 */
public class UnknownDependencyException extends DependenciesException {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 919423876225474073L;

	public UnknownDependencyException(String dependencyURL,
		Exception ex) {
		super(String.format("Can not retreive dependency at [%s]",
			dependencyURL), ex);
	}

}
