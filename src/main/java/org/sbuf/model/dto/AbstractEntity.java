package org.sbuf.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractEntity implements Serializable {

    @Override
    public final int hashCode() {  return HashCodeBuilder.reflectionHashCode(this); }

    @Override
    public final boolean equals(final Object obj) {  return EqualsBuilder.reflectionEquals(this, obj); }

    @Override
    public String toString() {  return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE); }
}
