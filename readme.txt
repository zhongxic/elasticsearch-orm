PUT /user
{
  "mappings": {
    "_doc": {
      "properties": {
        "id": {
          "type": "long"
        },
        "username": {
          "type": "keyword"
        },
        "password": {
          "type": "keyword"
        },
        "gender": {
          "type": "keyword"
        },
        "create_time": {
          "type": "date",
          "format": "yyyy-MM-dd HH:mm:ss"
        }
      }
    }
  }
}

PUT /user/_doc/1
{
  "id": 1,
  "username": "user,n,ame,,,",
  "password": "password",
  "gender": "m",
  "create_time": "2023-09-01 00:00:00"
}


PUT /user/_doc/2
{
  "id": 2,
  "username": "Mitch",
  "password": "password",
  "gender": "m",
  "create_time": "2023-09-02 00:00:00"
}

PUT /user/_doc/3
{
  "id": 3,
  "username": "Kitty",
  "password": "password",
  "gender": "w",
  "create_time": "2023-09-03 00:00:00"
}
