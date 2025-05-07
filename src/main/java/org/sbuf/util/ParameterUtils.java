import java.util.Arrays;
import java.util.List;

public class ParameterUtils {

    public static List<String> getSensitiveDataFields() {
        var sensitiveDataFields = ApplicationContextUtils.evalExpression("${commons.model.entity.to-string-exclude-fields:}");
        if (sensitiveDataFields instanceof String) {
            return Arrays.asList(((String) sensitiveDataFields).split(","));
        }

        return null;
    }
}
