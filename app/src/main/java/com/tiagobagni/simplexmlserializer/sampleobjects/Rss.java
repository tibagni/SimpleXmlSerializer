package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjects;

import java.util.List;

@XmlClass("rss")
public class Rss {
    @XmlObject public Channel channel;

    @Override
    public String toString() {
        return "Rss{" +
                "channel=" + channel +
                '}';
    }

    @XmlClass
    public static class Channel {
        @XmlField public String title;
        @XmlField public String link;
        @XmlField public String description;
        @XmlField public String language;
        @XmlField public String copyright;
        @XmlField public String pubDate;
        @XmlObjects("item") public List<Feed> feed;

        @Override
        public String toString() {
            return "Channel{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", description='" + description + '\'' +
                    ", language='" + language + '\'' +
                    ", copyright='" + copyright + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    ", feed=" + feed +
                    '}';
        }
    }

    @XmlClass
    public static class Feed {
        @XmlField public String title;
        @XmlField public String description;
        @XmlField public String link;
        @XmlField public String author;
        @XmlField public String guid;

        @Override
        public String toString() {
            return "Feed{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", link='" + link + '\'' +
                    ", author='" + author + '\'' +
                    ", guid='" + guid + '\'' +
                    '}';
        }
    }
}
