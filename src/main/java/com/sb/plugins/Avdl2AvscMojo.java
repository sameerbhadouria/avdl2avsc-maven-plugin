package com.sb.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.avro.Schema;
import org.apache.avro.compiler.idl.Idl;
import org.apache.avro.compiler.idl.ParseException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Generates Avro Schema from Avro IDL files.
 * 
 * @author sameerbhadouria
 *
 */
@Mojo(name="genschema")
public class Avdl2AvscMojo extends org.apache.maven.plugin.AbstractMojo {

	@Parameter(defaultValue="${basedir}/src/main/resources/avro/idl/")
	private File inputAvdlDirectory;
	
	@Parameter(defaultValue="${basedir}/src/main/resources/avro/schema/")
	private File outputSchemaDirectory;
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.maven.plugin.AbstractMojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		if (!inputAvdlDirectory.exists()) {
			throw new MojoExecutionException("Cannot find inputAvdlDirectory: " 
					+ inputAvdlDirectory.toString());
		}
		
		if (!inputAvdlDirectory.isDirectory()) {
			throw new MojoExecutionException("inputAvdlDirectory: " 
					+ inputAvdlDirectory.toString() + " is not a directory");
		}
		
		if (!outputSchemaDirectory.exists()) {
			outputSchemaDirectory.mkdirs();
		}
		else if (!outputSchemaDirectory.isDirectory()){
			throw new MojoExecutionException("outputSchemaDirectory: " 
					+ outputSchemaDirectory.toString() + " is not a directory");
		}
		
		getLog().info("Finding .avdl files in directory: " + inputAvdlDirectory.toString());
		File[] avdlFiles = inputAvdlDirectory.listFiles(new FilenameFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
			 */
			public boolean accept(File dir, String name) {
				return name.endsWith(".avdl");
			}
		});
		
		if (avdlFiles != null) {
			for (File avdlFile : avdlFiles) {
				generateSchema(avdlFile);
			}
		}
		
	}
	
	/**
	 * Generates the Avro schema avsc file from the specified avdl file.
	 * 
	 * @param avdlFile
	 */
	private void generateSchema(File avdlFile) {
		Idl idlParser = null;
		getLog().info("Found avdl file: " + avdlFile.getPath());
		try {
			idlParser = new Idl(avdlFile);
			
			for (Schema schema : idlParser.CompilationUnit().getTypes()) {
				String dirpath = outputSchemaDirectory.getAbsolutePath();
			    String filename = dirpath + "/" + schema.getName() + ".avsc";
			    getLog().info("Creating schema file: " + filename);
			    FileOutputStream fileOutputStream = new FileOutputStream(filename);
			    PrintStream printStream = new PrintStream(fileOutputStream);
			    printStream.println(schema.toString(true));
			    printStream.close();
			}
		} 
		catch (IOException e) {
			getLog().error("Failed to generate Avro schema for file: " + avdlFile.getName(), e);
		} 
		catch (ParseException e) {
			getLog().error("Failed to generate Avro schema for file: " + avdlFile.getName(), e);
		}
		finally {
			if (idlParser != null) {
				try {
					idlParser.close();
				} 
				catch (IOException e) {
					getLog().error("Erro closing IDL Parser.", e);
				}
			}
		}
	}

}
