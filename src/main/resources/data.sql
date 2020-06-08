insert into cart (clientid) value (3);
insert into cart_item (adid, start_date, end_date, in_cart, ownerid, cart_id) value (1, '2020-06-04','2020-06-08', false, 2, 1);
insert into request (clientid, ownerid, status, creation_time) value (3,2,'Paid', '2020-06-02 09:00:00');
insert into request_ad (adid, clientid, ownerid, current_price_per_km, payment, start_date, end_date, request_id) value (1,3,2,3,125,'2020-06-04','2020-06-08',1);

insert into reservation (adid, start_date, end_date, ownerid, current_price_per_km, client_first_name, client_last_name,client_email) values (1,'2020-06-12','2020-06-14',2,3,'Petar', 'Petrovic','petar@gmail.com');
