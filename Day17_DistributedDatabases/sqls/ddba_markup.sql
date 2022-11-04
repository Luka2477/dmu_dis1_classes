create database dmu_dis1_day17_ddba COLLATE Danish_Norwegian_CI_AS

go

use dmu_dis1_day17_ddba

create table personadr (
    cpr char(10),
    navn varchar(30),
    bynavn varchar(30)
)

create table kontoa (
    kontonr int identity(1001,2),
    kontohavercpr char(10),
    rente int -- hvis positiv renteindt gt, negativ renteudgift�
)

insert into personadr values ('1212921456','Ib Hansen','Esbjerg')
insert into personadr values ('1111971112','Per Olsen','Kolding')
insert into personadr values ('0909950056','Jens Andersen','Viborg')
insert into kontoa values('1212921456',1000)
insert into kontoa values('1111971112',100)
insert into kontoa values('1111971112',-200)
insert into kontoa values('0909950056',10)

go