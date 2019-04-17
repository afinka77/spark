create table account (id int primary key,
created_on timestamp,
modified_on timestamp,
total_balance number(10,2),
reserved_balance number(10,2),
name varchar2);

insert into account (id, created_on, modified_on, total_balance, reserved_balance, name) values
  (-1, now(), now(), 157.62, 0.00, 'LT477000000000001'),
  (-2, now(), now(),   4.78, 0.00, 'LT477000000000002');


create table customer (id int primary key,
created_on timestamp,
modified_on timestamp,
name varchar2,
account_id int,
uuid uuid);
insert into customer (id, created_on, modified_on, name, account_id, uuid) values
  (0, now(), now(), 'Jonas Jonaitis', -1, 'ae7af90ec65511e8a355529269fb1464'),
  (-1, now(), now(), 'Petras Petraitis', -2, 'ae7afad0c65511e8a355529269fb1465');

create table transaction (id int primary key,
created_on timestamp,
modified_on timestamp,
posted_on timestamp,
description varchar2);

insert into transaction (id, created_on, modified_on, posted_on, description) values
  (-1, now(), now(), '2019-04-17', 'Uz buta'),
  (-2, now(), now(), '2019-04-17', 'Uz nuoma');

create table transaction_posting (id int primary key,
created_on timestamp,
modified_on timestamp,
transaction_id int,
account_id int,
debit number(10,2),
credit number(10,2));
insert into transaction_posting (id, created_on, modified_on, transaction_id, account_id, debit, credit) values
  (-1, now(), now(), -1, -2, 10.20,  0.00),
  (-2, now(), now(), -1, -1,  0.00, 10.20),
  (-3, now(), now(), -1, -1,  0.00,  9.00),
  (-4, now(), now(), -1, -2,  9.00,  0.00);

