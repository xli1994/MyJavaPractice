package com.lxs.jersey.rs.customerservice.service;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import com.google.gson.Gson;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is resource class that provide REST service
 * 
 * @author lxs
 *
 */
@Path("/customers")
public class CustomerResource
{
	//this is used as a "db"
	private static Map<Integer, Customer> customerDB = new ConcurrentHashMap<Integer, Customer>();
	private static AtomicInteger idCounter = new AtomicInteger();

	//@Context
	//ServletContext context;
	
	@Context
	HttpHeaders headers2;
	@Context
	UriInfo uriInfo;

	//create faked data object, it should be from db.
	static
	{
		Customer customer;
		customer = new Customer();
		customer.setId(idCounter.incrementAndGet());
		customer.setFirstName("Bill");
		customer.setLastName("Burke");
		customer.setStreet("263 Clarendon Street");
		customer.setCity("Boston");
		customer.setState("MA");
		customer.setZip("02115");
		customer.setCountry("USA");
		customerDB.put(customer.getId(), customer);

		customer = new Customer();
		customer.setId(idCounter.incrementAndGet());
		customer.setFirstName("Joe");
		customer.setLastName("Burke");
		customer.setStreet("123 ABC Street");
		customer.setCity("Fremont");
		customer.setState("CA");
		customer.setZip("95687");
		customer.setCountry("USA");
		customerDB.put(customer.getId(), customer);

		customer = new Customer();
		customer.setId(idCounter.incrementAndGet());
		customer.setFirstName("Joe22");
		customer.setLastName("Lxs123");
		customer.setStreet("AAA");
		customer.setCity("SF Bay");
		customer.setState("CA");
		customer.setZip("98785");
		customer.setCountry("USA");
		customerDB.put(customer.getId(), customer);
	}

	public CustomerResource()
	{
		//this is not singleton, will be called for every request
		System.out.println("CustomerResource constructor called");
	}

	/**
	 * Create a customer with xml
	 * @param customer
	 * @return
	 */
	@POST
	@Consumes("application/xml")
	public Response createCustomer(Customer customer)
	{
		System.out.println("\n createCustomer (application/xml) got called: current map size="
				+ customerDB.size());
		customer.setId(idCounter.incrementAndGet());
		customerDB.put(customer.getId(), customer);
		System.out.println("createCustomer Created customer: " + customer.toString());

		//output all paths 
		String requestUri = uriInfo.getRequestUri().getPath();
		String baseUri = uriInfo.getBaseUri().getPath();
		String absUri = uriInfo.getAbsolutePath().getPath();
		String path = uriInfo.getPath();
		//String relativepath = uriInfo.relativize(uriInfo.getRequestUri()).getPath();
		String resolve = uriInfo.resolve(uriInfo.getBaseUri()).getPath();
		System.out.println("path=" + path + "; requestUri=" + requestUri + "; baseUri=" + baseUri
				+ ";absUri=" + absUri);
		//System.out.println("relativepath=" + relativepath + "; resolve=" + resolve);

		return Response.created(URI.create(path + "/" + customer.getId())).build();
	}

	/**
	 * Create customer with JSON
	 * jersey convert it json to object
	 * @param customer
	 * @return
	 */
	@POST
	@Consumes("application/json")
	public Response createCustomerJson(Customer customer)
	{
		System.out.println("\n createCustomerJson(application/json) got called;  current map size="
				+ customerDB.size());
		customer.setId(idCounter.incrementAndGet());
		customerDB.put(customer.getId(), customer);
		System.out.println("createCustomerJson Created customer: " + customer);
		String path = uriInfo.getPath();
		return Response.created(URI.create(path + "/" + customer.getId())).build();
	}

	@DELETE
	@Path("{id}")
	public Response deleteCustomer(@PathParam("id") int id)
	{
		System.out.println("deleteCustomer called; id="+id);
		customerDB.remove(id);
		 
		return Response.ok("deleted").build();
	}
	
	@GET
	@Path("{id}")
	//There is a problem to return Customer with JSON if xmlElement name is not the same as 
	//property name, such as @XmlElement(name = "last-name"), while property name is "lastName"
	//separate it to two methods
	@Produces({ "application/xml" /*, "application/json" */})
	public Customer getCustomerXML(@PathParam("id") int id, @Context HttpHeaders headers)
	{

		String header = headers.getRequestHeader("accept").get(0);
		System.out.println("\n getCustomerXML(): id=" + id + "; header accept=" + header
				+ ": current map size=" + customerDB.size());

		// get header from injected headers directly ( not from parameter, same result)
		//String header2 = headers2.getRequestHeader("accept").get(0);
		//System.out.println("header2 accept=" + header2);

		Customer customer = getCustomerFromDB(id);
		System.out.println("found customer, return id=" + id);

		return customer;
	}

	/**
	 * Get Customer with JSON String
	 * There is a problem to return Customer with JSON if xmlElement name is not the same as 
		property name, such as @XmlElement(name = "last-name"), while property name is "lastName"
		separate it to two methods
	 * @param id
	 * @param headers
	 * @return
	 */
	@GET
	@Path("{id}")
	@Produces({ "application/json" })
	public String getCustomerJSON(@PathParam("id") int id, @Context HttpHeaders headers)
	{

		String header = headers.getRequestHeader("accept").get(0);
		System.out.println("\n getCustomerJSON(): id=" + id + "; header accept=" + header
				+ ": current map size=" + customerDB.size());

		Customer customer = getCustomerFromDB(id);
		Gson gson = new Gson();
		String json = gson.toJson(customer);
		System.out.println("found customer, return id=" + id);

		return json;
	}

