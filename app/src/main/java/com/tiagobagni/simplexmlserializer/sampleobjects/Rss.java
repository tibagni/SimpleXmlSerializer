package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjects;

import java.util.List;

@XmlClass
public class Rss {
    @XmlObject("channel") public Channel channel;

    @Override
    public String toString() {
        return "Rss{" +
                "channel=" + channel +
                '}';
    }

    @XmlClass
    public static class Channel {
        @XmlField("title") public String title;
        @XmlField("link") public String link;
        @XmlField("description") public String description;
        @XmlField("language") public String language;
        @XmlField("copyright") public String copyright;
        @XmlField("pubDate") public String pubDate;
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
        @XmlField("title") public String title;
        @XmlField("description") public String description;
        @XmlField("link") public String link;
        @XmlField("author") public String author;
        @XmlField("guid") public String guid;

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
