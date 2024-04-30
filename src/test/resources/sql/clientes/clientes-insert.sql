insert into USUARIOS (id, username, password, role) values (100, 'ana@email.com', '$2a$12$gcTP1oLNOlOeE6NF6Hg9Ce2r4PKyFOC6xB2ni6L8/TBb.sTC/CBtK', 'ROLE_ADMIN');
insert into USUARIOS (id, username, password, role) values (101, 'bia@email.com', '$2a$12$gcTP1oLNOlOeE6NF6Hg9Ce2r4PKyFOC6xB2ni6L8/TBb.sTC/CBtK', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (102, 'bob@email.com', '$2a$12$gcTP1oLNOlOeE6NF6Hg9Ce2r4PKyFOC6xB2ni6L8/TBb.sTC/CBtK', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (103, 'toby@email.com', '$2a$12$gcTP1oLNOlOeE6NF6Hg9Ce2r4PKyFOC6xB2ni6L8/TBb.sTC/CBtK', 'ROLE_CLIENTE');

insert into CLIENTES(id, nome, cpf, id_usuario) values (10, 'Bianca Silva', '95240496005', 101);
insert into CLIENTES(id, nome, cpf, id_usuario) values (20, 'Roberto Gomes', '91446908003', 102);