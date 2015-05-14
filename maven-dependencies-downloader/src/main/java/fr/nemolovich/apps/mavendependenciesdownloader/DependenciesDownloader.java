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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Utilities to download all dependencies of a pom.xml {@link Model}.
 *
 * @author Nemolovich
 */
public final class DependenciesDownloader {

    /**
     * Logger.
     */
    private static final Logger LOGGER;
    private static final String NOT_NEEDED_SCOPES
        = "(test|provided|system)";
    private static final Pattern NOT_NEEDED_SCOPES_PATTERN
        = Pattern.compile(NOT_NEEDED_SCOPES,
            Pattern.CASE_INSENSITIVE);

    /**
     * Custom trust manager if needed.
     */
    private static ConcurrentLinkedQueue<TrustManager> trustManager;

    public static final String MAVEN_REPO
        = "https://repo1.maven.org/maven2/";
    private static final int BUFF_SIZE = 512;

    static {
        trustManager = new ConcurrentLinkedQueue<>();

        URL url = DependenciesDownloader.class.
            getResource("/config/log4j.properties");
        if (url != null) {
            PropertyConfigurator.configure(url);
        }
        LOGGER = Logger
            .getLogger(DependenciesDownloader.class);
    }

    /**
     * Hide constructor for Utility class.
     */
    private DependenciesDownloader() {
    }

