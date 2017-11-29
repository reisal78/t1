create table customer
(
	id bigint auto_increment
		primary key,
	first_name varchar(255) null,
	last_name varchar(255) null
)
;

create table traffic
(
	id bigint auto_increment
		primary key,
	date datetime null,
	downlink bigint null,
	uplink bigint null,
	customer_id bigint null,
	constraint FKk46yebm4yb90y9lp00l4uskwo
		foreign key (customer_id) references customer (id)
)
;

create index FKk46yebm4yb90y9lp00l4uskwo
	on traffic (customer_id)
;