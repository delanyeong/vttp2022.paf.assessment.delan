--Task 2
-- • Create a database called eshop
create database eshop;

use eshop;

-- • create a table called customers in the eshop database according to the requirements
create table customers (
    name        varchar(32)     primary key
    address     varchar(128)    not null,
    email       varchar(128)    not null
);

-- • insert statements 
insert into customers (name, address, email)
values  ("fred", "201 Cobblestone Lane", "fredflintstone@bedrock.com"),
        ("sherlock", "221B Baker Street, London", "sherlock@consultingdetective.org"),
        ("spongebob", "124 Conch Street, Bikini Bottom", "spongebob@yahoo.com")
        ("jessica", "698 Candlewood Land, Cabot Cove", "fletcher@gmail.com"),
        ("dursley", "4 Privet Drive, Little Whinging", "Surrey: dursley@gmail.com");


-- // private String orderId;
-- 			// private String deliveryId;
-- 			// private String name;
-- 			// private String address;
-- 			// private String email;
-- 			// private String status;
-- 			// private Date orderDate = new Date();
-- 			// private List<LineItem> lineItems = new LinkedList<>();
-- Task 3c.
create table orders (
    orderId     varchar(8)      UNIQUE not null,
    deliveryId  varchar(32)     UNIQUE not null,
    name        varchar(32)     UNIQUE not null,
    address     varchar(128)           not null,
    email       varchar(128)           not null,
    status      varchar(16)            not null,
    orderDate   date                           ,

    primary key(orderId),
    constraint fk_name
        foreign key(name) references customers(name)
);

create table lineItem (
    item        text        not null,
    quantity    integer     default '1',
    order_id    varchar(8)  not null,

    constraint fk_order_id
        foreign key(order_id) references orders(orderId)
)

-- Task 4
create table order_status (
    order_id        varchar(8)                      not null,
    delivery_id     varchar(128)                    not null,
    status          ENUM ("pending","dispatched")   not null,
    status_update   timestamp                       not null,

    constraint fk_order_id
        foreign key(order_id) references orders(orderId)

)

-- Task 6
select c.name, count(o.status) as cnt from customers c join orders o on c.name = o.name where c.name = "spongebob" and o.status = "dispatched";