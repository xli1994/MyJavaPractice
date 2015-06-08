# RESTService
This repository contains some java code examples for REST Service (JAXRS 2.0 with Jersery implementation)

6/6/2015: Two sets of codes are uploaded to "lxsweb/src/com/lxs/jersey/fileprocess/", including download/upload files from/to REST service with two client types:
1. Using URL (open HttpURLConnection);
2. Using Jaxrs Client

6/7/2015: A complete code example is uploaded to lxsweb/src/com/lxs/jersey/rs/customerservice. This example illustrates following operations with REST service:
1. Create (POST) java object with either XML or JSON media type;
2. Get java object with either XML or JSON media type;
3. Get a java.util.List or Map that is wrapped into a JSON, then converted back to List<T> or Map<?, ?>.
4. A jsp with ajax call to get json object from REST service (lxsweb/WebContent/ajaxCustomer.jsp)


