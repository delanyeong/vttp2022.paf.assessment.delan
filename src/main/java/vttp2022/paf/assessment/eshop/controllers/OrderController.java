package vttp2022.paf.assessment.eshop.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.MediaType;


import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.WarehouseService;

@RestController
@RequestMapping
public class OrderController {

	@Autowired
	private CustomerRepository custRepo;

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private WarehouseService whSvc;

	@PostMapping (path="/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> resp (@RequestBody MultiValueMap<String,String> form, HttpSession sess) {

		//TODO: Task 3

		String custName = form.getFirst("name");

		Optional<Customer> opt = custRepo.findCustomerByName(custName);

		if (opt.isPresent()) {
            opt.get();

			// 3b • Populate Model: LineItem

			List<LineItem> lineItems = (List<LineItem>)sess.getAttribute("cart");
        	if (null == lineItems) {
            System.out.println("This is a new session");
            System.out.printf("session id = %s\n", sess.getId());
            lineItems = new LinkedList<>();
            sess.setAttribute("cart", lineItems);
        }

			String item = form.getFirst("item");
			Integer quantity  = Integer.parseInt(form.getFirst("quantity"));
			LineItem l = new LineItem();
			l.setItem(item);
			l.setQuantity(quantity);
			lineItems.add(l);

			for (LineItem li: lineItems)
				System.out.printf("description: %s, quantity: %d\n", li.getItem(), li.getQuantity());

			Order newOrd = new Order();
			String orderId = UUID.randomUUID().toString().substring(0, 8);
			newOrd.setOrderId(orderId);

			// 3d • Save the order to the database
			try {
				boolean isSucc = orderRepo.addItems(lineItems, orderId);
				if (!isSucc) {
					return ResponseEntity
							.status(500)
							.body(Json.createObjectBuilder().add("error", "Failed to save")
							.build().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 3b • Populate Model: Order
			// private String orderId;
			// private String deliveryId;
			// private String name;
			// private String address;
			// private String email;
			// private String status;
			// private Date orderDate = new Date();
			// private List<LineItem> lineItems = new LinkedList<>();
			

			String delId = UUID.randomUUID().toString().substring(0, 8);
			newOrd.setDeliveryId(delId);

			newOrd.setName(opt.get().getName());
			newOrd.setAddress(opt.get().getAddress());
			newOrd.setEmail(opt.get().getEmail());
			newOrd.setStatus("Pending");
			newOrd.setLineItems(lineItems);

			long millis = System.currentTimeMillis();
        	java.sql.Date date = new java.sql.Date(millis);
			newOrd.setOrderDate(date);

			// Task 4
			OrderStatus orst = whSvc.dispatch(newOrd);
			if (orst.getStatus().equals("dispatched")) {

				JsonObject json = Json.createObjectBuilder()
				.add ("orderId", orst.getOrderId())
				.add ("deliveryId", orst.getDeliveryId())
				.add ("status", orst.getStatus())
				.build();

				return ResponseEntity
				.status(200)
				.body(json.toString());

			} else if (orst.getStatus().equals("pending")) {

				JsonObject json = Json.createObjectBuilder()
				.add ("orderId", orst.getOrderId())
				.add ("status", orst.getStatus())
				.build();

				return ResponseEntity
				.status(200)
				.body(json.toString());
			}


			// 3a • Check if customer is valid
			//Success Message 
			String succMsg = "Customer" + custName + "exist";

			return ResponseEntity
			.status(200)
			.body(Json.createObjectBuilder().add("success", succMsg)
			.build().toString());

			
        }

		// 3a • Check if customer is valid
		//Error Message 
		String errorMsg = "Customer" + custName + "not found";

			return ResponseEntity
			.status(404)
			.body(Json.createObjectBuilder().add("error", errorMsg)
			.build().toString());

	
	}

	@GetMapping (path="api/order/{name}/status", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<String> getStatus (@PathVariable String name) {

		Integer pendingCount = orderRepo.getPendingCount(name);
		Integer dispatchedCount = orderRepo.getDispatchedCount(name);

		JsonObject json = Json.createObjectBuilder()
				.add ("name", name)
				.add ("dispatched", dispatchedCount)
				.add ("pending", pendingCount)
				.build();

		return ResponseEntity
			.status(200)
			.body(json.toString());


	}

}
