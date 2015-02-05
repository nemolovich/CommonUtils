package fr.nemolovich.apps.mavendependenciesdownloader;

import java.io.File;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestDownloader {
	
	@Test
	public void test1() throws DependenciesException {
		Model m=new Model();
		Dependency d=new Dependency();
		d.setArtifactId("json");
		d.setGroupId("org.json");
		d.setVersion("20140107");
		
		m.addDependency(d);
		
		DependenciesDownloader.downloadDependencies(m, "tests", "http://google.com", DependenciesDownloader.MAVEN_REPO);
		assertTrue(new File("tests/json-20140107.jar").exists());	
	}
}