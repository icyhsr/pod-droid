package trainedge.pod_droid.Model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(name="rss",strict = false)
@Namespace(reference = "http://www.itunes.com/dtds/podcast-1.0.dtd",prefix = "itunes")
@ElementList(entry = "link", inline = true, required = false)
public class Podcast {
    @Attribute
    private String version;

    @Element(name = "channel")
    private Channel channel;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
