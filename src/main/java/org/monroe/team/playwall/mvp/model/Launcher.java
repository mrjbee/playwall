package org.monroe.team.playwall.mvp.model;

import java.util.Arrays;

/**
 * User: MisterJBee
 * Date: 10/17/13 Time: 7:15 PM
 * Open source: MIT Licence
 * (Do whatever you want with the source code)
 */
public class Launcher {

    public final String id;
    public final String title;
    public final String type;
    public final String dir;
    public final String[] exec;
    public final String proc;
    public final String dbId;
    public final boolean shutdown;

    public Launcher(String id, String title, String type, String dir, String[] exec, String proc, String gameId, boolean shutdown) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.dir = dir;
        this.exec = exec;
        this.proc = proc;
        this.dbId = gameId;
        this.shutdown = shutdown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Launcher launcher = (Launcher) o;

        if (dir != null ? !dir.equals(launcher.dir) : launcher.dir != null) return false;
        if (!Arrays.equals(exec, launcher.exec)) return false;
        if (id != null ? !id.equals(launcher.id) : launcher.id != null) return false;
        if (title != null ? !title.equals(launcher.title) : launcher.title != null) return false;
        if (type != null ? !type.equals(launcher.type) : launcher.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (dir != null ? dir.hashCode() : 0);
        result = 31 * result + (exec != null ? Arrays.hashCode(exec) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Launcher{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", dir='" + dir + '\'' +
                ", exec=" + (exec == null ? null : Arrays.asList(exec)) +
                ", shutdown=" + shutdown +
                '}';
    }
}
