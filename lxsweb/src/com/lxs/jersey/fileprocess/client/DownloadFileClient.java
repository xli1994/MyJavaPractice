package com.lxs.jersey.fileprocess.client;

 
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.lxs.rs.HostNamePath;

/**
 * Test download file from REST.
 *  This works for all file type
 * 
 * note: With URL client, 1. must set "Content-Type", 2. file name can't contain space
 * 
 * @author lxs
 *
 */
public class DownloadFileClient
{ 
	private static final Logger logger = LogManager.getLogger(DownloadFileClient.class);
	private static String FILENAME = "JK_Hosp.jpg"; //used as parameter to url
	private static String FILEPATH = "F://eclipseWorkSpace/testfiles"; //write file to this path
	private static String targetURL;
	
	public static void main(String[] args) throws Exception
	{
		logger.debug("start download");
		//here HostNamePath.HOSTPATH is your domain and service url, eg:"http://localhost:8080/myjob"
		targetURL = HostNamePath.HOSTPATH+"/file/download/"+ FILENAME;
		//Test URL client
		testURLClient();
		
		//test JAX-RS client
		//testJaxRsClient();
	}
	
	/**
	 * This use URL to open connection as client to download file
	 * @throws Exception
	 */
	public static void testURLClient( ) throws Exception
	{
		System.out.println("\n Test using URL to get file stream ===");
		System.out.println("Temp File name for param one =" + FILENAME);
		
		URL getUrl = new URL(targetURL);
		HttpURLConnection httpConn = (HttpURLConnection) getUrl
				.openConnection();
		httpConn.setInstanceFollowRedirects(false);
		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			System.out.println("responseCode is OK");
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			int contentLength = httpConn.getContentLength();
			System.out.println("disposition=" + disposition + "; contentType="
					+ contentType + ";contentLength="+contentLength);

			if (disposition != null)
			{
				// extracts file name from header field
				fileName = getFileName(disposition);
			}
			else
			{
				// extracts file name from URL
				// fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
				// fileURL.length());
				// use hardcode: this should not happen.
				fileName = FILENAME;
				System.out.println("use hardcode filename="+fileName);
			}
			/*
			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = " + contentLength);
			System.out.println("fileName = " + fileName);
			*/
			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			//rename file with prefix "download_"
			String saveFilePath = FILEPATH + File.separator + "download_"+fileName;
			writeFile(saveFilePath, inputStream);


			System.out.println("File downloading completed");
		}
		else
		{
			System.out.println("No file to download. Server replied HTTP code: "
							+ responseCode);
		}
		httpConn.disconnect();
		
		

	}

	/**
	 * This is to test download file with jax-rs client
	 * @throws Exception
	 */
	public static void testJaxRsClient( ) throws Exception
	{
		// =============Test using Client:  =========
		
		System.out.println("\n Test using JaxRsClient to download file ===");
		System.out.println("JAX-RS Client ::File name =" + FILENAME);

		Client client = ClientBuilder.newClient();
		Response response = client
				.target(targetURL)
				.request().accept(MediaType.APPLICATION_OCTET_STREAM_TYPE).get();

		String disposition=response.getHeaderString("Content-Disposition");
		String contentType = response.getHeaderString("Content-Type");
		
		//print all headers
		MultivaluedMap <String, Object> mHeaders = response.getHeaders();
		Set <Map.Entry<String, List<Object>>> set = mHeaders.entrySet();
		for (Map.Entry<String, List<Object>> entry : set)
		{
			String key = entry.getKey();
			System.out.print("\nheader key="+key+"; ");
			List <Object>list = entry.getValue();
			if(list != null && list.size() > 0)
			{
				for (Object obj : list)
				{
					System.out.print("header value="+obj+"; ");
				}
			}
		}
		
		System.out.println("\n\n Response status::"+response.getStatusInfo().getReasonPhrase());
		
		//process error
		if(response.getStatus() != Response.Status.OK.getStatusCode())
		{
			String error = response.readEntity(String.class);
			
			System.out.println("File download error, stop processing now. Exit! error="+error);
			return;
		}
		
		//read file stream
		InputStream inputStream = response.readEntity(InputStream.class);
	
		String fileName = null;
		System.out.println("\n\n contentDisposition="+disposition+";; contentType="+contentType);
		if (disposition != null)
		{
			// extracts file name from header field
			fileName = getFileName(disposition);
		}
		else
		{
			// extracts file name from URL
			// fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
			// fileURL.length());
			// use hardcode:
			fileName = FILENAME;
			System.out.println("use hardcode filename="+fileName);
		}
		
		//rename file with prefix "download_"
		String saveFilePath = FILEPATH + File.separator + "download_jaxrs_"+fileName;
		writeFile(saveFilePath, inputStream);

		System.out.println("File downloading completed with JaxRs client! filepath/name="+saveFilePath);
	}
	
	/**
	 * Write file to disk
	 * 
	 * @param saveFilePath
	 * @param inputStream
	 * @throws IOException
	 */
	public static void writeFile(String saveFilePath, InputStream inputStream)
			throws IOException
	{
		// opens an output stream to save into file
		FileOutputStream outputStream = new FileOutputStream(saveFilePath);
		int bytesRead = -1;
		byte[] buffer = new byte[1024];
		while ((bytesRead = inputStream.read(buffer)) != -1)
		{
			outputStream.write(buffer, 0, bytesRead);
		}

		outputStream.close();
		inputStream.close();
	}

	
	public static String getFileName(String disposition)
	{
		String fileName =null;
		// extracts file name from header field
		int index = disposition.indexOf("filename=");
		if (index > 0)
		{
			fileName = disposition.substring(index + 10,
					disposition.length() - 1);
			System.out.println("\nget filename="+fileName);
		}
		
		return fileName;
	}
}
