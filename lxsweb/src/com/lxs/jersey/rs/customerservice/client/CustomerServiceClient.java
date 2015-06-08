package com.lxs.jersey.rs.customerservice.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxs.jersey.rs.customerservice.service.Customer;
import com.lxs.jersey.rs.customerservice.service.CustomerList;
import com.lxs.rs.HostNamePath;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * This is to test post/get data from Rest service with various formas: xml, json, plain text, html..
 */
public class CustomerServiceClient
{
	private static Client client;
	//HostNamePath.HOSTPATH is your domain+context path, here is http://localhost:8080/lxsweb
	private static String sTarget = HostNamePath.HOSTPATH + "/customerservice" + "/customers";

	public static void main(String arg[]) throws Exception
	{
		client = ClientBuilder.newClient();

		CustomerServiceClient cus = new CustomerServiceClient();

		//create customer with xml
		cus.createCustomerXML();

		//create customer with Json
		cus.createCustomerJson();

		//get customer with xml
		cus.getCustomerXML();

		//get customer with JSON
		cus.getCustomerJSON();

		//get customer plain text or html
		cus.getCustomerHtmlPlainText();

		//get Json List
		cus.getCustomerListJson();

		//get XML List
		cus.getCustomerListXML();

		//get Json Map<Customer>
		cus.getCustomerMapJson();

		client.close();
	}

	/**
	 * Create a customer with xml 
	 */
	public void createCustomerXML()
	{
		System.out.println("*** Create a new Customer with xml ***");
		Customer newCustomer = this.getNewCustomer("Li");

		Response response = client.target(sTarget).request().post(Entity.xml(newCustomer));
		if (response.getStatus() != 201)
			throw new RuntimeException("Failed to create , status=" + response.getStatus());
		String location = response.getLocation().toString();
		System.out.println("Created customer with xml ===Location: " + location);
		response.close();

	}

	/**
	 * Create a customer with Json
	 */
	public void createCustomerJson()
	{
		System.out.println("*** Create a new Customer with Json ***");
		Customer newCustomer = this.getNewCustomer("Li-Json");

		Response response = client.target(sTarget).request()
				.post(Entity.entity(newCustomer, MediaType.APPLICATION_JSON_TYPE));
		if (response.getStatus() != 201)
			throw new RuntimeException("Failed to create , status=" + response.getStatus());
		String location = response.getLocation().toString();
		System.out.println("Created customer with Json ===Location: " + location);
		response.close();

		//post Json String to create Customer object
		//Note: JSON property name must match @XmlElement(name = "first-name"),
		//@XmlElement(name = "last-name") annotated in Customer.class
		//because jersery uses jaxp to convert it to customer object.
		System.out.println("\n------Create Customer with JSON String ==--------------");
		String cusJson = "{\"id\":2,\"first-name\":\"lxs222\",\"last-name\":\"Max222\","
				+ "\"street\":\"12345 st\",\"city\":\"Fremont\",\"state\":\"CA2\","
				+ "\"zip\":\"95670\",\"country\":\"USA\"}";

		response = client.target(sTarget).request()
				.post(Entity.entity(cusJson, MediaType.APPLICATION_JSON_TYPE));
		if (response.getStatus() != 201)
			throw new RuntimeException("Failed to create");
		location = response.getLocation().toString();
		System.out.println("===Created customer with json string,  Location: " + location);
		response.close();
	}

	/**
	 * Get Customer object with xml
	 * @throws Exception 
	 */
	public void getCustomerXML() throws Exception
	{
		System.out.println("\n*** GET Customer (id=2) with XML String**");
		String xml = client.target(sTarget + "/2").request().accept(MediaType.APPLICATION_XML_TYPE)
				.get(String.class);
		System.out.println("Got customer xml string=" + xml);

		//convert xml to java object:
		System.out.println("\n*** Convert XML to java Customer object with JAXB **");
		JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		StringReader sr = new StringReader(xml);
		Customer jaxbObj = (Customer) jaxbUnmarshaller.unmarshal(sr);
		System.out.println("After convert from xml, got Customer object=" + jaxbObj.toString());

		//Get Customer object directly, without unmarshaller
		System.out.println("\n*** GET Customer (id=3) as Customer Object directly (from xml) **");
		Customer customerobj = client.target(sTarget + "/3").request()
				.accept(MediaType.APPLICATION_XML_TYPE).get(Customer.class);
		System.out.println("Got customer object=" + customerobj);

	}

	/**
	 * Get customer with Json
	 */
	public void getCustomerJSON()
	{
		System.out.println("\n*** GET Customer (id=1) JSON String**");
		String json = client.target(sTarget + "/1").request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
		System.out.println("Got customer json string=" + json);

		//Convert json to/from java object, using google gson2.3.1.jar 
		System.out.println("\n*** Convert JSON to/from object with Google Gson **");

		//convert from json to object
		Gson gson = new Gson();
		Customer jCustomer = gson.fromJson(json, Customer.class);
		System.out.println("\n Converted  to Customer Object from json=" + jCustomer);

		//covert from Customer object to json
		String jsonCus = gson.toJson(jCustomer);
		System.out.println("\n Converted from Customer object  to json=" + jsonCus);

		System.out
				.println("\n*** Convert JSON to/from xml with json.simple: org.json.JSONObject ::");
		//convert json to xml:
		JSONObject jsonObj = new JSONObject(json);
		String xmlFromJson = XML.toString(jsonObj);
		System.out.println("\nConevrted from json to a xml with json.simple=" + xmlFromJson);

		//convert xml to json
		try
		{
			JSONObject xmlJSONObj = XML.toJSONObject(xmlFromJson);
			String jsonPrettyPrintString = xmlJSONObj.toString(4);
			System.out.println("\nConvert xml to json with json.simple=" + jsonPrettyPrintString);
		}
		catch (JSONException je)
		{
			System.out.println(je.toString());
		}

	}

