package cn.tendata.location.core;


import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

public class DefaultMessageSource extends ResourceBundleMessageSource {

    public DefaultMessageSource() {
        setBasename("cn.tendata.location.messages");
    }
    
    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new DefaultMessageSource());
    }
}
