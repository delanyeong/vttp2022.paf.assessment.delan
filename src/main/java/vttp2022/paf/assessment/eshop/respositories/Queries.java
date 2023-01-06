package vttp2022.paf.assessment.eshop.respositories;

public class Queries {
    public static final String SQL_SELECT_CUSTOMER_BY_NAME = "select * from customers where name = ?";
    public static final String SQL_INSERT_ITEMS = "insert into lineItem(item, quantity, order_id) values (?, ?, ?)";
    public static final String SQL_JOIN_CUST_ORDER = "select c.name, count(o.status) as cnt from customers c join orders o on c.name = o.name where c.name = spongebob and o.status = dispatched";
}
