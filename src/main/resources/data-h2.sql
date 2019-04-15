insert into account (id, created_on, modified_on, total_balance, reserved_balance, name) values
  (-1, now(), now(), 157.62, 0.00, 'LT477000000000001'),
  (-2, now(), now(),   4.78, 0.00, 'LT477000000000001');
  
insert into customer (id, created_on, modified_on, name, account_id, uuid) values
  (0, now(), now(), 'Jonas Jonaitis', -1, 'ae7af90ec65511e8a355529269fb1464'),
  (-1, now(), now(), 'Petras Petraitis', -2, 'ae7afad0c65511e8a355529269fb1465');