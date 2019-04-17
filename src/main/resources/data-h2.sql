create table account (id identity primary key auto_increment,
                      created_on timestamp default now() not null,
                      modified_on timestamp default now() not null,
                      total_balance number(10,2) not null,
                      reserved_balance number(10,2) not null,
                      name varchar2 not null);

insert into account (id, created_on, modified_on, total_balance, reserved_balance, name) values
  (-1, now(), now(), 100.00, 0.00, 'LT477000000000001'),
  (-2, now(), now(), 200.00, 0.00, 'LT477000000000002');


create table customer (id identity primary key auto_increment,
                       created_on timestamp default now() not null,
                       modified_on timestamp default now() not null,
                       name varchar2 not null,
                       account_id int not null);
alter table customer
    add foreign key (account_id)
    references account(id);

insert into customer (id, created_on, modified_on, name, account_id) values
  (-1, now(), now(), 'Jonas Jonaitis', -1),
  (-2, now(), now(), 'Petras Petraitis', -2);

create table payment (id identity primary key auto_increment,
                      created_on timestamp default now() not null,
                      modified_on timestamp default now() not null,
                      method varchar2 not null,
                      amount number(10,2) not null,
                      message varchar2 not null,
                      status varchar2 not null,
                      error_message varchar2,
                      debtor_account_id int not null,
                      creditor_account_id int not null);

alter table payment
    add foreign key (debtor_account_id)
    references account(id);
alter table payment
    add foreign key (creditor_account_id)
    references account(id);

insert into payment (id, created_on, modified_on, method, amount,  message, status, error_message, debtor_account_id, creditor_account_id) values
 (-1, now(), now(), 'SEPA', 10.20, 'Uz buta',  'success', null, -2, -1),
 (-2, now(), now(), 'SEPA',  9.00, 'Uz nuoma',  'success', null, -1, -2);

create table transaction (id identity primary key auto_increment,
                          created_on timestamp default now() not null,
                          modified_on timestamp default now() not null,
                          posted_on timestamp,
                          error_message varchar2,
                          payment_id int not null);
alter table transaction
    add foreign key (payment_id)
    references payment(id);

insert into transaction (id, created_on, modified_on, posted_on, payment_id) values
  (-1, now(), now(), '2019-04-17', -1),
  (-2, now(), now(), '2019-04-17', -2);

create table transaction_posting (id identity primary key auto_increment,
                                  created_on timestamp default now() not null,
                                  modified_on timestamp default now() not null,
                                  transaction_id int not null,
                                  account_id int not null,
                                  debit number(10,2),
                                  credit number(10,2));

alter table transaction_posting
    add foreign key (transaction_id)
    references transaction(id);

alter table transaction_posting
    add foreign key (account_id)
    references account(id);

insert into transaction_posting (id, created_on, modified_on, transaction_id, account_id, debit, credit) values
  (-1, now(), now(), -1, -2, 10.20,  0.00),
  (-2, now(), now(), -1, -1,  0.00, 10.20),
  (-3, now(), now(), -2, -1,  0.00,  9.00),
  (-4, now(), now(), -2, -2,  9.00,  0.00);

