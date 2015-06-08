<!DOCTYPE html>
<html>
<head>
<script src="/lxsweb/resources/js/jquery-1.11.2.min.js"></script>

</head>
<body>

<h2>Use jQuery to call REST service </h2>
<p><strong><font color="red">
 This is to get a json object from REST Service
 Test url is http://localhost:8080/lxsweb/ajaxCustomer.jsp<br>
 REST service url is : http://localhost:8080/lxsweb/customerservice/customers/1 (id=1)
 </font></strong></p>

<div id="myid" style="color:red; font-weight:bold">watch out this</div>
<br>

<button>Click me to get json from REST</button>

<br><br>

</body>

<script>
$(document).ready(function(){
    $("button").click(function(){
      //alert("me clicked");
      
      //this works use dataType=json 
      /*
        $.ajax({
                type: "GET",
                url: "/lxsweb/customerservice/customers/1",
                accept: "application/json",  
                contentType: "application/json; charset=utf-8",
                //dataType: "text",
                dataType: "json",
                success: function (data) {
                    //var myData = data.d; // data.d is a JSON object that represents out SayHello class.
                    // As it is already a JSON object we can just start using it
                    alert("data="+data);

                    //var response = JSON.parse(sdata);
                    //alert("data count="+response);
                      $.each( data, function( key, val ) {
					    //alert( "key=" + key + "; value=" + val  );
					    $.each( val, function( key2, val2 ) {
						    //alert( "key2=" + key2 + "; value2=" + val2  );
						    
						  });
					    
					  });
                      
                      alert(JSON.stringify(data));
                      console.log("lxs="+data);
              
                    $("#myid").html(data.customer.city);
                },
                
        		error: function(xhr){  
           			console.log(xhr);
         		}
        });	
      */
      
      //This is working too
      $.getJSON("/lxsweb/customerservice/customers/1", function(result){
          	alert("result (object)="+result);
             // $('#myid').html(result.id);
            alert(JSON.stringify(result));
            console.log("lxs="+result);
    		//display city in <div id="myid"></div>
          $("#myid").html(result.city);
          
      });
  
      
    });
});
</script>
</html>
