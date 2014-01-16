package com.sb.plugins;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import com.sb.plugins.Avdl2AvscMojo;

/**
 * Test class for the AVDL to AVSC conversion
 * 
 * @author sameerbhadouria
 *
 */
public class Avdl2AvscMojoTest extends AbstractMojoTestCase {

	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.testing.AbstractMojoTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * @see org.codehaus.plexus.PlexusTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testBasicSchemaGeneration() throws Exception {
		File pomFile = getTestFile("src/test/resources/unit/avdl2avsc/pom.xml");
		
		assertNotNull(pomFile);
		assertTrue(pomFile.exists());
		
		Avdl2AvscMojo mojo = (Avdl2AvscMojo) lookupMojo("genschema", pomFile);
		
		assertNotNull(mojo);
		
		mojo.execute();
	}
}
