package vttp2022.paf.assessment.eshop.respositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonObject;
import vttp2022.paf.assessment.eshop.models.LineItem;
import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

@Repository
public class OrderRepository {
	// TODO: Task 3

	@Autowired
	JdbcTemplate jdbcTemplate;

	public boolean addItems(List<LineItem> items, String orderId) throws Exception {
        
		// 3d • Save the order to the database
		// item, quantity, orderId
        List<Object[]> data = items.stream()
            .map(i -> {
                Object[] obj = new Object[3];
                obj[0] = i.getItem();
                obj[1] = i.getQuantity();
                obj[2] = orderId;
                return obj;
            })
            .toList();
            
        // Batch update
		try {
			jdbcTemplate.batchUpdate(SQL_INSERT_ITEMS, data);
		} catch (Exception e) {
			return false;
		}
		return true;
    }

	// • Task 6
	public Integer getPendingCount (String name) {

		final SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_JOIN_CUST_ORDER, name, "pending");

  		return rs.getInt("status_count");

	}
	
	// • Task 6
	public Integer getDispatchedCount (String name) {

		final SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_JOIN_CUST_ORDER, name, "dispatched");

  		return rs.getInt("status_count");

	}
}