	/**
	 * Get customer with text/plain
	 * 
	 * @param id
	 * @param headers
	 * @return
	 */
	@GET
	@Path("{id}")
	@Produces("text/plain")
	public String getCustomerString(@PathParam("id") int id, @Context HttpHeaders headers)
	{
		//you may pass headers as param, or inject HttpHeaders with @Context, like this:
		String header2 = headers2.getRequestHeader("accept").get(0);
		System.out.println("\n getCustomerString (text/plain) called; id=" + id
				+ ";  header accept=" + header2);

		Customer customer = getCustomerFromDB(id);
		System.out.println("found customer, return id=" + id);
		return customer.toString();
	}

	/**
	 * Get customer with html format
	 * 
	 * @param id
	 * @param headers
	 * @return
	 */
	@GET
	@Path("{id}")
	@Produces("text/html")
	public String getCustomerHtml(@PathParam("id") int id, @Context HttpHeaders headers)
	{
		String header2 = headers2.getRequestHeader("accept").get(0);
		System.out.println("\n getCustomerHtml (text/html) called; id=" + id + "; header accept="
				+ header2);

		Customer customer = getCustomerFromDB(id);
		System.out.println("found customer, return id=" + id);

		return "<h1>Customer As HTML</h1><pre>" + customer.toString() + "</pre>";
	}

	/**
	 * Get Customer List wrapped in JSON
	 * 
	 * @param headers
	 * @return
	 */
	@GET
	@Path("/list")
	@Produces({ "application/json" })
	public Response getCustomerListJSON(@Context HttpHeaders headers, @Context Request request)
	{
		String header = headers.getRequestHeader("accept").get(0);
		System.out.println("\n getCustomerListJSON called; current map size=" + customerDB.size()
				+ ";  header accept=" + header);

		List<Customer> cList = getCustomerList();

		//use json object
		JSONObject obj = new JSONObject();
		obj.put("MyJsonList", cList);
		System.out.println("Returning Json list--------");
		return Response.ok(obj.toString(), "application/json").build();

	}

	/**
	 * Get List<Customer> with XML
	 * To return xml, you can't return List directly, you must use JAXP annotated class 
	 * CustomerList that wraps List<Customer>, like this:
		@XmlRootElement(name = "customerList")
		public class CustomerList implements Serializable 
		{
			@XmlElement(name = "customers")
			public List<Customer> getCustomers()
			{
				return customers;
			}
			//setters and getters goes here
		}

	* @param headers
	* @return
	*/
	@GET
	@Path("/list")
	@Produces({ "application/xml" })
	public CustomerList getCustomerListXML(@Context HttpHeaders headers, @Context Request request)
	{
		String header = headers.getRequestHeader("accept").get(0);
		System.out.println("\n getCustomerListXML called; current map size=" + customerDB.size()
				+ ";  header accept=" + header);

		List<Customer> cList = getCustomerList();

		System.out.println("Returning xml list----------");

		CustomerList cl = new CustomerList();
		cl.setCustomers(cList);
		return cl;

		/* This doesn't work, got error  msg:
		 org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor writeResponseErrorMessage
			WARNING: No message body writer has been found for response class ArrayList.
		GenericEntity<List<Customer>> entity = new GenericEntity<List<Customer>>(cList) {};
		Response response = Response.ok(entity).build();
		return response;
		*/

	}

	/**
	 * Test: return a json map,
	 * @param headers
	 * @return
	 */
	@GET
	@Path("/map")
	@Produces({ "application/json" })
	public Response getCustomerMap(@Context HttpHeaders headers, @Context Request request)
	{
		String header = headers.getRequestHeader("accept").get(0);
		System.out.println("\n getCustomerMap(application/json) called; current map size=" + customerDB.size()+
				"; header accept=" + header);

		Map<Integer, Customer> map = new HashMap<Integer, Customer>();
		Customer customer1 = customerDB.get(1);
		Customer customer2 = customerDB.get(2);
		if (customer1 == null)
		{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		map.put(customer1.getId(), customer1);
		map.put(customer2.getId(), customer2);
		System.out.println("Returning Map wrapped in josn=====");

		//return Json String that wraps map:
		JSONObject obj = new JSONObject();
		obj.put("MyCustomerJsonMap", map);
		return Response.ok(obj.toString(), "application/json").build();

	}

	private Customer getCustomerFromDB(int id)
	{
		if (id < 1)
		{
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		//List existing customer data
		/*
		Set<Map.Entry<Integer, Customer>> entry = customerDB.entrySet();
		for (Map.Entry<Integer, Customer> ent : entry)
		{
			System.out.println("key=" + ent.getKey() + "; value id="
					+ ent.getValue());
		}
		*/
		Customer customer = customerDB.get(id);
		if (customer == null)
		{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		return customerDB.get(id);
	}
	
	
	/**
	 * This is to get a faked customer list (it should be from db or  other resources)
	 * @return
	 */
	private List<Customer> getCustomerList()
	{
		List<Customer> cList = new ArrayList<Customer>();
		//get 3 customer for testing
		Customer customer1 = customerDB.get(customerDB.size());
		Customer customer2 = customerDB.get(customerDB.size() - 1);
		Customer customer3 = customerDB.get(customerDB.size() - 2);
		if (customer1 == null)
		{
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		cList.add(customer1);
		cList.add(customer2);
		cList.add(customer3);
		
		return cList;
	}
}
