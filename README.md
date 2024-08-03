# Json Serializer Generator for Vert.x Data Objects

Generate JSON serializer for Vert.x data objects.

- reflection free
- multiple backends
  - Jackson
  - DslJson
  - FastJson

```java
@DataObject
@JsonSerGen(backends = Backend.FAST_JSON)
public class User {
  public String getFirstName() {
    return firstName;
  }
  // ...
  public Buffer toJsonBuffer() {
    return UserJsonSerializer.toJsonBuffer(this);
  }
}
```
