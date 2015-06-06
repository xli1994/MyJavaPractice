package com.lxs.jersey.fileprocess.service;

public class FileNotFoundException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String fileName;
	
	public FileNotFoundException(String fileName)
	{
		super(fileName + " not found!");
		this.fileName = fileName;
		
	}
	
	
}
