package com.lxs.jersey.fileprocess.service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/file")
public class FileProcessApplication extends Application
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> clazz = new HashSet<Class<?>>();

	public FileProcessApplication()
	{
		//singletons.add(new UploadFileResource());
		//singletons.add(new DownloadFileResource());
		clazz.add(UploadFileResource.class);
		clazz.add(DownloadFileResource.class);

		//register exception mapper
		singletons.add(FileNotFoundExceptionMapper.class);

	}

	@Override
	public Set<Class<?>> getClasses()
	{

		return clazz;
	}

	@Override
	public Set<Object> getSingletons()
	{
		return singletons;
	}
}
