insert into location(longitude,latitude,country,city) values(20.2,45.2,'Serbia','Novi Sad');

insert into role (id,name) values(1,'ROLE_ADMIN');
insert into role (id,name) values(2,'ROLE_USER');

insert into users (username,password,name,surname,email,location_id,is_activated,role_id,last_password_reset_date) values('pera123','$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra','Pera','Peric','pera@gmail.com',1,true,2,'2017-10-01 21:58:58.508-07');
insert into users (username,password,name,surname,email,location_id,is_activated,role_id,last_password_reset_date) values('ana123','$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra','Ana','Anic','ana@gmail.com',1,true,2,'2017-10-01 21:58:58.508-07');

insert into post(user_id,description,image,location_id,time_of_publishing) values(2, 'Short description','image1',1,'2017-10-01 21:30:00');

insert into comment(user_id,post_id,description,created_at) values(1,1,'Good Work!!','2017-10-01 21:30:00');

insert into user_following(follower_user_id,followed_user_id) values(1,2);

insert into post_user_likes(post_id,user_id) values(1,1)
