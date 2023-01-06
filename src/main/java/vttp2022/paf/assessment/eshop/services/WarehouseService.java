package vttp2022.paf.assessment.eshop.services;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;

import org.springframework.http.MediaType;


import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

@Service
public class WarehouseService {

	public static final String MY_NRIC_NAME = "YEONG JIA JUN DELAN";

	// You cannot change the method's signature
	// You may add one or more checked exceptions
	public OrderStatus dispatch(Order order) {

		// TODO: Task 4

		// Task4 • Dispatch
			// UriComponents uriComponents = UriComponentsBuilder.newInstance()
      		// 	.scheme("http").host("paf.chuklee").path("/{article-name}")
      		// 	.buildAndExpand(orderId);

			  JsonArrayBuilder itemsArray = Json.createArrayBuilder();

			  for (LineItem i : order.getLineItems()) {
				  itemsArray.add(Json.createObjectBuilder()
					  .add("item", i.getItem())
					  .add("quantity", i.getQuantity())
					  .build());
			  }
  
				JsonObject json = Json.createObjectBuilder()
				.add ("orderId", order.getOrderId())
				.add ("name", order.getName())
				.add ("address", order.getAddress())
				.add ("email", order.getEmail())
				.add ("lineItems", itemsArray)
				.add ("createdBy", MY_NRIC_NAME)
				.build();
		
					RequestEntity<String> req = RequestEntity
					.post("http://paf.chuklee.com/dispatch/" + order.getOrderId())
					.contentType(MediaType.APPLICATION_JSON)
					// .headers("Accept", MediaType.APPLICATION_JSON)
					.body(json.toString(), String.class);

				RestTemplate template = new RestTemplate();

				ResponseEntity<String> resp = template.exchange(req, String.class);

				String payload = resp.getBody();
        		System.out.println("payload: " + payload);

				JsonReader reader = Json.createReader(new StringReader(payload));
  				JsonObject j = reader.readObject();

				OrderStatus orst = new OrderStatus();
				orst.setOrderId(j.getString("orderId"));
				orst.setDeliveryId(j.getString("deliveryId"));
				orst.setStatus("pending");

				if ( null == orst.getDeliveryId() ) {
					orst.setStatus("pending");
					orst.setDeliveryId("");
					return orst;
				} else if ( null != orst.getDeliveryId()) {
					orst.setStatus("dispatched");
				}
					
		return null;

	}

	
}
