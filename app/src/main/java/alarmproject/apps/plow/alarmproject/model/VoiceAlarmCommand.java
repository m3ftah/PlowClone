package alarmproject.apps.plow.alarmproject.model;

/**
 * Created by YouNesS on 20/09/2015.
 */
public abstract class VoiceAlarmCommand {
    private String command;
    private int state;
    public VoiceAlarmCommand(String command) {
        this.command = command;
        this.state=0;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean tryParse(String s)
    {
        if (state==0)
        {
            if (s.toLowerCase().contains(command.toLowerCase()))
            {
                state=1;
                return true;
            }
            else return false;
        }
        else
        {
            if (s.toLowerCase().toLowerCase().split(":").length==2) {
                state=0;
                return true;
            }
            else return false;
        }
    }

    abstract public void action(int a,int b);
}
