package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjectList;

import java.util.List;

/**
 * Created by tiagobagni on 18/08/17.
 */
@XmlObject
public class ShoppingCart {
    @XmlObjectList("Items")
    private List<ShoppingCartItem> items;

    @XmlField("Name")
    private String name;

    public List<ShoppingCartItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingCartItem> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
