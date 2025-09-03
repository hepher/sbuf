String AWS_CLOUD_WATCH_LINE_SEPARATOR = "\n";

    String PATH_VARIABLE_UNIQUE_ID_VALUE = "{#{T(com.enel.notification.commons.util.LabelUtils).NOTIFICATION_UNIQUE_ID}}";

    String JWT_VALUE = "Authorization";
    String TRANSACTION_ID = "trace-id";
    String HEADER_AUTHENTICATION = "Authentication";
    String ANNOTATION_TRANSACTION_ID = "trace-id";
    String AWS_GATEWAY_UNIQUE_ID_HEADER = "X-UniqueID";
    String NOTIFICATION_UNIQUE_ID = "unique-id";
    String SERVICE_PROVIDER = "serviceProvider";
    String CORRELATION_ID = "correlation-id";
    String VERSION = "version";
    String MODULE_ID = "module-id";
    String NAMESPACE = "namespace";
    String CONTAINER = "container";
    String SPAN_ID = "span-id";
    String KAFKA_MESSAGE_CLASS = "message-class";
    String TENANT_ID = "tenantId";
    String SHOW = "show";

    String START_LOG_PARAMS = "START {}={}, Method={}, Params ::: ";
    String FORMAT_LOG_HEADER = "{}={} Method={} ::: ";
    String TASK_INFO_LOG = FORMAT_LOG_HEADER + "Task: '{}' completed in '{}' ms";
    String EXIT_LOG_HEADER_RESULT = "EXIT {}={}, Method={}, Result={}";
    String EXIT_LOG_HEADER_ERROR = "EXIT {}={}, Method={}, Error={}";

    String LOG_ERROR_TRACE_EXCEPTION = "ERROR {}={}, Method={}, Error={} " + AWS_CLOUD_WATCH_LINE_SEPARATOR + "{}";

    String LOG_REQUEST_RESPONSE = AWS_CLOUD_WATCH_LINE_SEPARATOR + "=============================== REQUEST/RESPONSE ===============================";
    String LOG_REQUEST_BEGIN = "=============================== REQUEST BEGIN    ===============================";
    String LOG_CLIENT_REQUEST_BEGIN = AWS_CLOUD_WATCH_LINE_SEPARATOR + "=============================== CLIENT REQUEST BEGIN    ===============================";
    String LOG_CLIENT_RESPONSE_BEGIN = "=============================== CLIENT RESPONSE BEGIN    ===============================";
    String LOG_REQUEST_END = "=============================== REQUEST END      ===============================";
    String LOG_CLIENT_REQUEST_END = "=============================== CLIENT REQUEST END      ===============================";
    String LOG_CLIENT_RESPONSE_END = "=============================== CLIENT RESPONSE END      ===============================";
    String LOG_RESPONSE_BEGIN = "=============================== RESPONSE BEGIN   ===============================";
    String LOG_RESPONSE_END = "=============================== RESPONSE END     ===============================";

    String LOG_INTERNAL_METHOD = "EXECUTION Class={}, Method={} ::: ";

    String LOG_KAFKA_MESSAGE_CONSUMED_BEGIN = AWS_CLOUD_WATCH_LINE_SEPARATOR + "=============================== KAFKA MESSAGE CONSUMED BEGIN    ===============================";
    String LOG_KAFKA_MESSAGE_CONSUMED_END = "=============================== KAFKA MESSAGE CONSUMED END     ===============================";

    String LOG_KEY = "KEY          : {}";
    String LOG_URI = "URI          : {}";
    String LOG_METHOD = "Method       : {}";
    String LOG_HEADERS = "Headers      : {}";
    String LOG_DURATION = "Duration     : {}";
    String LOG_BODY = "Body         : {}";
    String LOG_MESSAGE = "Message      : {}";
    String LOG_STATUS_CODE = "Status code  : {}";
    String LOG_TOPIC = "Topic        : {}";
    String LOG_OFFSET = "Offset       : {}";
    String LOG_ERROR_DETAIL = "Error detail : {}";

    String LOG_KAFKA_MESSAGE = StringUtils.join(new String[] {
            LOG_KAFKA_MESSAGE_CONSUMED_BEGIN,
            LOG_TOPIC,
            LOG_KEY,
            LOG_OFFSET,
            LOG_HEADERS,
            LOG_MESSAGE,
            LOG_DURATION,
            LOG_KAFKA_MESSAGE_CONSUMED_END
    }, AWS_CLOUD_WATCH_LINE_SEPARATOR);

    String LOG_ERROR_KAFKA_MESSAGE = StringUtils.join(new String[] {
            LOG_KAFKA_MESSAGE_CONSUMED_BEGIN,
            LOG_TOPIC,
            LOG_KEY,
            LOG_OFFSET,
            LOG_HEADERS,
            LOG_MESSAGE,
            LOG_DURATION,
            LOG_ERROR_DETAIL,
            LOG_KAFKA_MESSAGE_CONSUMED_END
    }, AWS_CLOUD_WATCH_LINE_SEPARATOR);

    String LOG_CHANNEL = "Channel      : {}";
    String LOG_SERVICE_NAME = "Service      : {}";

    String LOG_GRPC_REQUEST_BEGIN = AWS_CLOUD_WATCH_LINE_SEPARATOR + "=============================== GRPC REQUEST BEGIN    ===============================";
    String LOG_GRPC_REQUEST_END = "=============================== GRPC REQUEST END     ===============================";

    String LOG_GRPC_REQUEST = StringUtils.join(new String[] {
            LOG_GRPC_REQUEST_BEGIN,
            LOG_CHANNEL,
            LOG_SERVICE_NAME,
            LOG_METHOD,
            LOG_HEADERS,
            LOG_BODY,
            LOG_GRPC_REQUEST_END
    }, AWS_CLOUD_WATCH_LINE_SEPARATOR);

    String LOG_GRPC_RESPONSE = StringUtils.join(new String[] {
            LOG_GRPC_REQUEST_BEGIN,
            LOG_CHANNEL,
            LOG_SERVICE_NAME,
            LOG_METHOD,
            LOG_HEADERS,
            LOG_BODY,
            LOG_DURATION,
            LOG_RESPONSE_BEGIN,
            LOG_STATUS_CODE,
            LOG_HEADERS,
            LOG_BODY,
            LOG_GRPC_REQUEST_END
    }, AWS_CLOUD_WATCH_LINE_SEPARATOR);

    String LOG_REST_CALL_REQUEST_RESPONSE = StringUtils.join(new String[] {
            LOG_REQUEST_RESPONSE,
            LOG_REQUEST_BEGIN,
            LOG_URI,
            LOG_METHOD,
            LOG_HEADERS,
            LOG_DURATION,
            LOG_BODY,
            LOG_REQUEST_END,
            LOG_RESPONSE_BEGIN,
            LOG_STATUS_CODE,
            LOG_HEADERS,
            LOG_BODY,
            LOG_RESPONSE_END
    }, AWS_CLOUD_WATCH_LINE_SEPARATOR);

    String LOG_CLIENT_REQUEST = StringUtils.join(new String[] {
            LOG_CLIENT_REQUEST_BEGIN,
            LOG_URI,
            LOG_METHOD,
            LOG_HEADERS,
            LOG_BODY,
            LOG_CLIENT_REQUEST_END
    }, AWS_CLOUD_WATCH_LINE_SEPARATOR);

    String LOG_CLIENT_REQUEST_RESPONSE = StringUtils.join(new String[] {
            LOG_CLIENT_REQUEST_BEGIN,
            LOG_URI,
            LOG_METHOD,
            LOG_HEADERS,
            LOG_DURATION,
            LOG_BODY,
            LOG_CLIENT_REQUEST_END,
            LOG_CLIENT_RESPONSE_BEGIN,
            LOG_STATUS_CODE,
            LOG_HEADERS,
            LOG_BODY,
            LOG_CLIENT_RESPONSE_END
    }, AWS_CLOUD_WATCH_LINE_SEPARATOR);
