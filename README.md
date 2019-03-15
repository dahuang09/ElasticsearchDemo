# ElasticsearchDemo
show how to index entities into Elasticsearch, and query data from Elasticsearch


## query https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-query-builders.html 
### fuzzy search
   "multi_match": {
						"query": "000032",
						"fields": [],
						"type": "best_fields",
						"operator": "AND"
	 }
	 
### advance search
#### String field
1. contains: "must": [{"wildcard" : {"name": lowercase("damon")}}]
2. not contains: "must": [{"wildcard" : {"name": lowercase("damon")}}]
3. is: "must": [{"wildcard" : {"name": lowercase("000008")}}]
4. is not: "must_not": [{"term": {"name.keyword": {"value": "damon"}}}]
5. start with: "must": [{"match_phrase_prefix" : { "name" : "dam"}}]
6. does not start with: "mus_not": [{"match_phrase_prefix" : { "name" : "dam"}}]
7. end with: "must": {"wildcard" : {"name" : "*om"}}
8. does not end with: "must_not": {"wildcard" : {"name" : "*om"}}
9. is blank: "must_not": [{"exists": {"field": "name"}}]
10. is not blank: "must": [{"exists": {"field": "name"}}]	
#### Date field
1. is: "must": [{"term": {"birthDate": {"value": "2017-08-14T09:07:57.538+08:00"}}}]
2. is not: "must_not": [{"term": {"birthDate": {"value": "2017-08-14T09:07:57.538+08:00"}}}]
3. is greater than: "range": {"birthDate": {"gt": "2017-08-14T09:07:57.538+08:00"}}
4. is less than: "range": {"birthDate": {"lt": "2017-08-14T09:07:57.538+08:00"}}
5. is in the range: "range": {"birthDate": {"gte": "2017-08-14T09:07:57.538+08:00", "lte": "2018-07-14T09:07:57.538+08:00"}}
6. is not in the range: "filter": {"bool": {"should": [{"range": {"birthDate": {"gt": "2018-09-13T09:26:31.793+08:00"}}}, {"range": {"birthDate": {"lt": "2018-09-13T09:26:30.793+08:00"}}}], "minimum_should_match": 1}}
#### Decimal field
1. is: "must": [{"term": {"price": {"value": "11.323"}}}]
2. is not: "must": [{"term": {"price": {"value": "11.93"}}}]
3. is greater than: "must": [{"term": {"price": {"value": "11.93"}}}]
4. is less than: "range": {"price": {"lt": 11.3233}}
5. is in the range: "range": {"price": {"gte": 11.3333, "lte": 12.3333}}
