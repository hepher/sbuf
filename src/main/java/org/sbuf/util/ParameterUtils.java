import java.util.Arrays;
import java.util.List;

public class ParameterUtils {

    public static List<String> getSensitiveDataFields() {
        var sensitiveDataFields = ApplicationContextUtils.evalExpression("${bfw.model.entity.sensitive-data-fields:}");
        if (sensitiveDataFields instanceof String) {
            return Arrays.asList(((String) sensitiveDataFields).split("\\s*,\\s*"));
        }

        return null;
    }
}
