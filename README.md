# External Stub

A stub service for testing, load testing, local development, etc. It allows matching requests by headers and payload 
and configuring multiple response variants.

##### Requirements for Local Run:
* _Java_ 25+
* _gradle_ 9+
* _postgres_ - localhost:5432/postgres (postgres:postgres)
* _artemis_ - localhost:61616

##### Swagger
Swagger is available at: http://localhost:9999/swagger-ui/index.html

##### Stubbing MQ Queues:
###### Using REST:

call
```
POST /response/mq
```

Request body example:
```json
{
  "headerKey": "correlationId",
  "headerValue": "1212",
  "queue": "INT.QUEUE.IN",
  "responseBody": "{\"status\":\"OK\"}",
  "payloadType": "JSON",
  "matchingExpression": "$..messageId",
  "delayInSec": 0,
  "ttlInSec": 0
}
```
| Field              | Type           | Required | Default value |                                                                                                                                                   |
|--------------------|----------------|----------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| headerKey          | String         |          |               | Name of the header from the incoming message used to match this response. If not specified, all incoming requests to the queue will be stubbed    |
| headerValue        | String         |          |               | Value of the header from the incoming message. Used if _headerKey_ is specified                                                                   |
| queue              | String         | *        |               | Queue name                                                                                                                                        |
| responseBody       | String         | *        |               | Response returned by the stub                                                                                                                     |
| payloadType        | Enum: JSON/XML |          | JSON          | Message format in the queue.                                                                                                                      |
| matchingExpression | String         |          |               | JsonPath/XPath that the request payload must match. Used when returning different responses for the same queue depending on the incoming payload  |
| delayInSec         | Long           |          | 0             | Delay in seconds before the stub returns a response                                                                                           |
| ttlInSec           | Long           |          | 0             | Time in seconds the rule is stored in the service. Default is 1 day. If set to 0, it is stored indefinitely                         |

###### Via Database
Table _external_stub.mq_response_

| Field               | Type                     | NULLABLE |                                                                                                                                                  |
|---------------------|--------------------------|----------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| header_key          | varchar                  |          | Name of the header from the incoming message used to match this response. If not specified, all incoming requests to the queue will be stubbed   |
| header_value        | varchar                  |          | Value of the header from the incoming message. Used if _header_key_ is specified                                                                 |
| queue               | varchar                  |          | Queue name                                                                                                                                       |
| response_body       | varchar                  |          | Response returned by the stub                                                                                                                    |
| payload_type        | Enum: JSON/XML           |          | Message format in the queue                                                                                                                      |
| matching_expression | varchar                  |          | JsonPath/XPath that the request payload must match. Used when returning different responses for the same queue depending on the incoming payload |
| delay_in_sec        | long                     |          | Delay in seconds before the stub returns a response                                                                                              |
| ttl_in_sec          | timestamp with time zone | *        | DateTime after which the record will be removed from the database                                                                                |

To make the application start listening to a queue added via DB, call the next REST: `POST /response/mq/reload`

##### JsonPath/XPath Matching

Matching by JsonPath/XPath works as a search for payload nodes/elements that satisfy the specified expression.
If exists at least one matching node, the payload is considered a match

###### JsonPath examples
| JsonPath                                     | Description                                                                                            |
|----------------------------------------------|--------------------------------------------------------------------------------------------------------|
| $.[?(@.fieldName == 'fieldValue')]           | JSON contains a field _fieldName_ (at any depth, but not inside an array) with value _fieldValue_      |
| $..fieldName                                 | JSON contains a field _fieldName_ at any depth                                                         |
| $..arrayName[?(@.fieldName == 'fieldValue')] | SON contains an array _arrayName_ with an element that has a field _fieldName_ equal to _fieldValue_   |

###### XPath examples
| XPath        | Description                                            |
|--------------|--------------------------------------------------------|
| //XmlElement | XML contains an element named XmlElement at any depth  |



##### Stubbing REST Endpoints
###### Using REST

Call REST `POST /response/rest`
Request body example:
```json
{
  "headerKey": "correlationId",
  "headerValue": "1212",
  "method": "GET",
  "path": "requests/1/documents",
  "responseStatus": 200,
  "responseBody": "{\"status\":\"OK\"}",
  "delayInSec": 0,
  "ttlInSec": 0
}
```
| Field          | Type   | Required | Default |                                                                                                              |
|----------------|--------|----------|---------|--------------------------------------------------------------------------------------------------------------|
| headerKey      | String |          |         | Request header name                                                                                          |
| headerValue    | String |          |         | Request header value                                                                                         |
| method         | String | *        |         | Request method: GET, PUT, POST, PATCH, DELETE                                                                |
| path           | String | *        |         | Mocked Rest method path                                                                                      |
| responseStatus | Int    |          | 200     | Http response code                                                                                           |
| responseBody   | String | *        |         | Http response body                                                                                           |
| delayInSec     | Long   |          | 0       | Delay in seconds before the stub returns a response                                                          |
| ttlInSec       | Long   |          | 0       | Time in seconds the rule is stored in the service. Default is 1 day. If set to 0, it is stored indefinitely  |

Alternatively, you can add records directly to the table: _external_stub.rest_response_ (No need to call REST afterward.)
⚠ On the client application side (which calls the stubbed REST service), the URL must be set to: `http://<stubber-host>:9999/stub`