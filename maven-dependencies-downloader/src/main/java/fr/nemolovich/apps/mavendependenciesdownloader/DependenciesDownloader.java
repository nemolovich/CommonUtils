/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.mavendependenciesdownloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Utilities to download all dependencies of a pom.xml
 * {@link Model}.
 *
 * @author Nemolovich
 */
public final class DependenciesDownloader {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER
		= Logger.getLogger(DependenciesDownloader.class);
	private static final String NOT_NEEDED_SCOPES = "(test|provided|system)";
	private static final Pattern NOT_NEEDED_SCOPES_PATTERN = Pattern.compile(
		NOT_NEEDED_SCOPES, Pattern.CASE_INSENSITIVE);

	/**
	 * Download all dependencies JAR files from a pom
	 * {@link Model} to an output folder path.
	 *
	 * @param model {@link Model} - The pom model.
	 * @param mavenRepoURL {@link String} - The maven
	 * repository URL to search dependencies.
	 * @param outputFolderPath {@link String} - The output
	 * folder path.
	 * @throws DependenciesException If the output folder
	 * path can not be created.
	 */
	public static void downloadDependencies(Model model, String mavenRepoURL,
		String outputFolderPath) throws DependenciesException {

		if (mavenRepoURL != null) {

			/*
			 * Loop for each dependency in the pom model.
			 */
			for (Object o : model.getDependencies()) {
				Dependency d = (Dependency) o;
				String scope = d.getScope();

				String download = null;
				String fileName = null;

				/*
				 * If the scope need the dependency at runtime download it.
				 */
				if (neededScopeDependency(scope)) {
					download = getDownloadPath(d);
					fileName = download.substring(
						download.lastIndexOf('/') + 1);
				}

				/*
				 * Output folder path.
				 */
				File dependeciesFolder
					= new File(outputFolderPath);
				if (!dependeciesFolder.exists()) {
					if (!dependeciesFolder.mkdirs()) {
						/*
						 * Can not create output folder.
						 */
						throw new OutputDependcyFileException(
							dependeciesFolder.getPath(),
							new IOException(
								"Can not create output folder"));
					}
				}

				/*
				 * Initialize the output file.
				 */
				File outputFile = new File(
					String.format("%s/%s",
						dependeciesFolder.getPath(),
						fileName));

				/*
				 * Try to write data in output file.
				 */
				try {
					if (download != null && fileName != null) {

						try (DataOutputStream dlOut = new DataOutputStream(
							new FileOutputStream(outputFile))) {

							/*
							 * Get complete download URL.
							 */
							URL dlURL = new URL(String.format(
								"%s%s", mavenRepoURL, download));

							try {
								downloadFile(dlURL, dlOut);
							} catch (FileNotFoundException ex) {
								throw new UnknownDependencyException(
									dlURL.toString(), ex);
							}
						} catch (IOException ex) {
							/**
							 * File can not be downloaded.
							 */
							throw new OutputDependcyFileException(
								outputFile.getPath(), ex);
						}

					}
				} catch (DependenciesException ex) {
					LOGGER.error(String.format(
						"[%s] dependency download error",
						d.getArtifactId()), ex);
				}
				/*
				 * Remove output file if the download failed.
				 */
				if (outputFile.exists()
					&& outputFile.length() == 0) {
					outputFile.delete();
				}
			}
		}
	}

	/**
	 * Download a file from an url to the outputstream.
	 *
	 * @param dlURL {@link URL} - The file to download URL.
	 * @param dlOut {@link OutputStream} - The output stream
	 * to write data.
	 * @throws IOException If the file can not be located at
	 * the URL or the stream can not be read (or closed) or
	 * the data can not be write.
	 */
	private static void downloadFile(URL dlURL,
		OutputStream dlOut) throws IOException {
		InputStream dlIs = dlURL.openStream();
		DataInputStream dlIn;

		if (dlIs != null) {
			dlIn = new DataInputStream(dlIs);

			byte buffer[] = new byte[512];
			int len;
			while ((len = dlIn.read(buffer)) != -1) {
				dlOut.write(buffer, 0, len);
			}

			dlIn.close();
		}
	}

	/**
	 * Format dependency path from {@link Dependency}
	 * object.
	 *
	 * @param d {@link Dependency} - The dependency to
	 * format.
	 * @return {@link String} - The dependency format as
	 * <code>{group_id}/{artifact_id}/{version}/{artifact_id}-{version}.jar</code>.
	 */
	private static String getDownloadPath(Dependency d) {
		return d == null ? null : String.format(
			"%1$s/%2$s/%3$s/%2$s-%3$s.jar",
			d.getGroupId().replaceAll("\\.", "/"),
			d.getArtifactId().replaceAll("\\.", "/"),
			d.getVersion(),
			d.getArtifactId());
	}

	/**
	 * If the scope is a scope that required the jar at
	 * runtime returns <code>true</code>.
	 *
	 * <p>
	 * If the scope is <code>null</code> it takes the
	 * default value <code>"compile"</code>.
	 * </p>
	 *
	 * @param scope {@link String} - The scope to check.
	 * @return {@link Boolean boolean} - <code>true</code>
	 * if the scope required JAR (like
	 * <code>"runtime"</code>), <code>false</code> otherwise
	 * (like <code>"test"</code>).
	 */
	private static boolean neededScopeDependency(String scope) {
		scope = scope == null ? "compile" : scope;
		Matcher m = NOT_NEEDED_SCOPES_PATTERN.matcher(scope);
		return !m.matches();
	}
}
