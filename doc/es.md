```json
GET /_cat/indices?v
# 减少副本数量
PUT /cni-post/_settings
{
  "index" : {
    "number_of_replicas" : 0
  }
}

GET /cni-post/_search
{
  "query": {
    "match": {
      "title": "测试删除"
    }
  }
}


POST /_analyze
{
  "text":"今天天气适合爬山",
	"analyzer":"ik_max_word"
}

POST /_analyze
{
  "text":"今天天气适合爬山",
	"analyzer":"ik_smart"
}

DELETE /cni-post

PUT /cni-post
{
 "mappings": {
   "properties": {
     "id":{
       "type": "keyword"
     },
     "title":{
       "type": "text",
       "analyzer": "ik_max_word"
       
     },
     "summary":{
       "type": "text",
       "analyzer": "ik_smart"
       
     },
     "content":{
       "type": "text",
       "analyzer": "ik_smart"
       
     },
     "authorName":{
       "type": "text",
       "analyzer": "ik_max_word"
       
     },
     "categoryId":{
       "type": "keyword"
     },
     "tagsId":{
       "type": "long"
     },
     "type":{
       "type": "integer"
     },
     "isTop":{
       "type": "integer"
     },
      "viewCounts":{
       "type": "integer"
      },
       "commentCounts":{
       "type": "integer"
      },
      "coverUrl":{
       "type": "keyword",
       "index": false
      },
     "createTime":{
       "type": "date",
       "format": "epoch_millis"
     },
     "updateTime":{
       "type": "date",
       "format": "epoch_millis"
     },
     "lastCommentTime":{
       "type": "date",
       "format": "epoch_millis"
     }
   }
 } 
}
GET /cni-post/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "测试", 
            "fields": ["title", "summary", "content"]
          }
        },
        {
          "match": {
            "authorName": "admin"
          }
        },
        {"term": {
          "categoryId": {
            "value": 1845183972196343809
          }
        }}
      ],
      "should": [
        {
          "function_score": {
      
            "functions": [
                {
                "filter": {
                  "term": {
                    "lastCommentTime": 1734015450000
                  }
                },
                "weight": 100
              }
              
            ],
          "boost_mode": "multiply"
          }
        }
      ],
      "filter": [
        {"range": {
          "createdTime": {
            "gte": 1729002236000,
            "lte": 1734131204000
          }
        }}
      ]
    }
    }
    
  }
  
}

GET /cni-post/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "测试", 
            "fields": ["title","summary","content"]
          }
        },
        {
          "match": {
            "authorName": "patricLee"
          }
        },
        {"term": {
          "categoryId": {
            "value": 1845183972196343809
          }
        }},
        {"terms": {
          "tagsId":[1845188964466610178]
         
        }}
      ],
      "should": [
        {
          "function_score": {
      
            "functions": [
                {
                "filter": {
                  "term": {
                    "isTop": 0
                  }
                },
                "weight": 10
              }
              
            ],
          "boost_mode": "multiply"
          }
        }
      ],
      "filter": [
        {"range": {
          "createdTime": {
            "gte": 1729002236000,
            "lte": 1734131204000
          }
        }}
      ]
    }
  },
    "from": 0,
    "size": 10,  
     "sort": [
    {
      "lastCommentTime": {
        "order": "desc"
      }
    }
  ]
}
```