#select a.nazwisko, a.imie, s.tytul, s.plyta, s.s_sort from autorzy a inner join sluchowiska s on a.id = s.id_autora
#where a.nazwisko is not null and a.nazwisko <> "" 


#select a.nazwisko, a.imie, s.tytul from autorzy a left join sluchowiska s on a.id = s.id_autora
#where a.nazwisko is not null and a.nazwisko <> ""

# select * from autorzy
# where nazwisko like "MUL%"


/*
select imie, nazwisko from autorzy
where nazwisko like "A%"
order by nazwisko

select tytul, plyta from sluchowiska
where plyta = 87


select nazwisko from autorzy as a, sluchowiska as s
where s.id_autora = a.id
order by a.nazwisko


select imie, nazwisko, tytul, plyta, plik_mp3 from autorzy as a, sluchowiska as s
where a.id = s.id_autora and nazwisko like "A%"
order by a.nazwisko, s.tytul


select id, tytul, YEAR(data_wpisu) from sluchowiska
where s_sort is null
order by id


select imie, nazwisko, tytul, plyta, plik_mp3, hit from autorzy as a, sluchowiska as s
where a.id = s.id_autora and nazwisko like "A%"
order by a.nazwisko, s.tytul



select tytul as name from sluchowiska as s, autorzy as a
where s.id_autora = a.id
order by s.tytul


select imie, nazwisko, tytul, plyta, plik_mp3, hit from autorzy as a, sluchowiska as s
where a.id = s.id_autora and tytul like "6%"
order by tytul



select id as max_id from sluchowiska
where id = (select max(id) from sluchowiska)


select id as max_id from wypozyczenia
where id = (select max(id) from wypozyczenia)


select imie, nazwisko, tytul, plyta, plik_mp3, hit from autorzy as a, sluchowiska as s
where a.id = s.id_autora and nazwisko like "A%" and s.id > 749
order by a.nazwisko, s.tytul



select imie, nazwisko, tytul, plyta, plik_mp3, hit from autorzy as a, sluchowiska as s
where a.id = s.id_autora and  s.id > 749
order by tytul

*/