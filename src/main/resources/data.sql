insert into cart (clientid) value (3);
insert into cart_item (adid, start_date, end_date, in_cart, ownerid, cart_id) value (1, '2020-06-04','2020-06-08', false, 2, 1);
insert into request (clientid, ownerid, status, creation_time) value (3,2,'Paid', '2020-06-02 09:00:00');
insert into request_ad (adid, clientid, ownerid, current_price_per_km, payment, start_date, end_date, request_id, report_created) value (1,3,2,3,125,'2020-06-04','2020-06-08',1, false);

insert into request (clientid, ownerid, status, creation_time) value (3,2,'Paid', '2020-06-03 09:00:00');
insert into request_ad (adid, clientid, ownerid, current_price_per_km, payment, start_date, end_date, request_id, report_created) value (2,3,2,3,125,'2020-06-09','2020-06-13',2, false);

insert into reservation (adid, start_date, end_date, ownerid, current_price_per_km, client_first_name, client_last_name,client_email, client_phone_number, payment, report_created) values (1,'2020-06-14','2020-06-16',2,3,'Petar', 'Petrovic','petar@gmail.com','061265835',150, false);

insert into user_info (user_id, name, surname, email, role, company_name) value (3, 'Client1', 'Client', 'isa2019pacijent@outlook.com','ROLE_CLIENT', null);
insert into user_info (user_id, name, surname, email, role, company_name) value (2, 'Agent1', 'Agent', 'agent@gmail.com','ROLE_AGENT','My Company');

insert into message (content, date_time, from_user_info_id, request_id) value ('Ovo je poruka od klijenta.', '2020-06-04 09:11:12',  1, 1);
insert into message (content, date_time, from_user_info_id, request_id) value ('Ovo je poruka od vlasnika.', '2020-06-04 09:15:23',  2, 1);
