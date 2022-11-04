select * from dbo.kontoa
select * from dmu_dis1_day17_ddbb.dbo.kontob

select * from dbo.personadr
select * from dmu_dis1_day17_ddbb.dbo.personloen

go

create or alter view person as
    select a.cpr, a.navn, a.bynavn, b.loen, b.skatteprocent
    from dbo.personadr as a
    join dmu_dis1_day17_ddbb.dbo.personloen as b
    on a.cpr=b.cpr

go

create or alter view konto as
select *
from dbo.kontoa
union
select *
from dmu_dis1_day17_ddbb.dbo.kontob

go

select * from person
select * from konto

go

select p.cpr, p.navn, k.rente, p.skatteprocent, (k.rente * p.skatteprocent / 100.0) as "skat til betaling"
from person as p
join konto as k
on p.cpr=k.kontohavercpr

go

create or alter trigger person_insert_trigger
on person
instead of insert
as
    insert into dbo.personadr
    select cpr, navn, bynavn
    from inserted

    insert into dmu_dis1_day17_ddbb.dbo.personloen
    select cpr, loen, skatteprocent
    from inserted

go

insert into person (cpr, navn, bynavn, loen, skatteprocent) values (1234567890, 'Lukas Knudsen', 'Vildbjerg', 17500, 39)
select * from person

go