	/**
	 * Get customer(response) with plain text or html 
	 */
	public void getCustomerHtmlPlainText()
	{
		System.out.println("\n*** Get Customer plain text (toString) **");
		String plaintext = client.target(sTarget + "/1").request().accept(MediaType.TEXT_PLAIN)
				.get(String.class);
		System.out.println("Response plaintext=" + plaintext);

		System.out.println("\n*** GET Customer html text **");
		String htmltext = client.target(sTarget + "/2").request().accept(MediaType.TEXT_HTML)
				.get(String.class);
		System.out.println("Response htmltext=" + htmltext);
	}

	/**
	 * This is to get a json string for Customer, then convert to java.util.List<Customer>
	 */
	public void getCustomerListJson()
	{
		System.out.println("\n-------This is to get a Customer List with json --------------");
		Response response = client.target(sTarget + "/list").request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get();
		System.out.println("response status: =" + response.getStatus());
		//if (response.getStatus() != 201) throw new RuntimeException("Failed to create");

		String list = response.readEntity(String.class);
		if (list == null)
		{
			System.out.println("Got null or empty list!!!");

		}
		else
		{
			System.out.println("Returned customer from list json::=" + list);

			//use Gson to convert it to ArrayList
			JSONObject jsonObject = new JSONObject(list);
			JSONArray jArray = (JSONArray) jsonObject.get("MyJsonList");

			ArrayList<Customer> custList = (ArrayList<Customer>) new Gson().fromJson(
					jArray.toString(), new TypeToken<ArrayList<Customer>>()
					{
					}.getType());
			System.out.println("===Convert json list to ArrayList<Customer>: ");
			for (Customer cust : custList)
			{
				System.out.println("===loop Customer object from list==" + cust);
			}
		}
		System.out.println("===completed get List Json === ");
		response.close();
	}

	/**
	 * Get a CustomerList for Customer with xml, then get java.util.List<Customer>
	 * 
	 */
	public void getCustomerListXML()
	{
		System.out.println("\n-------This is to get a Customer List with XML --------------");
		Response response = client.target(sTarget + "/list").request()

		.accept(MediaType.APPLICATION_XML_TYPE).get();
		System.out.println("response status: =" + response.getStatus());
		//if (response.getStatus() != 201) throw new RuntimeException("Failed to create");

		CustomerList list = response.readEntity(CustomerList.class);
		if (list == null)
		{
			System.out.println("Got null or empty list!!!");

		}
		else
		{
			System.out.println("Returned customer from CustomerList xml::=" + list);

			List<Customer> custList = list.getCustomers();
			System.out.println("===Got List <Customer> from CustomerList:");
			for (Customer cust : custList)
			{
				System.out.println("===loop Customer object from list==" + cust);
			}
		}

		System.out.println("===completed get List XML === ");
		response.close();
	}

	/**
	 * This to get Map<Customer> wrapped in a JSON object
	 */
	public void getCustomerMapJson()
	{
		System.out.println("\n------Get  Map<Customer>wrapped in Json --------------");
		Response response = client.target(sTarget + "/map").request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get();
		System.out.println("response status: =" + response.getStatus());
		//if (response.getStatus() != 201) throw new RuntimeException("Failed to create");

		String sMap = response.readEntity(String.class);
		if (sMap == null)
		{
			System.out.println("Get map returned null or empty!!!");
		}
		else
		{
			System.out.println("Returned customer from lits::=" + sMap);

			//use Gson to convert it to Map
			JSONObject jsonMap = new JSONObject(sMap);
			JSONObject map2 = (JSONObject) jsonMap.get("MyCustomerJsonMap");

			HashMap<String, Customer> custMap = (HashMap<String, Customer>) new Gson().fromJson(
					map2.toString(), new TypeToken<HashMap<String, Customer>>()
					{
					}.getType());
			System.out.println("===Convert json Map to HashMap<String Customer>: ");
			Set<Map.Entry<String, Customer>> set = custMap.entrySet();
			for (Map.Entry entry : set)
			{
				System.out.println("===loop converted Customer object from map: key="
						+ entry.getKey() + "; customer=" + entry.getValue());
			}
		}
		System.out.println("===completed get Map=== ");
		response.close();
	}

	private Customer getNewCustomer(String firstName)
	{
		Customer newCustomer = new Customer();
		newCustomer.setFirstName(firstName);
		newCustomer.setLastName("X");
		newCustomer.setStreet("123 aa str");
		newCustomer.setCity("SF");
		newCustomer.setState("CA");
		newCustomer.setZip("94568");
		newCustomer.setCountry("USA");

		return newCustomer;
	}
}
