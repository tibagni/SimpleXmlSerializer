# SimpleXmlSerializer
A simple Android tool to help create XML from Java objects and also read XML Strings and convert into java objects.
## Download
Add the dependency in Gradle:
```gradle
dependencies {
    compile 'com.github.tibagni:simple-xml-serializer:0.1.1'
}
```
Package is available in *jcenter*
## Usage
*SimpleXmlSerializer* is a very simple and easy-to-use tool.
### Serializing an existing Java Object
Suppose you have the following Java class:
```java
public class Product {
    private String name;
    private String description;
    private double price;
    
    // getters and setters ...
}
```
If you want to convert this simple class into XML, all you have to do is add a few annotations:
```java
@XmlClass
public class Product {
    @XmlField("Name")
    private String name;

    @XmlField("Description")
    private String description;

    @XmlField("Price")
    private double price;
    
    // getters and setters ...
}
```
And tell *XmlSerializer* to do the job:
```java
private String toXml(Product product) throws IllegalAccessException {
    XmlSerializer serializer = new XmlSerializer();
    return serializer.serialize(product);
}
```
### Deserializing an Object
To convert an XML String into a Java Object, it is just as simple. Below code will convert the following xml:
```xml
<?xml version="1.0"?>
<Product>
  <Description>Product Description</Description>
  <Name>Product Name</Name>
  <Price>43.1</Price>
</Product>
```
into an instance of *Product* (as defined above). Just run the following code:
```java
private Product fromXml(String xml) throws XmlDeserializationException {
    XmlDeserializer deserializer = new XmlDeserializer(Product.class);
    return (Product) deserializer.deserialize(xml);
}
```
### Complex Objects
The examples above show how to serialize and deserialize a simple Object containing only primitive types (and String).
You are not limited to it. It is also possible to serialize/deserialize nested objects and objects with lists.
Check the **app** module for a detailed exapmple of use.
Below is an example of a more complex structure that you can use:
```java
@XmlClass
public class Product {
    @XmlField("Name")
    private String name;

    @XmlField("Description")
    private String description;

    @XmlField("Price")
    private double price;
    
    // getters and setters ...
}
...
@XmlClass
public class ShoppingCartItem {
    @XmlObject("Product")
    private Product product;

    @XmlField("Quantity")
    private int quantity;
    
    // getters and setters ...
}
...
@XmlClass
public class ShoppingCart {
    @XmlObjectList("Items")
    private List<ShoppingCartItem> items;

    @XmlField("Name")
    private String name;
    
    // getters and setters ...
}
```

## Annotations
As shown in the examples above, there are 4 annotations to use:
```java 
@XmlClass
```
Used at class level. This annotation is used to tell a class represents Xml content
```java 
@XmlField("xmlTag")
```
Used at field level. This annotation is used to tell a certain field is part of the Xml content. Only primitive types and String
```java 
@XmlObject("xmlTag")
```
Used at field level. This annotation is used to tell a certain field is part of the Xml content. Only non-primitive types
```java 
@XmlObjectList("xmlTag")
```
Used at field level. This annotation is used to tell a list is part of the Xml content. Only `java.util.List`
