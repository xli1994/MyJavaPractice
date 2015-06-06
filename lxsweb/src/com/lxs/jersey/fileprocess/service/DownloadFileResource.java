package com.lxs.jersey.fileprocess.service;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;

@Path("/download")
public class DownloadFileResource 
{

   @Context  
   UriInfo uriInfo;
   
   public DownloadFileResource() {
   }

   
   @GET
   @Path("{filename}")
   @Produces({MediaType.APPLICATION_OCTET_STREAM})
   public Response downloadFile(@PathParam("filename") String filename) throws Exception {
      String path = uriInfo.getPath();
      System.out.println("downloadFile called path="+path+"; filename="+filename);
      if(filename == null || filename.length() < 4)
      {
    	  throw new WebApplicationException("File name is required", Response.Status.BAD_REQUEST);
      }
      
      //test my exception works
      /*
      if(!"JK_Hosp.jpg".equalsIgnoreCase(filename))
      {
    	  throw new FileNotFoundException(filename);
      }
      */
      //filename ="JK_Hosp.jpg";
      File file =getFile(filename);
      if(file == null || !file.exists())
      {
    	  System.out.println("File ="+file.getName()+" doesn't exist, raising exception.");
    	  //throw new WebApplicationException("File doesn't exist", Response.Status.NOT_FOUND);
    	  //instead return a error message
    	  //return Response.serverError().header("ErrorCode", "404").entity("File not found").build();
    	  
    	  throw new FileNotFoundException(filename);
      }
      System.out.println("Responding file downloading now::"+file.getAbsolutePath());
      return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
    	      .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
    	      .build();
   }

   private File getFile(String filename) throws Exception
   {
	   String finalFileName = filename;
	   String filepath="F://eclipseWorkSpace/testfiles/";
	   //FileOutputStream out = new FileOutputStream(filepath+finalFileName);
	   File file = new File(filepath+filename);
	   System.out.println("get file::="+filepath+finalFileName);
	   return file;
   }
}
