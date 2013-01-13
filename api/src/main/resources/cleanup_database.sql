--new entries added after importing a CCD:
--meta entries: concept, drug, concept source, concept map, encounter type, location, 
--data entries: observation, order, drug order

delete from drug_order;
delete from drug where date_created > '2013-01-01';

delete from orders where date_created > '2013-01-01';
delete from encounter where date_created > '2013-01-01';
delete from obs where date_created > '2013-01-01';
delete from concept_map where date_created > '2013-01-01';
delete from concept_word where concept_name_id in (select distinct concept_name_id from concept_name where date_created > '2013-01-01');
delete from concept_name where date_created > '2013-01-01';
delete from concept where date_created > '2013-01-01';
delete from concept_source;
delete from location where date_created > '2013-01-01';
delete from encounter_type where date_created > '2013-01-01';
