/*
package cn.tendata.location.data.elasticsearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.util.Assert;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(indexName = "tendata_ip_location", type = "ip_location")
public class IpLocationDocument extends AbstractElasticsearchEntity {
    @Mapping(mappingPath = "/elasticsearch/mapping/ip-location-ipCidr-field.json")
    private String ipCidr;
    @Field(type = FieldType.Keyword)
    private String ipStart;
    @Field(type = FieldType.Keyword)
    private String ipEnd;
    @Field(type = FieldType.Keyword, index = false)
    private String continent;
    @Field(type = FieldType.Keyword, index = false)
    private String alpha2CountryCode;
    @Field(type = FieldType.Keyword, index = false)
    private String province;
    @Field(type = FieldType.Keyword, index = false)
    private String city;
    @Mapping(mappingPath = "/elasticsearch/mapping/ip-location-geoPoint-field.json")
    private GeoPoint geoPoint;
    @Field(type = FieldType.Date, index = false)
    private DateTime createdDate = DateTime.now();
    @Field(type = FieldType.Date, index = false)
    private DateTime lastModifiedDate = DateTime.now();

    @Data
    public static class GeoPoint implements Serializable {
        private double lat;
        private double lon;

    }

    public static class IpLocationDocumentBuilder {
        private IpLocationDocument ipLocationDocument;

        public IpLocationDocumentBuilder() {
            ipLocationDocument = new IpLocationDocument();
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

        public IpLocationDocument build() {
            Assert.hasText(ipLocationDocument.getIpCidr(),"ip cidr not be null");
            return ipLocationDocument;
        }


    }
}
*/
