package trainedge.pod_droid.restclient.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.web.client.RestTemplate;

import trainedge.pod_droid.tools.Util;


public class RssFeedRequest extends SpringAndroidSpiceRequest<byte[]> {


    private String url;
    public RssFeedRequest(String url) {
        super(byte[].class);
        this.url=url;
        Util.le("Making request to: "+url);
    }

    @Override
    public byte[] loadDataFromNetwork() throws Exception {
        RestTemplate restTemplate=getRestTemplate();

        return restTemplate.getForObject(url, byte[].class);
    }


}
