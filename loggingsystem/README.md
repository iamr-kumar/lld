# LOGGING SYSTEM

## API Design

POST - /log
{
"level": DEBUG,
"message": "[Service]: Something happened"
}

POST /log/updateConfig
{
"level": "DEBUG",
"appender": "Grafana"
}

GET /log?startTime={}&endTime={}
GET /log?level=DEBUG&startTime={}&endTime={}

## DB Design - NoSQL

{
level: "DEBUG"
message:
}

{
level,
message,
service,
timestamp,
stackTrace:
}
