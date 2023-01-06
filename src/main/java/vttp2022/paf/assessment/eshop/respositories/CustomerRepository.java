package vttp2022.paf.assessment.eshop.respositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp2022.paf.assessment.eshop.models.Customer;
import static vttp2022.paf.assessment.eshop.respositories.Queries.*;


@Repository
public class CustomerRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	// You cannot change the method's signature
	public Optional<Customer> findCustomerByName(String name) {
		// TODO: Task 3 

		//Perform the Query
        final SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_CUSTOMER_BY_NAME, name);

        
		Customer b = new Customer();
		b.setName(rs.getString("name"));
		b.setAddress(rs.getString("address"));
		b.setEmail(rs.getString("email"));

		return Optional.of(b);

	}
}
