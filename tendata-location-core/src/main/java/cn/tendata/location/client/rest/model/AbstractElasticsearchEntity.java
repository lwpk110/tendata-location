package cn.tendata.location.client.rest.model;

import cn.tendata.location.core.jackson.DataView;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;


public abstract class AbstractElasticsearchEntity implements Serializable {
    private static final long serialVersionUID = 4399537692124496214L;


    @JsonView(DataView.Basic.class)
    protected String id;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public boolean isNew() {
        return null == getId();
    }

    @Override
    public String toString() {

        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }


    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        AbstractElasticsearchEntity that = (AbstractElasticsearchEntity) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }
}

