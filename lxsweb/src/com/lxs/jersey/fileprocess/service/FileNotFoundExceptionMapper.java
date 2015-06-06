package com.lxs.jersey.fileprocess.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class FileNotFoundExceptionMapper implements ExceptionMapper<FileNotFoundException>
{
	@Override
	public Response toResponse(FileNotFoundException exception)
	{	
		return Response.status(Response.Status.PRECONDITION_FAILED).
				entity(exception.getMessage()).build();
	}

}
