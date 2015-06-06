package com.lxs.jersey.fileprocess.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;

@Path("/upload")
public class UploadFileResource
{

	@Context
	UriInfo uriInfo;

	public UploadFileResource()
	{
	}

	@POST
	@Path("{filename}")
	@Consumes({ MediaType.APPLICATION_OCTET_STREAM })
	public Response uploadFile(@PathParam("filename") String filename, InputStream is)
			throws Exception
	{
		String path = uriInfo.getPath();
		System.out.println("uploadFile called path=" + path + "; filename=" + filename);
		if (filename == null || filename.length() < 4)
		{
			throw new WebApplicationException("File name is required", Response.Status.BAD_REQUEST);
		}

		processFile(filename, is);

		return Response.created(URI.create(path)).build();
	}

	private void processFile(String filename, InputStream is) throws Exception
	{
		String finalFileName = "out_" + filename;
		String filepath = "F://eclipseWorkSpace/testfiles/";
		FileOutputStream out = new FileOutputStream(filepath + finalFileName);
		System.out.println("uploadFile: starting write file to :" + filepath + finalFileName);

		int readedBytes;
		byte[] buf = new byte[1024];
		while ((readedBytes = is.read(buf)) > 0)
		{
			out.write(buf, 0, readedBytes);
		}
		is.close();
		out.close();

		System.out.println("complete writing file");
	}
}
