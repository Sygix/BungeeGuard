package fr.PunKeel.BungeeGuard.Utils;

import com.google.common.base.Throwables;
import fr.PunKeel.BungeeGuard.Main;
import net.kencochrane.raven.Raven;
import net.kencochrane.raven.event.Event;
import net.kencochrane.raven.event.EventBuilder;
import net.kencochrane.raven.event.interfaces.ExceptionInterface;
import net.kencochrane.raven.event.interfaces.MessageInterface;
import net.md_5.bungee.api.ProxyServer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SentryHandler extends Handler {
    private Raven raven;
    private String version;

    private String[] blacklist = new String[]{
            "] <-> InitialHandler - encountered exception",
            "Handler - IOException: Connection reset by peer",
            "] <-> InitialHandler - read timed out",
            "] <-> InitialHandler - IOException: Connection reset by peer"
    };

    public SentryHandler(Raven raven, String version) {
        this.raven = raven;
        this.version = version;
    }

    /**
     * Transforms a {@link Level} into an {@link Event.Level}.
     *
     * @param level original level as defined in JUL.
     * @return log level used within raven.
     */
    protected static Event.Level getLevel(final Level level) {
        if (level.intValue() >= Level.SEVERE.intValue())
            return Event.Level.ERROR;
        else if (level.intValue() >= Level.WARNING.intValue())
            return Event.Level.WARNING;
        else if (level.intValue() >= Level.INFO.intValue())
            return Event.Level.INFO;
        else if (level.intValue() >= Level.ALL.intValue())
            return Event.Level.DEBUG;
        else return null;
    }

    /**
     * Extracts message parameters into a List of Strings.
     * <p/>
     * null parameters are kept as null.
     *
     * @param parameters parameters provided to the logging system.
     * @return the parameters formatted as Strings in a List.
     */
    protected static List<String> formatMessageParameters(Object[] parameters) {
        List<String> formattedParameters = new ArrayList<>(parameters.length);
        for (Object parameter : parameters)
            formattedParameters.add((parameter != null) ? parameter.toString() : null);
        return formattedParameters;
    }

    @Override
    public void publish(final LogRecord record) {
        // Do not log the event if the current thread is managed by raven
        if (!isLoggable(record))
            return;

        if (record.getLevel().intValue() < Level.WARNING.intValue())
            return;

        for (String blacklisted_message : blacklist) {
            if (record.getMessage().contains(blacklisted_message))
                return;
        }

        try {
            final Event event = buildEvent(record);
            ProxyServer.getInstance().getScheduler().runAsync(Main.plugin, new Runnable() {
                @Override
                public void run() {
                    raven.sendEvent(event);
                }
            });
        } catch (Exception e) {
            reportError("An exception occurred while creating a new event in Raven", e, ErrorManager.WRITE_FAILURE);
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {
        if (raven != null)
            raven.closeConnection();
    }

    /**
     * Builds an Event based on the log record.
     *
     * @param record Log generated.
     * @return Event containing details provided by the logging system.
     */
    protected Event buildEvent(LogRecord record) {
        EventBuilder eventBuilder = new EventBuilder()
                .withLevel(getLevel(record.getLevel()))
                .withTimestamp(new Date(record.getMillis()))
                .withLogger(record.getLoggerName());

        String message = record.getMessage();
        if (record.getResourceBundle() != null && record.getResourceBundle().containsKey(record.getMessage())) {
            message = record.getResourceBundle().getString(record.getMessage());
        }
        if (record.getParameters() != null) {
            List<String> parameters = formatMessageParameters(record.getParameters());
            eventBuilder.withSentryInterface(new MessageInterface(message, parameters));
            message = MessageFormat.format(message, record.getParameters());
        }
        eventBuilder.withMessage(message);

        Throwable throwable = record.getThrown();
        if (throwable != null) {
            eventBuilder.withSentryInterface(new ExceptionInterface(Throwables.getRootCause(throwable))); // Just a test.
        }
        if (record.getSourceClassName() != null && record.getSourceMethodName() != null) {
            StackTraceElement fakeFrame = new StackTraceElement(record.getSourceClassName(),
                    record.getSourceMethodName(), null, -1);
            eventBuilder.withCulprit(fakeFrame);
        } else {
            eventBuilder.withCulprit(record.getLoggerName());
        }
        eventBuilder.withServerName(Main.getMB().getServerId());
        eventBuilder.withTag("version", version);
        raven.runBuilderHelpers(eventBuilder);
        return eventBuilder.build();
    }
}
