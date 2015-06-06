package com.lxs.jersey.fileprocess.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.lxs.rs.HostNamePath;

/**
 * Test upload file to REST This works for all file type
 * 
 * note: 
 * With URL client,
 * 1.  must set "Content-Type",
 * 2. file name can't contain space
 * 
 * @author lxs
 *
 */
public class UploadFileClient
{
	private static final Logger logger = LogManager.getLogger(UploadFileClient.class);
	
	private static String FILENAME;
	private static String FILEPATH="F://eclipseWorkSpace/testfiles/";
	private static String targetURL;

	public static void main(String[] args) throws Exception
	{
		FILENAME = "test1.doc";
		targetURL = HostNamePath.HOSTPATH+"/file/upload/"+ FILENAME;
		//test URL client:
		//testURLClient();
		
		//test JAX-RS client
		testJaxRsClient();
	}
	
	/**
	 * This uses URL as client to upload file
	 * @throws Exception
	 */
	public static void testURLClient() throws Exception
	{
		// input a file: works!
		
		File file = new File(FILEPATH+FILENAME); // assume use selected different file
		
		System.out.println("File name =" + FILENAME);

		FileInputStream stream = new FileInputStream(file);
		System.out.println("\n Using URL to post stream ===");
		URL postUrl = new URL(targetURL);
		HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		//must set content type, whatever is ok, even application/json
		connection.setRequestProperty("Content-Type", "application/octet-stream");
		OutputStream os = connection.getOutputStream();
		int readedBytes;
		byte[] buf = new byte[1024];
		while ((readedBytes = stream.read(buf)) > 0)
		{
			os.write(buf, 0, readedBytes);
		}

		os.flush();
		stream.close();
		
		Assert.assertEquals(HttpURLConnection.HTTP_CREATED,
				connection.getResponseCode());
		System.out
				.println("Upload completed, Location: " + connection.getHeaderField("Location"));
		connection.disconnect();
		
	}

	/**
	 * This uses JAX-RS client to upload file
	 * @throws Exception
	 */
	public static void testJaxRsClient() throws Exception
	{
		// =============Test using Client:   =========	
		System.out.println("\n Using Client to post stream ===");
		
		File file = new File(FILEPATH + FILENAME); // assume use selected  file
		
		System.out.println("Client ::File name =" + FILENAME);

		FileInputStream stream = new FileInputStream(file);
		Client client = ClientBuilder.newClient();
		Response response = client
				.target(targetURL)
				.request()
				.post(Entity.entity(stream, MediaType.APPLICATION_OCTET_STREAM_TYPE));
		//if (response.getStatus() != 201)
		//	throw new RuntimeException("Failed to create");
		stream.close();
		String location = (String) response.getMetadata().get("Location")
				.get(0);
		System.out.println("Completed client upload file ::Location: " + location);
	}
	
	/**
	 * This method is not used in this function
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static OutputStream getOutputStream(InputStream stream)
			throws IOException
	{
		OutputStream b = new ByteArrayOutputStream();
		int readedBytes;
		byte[] buf = new byte[1024];
		while ((readedBytes = stream.read(buf)) > 0)
		{
			b.write(buf, 0, readedBytes);
		}
		b.close();
		return b;
		// return b.toByteArray();
	}

	/**
	 * This method is not used in this function
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static byte[] getOutputByte(InputStream stream) throws IOException
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		int readedBytes;
		byte[] buf = new byte[1024];
		while ((readedBytes = stream.read(buf)) > 0)
		{
			b.write(buf, 0, readedBytes);
		}
		b.close();

		return b.toByteArray();
	}
}
