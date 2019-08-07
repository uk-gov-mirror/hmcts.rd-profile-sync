create schema if not exists dbsyncdata;

create table sync_job(
 id bigint,
response integer,
status varchar(50),
error_message varchar(1024),
records_updated integer,
source varchar(50),
audit_ts timestamp,
constraint sync_job_pk primary key (id)
);

create sequence sync_job_id_seq
 increment by 1
 minvalue 0
 maxvalue 2147483647
 start with 1
 cache 1
 no cycle;