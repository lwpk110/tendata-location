package cn.tendata.location.client.rest.model;

import cn.tendata.location.client.rest.repository.IpLocationRepository;
import cn.tendata.location.client.rest.repository.RestClientRepositoryImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class IpLocationItem extends AbstractElasticsearchEntity {

    private static final String IP_LOCATION_INDEX = "tendata_ip_location";
    private static final String IP_LOCATION_TYPE = "ip_location";

    private String ipCidr;
    private String ipStart;
    private String ipEnd;
    private String continent;
    private String alpha2CountryCode;
    private String province;
    private String city;
    private GeoPoint geoPoint;
    private DateTime createdDate = DateTime.now();
    private DateTime lastModifiedDate = DateTime.now();

    public static String getIndexName(){
        return IP_LOCATION_INDEX;
    }

    public static String getTypeName(){
        return IP_LOCATION_TYPE;
    }


    @Data
    public static class GeoPoint implements Serializable {
        private double lat;
        private double lon;
    }

    public static class IpLocationDocumentBuilder {
        private IpLocationItem ipLocationDocument;

        public IpLocationDocumentBuilder() {
            ipLocationDocument = new IpLocationItem();
        }

        public IpLocationDocumentBuilder ipStartInt(String ipStartInt) {
            ipLocationDocument.setIpStart(ipStartInt);
            return this;
        }

        public IpLocationDocumentBuilder ipEndInt(String ipEndInt) {
            ipLocationDocument.setIpEnd(ipEndInt);
            return this;
        }

        public IpLocationDocumentBuilder continent(String continent) {
            ipLocationDocument.setContinent(continent);
            return this;
        }

        public IpLocationDocumentBuilder alpha2CountryCode(String alpha2CountryCode) {
            ipLocationDocument.setAlpha2CountryCode(alpha2CountryCode);
            return this;
        }

        public IpLocationDocumentBuilder province(String province) {
            ipLocationDocument.setProvince(province);
            return this;
        }

        public IpLocationDocumentBuilder city(String city) {
            ipLocationDocument.setCity(city);
            return this;
        }

        public IpLocationDocumentBuilder geoPoint(double lat, double lon) {
            GeoPoint geoPoint = new GeoPoint();
            geoPoint.setLat(lat);
            geoPoint.setLon(lon);
            ipLocationDocument.setGeoPoint(geoPoint);
            return this;
        }

        public IpLocationDocumentBuilder ipCidr(String cidr){
            ipLocationDocument.setIpCidr(cidr);
            return this;
        }

        public IpLocationItem build() {
            Assert.hasText(ipLocationDocument.getIpCidr(),"ip cidr not be null");
            return ipLocationDocument;
        }


    }
}
