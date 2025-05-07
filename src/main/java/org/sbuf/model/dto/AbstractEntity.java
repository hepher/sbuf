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
    public String toString() {
        ToStringStyle toStringStyle = ToStringStyle.SHORT_PREFIX_STYLE;

        List<String> sensitiveDataFields = ParameterUtils.getSensitiveDataFields();
        if (sensitiveDataFields != null && !sensitiveDataFields.isEmpty()) {
            toStringStyle = new SentivieDataToStringStyle(sensitiveDataFields);
        }

        ReflectionToStringBuilder toStringBuilder = new ReflectionToStringBuilder(this, toStringStyle, null, null, false, false);

        return toStringBuilder.toString();
    }

    private static class SentivieDataToStringStyle extends ToStringStyle {
        private final List<String> excludeFieldNames;

        public SentivieDataToStringStyle(List<String> excludeFieldNames) {
            this.excludeFieldNames = excludeFieldNames;

            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
        }

        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
            if (excludeFieldNames != null && excludeFieldNames.contains(fieldName)) {
                buffer.append("*****");
            } else {
                super.appendDetail(buffer, fieldName, value);
            }
        }

        private Object readResolve() {
            return this;
        }
    }
}