    /**
     * Download all dependencies JAR files from a pom {@link Model} to an output
     * folder path.
     *
     * @param model {@link Model} - The pom model.
     * @param outputFolderPath {@link String} - The output folder path.
     * @param mavenRepoURL {@link String}[] - The maven repositories URL to
     * search dependencies.
     * @throws DependenciesException If the output folder path can not be
     * created.
     */
    public static void downloadDependencies(Model model,
        String outputFolderPath, String... mavenRepoURL)
        throws DependenciesException {

        if (mavenRepoURL != null) {
            String[] mavenRepos = mavenRepoURL.length == 0
                ? new String[1] : mavenRepoURL;
            if (mavenRepoURL.length == 0) {
                mavenRepos[0] = MAVEN_REPO;
            }

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
                    fileName = download
                        .substring(download.lastIndexOf('/') + 1);
                }

                /*
                 * Output folder path.
                 */
                File dependenciesFolder = getDependenciesFolderFile(
                    outputFolderPath);

                /*
                 * Download data and write in file.
                 */
                if (download != null && fileName != null) {
                    writeData(d, mavenRepos, download, dependenciesFolder,
                        fileName);
                }
            }
        }
    }

    /**
     * Returns the File used as folder to save to dependencies files downloaed.
     *
     * @param folderPath {@link String} - The path folder to use.
     * @return {@link File} - The output folder as File.
     * @throws OutputDependcyFileException If the folder does not exist and can
     * not be created.
     */
    private static File getDependenciesFolderFile(String folderPath)
        throws OutputDependcyFileException {
        File result
            = new File(folderPath);
        if (!result.exists()
            && !result.mkdirs()) {
            /*
             * Can not create output folder.
             */
            throw new OutputDependcyFileException(
                result.getPath(), new IOException(
                    "Can not create output folder"));

        }
        return result;
    }

    /**
     * Download and write data for a download. A Maven dependency will be
     * download from given maven repositories with the download name to the
     * dependecies folder with a new file name.
     *
     * @param dependency {@link Dependency} - The Maven dependency.
     * @param mavenRepos {@link String}[] - The list or Maven repository to try
     * to download dependency.
     * @param downloadName {@link String} - The dependecy name to download.
     * @param dependenciesFolder {@link File} - The target dependencies folder.
     * @param fileName {@link String} - The target file name.
     */
    private static void writeData(Dependency dependency, String[] mavenRepos,
        String downloadName, File dependenciesFolder, String fileName) {


        /*
         * Initialize the output file.
         */
        File outputFile = new File(String.format("%s/%s",
            dependenciesFolder.getPath(), fileName));

        /*
         * Try to write data in output file.
         */
        try {

            try (DataOutputStream dlOut = new DataOutputStream(
                new FileOutputStream(outputFile))) {

                DependenciesException exception = null;

                /*
                 * Loop for each Maven repository URLs.
                 */
                for (String downloadURL : mavenRepos) {
                    exception = null;

                    if (!downloadURL.endsWith("/")) {
                        downloadURL = downloadURL.concat("/");
                    }
                    /*
                     * Get complete download URL.
                     */
                    URL dlURL = new URL(String.format("%s%s",
                        downloadURL, downloadName));

                    try {
                        downloadFile(dlURL, dlOut);
                        /*
                         * Break if download succeed, else try with another 
                         * URL.
                         */
                        break;
                    } catch (FileNotFoundException ex) {
                        exception = new UnknownDependencyException(
                            dlURL.toString(), ex);
                    } catch (KeyManagementException |
                        NoSuchAlgorithmException ex) {
                        exception = new DownloadSecurityException(
                            dlURL.toString(), ex);
                    }
                }
                if (exception != null) {
                    throw exception;
                }
            } catch (IOException ex) {
                /**
                 * File can not be downloaded.
                 */
                throw new OutputDependcyFileException(
                    outputFile.getPath(), ex);
            }

        } catch (DependenciesException ex) {
            LOGGER.error(
                String.format("[%s] dependency download error",
                    dependency.getArtifactId()), ex);
        }

        /*
         * Remove output file if the download failed.
         */
        removeEmptyFile(outputFile);
    }

    /**
     * Remove a file if its content is null after download.
     *
     * @param file {@link File} - The file to check content.
     */
    private static void removeEmptyFile(File file) {
        if (file.exists() && file.length() == 0) {
            if (file.delete()) {
                LOGGER.info(String.format(
                    "The download failed and the empty file \"%s\" has been removed",
                    file.getName()));
            } else {
                LOGGER.warn(String.format(
                    "The download failed and the empty file \"%s\" could not be removed",
                    file.getName()));
            }
        }
    }

    /**
     * Download a file from an url to the outputstream.
     *
     * @param dlURL {@link URL} - The file to download URL.
     * @param dlOut {@link OutputStream} - The output stream to write data.
     * @throws IOException If the file can not be located at the URL or the
     * stream can not be read (or closed) or the data can not be write.
     */
    private static void downloadFile(URL dlURL, OutputStream dlOut)
        throws IOException, KeyManagementException, NoSuchAlgorithmException {

        if (!trustManager.isEmpty()) {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustManager.toArray(new TrustManager[0]),
                new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                sc.getSocketFactory());
        }

        InputStream dlIs = dlURL.openStream();
        DataInputStream dlIn;

        if (dlIs != null) {
            dlIn = new DataInputStream(dlIs);

            byte buffer[] = new byte[BUFF_SIZE];
            int len;
            while ((len = dlIn.read(buffer)) != -1) {
                dlOut.write(buffer, 0, len);
            }

            dlIn.close();
        }
    }

    /**
     * Define a custom trust manager if needed.
     *
     * @param managers {@link TrustManager}[] - The list of managers to use.
     */
    public static void setTrustManager(TrustManager... managers) {
        trustManager = new ConcurrentLinkedQueue<>(
            Arrays.asList(managers));
    }

    /**
     * Format dependency path from {@link Dependency} object.
     *
     * @param d {@link Dependency} - The dependency to format.
     * @return {@link String} - The dependency format as
     * <code>{group_id}/{artifact_id}/{version}/{artifact_id}-{version}.jar</code>
     * .
     */
    private static String getDownloadPath(Dependency d) {
        return d == null ? null : String.format("%1$s/%2$s/%3$s/%2$s-%3$s.jar",
            d.getGroupId().replaceAll("\\.", "/"), d.getArtifactId()
            .replaceAll("\\.", "/"), d.getVersion());
    }

    /**
     * If the scope is a scope that required the jar at runtime returns
     * <code>true</code>.
     *
     * <p>
     * If the scope is <code>null</code> it takes the default value
     * <code>"compile"</code>.
     * </p>
     *
     * @param scope {@link String} - The scope to check.
     * @return {@link Boolean boolean} - <code>true</code> if the scope required
     * JAR (like <code>"runtime"</code>), <code>false</code> otherwise (like
     * <code>"test"</code>).
     */
    private static boolean neededScopeDependency(String scope) {
        String lookUpScope = scope == null ? "compile" : scope;
        Matcher m = NOT_NEEDED_SCOPES_PATTERN.matcher(lookUpScope);
        return !m.matches();
    }
}
