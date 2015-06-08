package com.lxs.jersey.rs.customerservice.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "customerList")
public class CustomerList implements Serializable 
{
	private static final long serialVersionUID = 1L;
	List<Customer> customers;

	public CustomerList()
	{
		customers = new ArrayList<>();
	}

	public void addCustomer(Customer customer)
	{
		customers.add(customer);
	}

	public boolean removeCustomer(Customer customer)
	{
		return customers.remove(customer);
	}

	@XmlElement(name = "customers")
	public List<Customer> getCustomers()
	{
		return customers;
	}

	public void setCustomers(List<Customer> customers)
	{
		this.customers = customers;
	}

	@Override
	public String toString()
	{
		return "CustomerList [customers=" + customers + "]";
	}



}
