package fyp.model;

import com.sohrab.obd.reader.obdCommand.ObdCommand;

//clearing codes.
public class ClearTroubleCodes extends ObdCommand {

    //passing AT commands to clear codes
    public ClearTroubleCodes() {
        super("04");
    }

    @Override
    protected void performCalculations() {

    }

    @Override
    public String getFormattedResult() {
        return null;
    }

    @Override
    public String getCalculatedResult() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
