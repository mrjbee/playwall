package org.monroe.team.playwall.logging;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 2:54 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class ConsoleLogger implements Logger{

    private final String tag;
    private final LogLevel level;

    public ConsoleLogger(String tag, LogLevel level) {
        this.tag = tag;
        this.level = level;
    }

    @Override
    public void i(String msgFormat, Object[] args) {
        if (!isAllowed(LogLevel.INFO)) return;
        String msg = prepareMessage(msgFormat, args, LogLevel.INFO);
        printout(msg, null);
    }

    @Override
    public void d(String msgFormat, Object[] args) {
        if (!isAllowed(LogLevel.DEBUG)) return;
        String msg = prepareMessage(msgFormat, args, LogLevel.DEBUG);
        printout(msg, null);
    }

    @Override
    public void w(Throwable th, String msgFormat, Object[] args) {
        if (!isAllowed(LogLevel.WARN)) return;
        String msg = prepareMessage(msgFormat, args, LogLevel.WARN);
        printout(msg, th);
    }

    @Override
    public void e(Throwable th, String msgFormat, Object[] args) {
        if (!isAllowed(LogLevel.ERROR)) return;
        String msg = prepareMessage(msgFormat, args, LogLevel.ERROR);
        printout(msg, th);
    }

    private void printout(String msg, Throwable th) {
        System.out.println(msg);
        if (th != null){
            th.printStackTrace();
        }
    }


    private String prepareMessage(String msgFormat, Object[] args, LogLevel logLevel) {
        StringBuilder builder = new StringBuilder();
        builder.append("[")
                .append(Thread.currentThread().getId())
                .append(":")
                .append(Thread.currentThread().getName())
                .append(" ");
        builder.append(tag).append(" ");
        builder.append(logLevel).append("] ");
        if (args == null || args.length == 0){
            builder.append(msgFormat);
        } else {
            builder.append(String.format(msgFormat,args));
        }
        return builder.toString();
    }

    private boolean isAllowed(LogLevel logLevel) {
        return level.include(logLevel);
    }

}
