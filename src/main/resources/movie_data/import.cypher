LOAD CSV WITH HEADERS FROM 'file:///movie.csv' AS line
CREATE (:Movie{
  id: line.id,
  name: line.title,
  url: line.url,
  image: line.cover,
  rate: toFloat(line.rate),
  category: line.category,
  district: line.district,
  language: line.language,
  showtime: toInteger(line.showtime),
  length: toInteger(line.length),
  othername: line.othername
});

LOAD CSV FROM 'file:///person.csv' AS line
CREATE (:Person{name:line[0]});

LOAD CSV FROM 'file:///genre.csv' AS line
CREATE (:Genre{name:line[0]});

LOAD CSV WITH HEADERS FROM 'file:///genre_movie.csv' AS line
MATCH (p:Genre) where p.name = line.genre WITH p,line
MATCH (m:Movie) where m.id = line.id 
MERGE (m)-[:is]->(p);

LOAD CSV WITH HEADERS FROM 'file:///actor.csv' AS line
MATCH (p:Person) where p.name = line.actor WITH p,line
MATCH (m:Movie) where m.id = line.id 
MERGE (p)-[:play]->(m);

LOAD CSV WITH HEADERS FROM 'file:///director.csv' AS line
MATCH (p:Person) where p.name = line.director WITH p,line
MATCH (m:Movie) where m.id = line.id 
MERGE (p)-[:direct]->(m);

LOAD CSV WITH HEADERS FROM 'file:///composer.csv' AS line
MATCH (p:Person) where p.name = line.composer WITH p,line
MATCH (m:Movie) where m.id = line.id 
MERGE (p)-[:write]->(m);

match (p:Person)-[]-(m:Movie) with p,avg(m.rate)*10 as avg
set p.rate = round(avg)/10;

match (n) set n.id=id(n);
CREATE INDEX ON :Movie(id);
CREATE INDEX ON :Movie(name);
CREATE INDEX ON :Person(name);
CREATE INDEX ON :Genre(name);