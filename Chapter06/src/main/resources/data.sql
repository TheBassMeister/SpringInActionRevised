delete from Taco_Order_Tacos;
delete from Taco_Ingredients;
delete from Taco;
delete from Taco_Order;
delete from Ingredient;
--delete from Users;
--delete from UserAuthorities;
insert into Ingredient (id, name, type) values ('FLTO', 'Flour Tortilla', 'WRAP');
insert into Ingredient (id, name, type) values ('COTO', 'Corn Tortilla', 'WRAP');
insert into Ingredient (id, name, type) values ('GRBF', 'Ground Beef', 'PROTEIN');
insert into Ingredient (id, name, type) values ('CARN', 'Carnitas', 'PROTEIN');
insert into Ingredient (id, name, type) values ('TMTO', 'Diced Tomatoes', 'VEGGIES');
insert into Ingredient (id, name, type) values ('LETC', 'Lettuce', 'VEGGIES');
insert into Ingredient (id, name, type) values ('CHED', 'Cheddar', 'CHEESE');
insert into Ingredient (id, name, type) values ('JACK', 'Monterrey Jack', 'CHEESE');
insert into Ingredient (id, name, type) values ('SLSA', 'Salsa', 'SAUCE');
insert into Ingredient (id, name, type) values ('SRCR', 'Sour Cream', 'SAUCE');
insert into User (id, username, password, fullname, street,city, state, zip, phone_number) values (
    '1','Woody', '$2a$10$wG1dZxfyfIrXegwQA5C6Q.r2TWLyuWHH4dJU688kvsmzWWk/.6SBm','Woody H', 'ToyStreet 21', 'Andy''s place', 'AZ','32134','2345673');
insert into User (id, username, password, fullname, street,city, state, zip, phone_number) values (
    '2','Buzz', '$2a$10$BDrY479.DI1QeJ.tL/NBhep/xWoNcwst5Kzd6T27Xf2oLLemQL5LO','Buzz Lightyear', 'Milky Way 42', 'Far Away Town', 'GA','32134','4327842');

--insert into Users(username, password, enabled) values ('Woody','bullseye', 'true');
--insert into UserAuthorities(username, authority) values ('Woody','RoleUser');
