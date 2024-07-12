insert into "user"(id,email,password,first_name,created_at,created_by)
values
    ('0190a018-c90b-7b23-bb7a-250c777dab7e','fulan@gmail.com','$2a$10$GrmjwPvOw3daCO2tUZQsI.dTc60OoTNhsOWK4INFsisXNtY6DO4aS','fulan',now(),'SYSTEM'),
    ('0190a0a0-0a3c-751f-8891-37239d7717e2','fulanah@gmail.com','$2a$10$0p0gHMu2K9.2dgdII7tqZeezCCH7yMhgM0x1.Dp/OEAD.dvjN7FbO','fulanah',now(),'SYSTEM');

insert into access_module(id,code,qualifier,created_at,created_by)
values
    ('0190a0a3-0ef3-7d5c-bfa1-b70c09612b75','chat',0,now(),'SYSTEM');

insert into role(id,name,qualifier,created_at,created_by)
values
    ('0190a0a4-fe21-7bfd-9171-5eebd9bec38f','Chat',ARRAY [1::bigint],now(),'SYSTEM');

insert into role_access_module(id,role_id,access_module_id,created_at,created_by)
values
    ('0190a0a6-7b65-70b3-a7d1-49b82f56238e','0190a0a4-fe21-7bfd-9171-5eebd9bec38f','0190a0a3-0ef3-7d5c-bfa1-b70c09612b75',now(),'SYSTEM');

insert into user_role(id,user_id,role_id,created_at,created_by) values
   ('0190a0a7-54d3-7b0b-b2ed-d57f14fb82f3','0190a0a0-0a3c-751f-8891-37239d7717e2','0190a0a4-fe21-7bfd-9171-5eebd9bec38f',now(),'SYSTEM'),
   ('0190a0b2-29f1-7b07-aac6-e64aa64c9489','0190a018-c90b-7b23-bb7a-250c777dab7e','0190a0a4-fe21-7bfd-9171-5eebd9bec38f',now(),'SYSTEM');