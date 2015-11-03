package alarmproject.apps.plow.alarmproject.model;

import android.content.Context;

/**
 * Created by YouNesS on 20/09/2015.
 */
public abstract class VoiceCommand {
    private String command;

    public VoiceCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean tryParse(String s)
    {
        return s.toLowerCase().contains(command.toLowerCase());
    }

    abstract public void action();
}
